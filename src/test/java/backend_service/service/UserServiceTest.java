package backend_service.service;

import backend_service.common.Gender;
import backend_service.common.UserStatus;
import backend_service.common.UserType;
import backend_service.controller.request.AddressRequest;
import backend_service.controller.request.UserCreationRequest;
import backend_service.controller.request.UserUpdateRequest;
import backend_service.controller.response.UserPageResponse;
import backend_service.controller.response.UserResponse;
import backend_service.exception.ResourceNotFoundException;
import backend_service.model.UserEntity;
import backend_service.repository.AddressRepository;
import backend_service.repository.UserRepository;
import backend_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private UserService userService;

  private @Mock UserRepository userRepository;
  private @Mock AddressRepository addressRepository;
  private @Mock PasswordEncoder passwordEncoder;
  private @Mock EmailService emailService;

  private static UserEntity tayJava;
  private static UserEntity johnDoe;

  @BeforeAll
  static void beforeAll() {
    tayJava = new UserEntity();
    tayJava.setId(1L);
    tayJava.setFirstName("Tay");
    tayJava.setLastName("Java");
    tayJava.setGender(Gender.MALE);
    tayJava.setBirthday(new Date());
    tayJava.setEmail("quoctay87@gmail.com");
    tayJava.setPhone("0975118228");
    tayJava.setUsername("tayjava");
    tayJava.setPassword("password");
    tayJava.setType(UserType.USER);
    tayJava.setStatus(UserStatus.ACTIVE);

    johnDoe = new UserEntity();
    johnDoe.setId(2L);
    johnDoe.setFirstName("John");
    johnDoe.setLastName("Doe");
    johnDoe.setGender(Gender.FEMALE);
    johnDoe.setBirthday(new Date());
    johnDoe.setEmail("johndoe@gmail.com");
    johnDoe.setPhone("0123456789");
    johnDoe.setUsername("johndoe");
    johnDoe.setPassword("password");
    johnDoe.setType(UserType.USER);
    johnDoe.setStatus(UserStatus.INACTIVE);
  }

  @BeforeEach
  void setUp() {
    // Khoi tao buoc trien khai la UserService
    userService = new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService);
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void testGetListUsers_Success() {
    // Gia lap phuong thuc
    Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(tayJava, johnDoe));
    when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

    // goi phuong thuc can test
    UserPageResponse result = userService.findAll(null, null, 0,  20);

    assertNotNull(result);
    assertEquals( 2, result.getTotalElements());
  }

  @Test
  void testSearchUser_Success() {

    // Gia lap phuong thuc
    Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(tayJava, johnDoe));
    when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(usePage);

    // goi phuong thuc can test
    UserPageResponse result = userService.findAll("tay",null, 0,  20);

    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
  }

  @Test
  void testGetListUsers_Empty() {

    // Gia lap phuong thuc
    Page<UserEntity> usePage = new PageImpl<>(List.of());
    when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

    // goi phuong thuc can test
    UserPageResponse result = userService.findAll( null,null, 0,  20);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
  }

  @Test
  void testGetUserById_Success() {
    // Gia lap phuong thuc
    when(userRepository.findById(1L)).thenReturn(Optional.of(tayJava));

    UserResponse result = userService.findById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
  }

  @Test
  void testGetUserById_Failure() {
    // Gia lap phuong thuc
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById(2L));
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void findByUsername() {
  }

  @Test
  void findByEmail() {
  }

  @Test
  void testSaveUser_Success() {
    // gia lap phuong thuc
    when(userRepository.save(any(UserEntity.class))).thenReturn(tayJava);

    //tao request
    UserCreationRequest userCreationRequest = new UserCreationRequest();
    userCreationRequest.setFistName("Tay");
    userCreationRequest.setLastName("Java");
    userCreationRequest.setGender(Gender.MALE);
    userCreationRequest.setBirthday(new Date());
    userCreationRequest.setEmail("quoctay87@gmail.com");
    userCreationRequest.setPhone("0975118228");
    userCreationRequest.setUsername("tayjava");

    AddressRequest addressRequest = new AddressRequest();
    addressRequest.setApartmentNumber("ApartmentNumber");
    addressRequest.setFloor("Floor");
    addressRequest.setBuilding("Building");
    addressRequest.setStreetNumber("StreetNumber");
    addressRequest.setStreet("Street");
    addressRequest.setCity("City");
    addressRequest.setCountry("Country");
    addressRequest.setAddressType(1);
    userCreationRequest.setAddresses(List.of(addressRequest));

    long userId = userService.save(userCreationRequest);

    assertEquals(1L, userId);
  }

  @Test
  void update() {
    Long userId = 2L;

    UserEntity updatedUser = new UserEntity();
    updatedUser.setId(userId);
    updatedUser.setFirstName("Jane");
    updatedUser.setLastName("Smith");
    updatedUser.setGender(Gender.FEMALE);
    updatedUser.setBirthday(new Date());
    updatedUser.setEmail("janesmith@gmail.com");
    updatedUser.setPhone("0123456789");
    updatedUser.setUsername("janesmith");
    updatedUser.setType(UserType.USER);
    updatedUser.setStatus(UserStatus.ACTIVE);

    // Giả lập hành vi của UserRepository
    when(userRepository.findById(userId)).thenReturn(Optional.of(johnDoe));
    when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

    UserUpdateRequest updateRequest = new UserUpdateRequest();
    updateRequest.setId(userId);
    updateRequest.setFirstName("Jane");
    updateRequest.setLastName("Smith");
    updateRequest.setGender(Gender.MALE);
    updateRequest.setBirthday(new Date());
    updateRequest.setEmail("janesmith@gmail.com");
    updateRequest.setPhone("0123456789");
    updateRequest.setUsername("janesmith");

    AddressRequest addressRequest = new AddressRequest();
    addressRequest.setApartmentNumber("ApartmentNumber");
    addressRequest.setFloor("Floor");
    addressRequest.setBuilding("Building");
    addressRequest.setStreetNumber("StreetNumber");
    addressRequest.setStreet("Street");
    addressRequest.setCity("City");
    addressRequest.setCountry("Country");
    addressRequest.setAddressType(1);
    updateRequest.setAddresses(List.of(addressRequest));

    userService.update(updateRequest);

    UserResponse result = userService.findById(userId);

    assertNotNull(result);
    assertEquals("Jane", result.getFirstName());
    assertEquals("Smith", result.getLastName());
  }

  @Test
  void changePassword() {
  }

  @Test
  void delete() {
    Long userId = 1L;

    when(userRepository.findById(userId)).thenReturn(Optional.of(tayJava));

    userService.delete(userId);

    assertEquals(UserStatus.INACTIVE, tayJava.getStatus());
    verify(userRepository, times(1)).save(tayJava);
  }
}