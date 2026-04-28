package com.sosim.server.event.dto.info;

import com.sosim.server.event.Event;
import com.sosim.server.type.PaymentType;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientEventInfo {

    private Long eventId;
    private String nickname;
    private String date;
    private Long amount;
    private String ground;
    private String memo;
    private String situation;

    public static ClientEventInfo from(Event event, String nickname) {
        return ClientEventInfo.builder()
            .eventId(event.getId())
            .nickname(nickname)
            .date(event.getGroundsDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .amount(event.getPayment())
            .ground(event.getGrounds())
            .memo("")
            .situation(toSituation(event.getPaymentType()))
            .build();
    }

    public static String toSituation(PaymentType paymentType) {
        switch (paymentType) {
            case FULL_PAYMENT:
                return "완납";
            case CONFIRMING:
                return "확인중";
            case NON_PAYMENT:
            default:
                return "미납";
        }
    }
}
