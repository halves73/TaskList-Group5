import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.service.TodoUserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ToDoUserServiceTest {
    @Mock
    UserRepository mockRepo = mock(UserRepository.class);
    TodoUserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        userService = new TodoUserService(mockRepo);
    }



    @Test
    void login_shouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception {

//  Arrange: Set up test data and environment
        String username = "user5";
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.update("password5".getBytes());
        byte[] hashedPassword = md.digest();
        String password = new String(Base64.getEncoder().encode(hashedPassword));
        User user = new User(1, username, password);
        when(mockRepo.findByUsername(username)).thenReturn(user);

//  Act: Perform the action under test
        User userAdd = userService.addUser(username, "password5");
        when(mockRepo.findByUsername(username)).thenReturn(userAdd);
        String authToken = userService.login(username,"password5");

//  Assert: Verify the results
    assertNotNull(authToken);
    assertTrue(authToken.startsWith("Bearer "));

    String jwtToken = authToken.substring(7);

    DecodedJWT jwt = JWT.decode(jwtToken);

    assertEquals(username, jwt.getClaim("username").asString());
    assertEquals("auth0", jwt.getIssuer());
    }
}
