package net.dengzixu.qwpush.propterties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wechat")
public record WeChatProperty(String corpId,
                             String corpSecret,
                             String agentId) {
}
