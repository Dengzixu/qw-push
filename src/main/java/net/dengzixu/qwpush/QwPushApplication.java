package net.dengzixu.qwpush;

import net.dengzixu.qwpush.propterties.QWPushProperty;
import net.dengzixu.qwpush.propterties.WeChatProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
@EnableConfigurationProperties({QWPushProperty.class, WeChatProperty.class})
public class QwPushApplication implements ApplicationRunner {
    // Logger
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(QwPushApplication.class);

    private final QWPushProperty qwPushProperty;
    private final WeChatProperty weChatProperty;
    private final ApplicationContext context;

    @Autowired
    public QwPushApplication(QWPushProperty qwPushProperty, WeChatProperty weChatProperty, ApplicationContext context) {
        this.qwPushProperty = qwPushProperty;
        this.weChatProperty = weChatProperty;
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(QwPushApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (StringUtils.isBlank(weChatProperty.corpId())) {
            logger.error("[CORP_ID] 缺少配置，请设置后再试。");
        }
        if (StringUtils.isBlank(weChatProperty.corpSecret())) {
            logger.error("[CORP_SECRET] 尚未设置，请设置后再试。");
        }
        if (StringUtils.isBlank(weChatProperty.agentId())) {
            logger.error("[AGENT_ID] 尚未设置，请设置后再试。");
        }

        if (StringUtils.isBlank(qwPushProperty.security().authKey())){
            logger.error("[AUTH_KEY] ，请设置后再试。");
            SpringApplication.exit(context);
        }
    }
}
