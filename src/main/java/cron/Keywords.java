/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Anders Wisch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cron;

import java.util.Arrays;

final class Keywords {
    private final int[][][] keywords = new int[26][26][26];

    public Keywords() {
        for (int[][] second : keywords)
            for (int[] third : second)
                Arrays.fill(third, -1);
    }

    public void put(String keyword, int value) {
        keywords[letterAt(keyword, 0)][letterAt(keyword, 1)][letterAt(keyword, 2)] = value;
    }

    public int get(String s, int start, int end) {
        if (end - start != 3)
            throw new IllegalArgumentException();
        int number = keywords[arrayIndex(s, start)][arrayIndex(s, start + 1)][arrayIndex(s, start + 2)];
        if (number >= 0)
            return number;
        throw new IllegalArgumentException();
    }

    private int arrayIndex(String s, int charIndex) {
        int index = letterAt(s, charIndex);
        if (index < 0 || index >= keywords.length)
            throw new IllegalArgumentException();
        return index;
    }

    private static int letterAt(String s, int charIndex) {
        return s.charAt(charIndex) - 'A';
    }
}