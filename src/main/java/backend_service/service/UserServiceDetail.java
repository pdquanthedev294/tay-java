package backend_service.service;

import backend_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public record UserServiceDetail(UserRepository userRepository) {
  public UserDetailsService userServiceDetail() {
    return userRepository::findByUsername;
  }
}