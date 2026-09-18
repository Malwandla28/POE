/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */
public class Login {

    private String username;
    private String password;
    private String firstName;
    private String lastName;

    // Check username: must contain _ and be 5 characters or less
    public boolean checkUserName(String user) {
        return user.contains("_") && user.length() <= 5;
    }

    // Check password complexity
    public boolean checkPasswordComplexity(String pass) {
        if (pass.length() < 8) {
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

    // Check cell phone number (must start with +27)
    public boolean checkCellPhoneNumber(String cell) {
        return cell.matches("^\\+27[0-9]{1,10}$");
    }

    // Register the user
    public String registerUser(String user, String pass, String cell) {

        if (!checkUserName(user)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(pass)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        // Save the details
        this.username = user;
        this.password = pass;

        // Split username to get first and last name (example: kyl_1)
        String[] parts = user.split("_");
        this.firstName = parts[0];
        this.lastName = parts[1];

        return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.";
    }

    // Check if login details are correct
    public boolean loginUser(String user, String pass) {
        return user.equals(this.username) && pass.equals(this.password);
    }

    // Return the login message
    public String returnLoginStatus(String user, String pass) {
        if (loginUser(user, pass)) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
