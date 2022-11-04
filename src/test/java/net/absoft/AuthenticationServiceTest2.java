package net.absoft;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

public class AuthenticationServiceTest2 {
    private AuthenticationService authenticationService;


    @BeforeMethod()
    public void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test(groups = "parallel")
    public void testSample() throws InterruptedException {
        Thread.sleep(2000);
        System.out.println("testSample: " + new Date());
        fail("FAILING TEST");
    }
    @Test(description = "Test Successful Authentication",
    groups = "parallel")
    public void testSuccessfulAuthentication() throws InterruptedException {
        Response response = authenticationService.authenticate("user1@test.com", "password1");
        assertEquals(response.getCode(), 200, "Response code should be 200");
        assertTrue(validateToken(response.getMessage()),
                "Token should be the 32 digits string. Got: " + response.getMessage());
        Thread.sleep(2000);
        System.out.println("testSuccessfulAuthentication " + new Date() );
    }
    @DataProvider(name = "invalidLogins", parallel = true)
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
    @Test (dataProvider = "invalidLogins",
            groups = "parallel"
    )
    public void testInvalidAuthentication(String email, String password, Response expectedResponse) throws InterruptedException {
        Response actualResponse = authenticationService.authenticate(email, password);
        assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 400");
        assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(), "Invalid email or password");
        Thread.sleep(2000);
        System.out.println("testInvalidAuthentication" + new Date());
    }
    private boolean validateToken(String token) {
        final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }
}
