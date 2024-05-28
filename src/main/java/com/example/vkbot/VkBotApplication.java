package com.example.vkbot;

import com.example.vkbot.service.LongPollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VkBotApplication implements CommandLineRunner {

    @Autowired
    private LongPollService longPollService;

    public static void main(String[] args) {
        SpringApplication.run(VkBotApplication.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {
        longPollService.start();
    }
}

