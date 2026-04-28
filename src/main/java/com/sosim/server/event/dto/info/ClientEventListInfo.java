package com.sosim.server.event.dto.info;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientEventListInfo {
    private Long totalCount;
    private List<ClientEventInfo> eventList;
}
