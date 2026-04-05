package backend_service.service.impl;

import backend_service.common.TokenType;
import backend_service.controller.request.SigInRequest;
import backend_service.controller.response.TokenResponse;
import backend_service.exception.ForbiddenException;
import backend_service.exception.InvalidDataException;
import backend_service.model.UserEntity;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    List<String> authorities = new ArrayList<>();
    try {
      Authentication authenticate = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getUsername(),
          request.getPassword()
        )
      );

      log.info("isAuthenticated = {}", authenticate.isAuthenticated());
      log.info("Authorities: {}", authenticate.getAuthorities().toString());
      authorities.add(authenticate.getAuthorities().toString());

      // Nếu xác thực thành công, lưu thông tin vào SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authenticate);
    } catch (AuthenticationException e) {
      log.error("Login fail, message={}", e.getMessage());
      throw new AccessDeniedException(e.getMessage());
    }

    String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
    String refreshToken = jwtService.generateRefreshToken( request.getUsername(), authorities);

    return TokenResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .build();
  }

  @Override
  public TokenResponse getRefreshToken(String refreshToken) {
    log.info("Get refresh token");

    if (!StringUtils.hasLength(refreshToken)) {
      throw new InvalidDataException("Token must be not blank");
    }

    try {
      // Verify token
      String userName = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

      // check user is active or inactivated
      UserEntity user = userRepository.findByUsername(userName);
      List<String> authorities = new ArrayList<>();
      user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

      // generate new access token
      String accessToken = jwtService.generateAccessToken(user.getUsername(), authorities);

      return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    } catch (Exception e) {
      log.error("Access denied! errorMessage: {}", e.getMessage());
      throw new ForbiddenException(e.getMessage());
    }
  }
}
