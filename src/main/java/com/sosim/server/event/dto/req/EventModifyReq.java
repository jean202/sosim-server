package com.sosim.server.event.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sosim.server.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EventModifyReq {

    @JsonAlias("nickname")
    private String userName;

    @Setter
    private User user;

    @JsonAlias("date")
    private String groundsDate;

    @JsonAlias("amount")
    private Long payment;

    @JsonAlias("ground")
    private String grounds;

    @JsonAlias("situation")
    private String paymentType;

    private String paymentTypeChangeYn;
}
