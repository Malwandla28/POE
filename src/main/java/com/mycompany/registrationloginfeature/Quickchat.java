/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Malwandla
 */

package com.mycompany.registrationloginfeature;

import java.util.List;
import java.util.Scanner;

/**
 * QuickChat menu: Part 2 (sending messages) and Part 3 (stored messages).
 */
public class Quickchat {

    public static void run(Scanner scanner, String sender) {
        System.out.println("\nWelcome to QuickChat.");

        // Part 3: read the stored messages from the JSON file into the arrays
        MessageManager manager = new MessageManager();
        int loaded = manager.loadStoredMessages(Message.getStorageFile());
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from file.");
        }

        int limit = readInt(scanner, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int entered = 0;        // also the message number (starts at 0)
        Message last = null;    // used to read the running total

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt(scanner, "Choose an option: ", 1, 4);

            switch (option) {
                case 1:
                    if (entered >= limit) {
                        System.out.println("You have already entered all " + limit + " messages.");
                        break;
                    }
                    while (entered < limit) {
                        last = sendOneMessage(scanner, entered, manager);
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
                case 4:
                    storedMessagesMenu(scanner, manager, sender);
                    break;
                default:
                    break;
            }
        }
    }

    // ---------------------------------------------------------------
    // Part 3: Stored Messages menu
    // ---------------------------------------------------------------

    private static void storedMessagesMenu(Scanner scanner, MessageManager manager, String sender) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages to a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back");
            int option = readInt(scanner, "Choose an option: ", 1, 7);

            switch (option) {
                case 1:
                    System.out.println(manager.displaySenderAndRecipients(sender));
                    break;
                case 2:
                    System.out.println("Longest stored message: " + manager.getLongestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.println("Recipient: " + manager.getRecipientByID(id));
                    System.out.println("Message: " + manager.searchByMessageID(id));
                    break;
                case 4:
                    System.out.print("Enter the recipient's number: ");
                    String recipient = scanner.nextLine().trim();
                    List<String> found = manager.searchByRecipient(recipient);
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String text : found) {
                        System.out.println("- " + text);
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(manager.deleteByHash(hash));
                    break;
                case 6:
                    System.out.println(manager.generateReport(MessageManager.STORED));
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    // ---------------------------------------------------------------
    // Part 2: sending one message
    // ---------------------------------------------------------------

    private static Message sendOneMessage(Scanner scanner, int messageNumber, MessageManager manager) {
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

        // Part 3: add the message to the arrays that match the user's choice
        if (choice == 1) {
            manager.addMessage(msg, MessageManager.SENT);
            System.out.println(msg.printMessage());
        } else if (choice == 2) {
            manager.addMessage(msg, MessageManager.DISREGARDED);
            String answer = scanner.nextLine().trim();
            System.out.println(answer.equals("0") ? "Message deleted." : "Message disregarded.");
        } else {
            manager.addMessage(msg, MessageManager.STORED);
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