package xyz.ufactions.customcrates.libs;

import lombok.experimental.UtilityClass;

import java.text.NumberFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;

@UtilityClass
public class UtilMath {

    public OptionalDouble getDouble(String string) {
        if (string == null || string.isEmpty()) return OptionalDouble.empty();
        try {
            return OptionalDouble.of(Double.parseDouble(string));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    public OptionalInt getInteger(String string) {
        if (string == null || string.isEmpty()) return OptionalInt.empty();
        try {
            return OptionalInt.of(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public String formatNumber(long number) {
        return NumberFormat.getInstance().format(number);
    }

    public String formatNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

    public boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int roundInventorySize(int num) {
        return (num / 9 + ((num % 9 == 0) ? 0 : 1)) * 9;
    }

    public Random random = new Random();

    public int r(int i) {
        return random.nextInt(i);
    }
}