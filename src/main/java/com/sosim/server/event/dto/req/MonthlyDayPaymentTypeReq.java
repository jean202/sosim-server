package com.sosim.server.event.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MonthlyDayPaymentTypeReq {

    private Integer year;
    private Integer month;
}
