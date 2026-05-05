package com.wealthfocus.util;

import java.time.LocalDate;
import java.time.YearMonth;

public class TimeRangeUtil {

    public static class Range {
        public final LocalDate start;
        public final LocalDate end;
        public final String label;
        public final String preset;

        public Range(LocalDate start, LocalDate end, String label, String preset) {
            this.start = start;
            this.end = end;
            this.label = label;
            this.preset = preset;
        }

        public LocalDate getStart() { return start; }
        public LocalDate getEnd() { return end; }
        public String getLabel() { return label; }
        public String getPreset() { return preset; }
    }

    public static Range get(String preset) {
        if (preset == null) preset = "current-month";
        LocalDate today = LocalDate.now();
        switch (preset) {
            case "last-month": {
                YearMonth lm = YearMonth.from(today).minusMonths(1);
                return new Range(lm.atDay(1), lm.atEndOfMonth(), "Last Month", preset);
            }
            case "last-3-months":
                return new Range(today.minusMonths(3), today, "Last 3 Months", preset);
            case "last-6-months":
                return new Range(today.minusMonths(6), today, "Last 6 Months", preset);
            case "current-year":
                return new Range(LocalDate.of(today.getYear(), 1, 1), today, "Current Year", preset);
            case "current-month":
            default: {
                YearMonth ym = YearMonth.from(today);
                return new Range(ym.atDay(1), ym.atEndOfMonth(), "Current Month", "current-month");
            }
        }
    }

    private TimeRangeUtil() {}
}
