package backend_service.service;

import backend_service.controller.request.SigInRequest;
import backend_service.controller.response.TokenResponse;

public interface AuthenticationService {
  TokenResponse getAccessToken(SigInRequest request);

  TokenResponse getRefreshToken(String request);
}
