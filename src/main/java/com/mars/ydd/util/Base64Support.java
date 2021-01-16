package com.mars.ydd.util;

import java.io.IOException;

/**
 *
 * @author sheng
 *
 */
public class Base64Support {

    /**
     * 将base64种的敏感字符+,/,=转化为_,-,. 以及base64会在编码串中产生换行符，虽然解码会不管
     *
     * @author: weip
     * @time: 2007-8-29 下午11:51:15
     * @param bytes
     * @return
     */
    public static String toUrlStr(byte[] bytes) {

        String str = new sun.misc.BASE64Encoder().encode(bytes);

        if (str == null)
            return "";

        str = str.replaceAll("\\+", "_");
        str = str.replaceAll("/", "-");
        str = str.replaceAll("=", ".");
        str = str.replaceAll("\\s", "");

        return str;
    }

    /**
     * 是toUrlStr逆过程
     *
     * @author: weip
     * @time: 2007-8-30 上午01:15:07
     * @param str
     * @return
     * @throws IOException
     */
    public static byte[] fromUrlStr(String str) throws IOException {

        if (str == null)
            return null;

        str = str.replaceAll("_", "+");
        str = str.replaceAll("-", "/");
        str = str.replaceAll("\\.", "=");

        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

        return dec;
    }

}
