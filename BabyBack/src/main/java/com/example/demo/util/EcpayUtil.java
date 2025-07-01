package com.example.demo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class EcpayUtil {

//    private static final String HASH_KEY = "5294y06JbISpM5x9";
//    private static final String HASH_IV = "v77hoKGq4kWxNNIS";

    private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
    private static final String HASH_IV = "EkRm7iFT261dpevs";

    public static String generateCheckMacValue(Map<String, String> params) {
        try {
            SortedMap<String, String> sorted = new TreeMap<>(params);
            StringBuilder sb = new StringBuilder("HashKey=").append(HASH_KEY);
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            sb.append("&HashIV=").append(HASH_IV);

            String raw = sb.toString();
            System.out.println("🔧 原始字串：" + raw);

            String urlEncoded = urlEncode(raw).toLowerCase();
            System.out.println("🔧 URL Encode 後：" + urlEncoded);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02X", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue 錯誤", e);
        }
    }

    private static String urlEncode(String input) throws Exception {
    	return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
//        return URLEncoder.encode(input, StandardCharsets.UTF_8)
//                .replaceAll("\\+", "%20")
//                .replaceAll("%21", "!")
//                .replaceAll("%28", "(")
//                .replaceAll("%29", ")")
//                .replaceAll("%2A", "*")
//                .replaceAll("%7E", "~");
    }
}
