package com.sosim.server.event.dto.info;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sosim.server.common.converter.LocalDateTimeToStringConverter;
import com.sosim.server.event.Event;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class EventListInfo {

    private Long userId;
    private Long eventId;

    @Setter
    private String userName;

    private String grounds;

    @JsonSerialize(converter = LocalDateTimeToStringConverter.class)
    private LocalDateTime groundsDate;

    private Long payment;

    private String paymentType;

    public static EventListInfo from(Event event) {
        return EventListInfo.builder()
            .eventId(event.getId())
            .userId(event.getUser().getId())
            .payment(event.getPayment())
            .groundsDate(event.getGroundsDate())
            .paymentType(event.getPaymentType().getParam())
            .grounds(event.getGrounds())
            .build();
    }
}
