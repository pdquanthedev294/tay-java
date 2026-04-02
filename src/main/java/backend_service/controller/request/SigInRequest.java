package backend_service.controller.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SigInRequest implements Serializable {
  private String username;
  private String password;
  private String platform; // web, mobile, miniApp
  private String deviceToken;
  private String versionApp;
}