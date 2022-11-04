package net.absoft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest {

  private String message;
  private AuthenticationService authenticationService;
  public AuthenticationServiceTest (String message) {
    this.message=message;
  }
  @BeforeMethod()
  public void setUp() {
    authenticationService = new AuthenticationService();
  }

  @Test(groups = "positive")
  @Parameters ({"email-address", "password"})
  public void testSuccessfulAuthentication(String email, String password) {
    Response response = new AuthenticationService().authenticate(email, password);
    assertEquals(response.getCode(), 200, "Response code should be 200");
    assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
    System.out.println("testSuccessfulAuthentication = " + message);
  }
@DataProvider(name = "invalidLogins")
  public Object [][] invalidLogins(){
    return new Object[][]{
         new Object[] {"user1@test", "wrong_password1",
                 new Response(401, "Invalid email or password")},
         new Object[] {"", "password1",
                 new Response(400, "Email should not be empty string")},
            new Object[] {"user1@test", "",
                    new Response(400, "Password should not be empty string")},
            new Object[] {"user1", "password1",
                    new Response(400, "Invalid email")}
    };
  }
  @Test (
          groups = "negative",
          dataProvider = "invalidLogins"
  )
  public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse = authenticationService.authenticate(email, password);
    assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 400");
    assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),  "Invalid email or password");
    System.out.println("testInvalidAuthentication");
  }

  @Test (groups = "negative")
  public void testAuthenticationWithWrongPassword() {
    Response response = authenticationService
            .authenticate("user1@test.com", "wrong_password1");
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(response.getCode(), 401, "Response code should be 401");
    sa.assertEquals(response.getMessage(), "Invalid email or password",
        "Response message should be \"Invalid email or password\"");
   sa.assertAll();
    System.out.println("testAuthenticationWithWrongPassword");
  }

  @Test (groups = "negative")
  public void testAuthenticationWithEmptyEmail() {
    Response expectedResponse = new Response(400, "Email should not be empty string");
    Response actualResponse = authenticationService.authenticate("", "password1");
    assertEquals(actualResponse, expectedResponse, "Unexpected response");
    System.out.println("testAuthenticationWithEmptyEmail");


  }

  @Test (groups = "negative")
  public void testAuthenticationWithInvalidEmail() {
    Response response = authenticationService.authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 200");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
    System.out.println("testAuthenticationWithInvalidEmail");
  }

  @Test (groups = "negative")
  public void testAuthenticationWithEmptyPassword() {
    Response response = authenticationService.authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
    System.out.println("testAuthenticationWithEmptyPassword");
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
