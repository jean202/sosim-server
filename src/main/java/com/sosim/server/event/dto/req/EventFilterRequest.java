package com.sosim.server.event.dto.req;

import com.sosim.server.event.DateFilterType;
import com.sosim.server.event.DateRange;
import com.sosim.server.type.PaymentType;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterRequest {

    private DateFilterType dateFilterType;

    private Integer year;
    private Integer month;
    private Integer week;
    private Integer day;

    private String nickname;

    private String paymentType;

    @NotNull
    private Integer page;

    public DateRange toDateRange() {
        if (dateFilterType == null) return null;
        return dateFilterType.resolve(year, month, week, day);
    }

    public PaymentType toPaymentType() {
        if (paymentType == null) return null;
        return PaymentType.getType(paymentType);
    }
}