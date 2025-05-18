package org.example;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class tool {

    public static void main(String[] args) {
        String url = "https://www.cashbackforex.com/chart?s=XAU.USD";
        System.out.println(encodeUrl( url));
          url="https://www.baidu.com/s?ie=utf-8&wd=人民币美元汇率k线";

        System.out.println(encodeUrl( url));
        url="https://coinmarketcap.com/currencies/bitcoin/";

        System.out.println(encodeUrl( url));
    }

    private static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 是标准编码，基本不会发生异常
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }

    }
}
