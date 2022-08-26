package com.error504.baf;

import java.util.Calendar;

public class SecureFiltering {
    public static String XssCheck(String str) { //보안 4.1.4
        str = str.replaceAll("&", "&amp;")
                .replaceAll("#", "&#35;")
                .replaceAll(";", "&#59;")
                .replaceAll("\\\\", "&#92;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\\(", "&#40;")
                .replaceAll("\\)", "&#41;")
                .replaceAll("'", "&#39;")
                .replaceAll("\"", "&quot;")
                .replaceAll("[$]", "&#36;")
                .replaceAll("[*]", "&#42;")
                .replaceAll("[+]", "&#43;")
                .replaceAll("[|]", "&#124;")
                .replaceAll("\\.", "&#46;")
                .replaceAll("\\?", "&#63;")
                .replaceAll("\\[", "&#91;")
                .replaceAll("\\]", "&#93;")
                .replaceAll("\\^", "&#94;")
                .replaceAll("\\{", "&#123;")
                .replaceAll("\\}", "&#125;")
                .replaceAll("!", "&#33;")
                .replaceAll("%", "&#37;")
                .replaceAll(",", "&#44;")
                .replaceAll("-", "&#45;")
                .replaceAll("/", "&#47;")
                .replaceAll(":", "&#58;")
                .replaceAll("=", "&#61;")
                .replaceAll("@", "&#64;")
                .replaceAll("_", "&#95;")
                .replaceAll("`", "&#96;")
                .replaceAll("~", "&#126;");
        return str;
    }

    //보안 규칙 4.1.10 LDAP삽입
    public static String LDAPCheck(String keyword) {
        if (!keyword.matches("[\\w\\s]*")) {
            throw new IllegalArgumentException("Invalid input");
        }
        return keyword;
    }

    //보안 규칙 4.1.14 정수 오버플로우
    public static boolean intOverflowCheck(int value) throws Exception {

        if (value < 0) {
            return false;
        }
        return true;
    }

    //보안 규칙 4.1.14 long 오버플로우
    public static boolean longOverflowCheck(Long value) throws Exception {

        if (value < 0) {
            return false;
        }
        return true;
    }
}
