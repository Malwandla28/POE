/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registrationloginfeature;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Part 3: holds the message arrays and the features that use them.
 *
 * The five required arrays are:
 *   sentMessages, disregardedMessages, storedMessages, messageHashes, messageIDs.
 * messageHashes, messageIDs, recipients, messageTexts and messageFlags are
 * PARALLEL arrays: index 0 in each describes the same message, so a search in one
 * array gives the matching value from the others.
 */
public class MessageManager {

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARDED = "Disregarded";

    // The three category arrays (message text only)
    private final List<String> sentMessages = new ArrayList<>();
    private final List<String> disregardedMessages = new ArrayList<>();
    private final List<String> storedMessages = new ArrayList<>();

    // Parallel arrays: one entry per message, all in the same order
    private final List<String> messageIDs = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> recipients = new ArrayList<>();
    private final List<String> messageTexts = new ArrayList<>();
    private final List<String> messageFlags = new ArrayList<>();

    // ---------------------------------------------------------------
    // Populating the arrays
    // ---------------------------------------------------------------

    /** Adds a message to the correct category array and to the parallel arrays. */
    public void addMessage(String id, String hash, String recipient, String text, String flag) {
        switch (flag) {
            case SENT:
                sentMessages.add(text);
                break;
            case STORED:
                storedMessages.add(text);
                break;
            case DISREGARDED:
                disregardedMessages.add(text);
                break;
            default:
                return; // unknown flag: nothing is added
        }
        messageIDs.add(id);
        messageHashes.add(hash);
        recipients.add(recipient);
        messageTexts.add(text);
        messageFlags.add(flag);
    }

    public void addMessage(Message m, String flag) {
        addMessage(m.getMessageID(), m.getMessageHash(), m.getRecipient(), m.getMessage(), flag);
    }

    /*
     * Reads the stored messages from the JSON file into the arrays.
     * Gson usage based on: Google (n.d.) Gson library, GitHub.
     * Available at: https://github.com/google/gson
     * (Replace with the exact page you used, in your institution's referencing style.)
     * Returns how many messages were loaded.
     */
    public int loadStoredMessages(Path file) {
        if (file == null || !Files.exists(file)) {
            return 0;
        }
        int loaded = 0;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                return 0;
            }
            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject entry = element.getAsJsonObject();
                addMessage(entry.get("messageID").getAsString(),
                        entry.get("messageHash").getAsString(),
                        entry.get("recipient").getAsString(),
                        entry.get("message").getAsString(),
                        STORED);
                loaded++;
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read the JSON file: " + e.getMessage());
        }
        return loaded;
    }

    // ---------------------------------------------------------------
    // Stored Messages menu features
    // ---------------------------------------------------------------

    /** a) Sender and recipient of every stored message. */
    public String displaySenderAndRecipients(String sender) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals(STORED)) {
                sb.append("Sender: ").append(sender)
                  .append(" | Recipient: ").append(recipients.get(i)).append("\n");
            }
        }
        return sb.length() == 0 ? "No stored messages." : sb.toString().trim();
    }

    /** b) The longest stored message. */
    public String getLongestStoredMessage() {
        String longest = "";
        for (String text : storedMessages) {
            if (text.length() > longest.length()) {
                longest = text;
            }
        }
        return longest.isEmpty() ? "No stored messages." : longest;
    }

    /** c) Message text for a message ID. */
    public String searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        return index == -1 ? "Message ID not found." : messageTexts.get(index);
    }

    /** c) Recipient for a message ID. */
    public String getRecipientByID(String id) {
        int index = messageIDs.indexOf(id);
        return index == -1 ? "Message ID not found." : recipients.get(index);
    }

    /** d) All sent or stored messages for one recipient. */
    public List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (int i = 0; i < recipients.size(); i++) {
            boolean notDisregarded = !messageFlags.get(i).equals(DISREGARDED);
            if (notDisregarded && recipients.get(i).equals(recipient)) {
                found.add(messageTexts.get(i));
            }
        }
        return found;
    }

    /** e) Deletes a message using its hash (removed from every array). */
    public String deleteByHash(String hash) {
        int index = -1;
        for (int i = 0; i < messageHashes.size(); i++) {
            if (messageHashes.get(i).equalsIgnoreCase(hash)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return "Message hash not found.";
        }

        String text = messageTexts.get(index);
        String flag = messageFlags.get(index);

        if (flag.equals(SENT)) {
            sentMessages.remove(text);
        } else if (flag.equals(STORED)) {
            storedMessages.remove(text);
        } else {
            disregardedMessages.remove(text);
        }

        messageIDs.remove(index);
        messageHashes.remove(index);
        recipients.remove(index);
        messageTexts.remove(index);
        messageFlags.remove(index);

        return "Message: \"" + text + "\" successfully deleted.";
    }

    /** f) Full details of every message with the given flag (Sent, Stored or Disregarded). */
    public String generateReport(String flag) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals(flag)) {
                sb.append("Message ID: ").append(messageIDs.get(i)).append("\n")
                  .append("Message Hash: ").append(messageHashes.get(i)).append("\n")
                  .append("Recipient: ").append(recipients.get(i)).append("\n")
                  .append("Message: ").append(messageTexts.get(i)).append("\n\n");
            }
        }
        return sb.length() == 0 ? "No " + flag.toLowerCase() + " messages." : sb.toString().trim();
    }

    // ---------------------------------------------------------------
    // Getters (copies, so the arrays can't be changed from outside)
    // ---------------------------------------------------------------

    public List<String> getSentMessages() { return new ArrayList<>(sentMessages); }
    public List<String> getDisregardedMessages() { return new ArrayList<>(disregardedMessages); }
    public List<String> getStoredMessages() { return new ArrayList<>(storedMessages); }
    public List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }
}
