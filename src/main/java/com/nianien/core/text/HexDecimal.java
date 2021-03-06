package com.nianien.core.text;

import com.nianien.core.exception.ExceptionHandler;

/**
 * 支持进十进制与N进制相互转换的工具类
 *
 * @author skyfalling
 */
public class HexDecimal {

    /**
     * 默认字符集合[0-9A-Za-z]
     */
    public final static char[] defaultCharset = Characters.NumberAndLetter;

    /**
     * 将十进制的数字转换为base进制<br>
     * 这里采用[0-9A-Za-z]字符集合,最大表示可表示62进制
     *
     * @param num  十进制数
     * @param base 进制基数
     * @return N进制数字字符串
     */
    public static String decToHex(long num, int base) {
        ExceptionHandler.throwIf(base > 62, "the base of hex must be no more than 62.");
        if (num == 0)
            return "0";
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.insert(0, defaultCharset[(int) (num % base)]);
            num /= base;
        }
        return sb.toString();
    }

    /**
     * 将十进制的数字转换为N进制,其中N为字符集的长度
     *
     * @param num     十进制数
     * @param charset N进制对应的字符集
     * @return N进制数字字符串
     */
    public static String decToHex(long num, char[] charset) {
        ExceptionHandler.throwIf(charset.length < 2, "the number of different characters must be more than 2.");
        if (num == 0)
            return "0";
        int N = charset.length;
        StringBuilder sb = new StringBuilder();
        while (num != 0) {
            sb.insert(0, charset[(int) (num % N)]);
            num /= N;
        }
        return sb.toString();
    }

    /**
     * 将N进制数转换为十进制<br>
     * 这里采用[0-9A-Za-z]字符集合,最大表示可表示62进制
     *
     * @param source N进制字符串
     * @param base   进制基数
     * @return 转换后的十进制数
     */
    public static long hexToDec(String source, int base) {
        ExceptionHandler.throwIf(base > 62, "the base of hex must be no more than 62.");
        if (source == "0")
            return 0;
        int len = source.length();
        long sum = 0;
        for (int i = 0; i < len; i++) {
            sum = sum * base;
            int n = getHexValue(source.charAt(i), base);
            if (n != 0) {
                sum += n;
            }
        }
        return sum;

    }

    /**
     * 将N进制数转换为十进制
     *
     * @param source
     * @param charset
     * @return 转换后的十进制数
     */
    public static long hexToDec(String source, char[] charset) {
        ExceptionHandler.throwIf(charset.length < 2, "the number of different characters must be more than 2.");
        if (source == "0")
            return 0;
        int len = source.length();
        int N = charset.length;
        long sum = 0;
        for (int i = 0; i < len; i++) {
            sum = sum * N;
            int n = getHexValue(source.charAt(i), charset);
            if (n != 0) {
                sum += n;
            }
        }
        return sum;

    }

    /**
     * 获取base进制字符ch在默认字符集[0-9A-Za-z]中对应的十进制数
     *
     * @param ch   字符集中的字符
     * @param base 进制基数
     * @return
     */
    private static int getHexValue(char ch, int base) {
        char[] charset = defaultCharset;
        int n = -1;
        int len = charset.length < base ? charset.length : base;
        for (int i = 0; i < len; i++) {
            if (ch == charset[i]) {
                n = i;
                break;
            }
        }
        ExceptionHandler.throwIf(n == -1, "no responding value for char[" + ch + "]");
        return n;
    }

    /**
     * 获取字符ch在给定字符集charset中对应的十进制数值
     *
     * @param ch
     * @param charset
     * @return
     */
    private static int getHexValue(char ch, char[] charset) {
        int n = -1;
        int len = charset.length;
        for (int i = 0; i < len; i++) {
            if (ch == charset[i]) {
                n = i;
                break;
            }
        }
        ExceptionHandler.throwIf(n == -1, "no responding value for char[" + ch + "]");
        return n;
    }
}
