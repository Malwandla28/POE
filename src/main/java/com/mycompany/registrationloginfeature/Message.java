/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginfeature;

/**
 *
 * @author Malwandla
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
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
    // JSON storage
    // ---------------------------------------------------------------

    /*
     * Stores this message in the JSON file. Any messages already in the file are
     * read first, so stored messages from earlier runs are kept.
     * Gson usage based on: Google (n.d.) Gson library, GitHub.
     * Available at: https://github.com/google/gson
     * (Replace with the exact page you used, in your institution's referencing style.)
     */
    public void storeMessage() {
        JsonArray array = new JsonArray();

        if (Files.exists(storageFile)) {
            try (Reader reader = Files.newBufferedReader(storageFile, StandardCharsets.UTF_8)) {
                JsonElement existing = JsonParser.parseReader(reader);
                if (existing.isJsonArray()) {
                    array = existing.getAsJsonArray();
                }
            } catch (IOException | RuntimeException e) {
                // File unreadable or empty: start a fresh list
            }
        }

        JsonObject entry = new JsonObject();
        entry.addProperty("messageID", messageID);
        entry.addProperty("messageHash", messageHash);
        entry.addProperty("recipient", recipient);
        entry.addProperty("message", message);
        array.add(entry);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try {
            if (storageFile.getParent() != null) {
                Files.createDirectories(storageFile.getParent());
            }
            Files.write(storageFile, gson.toJson(array).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not write JSON file: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helpers (also used by the unit tests)
    // ---------------------------------------------------------------

    public static void setStorageFile(Path path) { storageFile = path; }

    public static Path getStorageFile() { return storageFile; }

    /** Clears all lists and counters (used between unit tests). */
    public static void clearAll() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public int getMessageNumber() { return messageNumber; }
}