package backend_service.controller;


import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserPasswordRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.ApiResponse;
import backend_service.controller.response.UserResponse;
import backend_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Validated
public class UserController {

  private final UserService userService;

  @Operation(summary = "Get user list", description = "API retrieve user from db")
  @GetMapping("/list")
//  @PreAuthorize("hasAnyAuthority('manager', 'admin')")
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  public ApiResponse getList(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String sort,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {

    log.info("Get user list");

    return ApiResponse.builder()
      .status(HttpStatus.OK.value())
      .message("user list")
      .data(userService.findAll(keyword, sort, page, size))
      .build();
  }

  @Operation(summary = "Get user detail", description = "API retrieve user detail by ID")
  @GetMapping("/{userId}")
  @PreAuthorize("hasAuthority('user')")
  public ApiResponse getUserDetail(@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId) {
    log.info("Get user detail by ID: {}", userId);

    UserResponse userDetail = userService.findById(userId);

    return ApiResponse.builder()
      .status(HttpStatus.OK.value())
      .message("user")
      .data(userDetail)
      .build();
  }

  @Operation(summary = "Create User", description = "API add new user to db")
  @PostMapping("/add")
  public ApiResponse createUser(@RequestBody @Valid UserCreationRequest request) {

    return ApiResponse.builder()
      .status(HttpStatus.CREATED.value())
      .message("User created successfully")
      .data(userService.save(request))
      .build();
  }

  @Operation(summary = "Update User", description = "API update user to db")
  @PutMapping("/upd")
  public ApiResponse updateUser(@RequestBody @Valid UserUpdateRequest request) {
    log.info("Update user: {}", request);

    userService.update(request);

    return ApiResponse.builder()
      .status(HttpStatus.ACCEPTED.value())
      .message("User updated successfully")
      .build();
  }

  @Operation(summary = "Change Password", description = "API change password for user to database")
  @PatchMapping("/change-pwd")
  public ApiResponse changePassword(@RequestBody UserPasswordRequest request) {
    log.info("Changing password for user: {}", request);

    userService.changePassword(request);

    return ApiResponse.builder()
      .status(HttpStatus.NO_CONTENT.value())
      .message("Password updated successfully")
      .build();
  }

  @GetMapping("/confirm-email")
  public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException, IOException {
    log.info("Confirm email: {}", secretCode);
    try {
      // TODO checkcek or compare secretCode from database
    } catch (Exception e) {
      log.error("Confirm email was failure!, errorMessage={}", e.getMessage());
    } finally {
      response.sendRedirect("https://tayjava.vn/wp-admin");
    }
  }

  @Operation(summary = "Delete user", description = "API activate user from database")
  @DeleteMapping("/del/{userId}")
  @PreAuthorize("hasAuthority('admin')")
  public ApiResponse deleteUser(@PathVariable Long userId) {
    log.info("Deleting user: {}", userId);

    userService.delete(userId);

    return ApiResponse.builder()
      .status(HttpStatus.RESET_CONTENT.value())
      .message("User deleted successfully")
      .build();
  }
}