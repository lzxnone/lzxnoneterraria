package com.lzxnone.terraria.item.effect;

import java.util.Locale;

public class EffectTooltipUtil {
    //获取增加/减少百分比
    public static String formatPercent(double multiplier) {
        double percent;
        if(multiplier > 1.0) {
            percent = Math.max(0.0D, (multiplier - 1.0D) * 100.0D);
        }else {
            percent = Math.max(0.0D, (1.0D - multiplier) * 100.0D);
        }
        if(Math.abs(percent - Math.rint(percent)) < 1.0E-6D) return Integer.toString((int)Math.rint(percent));
        String formatted = String.format(Locale.ROOT, "%.2f", percent);
        while(formatted.endsWith("0")) formatted = formatted.substring(0, formatted.length() - 1);
        if(formatted.endsWith(".")) formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }

    //获取数值显示（整数不带小数，小数去尾零）
    public static String formatNumber(double value) {
        if(Math.abs(value - Math.rint(value)) < 1.0E-6D) return Integer.toString((int)Math.rint(value));
        String formatted = String.format(Locale.ROOT, "%.2f", value);
        while(formatted.endsWith("0")) formatted = formatted.substring(0, formatted.length() - 1);
        if(formatted.endsWith(".")) formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }
}
