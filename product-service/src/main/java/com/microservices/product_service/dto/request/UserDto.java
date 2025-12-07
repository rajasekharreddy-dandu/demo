package com.microservices.identity_service.dto.request;

import com.microservices.identity_service.enums.Role;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
public class UserDto {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String gender;
    private String phone;
    private String avatar;
    // @NotNull
    private Role role;

}

