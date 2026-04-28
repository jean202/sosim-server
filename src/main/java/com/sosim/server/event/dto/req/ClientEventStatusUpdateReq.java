package com.sosim.server.event.dto.req;

import java.util.List;
import lombok.Getter;

@Getter
public class ClientEventStatusUpdateReq {
    private List<Long> eventIdList;
    private String situation;
}
