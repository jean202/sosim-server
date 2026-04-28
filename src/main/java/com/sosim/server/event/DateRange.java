package com.sosim.server.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DateRange {
    private final LocalDateTime start;
    private final LocalDateTime end;
}