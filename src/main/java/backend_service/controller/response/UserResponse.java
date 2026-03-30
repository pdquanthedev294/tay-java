package backend_service.controller.response;

import backend_service.common.Gender;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
  private Long id;
  private String firstName;
  private String lastName;
  private Gender gender;
  private Date birthday;
  private String username;
  private String email;
  private String phone;
}
