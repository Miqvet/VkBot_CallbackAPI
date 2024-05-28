package com.example.vkbot.service;

import com.example.vkbot.model.VkEvent;
import com.example.vkbot.model.VkMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class LongPollService {

    @Value("${vk.group.id}")
    private String groupId;

    @Value("${vk.access.token}")
    private String accessToken;

    @Value("${vk.api.version}")
    private String apiVersion;

    private final RestTemplate restTemplate = new RestTemplate();
    private String server;
    private String key;
    private int ts;

    public void start() {
        initialize();
        poll();
    }

    private void initialize() {
        String url = String.format(
                "https://api.vk.com/method/groups.getLongPollServer?group_id=%s&access_token=%s&v=%s",
                groupId, accessToken, apiVersion
        );
        var response = restTemplate.getForObject(url, VkLongPollServerResponse.class);
        if (response != null && response.getResponse() != null) {
            this.server = response.getResponse().getServer();
            this.key = response.getResponse().getKey();
            this.ts = Integer.parseInt(response.getResponse().getTs());
        }
    }

    private void poll() {
        while (true) {
            String url = String.format(
                    "%s?act=a_check&key=%s&ts=%d&wait=25",
                    server, key, ts
            );
            String jsonResponse = restTemplate.getForObject(url, String.class);
//            System.out.println(jsonResponse); // Вывод полного JSON ответа

            VkEvent event = parseEvent(jsonResponse);
            if (event != null) {
                this.ts = event.getTs();
                event.getUpdates().forEach(this::handleMessage);
            }
        }
    }

    private VkEvent parseEvent(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonResponse, VkEvent.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleMessage(VkMessage vkMessage) {
        if ("message_new".equals(vkMessage.getType())) {
            VkMessage.MessageObject messageObject = vkMessage.getObject();
            VkMessage.MessageObject.Message message = messageObject.getMessage();
            String responseMessage = "Вы сказали: " + message.getText();
//            System.out.println("Text: " + message.getText());
//            System.out.println("Peer ID: " + message.getPeerId());
//            System.out.println("From ID: " + message.getFromId());
//            System.out.println("Message ID: " + message.getId());
            sendMessage(message.getPeerId(), responseMessage);
        }
    }

    private void sendMessage(int peerId, String message) {
        String url = String.format(
                "https://api.vk.com/method/messages.send?peer_id=%d&message=%s&access_token=%s&v=%s&random_id=%d",
                peerId, message, accessToken, apiVersion, System.currentTimeMillis()
        );
        restTemplate.getForObject(url, String.class);
    }

    private static class VkLongPollServerResponse {
        private LongPollServer response;

        public LongPollServer getResponse() {
            return response;
        }

        public void setResponse(LongPollServer response) {
            this.response = response;
        }

        public static class LongPollServer {
            private String key;
            private String server;
            private String ts;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getServer() {
                return server;
            }

            public void setServer(String server) {
                this.server = server;
            }

            public String getTs() {
                return ts;
            }

            public void setTs(String ts) {
                this.ts = ts;
            }
        }
    }
}