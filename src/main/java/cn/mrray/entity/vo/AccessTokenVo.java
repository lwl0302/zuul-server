package cn.mrray.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Arthur on 2017/8/23.
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccessTokenVo {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    // accessToken过期时间
    @JsonProperty(value = "expired_at")
    private Date expiredAt;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }
}
