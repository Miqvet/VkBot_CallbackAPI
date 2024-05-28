package com.example.vkbot.service;

import com.example.vkbot.model.VkEvent;
import com.example.vkbot.model.VkMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Service for handling VK Long Polling.
 * This service initializes the Long Poll server connection and continuously polls for new updates.
 */
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

    /**
     * Starts the Long Poll service.
     * Initializes the Long Poll server connection and starts polling.
     */
    public void start() {
        initialize();
        poll();
    }

    /**
     * Initializes the Long Poll server connection.
     * Retrieves the Long Poll server URL, key, and initial timestamp.
     */
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

    /**
     * Continuously polls the Long Poll server for new updates.
     * Parses each event and handles messages.
     */
    private void poll() {
        while (true) {
            String url = String.format(
                    "%s?act=a_check&key=%s&ts=%d&wait=25",
                    server, key, ts
            );
            String jsonResponse = restTemplate.getForObject(url, String.class);
            VkEvent event = parseEvent(jsonResponse);
            if (event != null) {
                this.ts = event.getTs();
                event.getUpdates().forEach(this::handleMessage);
            }
        }
    }

    /**
     * Parses the JSON response into a VkEvent object.
     *
     * @param jsonResponse the JSON response from the Long Poll server.
     * @return the parsed VkEvent object.
     */
    private VkEvent parseEvent(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonResponse, VkEvent.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Handles a new message event.
     *
     * @param vkMessage the VK message event.
     */
    private void handleMessage(VkMessage vkMessage) {
        if ("message_new".equals(vkMessage.getType())) {
            VkMessage.MessageObject messageObject = vkMessage.getObject();
            VkMessage.MessageObject.Message message = messageObject.getMessage();
            String responseMessage = "Вы сказали: " + message.getText();
            sendMessage(message.getPeerId(), responseMessage);
        }
    }

    /**
     * Sends a response message to the specified peer.
     *
     * @param peerId  the peer ID to send the message to.
     * @param message the message text to send.
     */
    private void sendMessage(int peerId, String message) {
        String url = String.format(
                "https://api.vk.com/method/messages.send?peer_id=%d&message=%s&access_token=%s&v=%s&random_id=%d",
                peerId, message, accessToken, apiVersion, System.currentTimeMillis()
        );
        restTemplate.getForObject(url, String.class);
    }

    /**
     * Represents the response from the VK Long Poll server initialization request.
     */
    @Data
    private static class VkLongPollServerResponse {
        private LongPollServer response;

        /**
         * Represents the Long Poll server details.
         */
        @Data
        public static class LongPollServer {
            private String key;
            private String server;
            private String ts;
        }
    }
}