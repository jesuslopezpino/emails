import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    private static final String LEADS_JSON_FILE = "test_leads_list.json";
    private static final String EMAIL_HTML_TEMPLATE = "invite_template.html";
    private static final String SENDER_NAME = "Jesus Lopez";  // Replace with your actual first name and last name
    private static final String EMAIL_SUBJECT = "Let Us Create a Professional Website for Your Business";  // Replace with your actual first name and last name



    public static void main(String[] args) {
        try {
            // Load properties from the config.properties file using ClassLoader
            Properties props = new Properties();
            InputStream input = EmailSender.class.getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {
                System.out.println("Sorry, the config.properties file was not found.");
                return;
            }

            // Load the properties
            props.load(input);

            // Read properties from the config file
            final String username = props.getProperty("mail.username");
            final String password = props.getProperty("mail.password");

            // Create session with authentication
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Load leads from the test_leads_list.json file
            List<Lead> leads = loadLeadsFromJson(LEADS_JSON_FILE);

            // Loop through each lead and send personalized emails
            for (Lead lead : leads) {
                // Read the HTML template from the file
                String htmlContent = readHtmlFromClasspath(EMAIL_HTML_TEMPLATE);

                // Replace placeholder with the lead's name
                htmlContent = htmlContent.replace("{{name}}", lead.getName());

                // Create MimeMessage object
                Message message = new MimeMessage(session);// Set the "From" field with your name and email
                message.setFrom(new InternetAddress(username, SENDER_NAME)); // Your email and full name
                // Add the List-Unsubscribe header
                message.addHeader("List-Unsubscribe", "<mailto:" + username +  ">, <https://www.thefreewebsiteteam.com/unsubscribe?email=" + lead.getEmail() + ">");

                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(lead.getEmail()) // Lead's email
                );
                message.setSubject(EMAIL_SUBJECT);

                // Set the content of the email with the personalized HTML content
                message.setContent(htmlContent, "text/html");

                // Send the email
                Transport.send(message);

                System.out.println("Email sent to " + lead.getEmail());
            }

        } catch (MessagingException | IOException e) {
            System.out.println("An error occurred while sending the email:");
            e.printStackTrace();
        }
    }

    // Method to load leads from the JSON file
    private static List<Lead> loadLeadsFromJson(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = EmailSender.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new FileNotFoundException("The file " + fileName + " was not found in the classpath.");
        }

        // Read and parse the JSON file into a list of Lead objects
        return objectMapper.readValue(inputStream, new TypeReference<List<Lead>>() {});
    }

    // Method to read HTML content from a file in the classpath
    private static String readHtmlFromClasspath(String fileName) throws IOException {
        try (InputStream inputStream = EmailSender.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("The file " + fileName + " was not found in the classpath.");
            }
            return new String(inputStream.readAllBytes(), "UTF-8");
        }
    }
}
