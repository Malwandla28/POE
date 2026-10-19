/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */



import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Message class for QuickChat.
 * Each Message has an ID, a message number, a recipient, the message text and a hash.
 */
public class Message {

    // Messages sent / stored while the program is running (shared by all Message objects)
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static Path storageFile = Paths.get("messages.json");

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;

    /** Creates a message with an auto-generated 10-digit ID. */
    public Message(int messageNumber, String recipient, String message) {
        this(generateMessageID(), messageNumber, recipient, message);
    }

    /** Creates a message with a supplied ID (used for unit testing). */
    public Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    // ---------------------------------------------------------------
    // Validation
    // ---------------------------------------------------------------

    /** Random ten-digit number, stored as text so leading zeros are kept. */
    private static String generateMessageID() {
        long id = ThreadLocalRandom.current().nextLong(10_000_000_000L);
        return String.format("%010d", id);
    }

    /** Message ID must not be more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Message ID confirmation message. */
    public String returnMessageIDMessage() {
        return "Message ID generated: " + messageID;
    }

    /**
     * Recipient must be no more than ten characters after the international code
     * and start with the code. Re-uses the cell phone check from the Login class (Part 1).
     */
    public String checkRecipientCell() {
        if (new Login().checkCellPhoneNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /** Message must not be more than 250 characters. */
    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        }
        int extra = message.length() - 250;
        return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
    }

    // ---------------------------------------------------------------
    // Hash
    // ---------------------------------------------------------------

    /** First two digits of the ID : message number : FIRSTWORDLASTWORD (all caps). */
    public String createMessageHash() {
        String[] words = message.trim().split("\\s+");
        String first = cleanWord(words[0]);
        String last = cleanWord(words[words.length - 1]);
        String idStart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (idStart + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Removes punctuation such as "?" or "," from a word. */
    private static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---------------------------------------------------------------
    // Sending, storing and displaying
    // ---------------------------------------------------------------

    /**
     * 1 = send, 2 = disregard, 3 = store for later.
     * Returns the message to display for the chosen option.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Full details of this message: ID, hash, recipient, message. */
    public String printMessage() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    /** All messages sent while the program is running. */
    public String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.printMessage()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /** Total number of messages sent. */
    public int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ---------------------------------------------------------------
    // JSON storage (written by hand, so no external library is needed)
    // ---------------------------------------------------------------

    /** Adds this message to the stored list and rewrites the JSON file. */
    public void storeMessage() {
        storedMessages.add(this);
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.message)).append("\"\n")
                .append("  }");
            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]\n");

        try {
            if (storageFile.getParent() != null) {
                Files.createDirectories(storageFile.getParent());
            }
            Files.write(storageFile, json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not write JSON file: " + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // ---------------------------------------------------------------
    // Helpers (also used by the unit tests)
    // ---------------------------------------------------------------

    public static void setStorageFile(Path path) { storageFile = path; }

    /** Clears all lists and counters (used between unit tests). */
    public static void clearAll() {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public int getMessageNumber() { return messageNumber; }
}