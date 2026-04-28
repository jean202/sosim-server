package com.sosim.server.event.dto.info;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class DayInfo {

    @Getter
    private Integer day;

    private Map<String, Integer> paymentTypeCountMap;
}
