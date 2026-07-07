package com.manqiaotechnology.executionosapi.common;

public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> failed(String message) {
        return new Result<>(500, message, null);
    }
}
