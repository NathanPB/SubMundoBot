package piva.sbb.bot.utils;

public class StringUtils {

    public static boolean isLongNumeric(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
