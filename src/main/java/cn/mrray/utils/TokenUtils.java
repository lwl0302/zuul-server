package cn.mrray.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by Arthur on 2017/8/23.
 */
public final class TokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    private TokenUtils() {}

    public static String genUuid() {
        return UUID.randomUUID().toString();
    }

    public static String extractAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("access_token");

        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter("access_token");
            if (StringUtils.isBlank(accessToken)) {
                logger.debug("extract access_token fail.");
                // 退出登录必须先有token值，也就是说必须先登录才可以
                return null;
            }
        }
        return accessToken;
    }

}
