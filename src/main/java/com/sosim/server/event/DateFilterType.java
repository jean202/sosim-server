package com.sosim.server.event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

public enum DateFilterType {

    MONTHLY {
        @Override
        public DateRange resolve(Integer year, Integer month, Integer week, Integer day) {
            YearMonth ym = YearMonth.of(year, month);
            return new DateRange(
                ym.atDay(1).atStartOfDay(),
                ym.atEndOfMonth().atTime(LocalTime.MAX)
            );
        }
    },

    WEEKLY {
        @Override
        public DateRange resolve(Integer year, Integer month, Integer week, Integer day) {
            YearMonth ym = YearMonth.of(year, month);
            int startDay = (week - 1) * 7 + 1;
            int endDay = Math.min(week * 7, ym.lengthOfMonth());
            return new DateRange(
                LocalDate.of(year, month, startDay).atStartOfDay(),
                LocalDate.of(year, month, endDay).atTime(LocalTime.MAX)
            );
        }
    },

    DAILY {
        @Override
        public DateRange resolve(Integer year, Integer month, Integer week, Integer day) {
            LocalDate date = LocalDate.of(year, month, day);
            return new DateRange(date.atStartOfDay(), date.atTime(LocalTime.MAX));
        }
    },

    TODAY {
        @Override
        public DateRange resolve(Integer year, Integer month, Integer week, Integer day) {
            LocalDate today = LocalDate.now();
            return new DateRange(today.atStartOfDay(), today.atTime(LocalTime.MAX));
        }
    };

    public abstract DateRange resolve(Integer year, Integer month, Integer week, Integer day);
}