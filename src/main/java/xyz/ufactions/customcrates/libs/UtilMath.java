package xyz.ufactions.customcrates.libs;

import java.util.Random;

public class UtilMath {

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int roundInventorySize(int num) {
        return (num / 9 + ((num % 9 == 0) ? 0 : 1)) * 9;
    }

    public static Random random = new Random();

    public static int r(int i) {
        return random.nextInt(i);
    }
}