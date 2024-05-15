//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.internal.system;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Misc {
    public static final String TAG = "Misc";

    public Misc() {
    }

    public static String formatInvariant(@NonNull String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }

    public static String formatInvariant(@NonNull String format) {
        return format;
    }

    public static String formatForUser(@NonNull String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public static String formatForUser(@NonNull String format) {
        return format;
    }

//    public static String formatForUser(@StringRes int resId, Object... args) {
//        return AppUtil.getDefContext().getString(resId, args);
//    }
//
//    public static String formatForUser(@StringRes int resId) {
//        return AppUtil.getDefContext().getString(resId);
//    }

    public static String encodeEntity(String string) {
        return encodeEntity(string, "");
    }

    public static String encodeEntity(String string, String rgchEscape) {
        StringBuilder builder = new StringBuilder();
        char[] var3 = string.toCharArray();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            switch(ch) {
                case '"':
                    builder.append("&quot;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '\'':
                    builder.append("&apos;");
                    break;
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                default:
                    if (rgchEscape.indexOf(ch) >= 0) {
                        builder.append(formatInvariant("&#x%x;", ch));
                    } else {
                        builder.append(ch);
                    }
            }
        }

        return builder.toString();
    }

    public static String decodeEntity(String string) {
        StringBuilder builder = new StringBuilder();

        for(int ich = 0; ich < string.length(); ++ich) {
            char ch = string.charAt(ich);
            if (ch == '&') {
                ++ich;

                int ichFirst;
                for(ichFirst = ich; string.charAt(ich) != ';'; ++ich) {
                }

                String payload = string.substring(ichFirst, ich - 1);
                byte var7 = -1;
                switch(payload.hashCode()) {
                    case 3309:
                        if (payload.equals("gt")) {
                            var7 = 2;
                        }
                        break;
                    case 3464:
                        if (payload.equals("lt")) {
                            var7 = 1;
                        }
                        break;
                    case 96708:
                        if (payload.equals("amp")) {
                            var7 = 0;
                        }
                        break;
                    case 3000915:
                        if (payload.equals("apos")) {
                            var7 = 4;
                        }
                        break;
                    case 3482377:
                        if (payload.equals("quot")) {
                            var7 = 3;
                        }
                }

                switch(var7) {
                    case 0:
                        builder.append('&');
                        break;
                    case 1:
                        builder.append('<');
                        break;
                    case 2:
                        builder.append('>');
                        break;
                    case 3:
                        builder.append('"');
                        break;
                    case 4:
                        builder.append('\'');
                        break;
                    default:
                        if (payload.length() <= 2 || payload.charAt(0) != '#' || payload.charAt(1) != 'x') {
                            throw illegalArgumentException("illegal entity reference");
                        }

                        payload = "0x" + payload.substring(2);
                        int i = Integer.decode(payload);
                        builder.append((char)i);
                }
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    public static long saturatingAdd(long x, long y) {
        if (x != 0L && y != 0L && !(x > 0L ^ y > 0L)) {
            if (x > 0L) {
                return 9223372036854775807L - x < y ? 9223372036854775807L : x + y;
            } else {
                return -9223372036854775808L - x > y ? -9223372036854775808L : x + y;
            }
        } else {
            return x + y;
        }
    }

    public static int saturatingAdd(int x, int y) {
        if (x != 0 && y != 0 && !(x > 0 ^ y > 0)) {
            if (x > 0) {
                return 2147483647 - x < y ? 2147483647 : x + y;
            } else {
                return -2147483648 - x > y ? -2147483648 : x + y;
            }
        } else {
            return x + y;
        }
    }

    public static boolean isEven(byte value) {
        return (value & 1) == 0;
    }

    public static boolean isEven(short value) {
        return (value & 1) == 0;
    }

    public static boolean isEven(int value) {
        return (value & 1) == 0;
    }

    public static boolean isEven(long value) {
        return (value & 1L) == 0L;
    }

    public static boolean isOdd(byte value) {
        return !isEven(value);
    }

    public static boolean isOdd(short value) {
        return !isEven(value);
    }

    public static boolean isOdd(int value) {
        return !isEven(value);
    }

    public static boolean isOdd(long value) {
        return !isEven(value);
    }

    public static boolean isFinite(double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }

    public static boolean approximatelyEquals(double a, double b) {
        return approximatelyEquals(a, b, 1.0E-9D);
    }

    public static boolean approximatelyEquals(double a, double b, double tolerance) {
        if (a == b) {
            return true;
        } else {
            double error = b == 0.0D ? Math.abs(a) : Math.abs(a / b - 1.0D);
            return error < tolerance;
        }
    }

    public static UUID uuidFromBytes(byte[] rgb, ByteOrder byteOrder) {
//        Assert.assertTrue(rgb.length == 16);
        ByteBuffer readBuffer = ByteBuffer.wrap(rgb).order(byteOrder);
        ByteBuffer writeBuffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        writeBuffer.putInt(readBuffer.getInt());
        writeBuffer.putShort(readBuffer.getShort());
        writeBuffer.putShort(readBuffer.getShort());
        writeBuffer.rewind();
        long mostSignificant = writeBuffer.getLong();
        writeBuffer.rewind();
        writeBuffer.put(readBuffer);
        writeBuffer.rewind();
        long leastSignificant = writeBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    public static boolean contains(byte[] array, byte value) {
        byte[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte i = var2[var4];
            if (i == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(short[] array, short value) {
        short[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            short i = var2[var4];
            if (i == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(int[] array, int value) {
        int[] var2 = array;
        int var3 = array.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int i = var2[var4];
            if (i == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(long[] array, long value) {
        long[] var3 = array;
        int var4 = array.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            long i = var3[var5];
            if (i == value) {
                return true;
            }
        }

        return false;
    }

    public static <T> T[] toArray(T[] contents, Collection<T> collection) {
        int s = collection.size();
        if (contents.length < s) {
            T[] newArray = (T[]) Array.newInstance(contents.getClass().getComponentType(), s);
            contents = newArray;
        }

        int i = 0;

        Object t;
        for(Iterator var4 = collection.iterator(); var4.hasNext(); contents[i++] = (T) t) {
            t = var4.next();
        }

        if (contents.length > s) {
            contents[s] = null;
        }

        return contents;
    }

    public static <T> T[] toArray(T[] contents, ArrayList<T> collection) {
        return collection.toArray(contents);
    }

    public static long[] toLongArray(Collection<Long> collection) {
        long[] result = new long[collection.size()];
        int i = 0;

        Long value;
        for(Iterator var3 = collection.iterator(); var3.hasNext(); result[i++] = value) {
            value = (Long)var3.next();
        }

        return result;
    }

    public static int[] toIntArray(Collection<Integer> collection) {
        int[] result = new int[collection.size()];
        int i = 0;

        Integer value;
        for(Iterator var3 = collection.iterator(); var3.hasNext(); result[i++] = value) {
            value = (Integer)var3.next();
        }

        return result;
    }

    public static short[] toShortArray(Collection<Short> collection) {
        short[] result = new short[collection.size()];
        int i = 0;

        Short value;
        for(Iterator var3 = collection.iterator(); var3.hasNext(); result[i++] = value) {
            value = (Short)var3.next();
        }

        return result;
    }

    public static byte[] toByteArray(Collection<Byte> collection) {
        byte[] result = new byte[collection.size()];
        int i = 0;

        Byte value;
        for(Iterator var3 = collection.iterator(); var3.hasNext(); result[i++] = value) {
            value = (Byte)var3.next();
        }

        return result;
    }

    public static <E> Set<E> intersect(Set<E> left, Set<E> right) {
        Set<E> result = new HashSet();
        Iterator var3 = left.iterator();

        while(var3.hasNext()) {
            E element = (E) var3.next();
            if (right.contains(element)) {
                result.add(element);
            }
        }

        return result;
    }

    public static IllegalArgumentException illegalArgumentException(String message) {
        return new IllegalArgumentException(message);
    }

    public static IllegalArgumentException illegalArgumentException(String format, Object... args) {
        return new IllegalArgumentException(formatInvariant(format, args));
    }

    public static IllegalArgumentException illegalArgumentException(Throwable throwable, String format, Object... args) {
        return new IllegalArgumentException(formatInvariant(format, args), throwable);
    }

    public static IllegalArgumentException illegalArgumentException(Throwable throwable, String message) {
        return new IllegalArgumentException(message, throwable);
    }

    public static IllegalStateException illegalStateException(String message) {
        return new IllegalStateException(message);
    }

    public static IllegalStateException illegalStateException(String format, Object... args) {
        return new IllegalStateException(formatInvariant(format, args));
    }

    public static IllegalStateException illegalStateException(Throwable throwable, String format, Object... args) {
        return new IllegalStateException(formatInvariant(format, args), throwable);
    }

    public static IllegalStateException illegalStateException(Throwable throwable, String message) {
        return new IllegalStateException(message, throwable);
    }

    public static RuntimeException internalError(String message) {
        return new RuntimeException("internal error:" + message);
    }

    public static RuntimeException internalError(String format, Object... args) {
        return new RuntimeException("internal error:" + formatInvariant(format, args));
    }

    public static RuntimeException internalError(Throwable throwable, String format, Object... args) {
        return new RuntimeException("internal error:" + formatInvariant(format, args), throwable);
    }

    public static RuntimeException internalError(Throwable throwable, String message) {
        return new RuntimeException("internal error:" + message, throwable);
    }
}
