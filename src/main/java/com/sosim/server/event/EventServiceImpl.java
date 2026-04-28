package com.sosim.server.event;

import com.sosim.server.config.exception.CustomException;
import com.sosim.server.event.dto.info.DayInfo;
import com.sosim.server.event.dto.info.EventInfo;
import com.sosim.server.event.dto.info.EventListInfo;
import com.sosim.server.event.dto.info.EventSingleInfo;
import com.sosim.server.event.dto.info.ListInfo;
import com.sosim.server.event.dto.req.EventCreateReq;
import com.sosim.server.event.dto.req.EventFilterRequest;
import com.sosim.server.event.dto.req.EventModifyReq;
import com.sosim.server.event.dto.req.MonthlyDayPaymentTypeReq;
import com.sosim.server.event.dto.req.PaymentTypeReq;
import com.sosim.server.group.Group;
import com.sosim.server.group.GroupRepository;
import com.sosim.server.participant.Participant;
import com.sosim.server.participant.ParticipantRepository;
import com.sosim.server.security.AuthUser;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.EventType;
import com.sosim.server.type.PaymentType;
import com.sosim.server.type.StatusType;
import com.sosim.server.user.User;
import com.sosim.server.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    public EventSingleInfo getEvent(long id) {
        Event event = getActiveEvent(id);
        EventSingleInfo eventSingleInfo = EventSingleInfo.from(event);

        resolveNickname(event).ifPresentOrElse(
            eventSingleInfo::setUserName,
            () -> eventSingleInfo.setUserName("")
        );

        boolean isAdmin = event.getGroup().getAdminId().equals(event.getUser().getId());
        eventSingleInfo.setAdminYn(isAdmin ? "true" : "false");

        return eventSingleInfo;
    }

    @Override
    public Long createEvent(AuthUser authUser, EventCreateReq eventCreateReq) {
        Group group = groupRepository.findByIdAndStatus(eventCreateReq.getGroupId(), StatusType.USING)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_GROUP));

        Participant participant = participantRepository
            .findByNicknameAndGroupAndStatus(eventCreateReq.getUserName(), group, StatusType.USING)
            .orElseThrow(() -> new CustomException(CodeType.INVALID_USER));

        if (!participant.getGroup().getAdminId().equals(Long.parseLong(authUser.getId()))) {
            throw new CustomException(CodeType.INVALID_EVENT_CREATER);
        }

        LocalDate localDate = LocalDate.parse(eventCreateReq.getGroundsDate(),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        User user = userRepository.findById(participant.getUser().getId())
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_USER));

        Event event = Event.builder()
            .groundsDate(LocalDateTime.of(localDate, LocalTime.MIDNIGHT))
            .payment(eventCreateReq.getPayment())
            .grounds(eventCreateReq.getGrounds())
            .paymentType(PaymentType.getType(eventCreateReq.getPaymentType()))
            .group(group)
            .user(user)
            .statusType(StatusType.USING)
            .eventType(EventType.DUES_PAYMENT)
            .build();

        eventRepository.save(event);
        return event.getId();
    }

    @Override
    public EventInfo updateEvent(AuthUser authUser, long id, EventModifyReq eventModifyReq) {
        Event event = getActiveEvent(id);

        if (!event.getGroup().getAdminId().equals(Long.parseLong(authUser.getId()))) {
            throw new CustomException(CodeType.INVALID_EVENT_CREATER);
        }

        Participant participant = participantRepository
            .findByNicknameAndGroupAndStatus(eventModifyReq.getUserName(), event.getGroup(), StatusType.USING)
            .orElseThrow(() -> new CustomException(CodeType.INVALID_USER));

        User user = userRepository.findById(participant.getUser().getId())
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_USER));
        eventModifyReq.setUser(user);

        event.updateEvent(eventModifyReq);
        eventRepository.save(event);

        EventInfo eventInfo = EventInfo.from(event);
        eventInfo.setUserName(participant.getNickname());
        return eventInfo;
    }

    @Override
    public void deleteEvent(AuthUser authUser, long id) {
        Event event = getActiveEvent(id);

        if (!event.getGroup().getAdminId().equals(Long.parseLong(authUser.getId()))) {
            throw new CustomException(CodeType.INVALID_EVENT_CREATER);
        }
        event.deleteEvent();
        eventRepository.save(event);
    }

    @Override
    public EventInfo changePaymentType(AuthUser authUser, long id, PaymentTypeReq paymentTypeReq) {
        Event event = getActiveEvent(id);

        if (!event.getGroup().getAdminId().equals(Long.parseLong(authUser.getId()))) {
            if (event.getUser().getId().equals(Long.parseLong(authUser.getId()))) {
                if (!event.getPaymentType().equals(PaymentType.NON_PAYMENT) ||
                    !paymentTypeReq.getPaymentType().equals("con")) {
                    throw new CustomException(CodeType.PAYMENT_TYPE_MUST_BE_NON);
                }
                event.setUserNonToCon(event.getUserNonToCon() + 1);
            } else {
                throw new CustomException(CodeType.INVALID_PAYMENT_TYPE_CHANGER);
            }
        }

        if (paymentTypeReq.getPaymentType().equals("full")) {
            if (event.getPaymentType().equals(PaymentType.NON_PAYMENT)) {
                event.setAdminNonToFull(event.getAdminNonToFull() + 1);
            } else if (event.getPaymentType().equals(PaymentType.CONFIRMING)) {
                event.setAdminConToFull(event.getAdminConToFull() + 1);
            } else {
                throw new CustomException(CodeType.INVALID_PAYMENT_TYPE_PARAMETER);
            }
        }

        event.changePaymentType(paymentTypeReq);
        eventRepository.save(event);

        EventInfo eventInfo = EventInfo.from(event);
        resolveNickname(event).ifPresentOrElse(
            eventInfo::setUserName,
            () -> eventInfo.setUserName("")
        );
        return eventInfo;
    }

    @Override
    public ListInfo<EventListInfo> getEventList(long groupId, EventFilterRequest request) {
        if (request.getPage() == null) {
            throw new CustomException(CodeType.INPUT_PAGE_DATA);
        }
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_GROUP));

        Page<Event> page = eventRepository.searchAll(groupId, request,
            PageRequest.of(request.getPage(), 16, Sort.by(Direction.ASC, "groundsDate")));

        return ListInfo.from(page.getTotalElements(), resolveEventInfoList(page.getContent(), group));
    }

    @Override
    public List<DayInfo> getMonthlyDayPaymentType(long groupId, MonthlyDayPaymentTypeReq mdpTreq) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_GROUP));

        YearMonth ym = YearMonth.of(mdpTreq.getYear(), mdpTreq.getMonth());
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(LocalTime.MAX);

        List<Event> eventList = eventRepository.findByGroupAndStatusTypeAndGroundsDateBetween(
            group, StatusType.USING, start, end);

        return eventList.stream()
            .map(x -> {
                int dayOfMonth = x.getGroundsDate().getDayOfMonth();
                Map<String, Integer> countMap = new HashMap<>();
                countMap.put("non", countByDay(eventList, dayOfMonth, PaymentType.NON_PAYMENT));
                countMap.put("con", countByDay(eventList, dayOfMonth, PaymentType.CONFIRMING));
                countMap.put("full", countByDay(eventList, dayOfMonth, PaymentType.FULL_PAYMENT));
                return DayInfo.builder().day(dayOfMonth).paymentTypeCountMap(countMap).build();
            })
            .filter(distinctByKey(DayInfo::getDay))
            .sorted(Comparator.comparingInt(DayInfo::getDay))
            .collect(Collectors.toList());
    }

    private List<EventListInfo> resolveEventInfoList(List<Event> events, Group group) {
        return events.stream()
            .map(event -> {
                EventListInfo info = EventListInfo.from(event);
                info.setUserName(resolveNickname(event).orElse(""));
                return info;
            })
            .collect(Collectors.toList());
    }

    private java.util.Optional<String> resolveNickname(Event event) {
        return participantRepository
            .findByUserAndGroupAndStatus(event.getUser(), event.getGroup(), StatusType.USING)
            .map(Participant::getNickname);
    }

    private int countByDay(List<Event> events, int day, PaymentType type) {
        return (int) events.stream()
            .filter(e -> e.getPaymentType().equals(type) && e.getGroundsDate().getDayOfMonth() == day)
            .count();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new HashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private Event getActiveEvent(long id) {
        return eventRepository.findByIdAndStatusType(id, StatusType.USING)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_EVENT));
    }
}
