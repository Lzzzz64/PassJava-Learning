package org.example.passjava.study.common;

import lombok.Data;

@Data
public class Result<T> {
    private T data;
    private int code;
    private String msg;
    public Result(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }
    public static <T> Result<T> success(T data, int code, String msg) {
        return new Result<>(data, code, msg);
    }
    public static <T> Result<T> success(T data, String msg) {
        return new Result<>(data, 200, msg);
    }
    public static <T> Result<T> success(T data) {
        return new Result<>(data, 200, "success");
    }
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(null, code, msg);
    }
}
