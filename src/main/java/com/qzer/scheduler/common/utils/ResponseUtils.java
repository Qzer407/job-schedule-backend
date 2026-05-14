package com.qzer.scheduler.common.utils;

import com.qzer.scheduler.common.dto.response.ApiResponse;

public class ResponseUtils {

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    public static ApiResponse<Void> success() {
        return ApiResponse.success(null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return ApiResponse.error(code, message);
    }

    public static ApiResponse<Void> badRequest(String message) {
        return ApiResponse.error("400", message);
    }

    public static ApiResponse<Void> unauthorized(String message) {
        return ApiResponse.error("401", message);
    }

    public static ApiResponse<Void> forbidden(String message) {
        return ApiResponse.error("403", message);
    }

    public static ApiResponse<Void> notFound(String message) {
        return ApiResponse.error("404", message);
    }

    public static ApiResponse<Void> serverError(String message) {
        return ApiResponse.error("500", message);
    }

    private ResponseUtils() {}
}
