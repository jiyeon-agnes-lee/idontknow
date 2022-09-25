package com.idk.api.user.dto;

import com.idk.api.user.domain.Age;
import lombok.*;

public class UserRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Join{
        private String email;
        private String name;
        private String password;
        private int districtId;
        private String gender;
        private Age age;

    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Login{
        private String email;
        private String password;

    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RePassword{
        private String email;

    }
}
