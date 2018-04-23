package cn.mrray.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

/**
 * Created by Arthur on 2017/8/23.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RespBody<T> {

    private HttpStatus status = HttpStatus.OK;

    private String message;

    private T data;

    public HttpStatus getStatus() {
        return status;
    }

    public RespBody<T> setStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RespBody<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public RespBody<T> setData(T data) {
        this.data = data;
        return this;
    }
}
