package cn.mrray.service.impl;

import cn.mrray.constant.Constant;
import cn.mrray.entity.domain.Token;
import cn.mrray.entity.domain.User;
import cn.mrray.entity.dto.UsernamePasswordDto;
import cn.mrray.entity.vo.AccessTokenVo;
import cn.mrray.entity.vo.RespBody;
import cn.mrray.repository.TokenRepository;
import cn.mrray.repository.UserRepository;
import cn.mrray.service.UserService;
import cn.mrray.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final Environment env;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @PostConstruct
    public void registUser() {
        List<User> userList = userRepository.findByRole(Constant.USER_ROLE_SUPER);

        if (CollectionUtils.isEmpty(userList)) {
            User root = new User();
            root.setUsername("ROOT");
            root.setPassword(passwordEncoder.encode("ROOT"));
            root.setRole(Constant.USER_ROLE_SUPER);
            userRepository.saveAndFlush(root);
        }

        userList = userRepository.findByRole(Constant.USER_ROLE_GENERAL);
        if (CollectionUtils.isEmpty(userList)) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Constant.USER_ROLE_GENERAL);
            userRepository.saveAndFlush(admin);
        }

    }

    @Transactional
    @Override
    public RespBody login(UsernamePasswordDto dto) {

        RespBody body = new RespBody<>();

        User user = userRepository.findByUsername(dto.getUsername());

        if (user == null) {
            body.setStatus(HttpStatus.BAD_REQUEST);
            body.setMessage("username/password error.");
            return body;
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            body.setStatus(HttpStatus.BAD_REQUEST);
            body.setMessage("username/password error.");
            return body;
        }

        List<Token> tokenList = user.getTokenList();

        if (!CollectionUtils.isEmpty(tokenList)) {
            for (Token token : tokenList) {
                Date expiredAt = token.getRefreshTokenExpiredAt();
                if (expiredAt == null || System.currentTimeMillis() > expiredAt.getTime()) {
                    tokenRepository.delete(token);
                }
            }
        }


        String accessTokenExpiredTime = env.getProperty("token.access-token.expired-time");
        String refreshTokenExpiredTime = env.getProperty("token.refresh-token.expired-time");

        Date now = new Date();
        Date accessTokenExpiredAt = DateUtils.addMinutes(now, Integer.parseInt(accessTokenExpiredTime));
        Date refreshTokenExpiredAt = DateUtils.addMinutes(now, Integer.parseInt(refreshTokenExpiredTime));
        String token = TokenUtils.genUuid();

        Token tokenBean = new Token();
        tokenBean.setAccessToken(token);
        tokenBean.setAccessTokenSignAt(now);
        tokenBean.setAccessTokenExpiredAt(accessTokenExpiredAt);

        String refreshToken = TokenUtils.genUuid();
        tokenBean.setRefreshToken(refreshToken);
        tokenBean.setRefreshTokenSignAt(now);
        tokenBean.setRefreshTokenExpiredAt(refreshTokenExpiredAt);

        tokenBean.setUser(user);

        tokenRepository.saveAndFlush(tokenBean);

        AccessTokenVo vo = new AccessTokenVo();
        vo.setRole(user.getRole());
        BeanUtils.copyProperties(tokenBean, vo);
        vo.setExpiredAt(accessTokenExpiredAt);
        body.setData(vo);

        return body;
    }

    @Override
    public RespBody validaToken(String accessToken) {
        RespBody body = new RespBody();

        Token token = tokenRepository.findByAccessToken(accessToken);
        if (token == null) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("token error.");
            return body;
        }

        Date accessTokenExpiredAt = token.getAccessTokenExpiredAt();
        if (accessTokenExpiredAt == null || System.currentTimeMillis() > accessTokenExpiredAt.getTime()) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("token has expired, please refresh token.");
            return body;
        }
        User user = token.getUser();
        body.setData(user);

        body.setStatus(HttpStatus.OK);

        return body;
    }

    @Override
    public RespBody refreshToken(String refreshToken) {
        RespBody body = new RespBody();
        Token token = tokenRepository.findByRefreshToken(refreshToken);

        if (token == null) {
            body.setStatus(HttpStatus.FORBIDDEN);
            body.setMessage("refresh token error.");
            return body;
        }

        Date refreshTokenExpiredAt = token.getRefreshTokenExpiredAt();
        if (refreshTokenExpiredAt == null || System.currentTimeMillis() > refreshTokenExpiredAt.getTime()) {
            body.setStatus(HttpStatus.FORBIDDEN);
            body.setMessage("refresh token has expired, please login.");
            return body;
        }

        String accessTokenExpiredTime = env.getProperty("token.access-token.expired-time");

        Date now = new Date();
        Date accessTokenExpiredAt = DateUtils.addMinutes(now, Integer.parseInt(accessTokenExpiredTime));
        String tokenValue = TokenUtils.genUuid();
        token.setAccessToken(tokenValue);
        token.setAccessTokenSignAt(now);
        token.setAccessTokenExpiredAt(accessTokenExpiredAt);
        tokenRepository.saveAndFlush(token);

        AccessTokenVo vo = new AccessTokenVo();
        BeanUtils.copyProperties(token, vo);
        vo.setExpiredAt(accessTokenExpiredAt);
        body.setData(vo);

        return body;
    }

    @Override
    public RespBody loginOut(String accessToken) {
        if (!StringUtils.isEmpty(accessToken)) {
            Token token = tokenRepository.findByAccessToken(accessToken);
            if (token != null) {
                tokenRepository.delete(token);
            }
        }
        return new RespBody();

    }

    @Override
    public RespBody queryAllGeneralUsers(String accessToken) {

        RespBody body = new RespBody();

        Token token = tokenRepository.findByAccessToken(accessToken);
        if (token == null) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("token error.");
            return body;
        }
        User currentUser = token.getUser();
        if (currentUser == null || !Constant.USER_ROLE_SUPER.equals(currentUser.getRole())) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("not super user.");
            return body;
        }


        List<User> users = userRepository.findByRole(Constant.USER_ROLE_GENERAL);
        List<String> userNames = new ArrayList<String>();
        for (User user : users) {
            userNames.add(user.getUsername());
        }
        body.setData(userNames);

        return body;
    }

    @Override
    @Transactional
    public RespBody modify(String accessToken, UsernamePasswordDto dto) {

        RespBody body = new RespBody();

        Token token = tokenRepository.findByAccessToken(accessToken);
        if (token == null) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("token error.");
            return body;
        }

        User currentUser = token.getUser();
        if (currentUser == null || !Constant.USER_ROLE_GENERAL.equals(currentUser.getRole())) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("not general user.");
            return body;
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            body.setStatus(HttpStatus.BAD_REQUEST);
            body.setMessage("old password error.");
            return body;
        }

        currentUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.saveAndFlush(currentUser);

        //修改密码后删除所有token
        List<Token> tokenList = currentUser.getTokenList();
        if (!CollectionUtils.isEmpty(tokenList)) {
            tokenRepository.delete(tokenList);
        }

        return body;
    }

    @Transactional
    @Override
    public RespBody reset(String accessToken, String username) {
        RespBody body = new RespBody();

        Token token = tokenRepository.findByAccessToken(accessToken);
        if (token == null) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("token error.");
            return body;
        }

        User currentUser = token.getUser();
        if (currentUser == null || !Constant.USER_ROLE_SUPER.equals(currentUser.getRole())) {
            body.setStatus(HttpStatus.UNAUTHORIZED);
            body.setMessage("not super user.");
            return body;
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            body.setStatus(HttpStatus.BAD_REQUEST);
            body.setMessage("this user does not exist.");
            return body;
        }

        user.setPassword(passwordEncoder.encode(user.getUsername()));

        //修改密码后删除所有token
        List<Token> tokenList = user.getTokenList();
        if (!CollectionUtils.isEmpty(tokenList)) {
            tokenRepository.delete(tokenList);
        }

        return body;
    }
}
