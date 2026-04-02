package backend_service.service.impl;

import backend_service.controller.request.SigInRequest;
import backend_service.controller.response.TokenResponse;
import backend_service.repository.UserRepository;
import backend_service.service.AuthenticationService;
import backend_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Override
  public TokenResponse getAccessToken(SigInRequest request) {
    log.info("Get access token");

    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getUsername(),
          request.getPassword()
        )
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (AuthenticationException e) {
      log.error("Login fail, message={}", e.getMessage());
      throw new AccessDeniedException(e.getMessage());
    }

    var user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }

    String accessToken = jwtService.generateAccessToken(user.getId(), request.getUsername(), user.getAuthorities());
    String refreshToken = jwtService.generateRefreshToken(user.getId(), request.getUsername(), user.getAuthorities());

    return TokenResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

  @Override
  public TokenResponse getRefreshToken(String request) {
    return null;
  }
}
