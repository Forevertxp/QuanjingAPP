package com.quanjing.weitu.app.common;

public class MWTUtils {
    private static long lastClickTime;

    public static boolean isValidCellphoneNumber(String cellphone) {
        return cellphone.matches("^(1[34578][0-9])\\d{8}$");
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
