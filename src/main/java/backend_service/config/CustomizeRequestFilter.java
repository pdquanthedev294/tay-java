package backend_service.config;

import backend_service.common.TokenType;
import backend_service.service.JwtService;
import backend_service.service.UserServiceDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContext;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
public class CustomizeRequestFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserServiceDetail userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("{} {}", request.getMethod(), request.getRequestId());

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {

      authHeader = authHeader.substring(7);
      log.info("Bearer authHeader: {}", authHeader.substring(0, 20));

      String username = "";
      try {
        username = jwtService.extractUsername(authHeader, TokenType.ACCESS_TOKEN);
        log.info("username: {}", username);
      } catch (AccessDeniedException e) {
        log.error("Access Dined, message={}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse(e.getMessage()));
        return;
      }

      UserDetails userDetails = userDetailsService.userServiceDetail().loadUserByUsername(username);

      SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
      UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      securityContext.setAuthentication(authentication);
      SecurityContextHolder.setContext(securityContext);

      filterChain.doFilter(request, response);
      return;
    }


    filterChain.doFilter(request, response);
  }

  private String errorResponse(String message) {
    try {
      ErrorResponse error = new ErrorResponse();
      error.setTimestamp(new Date());
      error.setError("Forbidden");
      error.setStatus(HttpServletResponse.SC_FORBIDDEN);
      error.setMessage(message);

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      return gson.toJson(error);
    } catch (Exception e) {
      return ""; // Return an empty string if serialization fails
    }
  }

  @Setter
  @Getter
  private class ErrorResponse {
    private Date timestamp;
    private int status;
    private String error;
    private String message;
  }
}
