package backend_service.service.impl;

import backend_service.common.UserStatus;
import backend_service.controller.request.AddressRequest;
import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserPasswordRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.UserPageResponse;
import backend_service.controller.response.UserResponse;
import backend_service.exception.InvalidDataException;
import backend_service.exception.ResourceNotFoundException;
import backend_service.model.AddressEntity;
import backend_service.model.UserEntity;
import backend_service.repository.AddressRepository;
import backend_service.repository.UserRepository;
import backend_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserPageResponse findAll(String keyword, String sort, int page, int size) {
    log.info("findAll start");
    // Sorting
    Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
    if (StringUtils.hasLength(sort)) {
      Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // tencot:asc/desc
      Matcher matcher = pattern.matcher(sort);
      if (matcher.find()) {
        String columnName = matcher.group(1);
        if (matcher.group(3).equalsIgnoreCase("asc")) {
          order = new Sort.Order(Sort.Direction.ASC, columnName);
        } else {
          order = new Sort.Order(Sort.Direction.DESC, columnName);
        }
      }
    }

    // Xu ly truong hop FE muon bat dau voi page = 1
    int pageNo = 0;
    if (page > 0) {
      pageNo = page - 1;
    }

    // Paging
    Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

    Page<UserEntity> entityPage;

    if (StringUtils.hasLength(keyword)) {
      keyword = "%" + keyword.toLowerCase() + "%";
      entityPage = userRepository.searchByKeyword(keyword, pageable);
    } else {
      entityPage = userRepository.findAll(pageable);
    }

    return getUserPageResponse(page, size, entityPage);
  }


  @Override
  public UserResponse findById(Long id) {
    log.info("Find user by id: {}", id);

    UserEntity userEntity = getUserEntity(id);

    return UserResponse.builder()
      .id(id)
      .firstName(userEntity.getFirstName())
      .lastName(userEntity.getLastName())
      .gender(userEntity.getGender())
      .birthday(userEntity.getBirthday())
      .username(userEntity.getUsername())
      .phone(userEntity.getPhone())
      .email(userEntity.getEmail())
      .build();
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

    UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
    if (userByEmail != null) {
      throw new InvalidDataException("User with email: " + req.getEmail() + " already exists");
    }

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
  @Transactional(rollbackFor = Exception.class)
  public void update(UserUpdateRequest req) {
    log.info("Updating user: {}", req);

    UserEntity user = getUserEntity(req.getId());
    user.setFirstName(req.getFirstName());
    user.setLastName(req.getLastName());
    user.setGender(req.getGender());
    user.setBirthday(req.getBirthday());
    user.setEmail(req.getEmail());
    user.setPhone(req.getPhone());
    user.setUsername(req.getUsername());

    userRepository.save(user);
    log.info("Updated user: {}", user);

    // save address
    List<AddressEntity> addresses = new ArrayList<>();

    req.getAddresses().forEach((AddressRequest address) -> {
      AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(user.getId(), address.getAddressType());
      if (addressEntity == null) {
        addressEntity = new AddressEntity();
      }

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
    log.info("Updated addresses: {}", addresses);
  }

  @Override
  public void changePassword(UserPasswordRequest req) {
    log.info("Changing password for user: {}", req);

    // Get user by id
    UserEntity user = getUserEntity(req.getId());
    if (req.getPassword().equals(req.getConfirmPassword())) {
      user.setPassword(passwordEncoder.encode(req.getPassword()));
    }

    userRepository.save(user);
    log.info("Changed password for user: {}", req);
  }

  @Override
  public void delete(Long id) {
    log.info("Deleting user: {}", id);

    UserEntity user = getUserEntity(id);
    user.setStatus(UserStatus.INACTIVE);
    userRepository.save(user);
    log.info("Deleted user: {}", user);
  }


  /*
  * Get user by id
  *
  * @param id
  * @return
  * */
  private UserEntity getUserEntity(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));
  }


  /*
  * Convert UserEntities to user
  * @Param page
  * @param size
  * @param userEntities
  * @return
  * */
  private static UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntities) {
    log.info("Convert User Entity Page: {}", userEntities);

    List<UserResponse> userList = userEntities.stream().map((UserEntity entity) -> UserResponse.builder()
      .id(entity.getId())
      .firstName(entity.getFirstName())
      .lastName(entity.getLastName())
      .gender(entity.getGender())
      .birthday(entity.getBirthday())
      .username(entity.getUsername())
      .phone(entity.getPhone())
      .email(entity.getEmail())
      .build()
    ).toList();

    UserPageResponse response = new UserPageResponse();
    response.setPageNumber(page);
    response.setPageSize(size);
    response.setTotalElements(userEntities.getNumberOfElements());
    response.setTotalPages(userEntities.getTotalPages());
    response.setUsers(userList);
    return response;
  }
}
