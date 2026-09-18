/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */
import java.util.Scanner;

public class RegistrationLoginFeature {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("=== REGISTRATION ===");

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
        if (result.contains("successfully")) {
            System.out.println("\n=== LOGIN ===");

            System.out.print("Enter username: ");
            String loginUser = scanner.nextLine();

            System.out.print("Enter password: ");
            String loginPass = scanner.nextLine();

            System.out.println(login.returnLoginStatus(loginUser, loginPass));
        }

        scanner.close();
    }
}
