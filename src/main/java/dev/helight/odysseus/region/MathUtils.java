package dev.helight.odysseus.region;

import org.bukkit.Location;

import java.util.List;
import java.util.regex.Pattern;

public class MathUtils {

    public static boolean contains(List<Location> polygon, Location location) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            if ((polygon.get(i).getZ() > location.getZ()) != (polygon.get(j).getZ() > location.getZ()) &&
                    (location.getX() < (polygon.get(j).getX() - polygon.get(i).getX()) * (location.getZ() - polygon.get(i).getZ()) / (polygon.get(j).getZ()-polygon.get(i).getZ()) + polygon.get(i).getX())) {
                result = !result;
            }
        }
        return result;
    }

    public static boolean containsBox(Location a, Location b, Location location) {
        return (location.getX() >= a.getX() && location.getX() <= b.getX()) &&
                (location.getY() >= a.getY() && location.getY() <= b.getY()) &&
                (location.getZ() >= a.getZ() && location.getZ() <= b.getZ());
    }

    public static String toDoubleString(double number, int decimal) {
        double multiply = Math.pow(10, decimal);
        double rounded = Math.round(number * multiply) / multiply;
        String s = Double.toString(number);
        String[] strings = s.split(Pattern.quote("."));
        StringBuilder dec = new StringBuilder(strings[1]);
        while (dec.length() < decimal) {
            dec.append("0");
        }
        return strings[0] + "." + dec.toString();
    }

}
