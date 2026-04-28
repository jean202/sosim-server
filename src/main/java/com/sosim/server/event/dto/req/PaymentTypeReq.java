package com.sosim.server.event.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTypeReq {

    @JsonAlias("situation")
    private String paymentType;
}
