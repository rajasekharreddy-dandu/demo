package com.microservices.identity_service.controller;


import com.microservices.identity_service.dto.response.ResponseMessage;
import com.microservices.identity_service.exception.payload.ErrorResponse;
import com.microservices.identity_service.exception.wrapper.TokenErrorOrAccessTimeOut;
import com.microservices.identity_service.exception.wrapper.UserNotFoundException;
//import com.microservices.identity_service.http.HeaderGenerator;
import com.microservices.identity_service.dto.request.ChangePasswordRequest;
import com.microservices.identity_service.dto.request.SignUp;
import com.microservices.identity_service.dto.request.UserDto;
import com.microservices.identity_service.http.HeaderGenerator;
import com.microservices.identity_service.model.User;
import com.microservices.identity_service.service.AuthService;
import com.microservices.identity_service.service.JwtService;
import com.microservices.identity_service.utils.ControllerHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Operations related to users")
public class UserManager {

    private final ModelMapper modelMapper;
    private final AuthService userService;
    private final HeaderGenerator headerGenerator;
    private final ControllerHelper controllerHelper;
    private final JwtService jwtService;


    @Operation(summary = "Update user information", description = "Update the user information with the provided details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER')or hasAuthority('ADMIN'))")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody SignUp updateDTO) {
//        try {
            User user = userService.update(id, updateDTO); // synchronous call
//            return ResponseEntity.ok(new ResponseMessage("Update user: " + updateDTO.getUsername() + " successfully."));
            log.info("User {} Updated successfully.",user.getId());
            return ResponseEntity.ok(controllerHelper.getUserResonse(user,false));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    new ResponseMessage("Update user: " + updateDTO.getUsername() + " failed: " + e.getMessage()));
//        }
    }


    @Operation(summary = "Change user password",
            description = "Change the password for the authenticated user.")
    @ApiResponse(responseCode = "200",
            description = "Password changed successfully"
    )
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @Operation(summary = "Delete user",
            description = "Delete a user with the specified ID.")
    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('SUPER_ADMIN') or hasAuthority('ADMIN'))")
    public String delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @Operation(summary = "Get user by username",
            description = "Retrieve user information based on the provided username.")
    @GetMapping("/user")
    @PreAuthorize("(hasAuthority('ADMIN')) or (hasAuthority('USER') and principal.username == #username)")
    public ResponseEntity<?> getUserByUsername(@RequestParam("username") String username) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with: " + username));

            UserDto userDto = modelMapper.map(user, UserDto.class);

            return new ResponseEntity<>(
                    userDto,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK
            );
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(
                    null,
                    headerGenerator.getHeadersForError(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user information based on the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{id}")
//    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and principal.id == #id)")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found with: " + id));

            UserDto userDto = modelMapper.map(user, UserDto.class);

            return new ResponseEntity<>(
                    userDto,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK
            );
        } catch (UserNotFoundException ex) {
            return new ResponseEntity<>(
                    null,
                    headerGenerator.getHeadersForError(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Operation(summary = "Get a secure user resource"
//            authorizations = { @Authorization(value="JWT") }
    )
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                     @RequestParam(defaultValue = "ASC") String sortOrder) {

        Page<UserDto> usersPage = userService.findAllUsers(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(usersPage, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
    }

    @Operation(summary = "Get user information from token",
            description = "Retrieve user information based on the provided JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtService.extractUserName(token);
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new TokenErrorOrAccessTimeOut("Token error or access timeout"));
            UserDto userDto = modelMapper.map(user, UserDto.class);

            return new ResponseEntity<>(
                    userDto,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK
            );
        } catch (TokenErrorOrAccessTimeOut ex) {
            return new ResponseEntity<>(
                    new ResponseMessage(ex.getMessage()),
                    headerGenerator.getHeadersForError(),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    new ResponseMessage("Unexpected error occurred: " + ex.getMessage()),
                    headerGenerator.getHeadersForError(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}

