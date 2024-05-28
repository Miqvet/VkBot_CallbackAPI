package com.example.vkbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents the event received from the VK Long Poll server.
 * This class is used to deserialize the JSON response from the VK API.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkEvent {

    /**
     * The timestamp of the event.
     */
    @JsonProperty("ts")
    private int ts;

    /**
     * List of updates received in the event.
     */
    @JsonProperty("updates")
    private List<VkMessage> updates;
}