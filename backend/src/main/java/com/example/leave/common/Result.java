package com.example.leave.common;

import lombok.Data;

/**
 * 统一API响应结果封装
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> {

    /** 状态码 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // ==================== 失败响应 ====================

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    // ==================== 常用快捷方法 ====================

    /**
     * 请求参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }

    /**
     * 服务器内部错误
     */
    public static <T> Result<T> internalError(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
