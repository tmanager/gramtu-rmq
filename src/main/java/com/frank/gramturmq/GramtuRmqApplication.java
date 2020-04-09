package com.frank.gramturmq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableRabbit
@SpringBootApplication
public class GramtuRmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(GramtuRmqApplication.class, args);
    }

}
