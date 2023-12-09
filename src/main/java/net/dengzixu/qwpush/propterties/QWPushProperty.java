package net.dengzixu.qwpush.propterties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("qw-push")
public record QWPushProperty(Security security) {

    public record Security(String authKey) {

    }
}
