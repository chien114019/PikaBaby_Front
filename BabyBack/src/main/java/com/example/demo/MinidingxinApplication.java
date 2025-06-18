package com.example.demo;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MinidingxinApplication {

	public static void main(String[] args) {
		// 設定 JVM 時區為台灣
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
		SpringApplication.run(MinidingxinApplication.class, args);
	}

}
