package cn.mrray.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by ln on 2017/11/27.
 */
@Entity
@Table(name = "t_token")
public class Token extends SuperEntity{

    @ManyToOne(targetEntity = User.class)
    private User user;

    // 访问token
    @Column(unique = true)
    private String accessToken;

    // 访问token签发时间
    private Date accessTokenSignAt;

    // 访问token过期时间
    private Date accessTokenExpiredAt;

    // 刷新token
    @Column(unique = true)
    private String refreshToken;

    // 刷新token签发时间
    private Date refreshTokenSignAt;

    // 刷新token过期时间
    private Date refreshTokenExpiredAt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getAccessTokenSignAt() {
        return accessTokenSignAt;
    }

    public void setAccessTokenSignAt(Date accessTokenSignAt) {
        this.accessTokenSignAt = accessTokenSignAt;
    }

    public Date getAccessTokenExpiredAt() {
        return accessTokenExpiredAt;
    }

    public void setAccessTokenExpiredAt(Date accessTokenExpiredAt) {
        this.accessTokenExpiredAt = accessTokenExpiredAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getRefreshTokenSignAt() {
        return refreshTokenSignAt;
    }

    public void setRefreshTokenSignAt(Date refreshTokenSignAt) {
        this.refreshTokenSignAt = refreshTokenSignAt;
    }

    public Date getRefreshTokenExpiredAt() {
        return refreshTokenExpiredAt;
    }

    public void setRefreshTokenExpiredAt(Date refreshTokenExpiredAt) {
        this.refreshTokenExpiredAt = refreshTokenExpiredAt;
    }
}
