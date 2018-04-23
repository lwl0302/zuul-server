package cn.mrray.filter;

import cn.mrray.constant.Constant;
import cn.mrray.entity.domain.User;
import cn.mrray.entity.dto.UsernamePasswordDto;
import cn.mrray.entity.vo.AccessTokenVo;
import cn.mrray.entity.vo.RespBody;
import cn.mrray.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by Arthur on 2017/8/22.
 */
public class AccessFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AccessFilter.class);

    @Autowired
    private UserService userService;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String method = request.getMethod();
        String url = request.getRequestURI();

        // 不需要过滤的
        List<String> unf = new ArrayList<>();
        unf.add("GET:/tms/api/v1/license/");
        unf.add("GET:/tms/api/v1/license/serial");
        String uri = String.format("%s:%s", method, url);
        if (unf.contains(uri)) {
            return false;
        }

        return true;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpServletResponse response = ctx.getResponse();

        String method = request.getMethod();
        String url = request.getRequestURI();

        logger.info("{}:{}", method, url);

        // 需要登录
        if ("POST:/auth/login".equals(String.format("%s:%s", method, url))) {
            ctx.setSendZuulResponse(false);
            try (InputStream is = request.getInputStream()) {

                // 从body中封装参数
                UsernamePasswordDto dto = JSONObject.parseObject(is, UsernamePasswordDto.class);

                RespBody body = userService.login(dto);
                AccessTokenVo tokenVo = (AccessTokenVo) body.getData();
                Cookie cookie = new Cookie("token", tokenVo.getAccessToken());
                response.addCookie(cookie);
                ctx.setResponseStatusCode(body.getStatus().value());
                ctx.setResponseBody(JSON.toJSONString(body));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                ctx.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
            }
            return null;
        }

        if ("POST:/auth/loginOut".equals(String.format("%s:%s", method, url))) {
            ctx.setSendZuulResponse(false);
            String accessToken = request.getHeader("access_token");
            if (StringUtils.isBlank(accessToken)) {
                accessToken = request.getParameter("access_token");
            }
            RespBody body = userService.loginOut(accessToken);
            ctx.setResponseStatusCode(body.getStatus().value());
            return null;

        }

        // 需要登录
        if ("POST:/auth/refresh".equals(String.format("%s:%s", method, url))) {
            ctx.setSendZuulResponse(false);
            String refreshToken = request.getParameter("refresh_token");
            if (StringUtils.isEmpty(refreshToken)) {
                refreshToken = request.getHeader("refresh_token");
            }

            RespBody body = userService.refreshToken(refreshToken);

            ctx.setResponseStatusCode(body.getStatus().value());
            ctx.setResponseBody(JSON.toJSONString(body));
            return null;
        }

        // 验证Token
        String accessToken = request.getHeader("access_token");
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter("access_token");
            if (StringUtils.isBlank(accessToken)) {
                logger.debug("access_token not found.");

                RespBody body = new RespBody()
                        .setStatus(HttpStatus.UNAUTHORIZED)
                        .setMessage("token not found");

                ctx.setResponseStatusCode(body.getStatus().value());
                ctx.setResponseBody(JSON.toJSONString(body));

                return null;
            }
        }


        RespBody body = userService.validaToken(accessToken);

        if (HttpStatus.OK != body.getStatus()) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(body.getStatus().value());
            ctx.setResponseBody(JSON.toJSONString(body));
        } else {

            if ("GET:/auth/users".equals(String.format("%s:%s", method, url))) {
                ctx.setSendZuulResponse(false);
                RespBody resp = userService.queryAllGeneralUsers(accessToken);
                ctx.setResponseStatusCode(resp.getStatus().value());
                ctx.setResponseBody(JSON.toJSONString(resp));
            } else if ("POST:/auth/modify".equals(String.format("%s:%s", method, url))) {
                ctx.setSendZuulResponse(false);
                try (InputStream is = request.getInputStream()) {

                    // 从body中封装参数
                    UsernamePasswordDto dto = JSONObject.parseObject(is, UsernamePasswordDto.class);
                    RespBody resp = userService.modify(accessToken, dto);
                    ctx.setResponseStatusCode(resp.getStatus().value());
                    ctx.setResponseBody(JSON.toJSONString(resp));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

            } else if ("POST:/auth/reset".equals(String.format("%s:%s", method, url))) {
                ctx.setSendZuulResponse(false);
                try (InputStream is = request.getInputStream()) {

                    // 从body中封装参数
                    UsernamePasswordDto dto = JSONObject.parseObject(is, UsernamePasswordDto.class);
                    RespBody resp = userService.reset(accessToken, dto.getUsername());
                    ctx.setResponseStatusCode(resp.getStatus().value());
                    ctx.setResponseBody(JSON.toJSONString(resp));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

            }else if(Constant.USER_ROLE_SUPER.equals(((User)body.getData()).getRole())){//拦截root用户对其他资源的访问
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                ctx.setResponseBody("super user can not access other resources.");
            }


        }

        return null;
    }
}
