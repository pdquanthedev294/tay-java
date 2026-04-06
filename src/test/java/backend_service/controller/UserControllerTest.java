package backend_service.controller;

import backend_service.common.Gender;
import backend_service.controller.response.UserPageResponse;
import backend_service.controller.response.UserResponse;
import backend_service.service.JwtService;
import backend_service.service.UserService;
import backend_service.service.UserServiceDetail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
public class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
//  @MockBean
  private UserService userService;

  @MockitoBean
  private UserServiceDetail userServiceDetail;

  @MockitoBean
  private JwtService jwtService; // Mocked bean for tests

  private static UserResponse tayJava;
  private static UserResponse johnDoe;

  @BeforeAll
  static void setUp() {
    // Chuẩn bị dữ liệu
    tayJava = new UserResponse();
    tayJava.setId(1L);
    tayJava.setFirstName("Tay");
    tayJava.setLastName("Java");
    tayJava.setGender(Gender.MALE);
    tayJava.setBirthday(new Date());
    tayJava.setEmail("quoctay87@gmail.com");
    tayJava.setPhone("0975118228");
    tayJava.setUsername("tayjava");

    johnDoe = new UserResponse();
    johnDoe.setId(2L);
    johnDoe.setFirstName("John");
    johnDoe.setLastName("Doe");
    johnDoe.setGender(Gender.FEMALE);
    johnDoe.setBirthday(new Date());
    johnDoe.setEmail("johndoe@gmail.com");
    johnDoe.setPhone("0123456789");
    johnDoe.setUsername("johndoe");
  }

  @Test
  @WithMockUser(authorities = {"admin", "manager"})
  void testGetUser() throws Exception {
    List<UserResponse> userResponses = List.of(tayJava, johnDoe);

    UserPageResponse userPageResponse = new UserPageResponse();
    userPageResponse.setPageNumber(0);
    userPageResponse.setPageSize(20);
    userPageResponse.setTotalPages(1);
    userPageResponse.setTotalElements(2);
    userPageResponse.setUsers(userResponses);

    when(userService.findAll( null,  null, 0,  20)).thenReturn(userPageResponse);

    mockMvc.perform(get( "/user/list").contentType(String.valueOf(MediaType.APPLICATION_JSON)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status", is(200)))
           .andExpect(jsonPath("$.message", is("users")))
           .andExpect(jsonPath("$.data.totalElements", is(2)));
  }
}
