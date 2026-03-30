package backend_service.controller.request;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class AddressRequest implements Serializable {
  private String apartmentNumber;
  private String floor;
  private String building;
  private String streetNumber;
  private String street;
  private String city;
  private String country;
  private Integer addressType;
}
