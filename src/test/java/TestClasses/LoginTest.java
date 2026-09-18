/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package TestClasses;

import com.mycompany.registrationloginfeature.Login;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Malwandla
 */
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    Login login = new Login();

    @Test
    public void testCheckUserName() {
        assertTrue(login.checkUserName("kyl_1"));
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testCheckPasswordComplexity() {
        assertTrue(login.checkPasswordComplexity("Ch&sec@ke99!"));
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumber() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testLoginUser() {
        login.registerUser("kyl_1", "Ch&sec@ke99!", "+27838968976");

        assertTrue(login.loginUser("kyl_1", "Ch&sec@ke99!"));
        assertFalse(login.loginUser("kyl_1", "wrongpassword"));
    }
}