package com.project.tableforyou.utils.api;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiUtil<T> {

    private T response;

    @Builder
    private ApiUtil(T response) {
        this.response = response;
    }

    public static <T> ApiUtil<T> from(T response) {
        return ApiUtil.<T>builder()
                .response(response)
                .build();
    }
}
