package backend_service.controller;

import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserPasswordRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
public class UserController {

  @Operation(summary = "Get user list", description = "API retrieve user from db")
  @GetMapping("/list")
  public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {

    UserResponse userResponse1 = new UserResponse();
    userResponse1.setId(1L);
    userResponse1.setFirstName("Tay");
    userResponse1.setLastName("Java");
    userResponse1.setGender("");
    userResponse1.setBirthday(new Date());
    userResponse1.setUsername("admin");
    userResponse1.setEmail("admin@gmail.com");
    userResponse1.setPhone("0975118228");


    UserResponse userResponse2 = new UserResponse();

    List<UserResponse> userList = List.of(userResponse1, userResponse2);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.OK.value());
    result.put("message", "user list");
    result.put("data", userList);

    return result;
  }

  @Operation(summary = "Get user detail", description = "API retrieve user detail by ID")
  @GetMapping("/{userId}")
  public Map<String, Object> getUserDetail(@PathVariable Long userId) {

    UserResponse userDetail = new UserResponse();
    userDetail.setId(userId);
    userDetail.setFirstName("Tay");
    userDetail.setLastName("Java");
    userDetail.setGender("");
    userDetail.setBirthday(new Date());
    userDetail.setUsername("admin");
    userDetail.setEmail("admin@gmail.com");
    userDetail.setPhone("0975118228");

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.OK.value());
    result.put("message", "user");
    result.put("data", userDetail);

    return result;
  }

  @Operation(summary = "Create User", description = "API add new user to db")
  @PostMapping("/add")
  public Map<String, Object> createUser(UserCreationRequest request) {

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.CREATED.value());
    result.put("message", "User created successfully");
    result.put("data", 3);

    return result;
  }

  @Operation(summary = "Update User", description = "API update user to db")
  @PutMapping("/upd")
  public Map<String, Object> updateUser(UserUpdateRequest request) {

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.ACCEPTED.value());
    result.put("message", "User updated successfully");
    result.put("data", "");

    return result;
  }

  @Operation(summary = "Change Password", description = "API change password for user to database")
  @PatchMapping("/change-pwd")
  public Map<String, Object> changePassword(UserPasswordRequest request) {

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.NO_CONTENT.value());
    result.put("message", "Password updated successfully");
    result.put("data", "");

    return result;
  }

  @Operation(summary = "Delete user", description = "API activate user from database")
  @DeleteMapping("/del/{userId}")
  public Map<String, Object> deleteUser(@PathVariable Long userId) {

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("status", HttpStatus.RESET_CONTENT.value());
    result.put("message", "User deleted successfully");
    result.put("data", "");

    return result;
  }
}