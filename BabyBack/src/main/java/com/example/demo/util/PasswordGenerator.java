//package com.example.demo.util;
//加密密碼產生器
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PasswordGenerator implements CommandLineRunner {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        String raw = "1234"; //輸入想要加密的密碼
//        String encoded = passwordEncoder.encode(raw);
//        System.out.println("✅ 加密後的密碼：" + encoded);
//    }
//}
