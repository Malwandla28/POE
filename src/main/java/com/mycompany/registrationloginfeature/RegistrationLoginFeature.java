/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */
import java.util.Scanner;
 
/**
 * @author Malwandla
 */
public class RegistrationLoginFeature {
 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();
 
        System.out.println("=== REGISTRATION ===");
 
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
 
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        login.setNames(firstName, lastName);
 
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
 
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
 
        System.out.print("Enter cell phone number: ");
        String cell = scanner.nextLine();
 
        // Register
        String result = login.registerUser(username, password, cell);
        System.out.println(result);
 
        // Only try to login if registration was successful
        if (result.contains("registered successfully")) {
            System.out.println("\n=== LOGIN ===");
 
            System.out.print("Enter username: ");
            String loginUser = scanner.nextLine();
 
            System.out.print("Enter password: ");
            String loginPass = scanner.nextLine();
 
            boolean loggedIn = login.loginUser(loginUser, loginPass);
            System.out.println(login.returnLoginStatus(loginUser, loginPass));
 
            // Part 2: only logged-in users can send messages
            if (loggedIn) {
                Quickchat.run(scanner);
            }
        }
 
        scanner.close();
    }
}
 