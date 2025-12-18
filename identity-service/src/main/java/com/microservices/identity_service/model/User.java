package com.microservices.identity_service.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservices.identity_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Setter
@Getter
@Entity
// @Table(name="user_details")
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "unique_username", columnNames = "userName"),
        @UniqueConstraint(name = "unique_email", columnNames = "email"),
        @UniqueConstraint(name = "unique_phone", columnNames = "phoneNumber")
})
public class User {
    // @Id
    // @GeneratedValue
    // private Long id;
    // private String firstname;
    // private String lastname;
    // private String username;
    // private String email;
    // private String password;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", unique = true, nullable = false, updatable = false)
    private Long id;

//    @NotBlank(message = "Full name must not be blank")
//    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Column(name = "fullName")
    private String fullname;

//    @NotBlank(message = "Username must not be blank")
//    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "userName")
    private String username;

    @NaturalId
//    @NotBlank
//    @Size(max = 50)
//    @Email(message = "Input must be in Email format")
    @Column(name = "email")
    private String email;

    @JsonIgnore
//    @NotNull(message = "Password must not be null")
//    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Column(name = "password")
    private String password;

//    @NotBlank(message = "Gender must not be blank")
    @Column(name = "gender", nullable = false)
    private String gender;

//    @Pattern(regexp = "^\\+91[0-9]{9,10}$|^0[0-9]{9,10}$", message = "The phone number is not in the correct format")
//    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters")
    @Column(name = "phoneNumber", unique = true)
    private String phone;

    @Pattern(regexp = "^(http|https)://.*$", message = "Avatar URL must be a valid HTTP or HTTPS URL")
    @Lob
    @Column(name = "imageUrl" , length = 2048)
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

}

