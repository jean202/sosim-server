package com.sosim.server.event;

import com.sosim.server.common.response.Response;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.event.dto.info.DayInfo;
import com.sosim.server.event.dto.info.ClientEventInfo;
import com.sosim.server.event.dto.info.ClientEventStatusUpdateInfo;
import com.sosim.server.event.dto.info.ClientMonthStatusInfo;
import com.sosim.server.event.dto.info.EventInfo;
import com.sosim.server.event.dto.info.EventListInfo;
import com.sosim.server.event.dto.info.ListInfo;
import com.sosim.server.event.dto.req.ClientEventStatusUpdateReq;
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
import com.sosim.server.type.StatusType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/event/penalty")
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final ParticipantRepository participantRepository;

    @PostMapping
    public ResponseEntity<?> createEvent(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody EventCreateReq eventCreateReq) {
        Long eventId = this.eventService.createEvent(authUser, eventCreateReq);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_CREATE_SUCCESS, eventId), CodeType.EVENT_CREATE_SUCCESS.getHttpStatus());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable("eventId") long id) {
        Event event = getActiveEvent(id);
        ClientEventInfo clientEventInfo = ClientEventInfo.from(event, resolveNickname(event));
        return new ResponseEntity<>(Response.create(CodeType.EVENT_INFO_SUCCESS, clientEventInfo), CodeType.EVENT_INFO_SUCCESS.getHttpStatus());
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@AuthenticationPrincipal AuthUser authUser, @PathVariable("eventId") long id, @RequestBody EventModifyReq eventModifyReq) {
        EventInfo eventInfo = this.eventService.updateEvent(authUser, id, eventModifyReq);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_UPDATE_SUCCESS, eventInfo), CodeType.EVENT_UPDATE_SUCCESS.getHttpStatus());
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@AuthenticationPrincipal AuthUser authUser, @PathVariable("eventId") long id) {
        this.eventService.deleteEvent(authUser, id);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_DELETE_SUCCESS, null), CodeType.EVENT_DELETE_SUCCESS.getHttpStatus());
    }

    @PatchMapping("/status/{eventId}")
    public ResponseEntity<?> changePaymentType(@AuthenticationPrincipal AuthUser authUser, @PathVariable("eventId") long id, @RequestBody PaymentTypeReq paymentTypeReq) {
        EventInfo eventInfo = this.eventService.changePaymentType(authUser, id, paymentTypeReq);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_PAYMENT_TYPE_CHANGE_SUCCESS, eventInfo), CodeType.EVENT_PAYMENT_TYPE_CHANGE_SUCCESS.getHttpStatus());
    }

    @PatchMapping
    public ResponseEntity<?> changePaymentTypes(@AuthenticationPrincipal AuthUser authUser, @RequestBody ClientEventStatusUpdateReq request) {
        if (request.getEventIdList() == null || request.getEventIdList().isEmpty()) {
            throw new CustomException(CodeType.BINDING_ERROR);
        }

        PaymentTypeReq paymentTypeReq = new PaymentTypeReq();
        paymentTypeReq.setPaymentType(request.getSituation());

        for (Long eventId : request.getEventIdList()) {
            this.eventService.changePaymentType(authUser, eventId, paymentTypeReq);
        }

        ClientEventStatusUpdateInfo response = new ClientEventStatusUpdateInfo(request.getEventIdList(), ClientEventInfo.toSituation(com.sosim.server.type.PaymentType.getType(request.getSituation())));
        return new ResponseEntity<>(Response.create(CodeType.EVENT_PAYMENT_TYPE_CHANGE_SUCCESS, response), CodeType.EVENT_PAYMENT_TYPE_CHANGE_SUCCESS.getHttpStatus());
    }

    @GetMapping("/list/{groupId}")
    public ResponseEntity<?> getEventList(@PathVariable("groupId") long groupId, @Valid @ModelAttribute EventFilterRequest eventFilterRequest) {
        ListInfo<EventListInfo> eventList = this.eventService.getEventList(groupId, eventFilterRequest);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_LIST_SUCCESS, eventList), CodeType.EVENT_LIST_SUCCESS.getHttpStatus());
    }

    @GetMapping("/mstatus/{groupId}")
    public ResponseEntity<?> getMstatus(@PathVariable("groupId") long groupId, @Valid @ModelAttribute MonthlyDayPaymentTypeReq mdpTreq) {
        List<DayInfo> monthlyDayList = this.eventService.getMonthlyDayPaymentType(groupId, mdpTreq);
        return new ResponseEntity<>(Response.create(CodeType.EVENT_MONTH_STATUS_SUCCESS, monthlyDayList), CodeType.EVENT_MONTH_STATUS_SUCCESS.getHttpStatus());
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarStatus(@RequestParam("groupId") long groupId,
                                               @RequestParam("startDate") String startDate,
                                               @RequestParam("endDate") String endDate) {
        Group group = groupRepository.findByIdAndStatusType(groupId, StatusType.ACTIVE)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_GROUP));

        EventFilterRequest request = EventFilterRequest.builder()
            .page(0)
            .startDate(startDate)
            .endDate(endDate)
            .build();

        Map<Integer, Map<String, Integer>> statusOfDay = new HashMap<>();
        for (Event event : eventRepository.findByGroupAndStatusTypeAndGroundsDateBetween(
            group, StatusType.ACTIVE, request.toDateRange().getStart(), request.toDateRange().getEnd())) {
            int day = event.getGroundsDate().getDayOfMonth();
            Map<String, Integer> counts = statusOfDay.computeIfAbsent(day, ignored -> createEmptyStatusMap());
            String situation = ClientEventInfo.toSituation(event.getPaymentType());
            counts.put(situation, counts.get(situation) + 1);
        }

        return new ResponseEntity<>(
            Response.create(CodeType.EVENT_MONTH_STATUS_SUCCESS, new ClientMonthStatusInfo(statusOfDay)),
            CodeType.EVENT_MONTH_STATUS_SUCCESS.getHttpStatus()
        );
    }

    private Event getActiveEvent(long id) {
        return eventRepository.findByIdAndStatusType(id, StatusType.ACTIVE)
            .orElseThrow(() -> new CustomException(CodeType.NOT_FOUND_EVENT));
    }

    private String resolveNickname(Event event) {
        Participant activeParticipant = participantRepository
            .findByUserAndGroupAndStatusType(event.getUser(), event.getGroup(), StatusType.ACTIVE)
            .orElse(null);
        if (activeParticipant != null) {
            return activeParticipant.getNickname();
        }

        for (Participant participant : participantRepository.findListByUserAndGroupAndStatusType(event.getUser(), event.getGroup(), StatusType.DELETED)) {
            if (participant.getCreateDate().isBefore(event.getCreateDate())
                && participant.getDeleteDate() != null
                && participant.getDeleteDate().isAfter(event.getCreateDate())) {
                return participant.getNickname();
            }
        }

        return "";
    }

    private Map<String, Integer> createEmptyStatusMap() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("미납", 0);
        counts.put("완납", 0);
        counts.put("확인중", 0);
        return counts;
    }
}
