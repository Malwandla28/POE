/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */
import java.util.regex.Pattern;
 
/**
 * Login class: registration validation and login verification.
 *
 * @author Malwandla
 */
public class Login {
 
    private String username;
    private String password;
    private String cellPhone;
    private String firstName;
    private String lastName;
 
    
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");
 
    public Login() {
    }
 
    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
 
    public void setNames(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
 
    // ---------------- Validation ----------------
 
    // Username must contain an underscore and be no more than five characters long
    public boolean checkUserName(String user) {
        return user != null && user.contains("_") && user.length() <= 5;
    }
 
    // Password: 8+ characters, a capital letter, a number and a special character
    public boolean checkPasswordComplexity(String pass) {
        if (pass == null || pass.length() < 8) {
            return false;
        }
 
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
 
        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            }
            if (Character.isDigit(c)) {
                hasNumber = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }
 
    // Cell number must contain the international code and be the correct length
    public boolean checkCellPhoneNumber(String cell) {
        return cell != null && CELL_PATTERN.matcher(cell).matches();
    }
 
    // ---------------- Messages ----------------
 
    public String returnUserNameMessage(String user) {
        if (checkUserName(user)) {
            return "Username successfully captured.";
        }
        return "Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.";
    }
 
    public String returnPasswordMessage(String pass) {
        if (checkPasswordComplexity(pass)) {
            return "Password successfully captured.";
        }
        return "Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a special character.";
    }
 
    public String returnCellPhoneMessage(String cell) {
        if (checkCellPhoneNumber(cell)) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.";
    }
 
    // ---------------- Registration and login ----------------
 
    // Returns the first problem found, or a success message once everything is valid
    public String registerUser(String user, String pass, String cell) {
        if (!checkUserName(user)) {
            return returnUserNameMessage(user);
        }
        if (!checkPasswordComplexity(pass)) {
            return returnPasswordMessage(pass);
        }
        if (!checkCellPhoneNumber(cell)) {
            return returnCellPhoneMessage(cell);
        }
 
        this.username = user;
        this.password = pass;
        this.cellPhone = cell;
 
        return returnUserNameMessage(user) + "\n"
                + returnPasswordMessage(pass) + "\n"
                + returnCellPhoneMessage(cell) + "\n"
                + "User has been registered successfully.";
    }
 
    // Checks the login details match the details stored at registration
    public boolean loginUser(String user, String pass) {
        return username != null && password != null
                && username.equals(user) && password.equals(pass);
    }
 
    // Returns the login status message
    public String returnLoginStatus(String user, String pass) {
        if (loginUser(user, pass)) {
            return "Welcome " + firstName + " ," + lastName + " it is great to see you.";
        }
        return "Username or password incorrect, please try again.";
    }
 
    public String getCellPhone() {
        return cellPhone;
    }
}
 