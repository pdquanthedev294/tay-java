package backend_service.controller.request;

import backend_service.common.Gender;
import backend_service.common.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserCreationRequest implements Serializable {
  @NotBlank(message = "firstName must be not blank")
  private String fistName;

  @NotBlank(message = "lastName must be not blank")
  private String lastName;

  private Gender gender;
  private Date birthday;
  private String username;

  @Email(message = "Email invalid")
  private String email;

  private String phone;
  private UserType type;
  private List<AddressRequest> addresses;
}