package com.sosim.server.event.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class EventCreateReq {

    @NotNull
    private long groupId;

    @NotEmpty
    @JsonAlias("nickname")
    private String userName;

    @NotEmpty
    @JsonAlias("date")
    private String groundsDate;

    @NotNull
    @JsonAlias("amount")
    private Long payment;

    @Size(max = 65)
    @JsonAlias("ground")
    private String grounds;

    @NotNull
    @JsonAlias("situation")
    private String paymentType;
}
