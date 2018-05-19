package de.unitrier.st.util;

public class MathUtils {
    private static final double EPSILON = 0.00001;  // for comparison of doubles

    public static String replaceStringAt(String str, int beginIndex, int endIndex, String replacement) {
        // beginIndex inclusive, endIndex exclusive
        return str.substring(0, beginIndex) + replacement + str.substring(endIndex, str.length());
    }

    public static boolean equals(double value1, double value2) {
        // see http://www.cygnus-software.com/papers/comparingfloats/Comparing%20floating%20point%20numbers.htm
        return equals(value1, value2, EPSILON);
    }

    private static boolean equals(double value1, double value2, double epsilon) {
        return Math.abs(value1 - value2) < epsilon;
    }

    public static boolean lessThan(double value1, double value2) {
        return lessThan(value1, value2, EPSILON);
    }

    private static boolean lessThan(double value1, double value2, double epsilon) {
        return (value2 - value1) - epsilon > 0;
    }

    public static boolean greaterThan(double value1, double value2) {
        return greaterThan(value1, value2, EPSILON);
    }

    private static boolean greaterThan(double value1, double value2, double epsilon) {
        return (value2 - value1) + epsilon < 0;
    }
}
