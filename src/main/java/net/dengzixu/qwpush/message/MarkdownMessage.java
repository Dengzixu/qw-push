package net.dengzixu.qwpush.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record MarkdownMessage(@JsonProperty("touser")
                              String touser,
                              @JsonProperty("msgtype")
                              String msgtype,
                              @JsonProperty("agentid")
                              long agentid,
                              @JsonProperty("markdown")
                              Content content) implements IMessage {


    private record Content(@JsonProperty("content")
                           String content) {

    }


    public MarkdownMessage(long agentid, String content) {
        this("@all", "markdown", agentid, new Content(content));
    }

    @Override
    public String asJsonText() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
