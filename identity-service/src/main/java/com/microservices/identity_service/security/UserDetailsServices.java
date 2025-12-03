package com.microservices.identity_service.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.identity_service.exception.wrapper.EmailOrUsernameNotFoundException;
import com.microservices.identity_service.model.User;
import com.microservices.identity_service.repository.UserRepository;


@Component
public class UserDetailsServices implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //     Optional<User> credential = userRepository.findByUsername(username);
    //     return credential.map(UserPrinciple::new).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    // }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EmailOrUsernameNotFoundException("Email or Username does not exist, please try again: " + username));

        return UserPrinciple.build(user);
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailOrUsernameNotFoundException("Email or Username does not exist, please try again: " + email));

        return UserPrinciple.build(user);
    }

    @Transactional
    public UserDetails loadUserByPhone(String phone) {
        User user = userRepository.findByEmail(phone)
                .orElseThrow(() -> new EmailOrUsernameNotFoundException("User not found, phone and password: " + phone));

        return UserPrinciple.build(user);
    }
}

