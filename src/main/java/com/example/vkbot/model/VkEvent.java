package com.example.vkbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkEvent {
    @JsonProperty("ts")
    private int ts;
    @JsonProperty("updates")
    private List<VkMessage> updates;
}