package com.sosim.server.event.dto.info;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientEventStatusUpdateInfo {
    private List<Long> eventIdList;
    private String situation;
}
