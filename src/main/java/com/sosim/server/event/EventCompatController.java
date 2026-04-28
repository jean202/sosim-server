package com.sosim.server.event;

import com.sosim.server.common.response.Response;
import com.sosim.server.config.exception.CustomException;
import com.sosim.server.event.dto.info.ClientEventInfo;
import com.sosim.server.event.dto.info.ClientEventListInfo;
import com.sosim.server.event.dto.info.ListInfo;
import com.sosim.server.event.dto.req.EventFilterRequest;
import com.sosim.server.participant.Participant;
import com.sosim.server.participant.ParticipantRepository;
import com.sosim.server.type.CodeType;
import com.sosim.server.type.StatusType;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventCompatController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    @GetMapping("/penalties")
    public ResponseEntity<?> getPenalties(@RequestParam("groupId") long groupId,
                                          @RequestParam(value = "situation", required = false) String situation,
                                          @ModelAttribute EventFilterRequest request) {
        if (request.getPage() == null) {
            request.setPage(0);
        }
        request.setPaymentType(situation);

        ListInfo<com.sosim.server.event.dto.info.EventListInfo> eventList = eventService.getEventList(groupId, normalizeSituation(request));
        ClientEventListInfo content = new ClientEventListInfo(
            eventList.getTotalCount(),
            eventList.getList().stream()
                .map(info -> ClientEventInfo.builder()
                    .eventId(info.getEventId())
                    .nickname(info.getUserName())
                    .date(info.getGroundsDate().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                    .amount(info.getPayment())
                    .ground(info.getGrounds())
                    .memo("")
                    .situation(ClientEventInfo.toSituation(com.sosim.server.type.PaymentType.getType(info.getPaymentType())))
                    .build())
                .collect(Collectors.toList())
        );

        return new ResponseEntity<>(Response.create(CodeType.EVENT_LIST_SUCCESS, content), CodeType.EVENT_LIST_SUCCESS.getHttpStatus());
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEventsByIds(@RequestParam(value = "groupId", required = false) Long groupId,
                                            @RequestParam("eventIdList") List<Long> eventIdList) {
        if (eventIdList == null || eventIdList.isEmpty()) {
            throw new CustomException(CodeType.BINDING_ERROR);
        }

        Map<Long, Event> eventById = eventRepository.findAllById(eventIdList).stream()
            .filter(event -> event.getStatusType() == StatusType.USING)
            .filter(event -> groupId == null || event.getGroup().getId().equals(groupId))
            .collect(Collectors.toMap(Event::getId, Function.identity()));

        List<ClientEventInfo> eventList = eventIdList.stream()
            .map(eventById::get)
            .filter(java.util.Objects::nonNull)
            .map(event -> ClientEventInfo.from(event, resolveNickname(event)))
            .collect(Collectors.toList());

        return new ResponseEntity<>(
            Response.create(CodeType.EVENT_LIST_SUCCESS, new ClientEventListInfo((long) eventList.size(), eventList)),
            CodeType.EVENT_LIST_SUCCESS.getHttpStatus()
        );
    }

    private EventFilterRequest normalizeSituation(EventFilterRequest request) {
        return request;
    }

    private String resolveNickname(Event event) {
        Participant activeParticipant = participantRepository
            .findByUserAndGroupAndStatus(event.getUser(), event.getGroup(), StatusType.USING)
            .orElse(null);
        if (activeParticipant != null) {
            return activeParticipant.getNickname();
        }
        return "";
    }
}
