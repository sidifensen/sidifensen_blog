package com.sidifensen.domain.result;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    // 返回成功的结果
    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    // 带数据的返回成功的结果
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    // 返回错误的结果
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 400;// 400代表请求参数错误
        return result;
    }

    // 返回未认证的结果
    public static <T> Result<T> unauthorized(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 401;// 401代表未认证
        return result;
    }

    // 将Result对象转换为JSON字符串
    public String toJson() {
        return JSON.toJSONString(this);
    }



}
