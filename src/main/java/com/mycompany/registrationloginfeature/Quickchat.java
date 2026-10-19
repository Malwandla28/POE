/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Malwandla
 */

package com.mycompany.registrationloginfeature;

import java.util.Scanner;

/**
 * Part 2: QuickChat menu and message sending.
 */
public class Quickchat {

    public static void run(Scanner scanner) {
        System.out.println("\nWelcome to QuickChat.");

        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int entered = 0;        // also the message number (starts at 0)
        Message last = null;    // used to read the running total

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readInt(scanner, "Choose an option: ", 1, 3);

            switch (option) {
                case 1:
                    if (entered >= limit) {
                        System.out.println("You have already entered all " + limit + " messages.");
                        break;
                    }
                    while (entered < limit) {
                        last = sendOneMessage(scanner, entered);
                        entered++;
                    }
                    System.out.println("\nTotal messages sent: " + last.returnTotalMessages());
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    break;
            }
        }
    }

    private static Message sendOneMessage(Scanner scanner, int messageNumber) {
        System.out.println("\n--- Message " + (messageNumber + 1) + " ---");

        // Recipient: loop until valid
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (e.g. +27718693002): ");
            recipient = scanner.nextLine().trim();
            String result = new Message(messageNumber, recipient, "check cell").checkRecipientCell();
            System.out.println(result);
            if (result.equals("Cell phone number successfully captured.")) {
                break;
            }
        }

        // Message text: loop until 250 characters or fewer
        Message msg;
        while (true) {
            System.out.print("Enter your message: ");
            String text = scanner.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }
            msg = new Message(messageNumber, recipient, text);
            String result = msg.checkMessageLength();
            if (result.equals("Message ready to send.")) {
                System.out.println("Message sent");
                break;
            }
            System.out.println("Please enter a message of less than 250 characters.");
            System.out.println(result);
        }

        System.out.println(msg.returnMessageIDMessage());

        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice = readInt(scanner, "Choose an option: ", 1, 3);

        System.out.println(msg.sentMessage(choice));

        if (choice == 1) {
            System.out.println(msg.printMessage());
        } else if (choice == 2) {
            String answer = scanner.nextLine().trim();
            System.out.println(answer.equals("0") ? "Message deleted." : "Message disregarded.");
        }
        return msg;
    }

    /** Reads a whole number between min and max, repeating until valid. */
    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through
            }
            System.out.println("Please enter a valid number.");
        }
    }
}