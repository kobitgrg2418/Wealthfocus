package com.wealthfocus.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Money {

    private static final DecimalFormat FMT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        FMT = new DecimalFormat("#,##0.00", symbols);
    }

    public static String formatNPR(BigDecimal amount) {
        if (amount == null) return "रू 0.00";
        return "रू " + FMT.format(amount.abs());
    }

    public static String formatNPR(double amount) {
        return formatNPR(BigDecimal.valueOf(amount));
    }

    public static String formatSigned(BigDecimal amount) {
        if (amount == null) return "रू 0.00";
        String sign = amount.signum() < 0 ? "-" : amount.signum() > 0 ? "+" : "";
        return sign + formatNPR(amount);
    }

    private Money() {}
}
