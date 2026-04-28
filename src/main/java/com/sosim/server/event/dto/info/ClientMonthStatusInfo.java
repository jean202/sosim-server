package com.sosim.server.event.dto.info;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientMonthStatusInfo {
    private Map<Integer, Map<String, Integer>> statusOfDay;
}
