package backend_service.service;

import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserPasswordRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.UserResponse;

import java.util.List;

public interface UserService {
  List<UserResponse> findAll();

  UserResponse findById(Long id);

  UserResponse findByUsername(String username);

  UserResponse findByEmail(String email);

  long save(UserCreationRequest req);

  void update(UserUpdateRequest req);

  void changePassword(UserPasswordRequest req);

  void delete(Long id);
}
