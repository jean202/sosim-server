package com.sosim.server.event.dto.req;

import com.sosim.server.event.DateFilterType;
import com.sosim.server.event.DateRange;
import com.sosim.server.type.PaymentType;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private String startDate;
    private String endDate;

    @NotNull
    private Integer page;

    public DateRange toDateRange() {
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            return new DateRange(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }
        if (dateFilterType == null) return null;
        return dateFilterType.resolve(year, month, week, day);
    }

    public PaymentType toPaymentType() {
        if (paymentType == null) return null;
        return PaymentType.getType(paymentType);
    }
}
