package com.clearlee.lockscreenmusiccontrol.util;

/**
 * Created by ZerdoorPHPDC on 2017/12/26 0026.
 */

public class Common {

    //获取秒为单位的时长转成以时分秒显示的字符串
    public static String getSecDuration2HMSFormatString(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = getDoubleDigitLeftFillZeroString(minute) + ":" + getDoubleDigitLeftFillZeroString(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                if (hour > 0) {
                    timeStr = getDoubleDigitLeftFillZeroString(hour) + ":" + getDoubleDigitLeftFillZeroString(minute) + ":" + getDoubleDigitLeftFillZeroString(second);
                } else {
                    timeStr = getDoubleDigitLeftFillZeroString(minute) + ":" + getDoubleDigitLeftFillZeroString(second);
                }
            }
        }
        return timeStr;
    }

    //获取两位数格式的数字，未满10左边补0后的字符串
    public static String getDoubleDigitLeftFillZeroString(int i) {
        if (i >= 0 && i < 10)
            return "0" + Integer.toString(i);
        else
            return "" + i;
    }

}
