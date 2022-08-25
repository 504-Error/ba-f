package com.error504.baf;

import java.util.Calendar;

public class SecureFiltering {
        public static String XssCheck(String info) { //보안 4.1.4
                info = info.replaceAll("&", "&amp");
                info = info.replaceAll("<", "&lt;");
                info = info.replaceAll(">", "&gt;");
//                info = info.replaceAll("￦"", "&quot;");
//                info = info.replaceAll("'", "&x27;");
//                info = info.replaceAll("/"", "&#x2F;");
//                info = info.replaceAll("(", "&#28;");
//                info = info.replaceAll(")", "&#X29;");
        return info;
    }
}
