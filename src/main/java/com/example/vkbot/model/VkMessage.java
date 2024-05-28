package com.example.vkbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a VK message update.
 * This class is used to deserialize the JSON object of a message update from the VK API.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkMessage {

    /**
     * The type of the update, e.g., "message_new".
     */
    @JsonProperty("type")
    private String type;

    /**
     * The object containing the message details.
     */
    @JsonProperty("object")
    private MessageObject object;

    /**
     * Represents the object containing the message details.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageObject {

        /**
         * The actual message data.
         */
        @JsonProperty("message")
        private Message message;

        /**
         * Represents the actual message data.
         */
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {

            /**
             * The unique identifier of the message.
             */
            @JsonProperty("id")
            private int id;

            /**
             * The identifier of the user who sent the message.
             */
            @JsonProperty("from_id")
            private int fromId;

            /**
             * The peer identifier where the message was sent.
             */
            @JsonProperty("peer_id")
            private int peerId;

            /**
             * The text content of the message.
             */
            @JsonProperty("text")
            private String text;
        }
    }
}
