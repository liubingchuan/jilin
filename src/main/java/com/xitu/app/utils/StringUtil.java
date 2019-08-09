package com.xitu.app.utils;

import java.util.*;

public class StringUtil {

    /**
     * 判断字符是否为空串, 为null或者长度为0 则为空串
     *
     * @param s String
     * @return boolean boolean
     */
    public static boolean isNull(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 将字符串按指定分隔符分隔成数组 使用StringTokenizer工具类
     *
     * @param s     String
     * @param delim String 分隔符, 例: (,; \r\n)
     * @return String[] string [ ]
     * @see StringTokenizer;
     */
    public static String[] toArray(String s, String delim) {
        return toArray(s, delim, false);
    }

    /**
     * 将字符串按指定分隔符分隔成数组 使用StringTokenizer工具类
     *
     * @param s            String
     * @param delim        String
     * @param returnDelims boolean
     * @return String[] string [ ]
     * @see StringTokenizer;
     */
    public static String[] toArray(String s, String delim, boolean returnDelims) {
        StringTokenizer st = new StringTokenizer(s, delim, returnDelims);
        int length = st.countTokens();
        String[] array = new String[length];
        int i = 0;
        while (st.hasMoreTokens()) {
            array[i++] = st.nextToken();
        }
        return array;
    }

    /**
     * 将字符串按 width 切割
     *
     * @param s         String
     * @param len       int
     * @param htmltitle boolean
     * @return string
     */
    public static String toFactor(String s, int len, boolean htmltitle) {
        int slen = s.length();
        String ss = slen > len && len != 0 ? s.substring(0, len - 1) + "..." : s;
        if (htmltitle) {
            ss = "<div style=\"white-space:nowrap;\" title=\"" + s + "\">" + ss + "</div>";
        }
        return ss;
    }

    /**
     * 随机数
     */
    private static final Random random = new Random();

    /**
     * 将字符串按格式生成 <br>
     * %n:number<br>
     * <br>
     * %y:year<br>
     * %M:month<br>
     * %d:day<br>
     * <br>
     * %h:hour<br>
     * %m:minute<br>
     * %s:second<br>
     * %w:week<br>
     * <br>
     * %ri:rondom.int<br>
     * %rl:rondom.long<br>
     * %rf:rondom.float<br>
     * %rd:rondom.double<br>
     *
     * @param s String<br>
     * @param n Number
     * @return String string
     */
    public static String toFactor(String s, Number n) {
        Calendar calendar = Calendar.getInstance();
        if (s.contains("%n"))
            s = s.replace("%n", String.valueOf(n));
        if (s.contains("%y"))
            s = s.replace("%y", String.valueOf(calendar.get(Calendar.YEAR)));
        if (s.contains("%M"))
            s = s.replace("%M", String.valueOf(calendar.get(Calendar.MONTH)));
        if (s.contains("%d"))
            s = s.replace("%d", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        if (s.contains("%h"))
            s = s.replace("%h", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        if (s.contains("%m"))
            s = s.replace("%m", String.valueOf(calendar.get(Calendar.MINUTE)));
        if (s.contains("%s"))
            s = s.replace("%s", String.valueOf(calendar.get(Calendar.SECOND)));
        if (s.contains("%w"))
            s = s.replace("%w", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        if (s.contains("%ri"))
            s = s.replace("%ri", String.valueOf((random.nextInt())));
        if (s.contains("%rl"))
            s = s.replace("%rl", String.valueOf((random.nextLong())));
        if (s.contains("%rf"))
            s = s.replace("%rf", String.valueOf((random.nextFloat())));
        if (s.contains("%rd"))
            s = s.replace("%rd", String.valueOf((random.nextDouble())));
        return s;
    }

    /**
     * 替换 <br>
     *
     * @param s the s
     * @return string
     */
    public static String toEncodeHTML(String s) {
        if (s.indexOf('&') > -1)
            s = s.replace("&", "&amp;");
        if (s.indexOf('<') > -1)
            s = s.replace("<", "&lt;");
        if (s.indexOf('>') > -1)
            s = s.replace("<", "&gt;");
        if (s.contains("'"))
            s = s.replace("'", "&apos;");
        if (s.indexOf('"') > -1)
            s = s.replace("\"", "&quot;");

        return s;
    }

    /**
     * 替换 <br>
     *
     * @param s the s
     * @return string
     */
    public static String toDecodeHTML(String s) {
        if (s.contains("&amp;"))
            s = s.replace("&amp;", "&");
        if (s.contains("&lt;"))
            s = s.replace("&lt;", "<");
        if (s.contains("&gt;"))
            s = s.replace("&gt;", "<");
        if (s.contains("&apos;"))
            s = s.replace("&apos;", "'");
        if (s.contains("&quot;"))
            s = s.replace("&quot;", "\"");
        return s;
    }

    /**
     * 字符串首写字母大写
     *
     * @param s String
     * @return String string
     */
    public static String toUpperCaseFirstChar(String s) {
        char[] cc = s.toCharArray();
        cc[0] = s.toUpperCase().charAt(0);
        return String.valueOf(cc);
    }

    /**
     * 字符串首写字母小写
     *
     * @param s String
     * @return String string
     */
    public static String toLowerCaseFirstChar(String s) {
        char[] cc = s.toCharArray();
        cc[0] = s.toLowerCase().charAt(0);
        return String.valueOf(cc);
    }

    /**
     * 表名转换对象名
     *
     * @param tableName String
     * @param prefix    String[] 表名前辍
     * @return String string
     */
    public static String tableName2objectName(String tableName, String[] prefix) {
        return toObjectName(toWipePrefix(tableName, prefix));
    }

    /**
     * 剃除字符串的前缀. 如：wa_dictionary -> dictionary
     *
     * @param s      String 为要操作的字符串
     * @param prefix String[] 前缀集.
     * @return String string
     */
    public static String toWipePrefix(String s, String[] prefix) {
        if (prefix == null || prefix.length == 0) {
            return s;
        }
        String wipeStr = s;
        for (String pre : prefix) {
            int index = -1;
            if ((index = s.toLowerCase().indexOf(pre.toLowerCase())) != -1) {
                int startIndex = index + pre.length();
                int endIndex = s.length();
                wipeStr = s.substring(startIndex, endIndex);
            }
        }
        return wipeStr;

    }

    /**
     * 将字符中大写字母前加入"_",转为ID命名 例: UserPasswd -> user_passwd
     *
     * @param objectName the object name
     * @return String string
     */
    public static String toObjectId(String objectName) {
        StringBuffer buff = new StringBuffer();
        char[] cs = objectName.toCharArray();
        buff.append(cs[0]);
        for (int i = 1; i < cs.length; i++) {
            buff.append(Character.isUpperCase(cs[i]) ? "_" : "");
            buff.append(cs[i]);
        }
        return buff.toString().toLowerCase();
    }

    /**
     * 将字符中"_"后的第一个字母转为大写,符合对象类命名规则. 例: user_passwd -> UserPasswd
     *
     * @param objectId the object id
     * @return String string
     */
    public static String toObjectName(String objectId) {
        String objectName = objectId;
        if (!objectId.contains("_")) {
            char[] cs = objectId.toLowerCase().toCharArray();
            cs[0] = Character.toUpperCase(cs[0]);
            objectName = String.valueOf(cs);

        } else {
            StringBuilder buff = new StringBuilder();
            char[] cs = objectId.toLowerCase().toCharArray();
            cs[0] = Character.toUpperCase(cs[0]);
            for (int i = 0; i < cs.length; i++) {
                if ('_' == cs[i]) {
                    i++;
                    cs[i] = Character.toUpperCase(cs[i]);
                }
                buff.append(cs[i]);
            }
            objectName = buff.toString();
        }
        return objectName;
    }

    /**
     * 查找，按正则pattern查询
     *
     * @param source  the source
     * @param pattern the pattern
     * @return String[] string [ ]
     */
    public static String[] scan(String source, String pattern) {
        Scanner scanner = new Scanner(source);
        ArrayList<String> ss = new ArrayList<String>();
        String s;
        while (null != (s = scanner.findInLine(pattern))) {
            ss.add(s);
        }
        return (String[]) ss.toArray();
    }

    /**
     * The constant HEX.
     */
    public static final String HEX = "0123456789ABCDEF";

    /**
     * byte[]转换成十六进制字符串
     *
     * @param bytes byte[]
     * @return String string
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        char[] hexs = HEX.toCharArray();

        for (int i = 0; i < bytes.length; i++) {
            int h = (bytes[i] & 0x0f0) >> 4; // 相当除以16
            int l = (bytes[i] & 0x0f);
            buff.append(hexs[h]).append(hexs[l]);
        }
        return buff.toString();
    }

    /**
     * 十六进制字符串转换成byte[]
     *
     * @param hex String
     * @return byte[] byte [ ]
     */
    public static byte[] hex2bytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        char[] hexs = hex.toCharArray();

        for (int i = 0; i < bytes.length; i++) {
            int h = HEX.indexOf(hexs[2 * i]) * 16;
            int l = HEX.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) ((h + l) & 0xff);
        }
        return bytes;
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str String
     * @return String string
     */
    public static String str2hex(String str) {
        return bytes2hex(str.getBytes());
    }

    /**
     * 十六进制转换字符串
     *
     * @param hex String
     * @return String string
     */
    public static String hex2str(String hex) {
        return new String(hex2bytes(hex));
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param str the str
     * @return the string
     */
    public static String str2unicode(String str) {
        StringBuffer buff = new StringBuffer();
        char[] chars = str.toCharArray();

        for (char c : chars) {
            int ascii = (int) c; // ascii
            String hex = Integer.toHexString(ascii);

            if (ascii > 128) {
                buff.append("\\u").append(hex);
            } else {
                buff.append("\\u00").append(hex); // 低位在前面补00
            }
        }
        return buff.toString();
    }

    /**
     * 从参数字符串中按名取值，如：a=123&b=456&c=789&d=qwer 若取c值，返回789
     *
     * @param param the param
     * @param name  the name
     * @return String value in param
     */
    public static String getValueInParam(String param, String name) {
        String[] ss = param.split("&");
        for (String s : ss) {
            if (s.indexOf(name + "=") == 0) {
                return s.substring(s.indexOf('=') + 1);
            }
        }
        return null;
    }

    /**
     * Gets first.
     *
     * @param name   the name
     * @param string the string
     * @return the first
     */
    public static String getFirst(String name, String string) {
        String fileName = null;
        String[] split = name.split(string);
        if (split.length > 0) {
            fileName = split[0];
        }
        return fileName;
    }

}
