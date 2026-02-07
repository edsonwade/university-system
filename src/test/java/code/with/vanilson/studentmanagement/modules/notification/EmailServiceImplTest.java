package code.with.vanilson.studentmanagement.modules.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailServiceImpl Unit Tests")
class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String TEST_TO = "test@example.com";
    private final String TEST_SUBJECT = "Test Subject";
    private final String TEST_TEXT = "Test email content";
    private final String TEST_FROM = "your-outlook-email@outlook.com";

    @BeforeEach
    void setUp() {
        // No specific setup needed as we're testing a simple service
    }

    @Test
    @DisplayName("Should send simple message successfully")
    void sendSimpleMessage_Success() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, TEST_TEXT));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle mail send exception gracefully")
    void sendSimpleMessage_MailSendException_LogsError() {
        // Given
        doThrow(new MailSendException("Mail server error"))
                .when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, TEST_TEXT));
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle runtime exception gracefully")
    void sendSimpleMessage_RuntimeException_LogsError() {
        // Given
        doThrow(new RuntimeException("Unexpected error"))
                .when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, TEST_TEXT));
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with null subject")
    void sendSimpleMessage_NullSubject_Success() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, null, TEST_TEXT));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with null text")
    void sendSimpleMessage_NullText_Success() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, null));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with empty subject")
    void sendSimpleMessage_EmptySubject_Success() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, "", TEST_TEXT));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with empty text")
    void sendSimpleMessage_EmptyText_Success() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, ""));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with very long content")
    void sendSimpleMessage_VeryLongContent_Success() {
        // Given
        String longContent = "A".repeat(10000); // Very long content
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, longContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with special characters")
    void sendSimpleMessage_SpecialCharacters_Success() {
        // Given
        String specialSubject = "Test Subject with émojis 🎉 and special chars: !@#$%^&*()";
        String specialText = "Email content with special characters: àáâãäåæçèéêë ñòóôõö ùúûüý ÿ 🚀 🌟 💻";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, specialSubject, specialText));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with HTML-like content")
    void sendSimpleMessage_HtmlLikeContent_Success() {
        // Given
        String htmlContent = "<h1>Hello World</h1><p>This is <strong>HTML</strong> content.</p>";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, htmlContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message to multiple recipients (comma-separated)")
    void sendSimpleMessage_MultipleRecipients_Success() {
        // Given
        String multipleRecipients = "user1@example.com,user2@example.com,user3@example.com";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(multipleRecipients, TEST_SUBJECT, TEST_TEXT));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with newline characters")
    void sendSimpleMessage_NewlineCharacters_Success() {
        // Given
        String multilineText = "Line 1\nLine 2\r\nLine 3\rLine 4";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, multilineText));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with tab characters")
    void sendSimpleMessage_TabCharacters_Success() {
        // Given
        String tabbedText = "Column1\tColumn2\tColumn3\nValue1\tValue2\tValue3";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, tabbedText));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with Unicode characters")
    void sendSimpleMessage_UnicodeCharacters_Success() {
        // Given
        String unicodeText = "Unicode test: 中文 русский العربية हिन्दी עברית 日本語 한국어";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, unicodeText));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with email addresses in content")
    void sendSimpleMessage_EmailAddressesInContent_Success() {
        // Given
        String emailContent = "Contact us at support@example.com or admin@example.org for help.";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, emailContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with URLs in content")
    void sendSimpleMessage_UrlsInContent_Success() {
        // Given
        String urlContent = "Visit our website at https://www.example.com or http://test.org for more info.";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, urlContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with numeric content")
    void sendSimpleMessage_NumericContent_Success() {
        // Given
        String numericContent = "Invoice #12345\nAmount: $99.99\nTax: $8.99\nTotal: $108.98";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, numericContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with JSON-like content")
    void sendSimpleMessage_JsonLikeContent_Success() {
        // Given
        String jsonContent = "{\"name\":\"John\",\"age\":30,\"email\":\"john@example.com\"}";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, jsonContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with XML-like content")
    void sendSimpleMessage_XmlLikeContent_Success() {
        // Given
        String xmlContent = "<user><name>John</name><age>30</age><email>john@example.com</email></user>";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, xmlContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with SQL-like content")
    void sendSimpleMessage_SqlLikeContent_Success() {
        // Given
        String sqlContent = "SELECT * FROM users WHERE email = 'john@example.com' AND active = 1;";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, sqlContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with JavaScript-like content")
    void sendSimpleMessage_JavascriptLikeContent_Success() {
        // Given
        String jsContent = "function greet(name) { return 'Hello, ' + name + '!'; } console.log(greet('World'));";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, jsContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with CSS-like content")
    void sendSimpleMessage_CssLikeContent_Success() {
        // Given
        String cssContent = ".email { font-family: Arial; color: #333; margin: 10px; } .header { font-weight: bold; }";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, cssContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle null recipient gracefully")
    void sendSimpleMessage_NullRecipient_HandlesGracefully() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(null, TEST_SUBJECT, TEST_TEXT));
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle empty recipient gracefully")
    void sendSimpleMessage_EmptyRecipient_HandlesGracefully() {
        // Given
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When & Then
        assertDoesNotThrow(() -> emailService.sendSimpleMessage("", TEST_SUBJECT, TEST_TEXT));
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with whitespace-only content")
    void sendSimpleMessage_WhitespaceOnlyContent_Success() {
        // Given
        String whitespaceContent = "   \n\t   \r\n   \t   ";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(TEST_TO, TEST_SUBJECT, whitespaceContent));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send message with mixed case email")
    void sendSimpleMessage_MixedCaseEmail_Success() {
        // Given
        String mixedCaseEmail = "Test.User@EXAMPLE.COM";
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // When
        assertDoesNotThrow(() -> emailService.sendSimpleMessage(mixedCaseEmail, TEST_SUBJECT, TEST_TEXT));

        // Then
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}