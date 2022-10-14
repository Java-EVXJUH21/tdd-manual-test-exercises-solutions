package me.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        {
            Cooldowns.time = new RealTime();
            Cooldowns.setCooldown("test", 1000);
            var result = Cooldowns.tryCooldown("test", 5000);

            runTestBoolean(false, result);
        }

        {
            Cooldowns.time = new RealTime();
            Cooldowns.setCooldown("testa", 0);
            var result = Cooldowns.tryCooldown("testb", 5000);

            runTestBoolean(false, result);
        }

        {
            Cooldowns.time = new RealTime();
            var result = Cooldowns.tryCooldown("testb", 5000);

            runTestBoolean(false, result);
        }

        {
            Cooldowns.time = new TestTime(0);
            Cooldowns.setCooldown("test", 0);
            Cooldowns.tryCooldown("test", 5000);
            var result = Cooldowns.getCooldown("test");

            runTestLong(5000, result);
        }

        {
            Cooldowns.time = new TestTime(0);
            Cooldowns.setCooldown("test", 5000);
            Cooldowns.time = new TestTime(6000);
            var result = Cooldowns.tryCooldown("test", 5000);

            runTestBoolean(true, result);
        }
    }

    public static int testCount = 0;
    public static void runTestBoolean(boolean expected, boolean actual) {
        testCount++;
        if (expected != actual) {
            System.out.println("Expected " + expected + " but got " + actual);
            throw new RuntimeException("Test #" + testCount + " failed.");
        } else {
            System.out.println("Test #" + testCount + " passed.");
        }
    }

    public static void runTestLong(long expected, long actual) {
        testCount++;
        if (expected != actual) {
            System.out.println("Expected " + expected + " but got " + actual);
            throw new RuntimeException("Test #" + testCount + " failed.");
        } else {
            System.out.println("Test #" + testCount + " passed.");
        }
    }

    interface Time {
        long getTime();
    }

    static class RealTime implements Time {
        @Override
        public long getTime() {
            return System.currentTimeMillis();
        }
    }

    static class TestTime implements Time {
        private long time;
        public TestTime(long time) {
            this.time = time;
        }

        @Override
        public long getTime() {
            return time;
        }
    }

    /**
     * Utility class to handle cooldowns. Assign an id to a cooldown to reference it.
     */
    static class Cooldowns {

        static Time time;
        static Map<String, Long> cooldowns = new HashMap<>();

        /**
         * Calculates remaining time for a cooldown
         *
         * @param id id of the cooldown
         * @return time in milliseconds remaining on the cooldown
         */
        static long getCooldown(String id) {
            return calculateRemainder(cooldowns.get(id));
        }

        /**
         * Adds or overwrites a cooldown.
         *
         * @param id what to identify the cooldown as
         * @param delay how long the cooldown is in milliseconds
         * @return how long the previous cooldown was if it was overwritten
         */
        static long setCooldown(String id, long delay) {
            return calculateRemainder(cooldowns.put(id, time.getTime() + delay));
        }

        /**
         * Checks whether a cooldown has expired or not
         *
         * @param id the id of the cooldown
         * @param delay what to update the cooldown to if the time is up
         * @return whether the cooldown was expired or not
         */
        static boolean tryCooldown(String id, long delay) {
            if (!cooldowns.containsKey(id)) return false;

            if (getCooldown(id) <= 0) {
                setCooldown(id, delay);
                return true;
            }

            return false;
        }

        /**
         * Removes a cooldown
         *
         * @param id the id of the cooldown that should be removed
         */
        static void removeCooldowns(String id) {
            cooldowns.remove(id);
        }

        /**
         * Calculates time until expiry
         *
         * @param expireTime the complete time when a cooldown should expire
         * @return the difference between currently and the expiry time, or in other words how long until it expires
         */
        static long calculateRemainder(Long expireTime) {
            return expireTime != null ? expireTime - time.getTime() : Long.MIN_VALUE;
        }
    }

    /*public static void main(String[] args) {
        {
            var location = new Location();
            location.x = 1.0;
            location.y = 2.0;
            location.z = 3.0;
            location.pitch = 4.0f;
            location.yaw = 5.0f;

            var result = serialize(location);
            runTest("x:1.0,y:2.0,z:3.0,p:4.0,ya:5.0", result);
        }

        {
            var location = new Location();
            location.x = -1.0;
            location.y = 21.23;
            location.z = 30.0;
            location.pitch = 400.0f;
            location.yaw = 5.56f;

            var result = serialize(location);
            runTest("x:-1.0,y:21.23,z:30.0,p:400.0,ya:5.56", result);
        }

        {
            var location = new Location();
            location.x = -1.0;
            location.y = 10.0 / 3.0;
            location.z = 30.0;
            location.pitch = 400.0f;
            location.yaw = 5.56f;

            var result = serialize(location);
            runTest("x:-1.0,y:" + (10.0 / 3.0) + ",z:30.0,p:400.0,ya:5.56", result);
        }

        {
            var s = "x:-1.0,y:21.23,z:30.0,p:400.0,ya:5.56";
            var expected = new Location();
            expected.x = -1.0;
            expected.y = 21.23;
            expected.z = 30.0;
            expected.pitch = 400.0f;
            expected.yaw = 5.56f;

            var result = deserialize(s);
            runTest(expected, result);
        }

        {
            var s = "x:-1.0,y:21.23,z:-30.0,p:48800.0,ya:0.005";
            var expected = new Location();
            expected.x = -1.0;
            expected.y = 21.23;
            expected.z = -30.0;
            expected.pitch = 48800.0f;
            expected.yaw = 0.005f;

            var result = deserialize(s);
            runTest(expected, result);
        }

        for (int i = 0; i < 100; i++) {
            var expected = new Location();
            expected.x = Math.random() * 10;
            expected.y = Math.random() * 10;
            expected.z = Math.random() * 10;
            expected.pitch = (float) (Math.random() * 5.0);
            expected.yaw = (float) (Math.random() * 5.0);

            var s = serialize(expected);
            var result = deserialize(s);
            runTest(expected, result);
        }
    }

    public static int testCount =  0;
    public static void runTest(Object expected, Object actual) {
        testCount++;
        if (!expected.equals(actual)) {
            System.out.println("Expected " + expected + " but got " + actual);
            throw new RuntimeException("Test #" + testCount + " failed.");
        } else {
            System.out.println("Test #" + testCount + " passed.");
        }
    }

    static class Location {
        public double x, y, z;
        public float pitch, yaw;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Location)) return false;

            var l = (Location) obj;
            return l.x == this.x && l.y == this.y && l.z == this.z && l.pitch == this.pitch && l.yaw == this.yaw;
        }
    }

    static String serialize(Location location) {
        var builder = new StringBuilder();
        builder.append("x:");
        builder.append(location.x);
        builder.append(",");
        builder.append("y:");
        builder.append(location.y);
        builder.append(",");
        builder.append("z:");
        builder.append(location.z);
        builder.append(",");
        builder.append("p:");
        builder.append(location.pitch);
        builder.append(",");
        builder.append("ya:");
        builder.append(location.yaw);
        return builder.toString();
    }

    static Location deserialize(String content) {
        var parts = content.split(",");
        var sx = parts[0].split("x:")[1];
        var sy = parts[1].split("y:")[1];
        var sz = parts[2].split("z:")[1];
        var spitch = parts[3].split("p:")[1];
        var syaw = parts[4].split("ya:")[1];

        var location = new Location();
        location.x = Double.parseDouble(sx);
        location.y = Double.parseDouble(sy);
        location.z = Double.parseDouble(sz);
        location.pitch = Float.parseFloat(spitch);
        location.yaw = Float.parseFloat(syaw);

        return location;
    }*/

    /*static class A {
        public String a, b;
        public A(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    static class B {
        public int a, b;
        public B(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof B)) return false;
            var b = (B) obj;

            return b.a == this.a && b.b == this.b;
        }
    }

    public static void main(String[] args) {
        {
            var input1 = new Integer[]{4, 5, 6};
            var list1 = mapArray(input1, value -> value * 2);
            var expected = List.of(8, 10, 12);

            runTest(expected, list1);
        }

        {
            var input1 = new Integer[]{ 4, 5, 6};
            var list1 = mapArray(input1, value -> -value);
            var expected = List.of(-4, -5, -6);

            runTest(expected, list1);
        }

        {
            var input1 = new Integer[]{ 4, 5, 6};
            var list1 = mapArray(input1, value -> value + "");
            var expected = List.of("4", "5", "6");

            runTest(expected, list1);
        }

        {
            var input1 = new A[]{ new A("56", "89"), new A("67", "98"), new A("4", "7") };
            var list1 = mapArray(input1, value -> new B(Integer.parseInt(value.a), Integer.parseInt(value.b)));
            var expected = List.of(new B(56, 89), new B(67, 98), new B(4, 7));

            runTest(expected, list1);
        }
    }

    public static int testCount =  0;
    public static <T> void runTest(List<T> expected, List<T> actual) {
        testCount++;
        if (!expected.equals(actual)) {
            System.out.println("Expected " + expected + " but got " + actual);
            throw new RuntimeException("Test #" + testCount + " failed.");
        } else {
            System.out.println("Test #" + testCount + " passed.");
        }
    }*/

    interface Mapper<T, O> {
        T map(O value);
    }

    static <T, O> List<T> mapArray(O[] array, Mapper<T, O> mapper) {
        var list = new ArrayList<T>();
        for (var value : array) {
            list.add(mapper.map(value));
        }

        return list;
    }

    /*public static void main(String[] args) {
        {
            int[] input1 = new int[]{1, 2, 3};
            int[] input2 = new int[]{4, 5, 6};
            int[] input3 = new int[]{7, 8, 9};

            int[] expected = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
            int[] result = concatenateIntArrays(input1, input2, input3);

            runTest(expected, result);
        }

        {
            int[] input1 = new int[] { -1, 2, -32 };
            int[] input2 = new int[] { 4, 56, -6 };

            int[] expected = new int[] { -1, 2, -32, 4, 56, -6 };
            int[] result = concatenateIntArrays(input1, input2);

            runTest(expected, result);
        }

        {
            int[] input1 = new int[] { -1 };
            int[] input2 = new int[] { 4 };
            int[] input3 = new int[] { 6 };
            int[] input4 = new int[] { 49 };
            int[] input5 = new int[] { 0 };
            int[] input6 = new int[] { -56 };

            int[] expected = new int[] { -1, 4, 6, 49, 0, -56 };
            int[] result = concatenateIntArrays(input1, input2, input3, input4, input5, input6);

            runTest(expected, result);
        }

        {
            int[] input1 = new int[] { -1 };
            int[] input2 = new int[] { };
            int[] input3 = new int[] { 6 };

            int[] expected = new int[] { -1, 6 };
            int[] result = concatenateIntArrays(input1, input2, input3);

            runTest(expected, result);
        }
    }

    public static int testCount =  0;
    public static void runTest(int[] expected, int[] actual) {
        testCount++;
        if (!arrayEquals(expected, actual)) {
            System.out.println("Expected " + expected + " but got " + actual);
            throw new RuntimeException("Test #" + testCount + " failed.");
        } else {
            System.out.println("Test #" + testCount + " passed.");
        }
    }

    public static boolean arrayEquals(int[] a, int[] b) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }

        return true;
    }*/

    /**
     * Concatenates multiple int arrays into a single array and returns it
     *
     * @param arrays the arrays to concatenate
     * @return all arrays concatenated into a single array
     */
    static int[] concatenateIntArrays(int []... arrays) {
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }
        int[] result = new int[totalLength];
        int startingPos = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, startingPos, array.length);
            startingPos += array.length;
        }
        return result;
    }
}
