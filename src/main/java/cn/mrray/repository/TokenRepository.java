package cn.mrray.repository;

import cn.mrray.entity.domain.Token;

/**
 * Created by ln on 2017/11/27.
 */
public interface TokenRepository extends BaseRepository<Token> {
    Token findByAccessToken(String accessToken);

    Token findByRefreshToken(String refreshToken);
}
