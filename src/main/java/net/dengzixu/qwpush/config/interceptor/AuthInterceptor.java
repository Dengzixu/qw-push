package net.dengzixu.qwpush.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.dengzixu.qwpush.propterties.QWPushProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final QWPushProperty qwPushProperty;

    @Autowired
    public AuthInterceptor(QWPushProperty qwPushProperty) {
        this.qwPushProperty = qwPushProperty;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Pattern pattern = Pattern.compile("^/push/(\\w*?)/");

        Matcher result = pattern.matcher(request.getRequestURI());

        if (result.find()) {
            String urlKey = result.group(1);

            if (!urlKey.equals(qwPushProperty.security().authKey())) {
                return false;
            }
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
