package backend_service.controller.request;

import backend_service.common.Gender;
import backend_service.common.UserType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserCreationRequest implements Serializable {
  private String fistName;
  private String lastName;
  private Gender gender;
  private Date birthday;
  private String username;
  private String email;
  private String phone;
  private UserType type;
  private List<AddressRequest> addresses;
}