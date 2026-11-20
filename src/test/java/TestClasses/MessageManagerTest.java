/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestClasses;

import com.mycompany.registrationloginfeature.Message;
import com.mycompany.registrationloginfeature.MessageManager;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 unit tests, using the Test Data from the brief.
 */
public class MessageManagerTest {

    // Test data messages 1 to 5
    private static final String MSG_1 = "Did you get the cake?";
    private static final String MSG_2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG_3 = "Yohoooo, I am at your gate.";
    private static final String MSG_4 = "It is dinner time!";
    private static final String MSG_5 = "Ok, I am leaving without you.";

    private static final String RECIPIENT_2_AND_5 = "+27838884567";
    private static final String MSG_4_ID = "0838884567"; // developer entry for message 4

    private MessageManager manager;

    @BeforeEach
    public void setUp() {
        manager = new MessageManager();
        add("1000000001", 0, "+27834557896", MSG_1, MessageManager.SENT);
        add("1000000002", 1, RECIPIENT_2_AND_5, MSG_2, MessageManager.STORED);
        add("1000000003", 2, "+27834484567", MSG_3, MessageManager.DISREGARDED);
        add(MSG_4_ID, 3, "0838884567", MSG_4, MessageManager.SENT);
        add("1000000005", 4, RECIPIENT_2_AND_5, MSG_5, MessageManager.STORED);
    }

    /** Builds a Message (so the hash is generated) and adds it to the arrays. */
    private void add(String id, int number, String recipient, String text, String flag) {
        manager.addMessage(new Message(id, number, recipient, text), flag);
    }

    // ---------------- Arrays correctly populated ----------------

    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals(List.of(MSG_1, MSG_4), manager.getSentMessages());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        assertEquals(List.of(MSG_3), manager.getDisregardedMessages());
    }

    @Test
    public void testStoredMessagesArrayPopulated() {
        assertEquals(List.of(MSG_2, MSG_5), manager.getStoredMessages());
    }

    @Test
    public void testMessageIDsArrayPopulated() {
        assertEquals(List.of("1000000001", "1000000002", "1000000003", MSG_4_ID, "1000000005"),
                manager.getMessageIDs());
    }

    @Test
    public void testMessageHashesArrayPopulated() {
        assertEquals(List.of("10:0:DIDCAKE", "10:1:WHERETIME", "10:2:YOHOOOOGATE",
                "08:3:ITTIME", "10:4:OKYOU"), manager.getMessageHashes());
    }

    // ---------------- Features ----------------

    @Test
    public void testDisplayLongestMessage() {
        assertEquals(MSG_2, manager.getLongestStoredMessage());
    }

    @Test
    public void testSearchByMessageID() {
        assertEquals("It is dinner time!", manager.searchByMessageID("0838884567"));
        assertEquals("0838884567", manager.getRecipientByID("0838884567"));
    }

    @Test
    public void testSearchByMessageIDNotFound() {
        assertEquals("Message ID not found.", manager.searchByMessageID("9999999999"));
    }

    @Test
    public void testSearchByRecipient() {
        assertEquals(List.of(MSG_2, MSG_5), manager.searchByRecipient("+27838884567"));
    }

    @Test
    public void testSearchByRecipientIgnoresDisregarded() {
        assertTrue(manager.searchByRecipient("+27834484567").isEmpty());
    }

    @Test
    public void testDeleteByHash() {
        String expected = "Message: \"" + MSG_2 + "\" successfully deleted.";
        assertEquals(expected, manager.deleteByHash("10:1:WHERETIME"));
        assertEquals(List.of(MSG_5), manager.getStoredMessages());
        assertEquals(4, manager.getMessageIDs().size());
    }

    @Test
    public void testDeleteByHashNotFound() {
        assertEquals("Message hash not found.", manager.deleteByHash("00:0:NOPE"));
    }

    @Test
    public void testDisplaySenderAndRecipients() {
        String expected = "Sender: Kyle Smith | Recipient: +27838884567\n"
                + "Sender: Kyle Smith | Recipient: +27838884567";
        assertEquals(expected, manager.displaySenderAndRecipients("Kyle Smith"));
    }

    @Test
    public void testReportShowsSentMessages() {
        String report = manager.generateReport(MessageManager.SENT);
        assertTrue(report.contains("10:0:DIDCAKE"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(MSG_1));
        assertTrue(report.contains("08:3:ITTIME"));
        assertTrue(report.contains(MSG_4));
        assertFalse(report.contains(MSG_2));
    }

    @Test
    public void testReportShowsStoredMessages() {
        String report = manager.generateReport(MessageManager.STORED);
        assertTrue(report.contains("10:1:WHERETIME"));
        assertTrue(report.contains(RECIPIENT_2_AND_5));
        assertTrue(report.contains(MSG_2));
        assertTrue(report.contains(MSG_5));
    }

    // ---------------- JSON file read into the stored array ----------------

    @Test
    public void testLoadStoredMessagesFromJson(@TempDir Path tempDir) {
        Message.setStorageFile(tempDir.resolve("messages.json"));
        new Message("2000000001", 0, "+27834557896", MSG_1).storeMessage();
        new Message("2000000002", 1, RECIPIENT_2_AND_5, MSG_2).storeMessage();

        MessageManager fresh = new MessageManager();
        int loaded = fresh.loadStoredMessages(Message.getStorageFile());

        assertEquals(2, loaded);
        assertEquals(List.of(MSG_1, MSG_2), fresh.getStoredMessages());
        assertEquals(List.of("2000000001", "2000000002"), fresh.getMessageIDs());
    }

    @Test
    public void testLoadStoredMessagesMissingFile(@TempDir Path tempDir) {
        assertEquals(0, new MessageManager().loadStoredMessages(tempDir.resolve("none.json")));
    }
}
