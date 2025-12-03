package com.microservices.identity_service.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.microservices.identity_service.model.User;
import  com.microservices.identity_service.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.util.Collection;

import lombok.experimental.Accessors;

@Data
@With
@Builder
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserPrinciple implements UserDetails {

    // private String username;
    // private String password;
    private Long id;
    private String fullname;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String gender;
    private String phone;
    private String avatar;
    private Role role;
    public static UserPrinciple build(User user) {
//         List<GrantedAuthority> authorityList = user.getRole()
//                 .stream()
//                 .map(role -> new SimpleGrantedAuthority(role.name().name()))
//                 .collect(Collectors.toList());

        return UserPrinciple.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .gender(user.getGender())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

