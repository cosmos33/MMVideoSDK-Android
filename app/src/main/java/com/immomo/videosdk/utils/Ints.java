package com.immomo.videosdk.utils;

/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.Arrays;

/**
 * Static utility methods pertaining to {@code int} primitives, that are not already found in either
 * {@link Integer} or {@link Arrays}.
 * <p>
 * <p>See the Guava User Guide article on
 * <a href="https://github.com/google/guava/wiki/PrimitivesExplained">primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Ints {
    private Ints() {
    }

    /**
     * Compares the two specified {@code int} values. The sign of the value returned is the same as
     * that of {@code ((Integer) a).compareTo(b)}.
     * <p>
     * <p><b>Note for Java 7 and later:</b> this method should be treated as deprecated; use the
     * equivalent {@link Integer#compare} method instead.
     *
     * @param a the first {@code int} to compare
     * @param b the second {@code int} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     * greater than {@code b}; or zero if they are equal
     */
    public static int compare(int a, int b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    }

    /**
     * Returns {@code true} if {@code target} is present as an element anywhere in {@code array}.
     *
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return {@code true} if {@code array[i] == target} for some value of {@code
     * i}
     */
    public static boolean contains(int[] array, int target) {
        for (int value : array) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index of the first appearance of the value {@code target} in {@code array}.
     *
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return the least index {@code i} for which {@code array[i] == target}, or {@code -1} if no
     * such index exists.
     */
    public static int indexOf(int[] array, int target) {
        return indexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int indexOf(int[] array, int target, int start, int end) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the start position of the first occurrence of the specified {@code
     * target} within {@code array}, or {@code -1} if there is no such occurrence.
     * <p>
     * <p>More formally, returns the lowest index {@code i} such that
     * {@code Arrays.copyOfRange(array, i, i + target.length)} contains exactly the same elements as
     * {@code target}.
     *
     * @param array  the array to search for the sequence {@code target}
     * @param target the array to search for as a sub-sequence of {@code array}
     */
    public static int indexOf(int[] array, int[] target) {
        if (target.length == 0) {
            return 0;
        }

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last appearance of the value {@code target} in {@code array}.
     *
     * @param array  an array of {@code int} values, possibly empty
     * @param target a primitive {@code int} value
     * @return the greatest index {@code i} for which {@code array[i] == target}, or {@code -1} if no
     * such index exists.
     */
    public static int lastIndexOf(int[] array, int target) {
        return lastIndexOf(array, target, 0, array.length);
    }

    // TODO(kevinb): consider making this public
    private static int lastIndexOf(int[] array, int target, int start, int end) {
        for (int i = end - 1; i >= start; i--) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the least value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code int} values
     * @return the value present in {@code array} that is less than or equal to every other value in
     * the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static int min(int... array) {
        int min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * Returns the greatest value present in {@code array}.
     *
     * @param array a <i>nonempty</i> array of {@code int} values
     * @return the value present in {@code array} that is greater than or equal to every other value
     * in the array
     * @throws IllegalArgumentException if {@code array} is empty
     */
    public static int max(int... array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * Returns a string containing the supplied {@code int} values separated by {@code separator}. For
     * example, {@code join("-", 1, 2, 3)} returns the string {@code "1-2-3"}.
     *
     * @param separator the text that should appear between consecutive values in the resulting string
     *                  (but not at the start or end)
     * @param array     an array of {@code int} values, possibly empty
     */
    public static String join(String separator, int... array) {
        if (array.length == 0) {
            return "";
        }

        // For pre-sizing a builder, just get the right order of magnitude
        StringBuilder builder = new StringBuilder(array.length * 5);
        builder.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            builder.append(separator).append(array[i]);
        }
        return builder.toString();
    }

    public static int minIgnore(int[] array, int ignore) {
        int min = Integer.MAX_VALUE;
        for (int value : array) {
            if (value == ignore) continue;
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public static int maxIgnore(int[] array, int ignore) {
        int max = Integer.MIN_VALUE;
        for (int value : array) {
            if (value == ignore) continue;
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
