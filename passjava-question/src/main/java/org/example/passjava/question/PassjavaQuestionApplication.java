package org.example.passjava.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PassjavaQuestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PassjavaQuestionApplication.class, args);
	}

}
