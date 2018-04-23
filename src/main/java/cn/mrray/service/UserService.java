package cn.mrray.service;

import cn.mrray.entity.dto.UsernamePasswordDto;
import cn.mrray.entity.vo.RespBody;

public interface UserService {
    RespBody login(UsernamePasswordDto dto);

    RespBody validaToken(String accessToken);

    RespBody refreshToken(String refreshToken);

    RespBody loginOut(String accessToken);

    RespBody queryAllGeneralUsers(String accessToken);

    RespBody modify(String accessToken, UsernamePasswordDto dto);

    RespBody reset(String accessToken, String username);
}
