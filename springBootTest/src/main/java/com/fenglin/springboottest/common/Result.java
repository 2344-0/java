package com.fenglin.springboottest.common;

/**
 * 统一响应结果，结构与前端约定保持一致：{ code, msg, data }
 * code = 0 表示成功，非 0 表示失败（前端据此判断成败并弹出 msg）
 */
public class Result<T> {

    /** 成功码 */
    public static final int CODE_OK = 0;

    /** 失败码 */
    public static final int CODE_FAIL = 1;

    private int code;

    private String msg;

    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(CODE_OK, "操作成功", null);
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<>(CODE_OK, msg, null);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(CODE_OK, msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(CODE_FAIL, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
