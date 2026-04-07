package backend_service.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class ApiResponse implements Serializable {
  private int status;
  private String message;
  private transient Object data;
}
