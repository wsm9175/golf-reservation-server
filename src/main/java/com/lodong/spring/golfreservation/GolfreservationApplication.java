package com.lodong.spring.golfreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class GolfreservationApplication {
	/*@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}*/
	public static void main(String[] args) {
		SpringApplication.run(GolfreservationApplication.class, args);
	}
}
