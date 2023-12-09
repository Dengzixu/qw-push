package net.dengzixu.qwpush.mvc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dengzixu.qwpush.constant.Constant;
import net.dengzixu.qwpush.message.IMessage;
import net.dengzixu.qwpush.message.MarkdownMessage;
import net.dengzixu.qwpush.message.TextMessage;
import net.dengzixu.qwpush.mvc.service.PushService;
import net.dengzixu.qwpush.propterties.WeChatProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Service
public class PushServiceImpl implements PushService {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(PushServiceImpl.class);

    // Cache Token Key
    private static final String CACHE_TOKEN_KEY = "TOKEN_KEY";

    // Cache Max Size
    private static final long CACHE_MAX_SIZE = 10_000;

    private static final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(CACHE_MAX_SIZE)
            .build();

    private final WeChatProperty weChatProperty;

    @Autowired
    public PushServiceImpl(WeChatProperty weChatProperty) {
        this.weChatProperty = weChatProperty;
    }

    @Override
    public void push(String messageType, String messageContent) {
        // 创建 HttpClient
        HttpClient httpClient = HttpClient.newBuilder()
                .build();

        String token = tokenCache.getIfPresent(CACHE_TOKEN_KEY);

        // 判断 Token 是否存在
        if (null == token) {
            logger.warn("Access Token 不存在或过期，重新获取");

            try {
                // URI
                final URI tokenURI = new URI(String.format(Constant.WX_GET_TOKEN_URL, weChatProperty.corpId(), weChatProperty.corpSecret()));

                HttpRequest tokenRequest = HttpRequest.newBuilder(tokenURI)
                        .GET()
                        .build();

                HttpResponse<String> tokenResponse = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

                JsonNode tokenJsonNode = new ObjectMapper().readValue(tokenResponse.body(), JsonNode.class);

                if (tokenJsonNode.get("errcode").asInt() != 0) {
                    logger.error("Access Token 获取错误, 错误消息:{}", tokenJsonNode.get("errmsg").asText());
                    throw new RuntimeException("Access Token API 请求错误");
                }

                final String getToken = tokenJsonNode.get("access_token").asText();

                token = tokenCache.get(CACHE_TOKEN_KEY, k -> getToken);

                logger.info("Access Token 获取成功");
            } catch (URISyntaxException e) {
                logger.error("URI 语法错误", e);
            } catch (IOException | InterruptedException e) {
                logger.error("Access Token 请求错误", e);
            }
        }


        // 判断消息类型
        IMessage message = switch (messageType) {
            case "text" -> new TextMessage(Long.parseLong(weChatProperty.agentId()), messageContent);
            case "markdown" -> new MarkdownMessage(Long.parseLong(weChatProperty.agentId()), messageContent);
            default -> throw new IllegalStateException("未知的消息类型: " + messageType);
        };

        try {
            final URI sendMessageURI = new URI(String.format(Constant.WX_SEND_MESSAGE_URL, token));

            HttpRequest tokenRequest = HttpRequest.newBuilder(sendMessageURI)
                    .POST(HttpRequest.BodyPublishers.ofString(message.asJsonText()))
                    .build();

            HttpResponse<String> response = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode responseJsonNode = new ObjectMapper().readValue(response.body(), JsonNode.class);

        } catch (URISyntaxException e) {
            logger.error("URI 语法错误", e);
        } catch (IOException | InterruptedException e) {
            logger.error("Access Token 请求错误", e);
        }

    }
}
