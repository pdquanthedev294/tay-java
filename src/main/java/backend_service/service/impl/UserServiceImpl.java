package backend_service.service.impl;

import backend_service.common.UserStatus;
import backend_service.controller.request.AddressRequest;
import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserPasswordRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.UserResponse;
import backend_service.model.AddressEntity;
import backend_service.model.UserEntity;
import backend_service.repository.AddressRepository;
import backend_service.repository.UserRepository;
import backend_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;

  @Override
  public List<UserResponse> findAll() {
    return List.of();
  }

  @Override
  public UserResponse findById(Long id) {
    return null;
  }

  @Override
  public UserResponse findByUsername(String username) {
    return null;
  }

  @Override
  public UserResponse findByEmail(String email) {
    return null;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public long save(UserCreationRequest req) {
    log.info("Saving user: {}", req);
    UserEntity user = new UserEntity();
    user.setFirstName(req.getFistName());
    user.setLastName(req.getLastName());
    user.setGender(req.getGender());
    user.setBirthday(req.getBirthday());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setUsername(req.getUsername());
    user.setType(req.getType());
    user.setStatus(UserStatus.NONE);
    userRepository.save(user);
    log.info("Save user: {}", user);

    if (user.getId() != null) {
//      System.out.println(10/0);
//       Khi ma thuc hien sava repository thi log in
//       ra cau lenh Hibernate roi de sava vao database nhung ma trong database chua luu
      log.info("user id: {}", user.getId());
      List<AddressEntity> addresses = new ArrayList<>();
      req.getAddresses().forEach((AddressRequest address) -> {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setApartmentNumber(address.getApartmentNumber());
        addressEntity.setFloor(address.getFloor());
        addressEntity.setBuilding(address.getBuilding());
        addressEntity.setStreetNumber(address.getStreetNumber());
        addressEntity.setStreet(address.getStreet());
        addressEntity.setCity(address.getCity());
        addressEntity.setCountry(address.getCountry());
        addressEntity.setAddressType(address.getAddressType());
        addressEntity.setUserId(user.getId());
        addresses.add(addressEntity);
      });
      addressRepository.saveAll(addresses);
      log.info("Saved addresses: {}", addresses);
    }

    return user.getId();
  }

  @Override
  public void update(UserUpdateRequest req) {

  }

  @Override
  public void changePassword(UserPasswordRequest req) {

  }

  @Override
  public void delete(Long id) {

  }
}
