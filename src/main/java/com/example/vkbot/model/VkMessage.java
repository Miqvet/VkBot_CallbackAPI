package com.example.vkbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkMessage {
    @JsonProperty("type")
    private String type;
    @JsonProperty("object")
    private MessageObject object;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageObject {
        @JsonProperty("message")
        private Message message;
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {
            @JsonProperty("id")
            private int id;
            @JsonProperty("from_id")
            private int fromId;
            @JsonProperty("peer_id")
            private int peerId;
            @JsonProperty("text")
            private String text;
        }
    }
}
