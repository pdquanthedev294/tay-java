package backend_service.controller.request;

import java.io.Serializable;
import java.util.Date;

public class UserUpdateRequest  implements Serializable {
  private Long id;
  private String fistName;
  private String lastName;
  private String gender;
  private Date birthday;
  private String username;
  private String email;
  private String phone;
}
