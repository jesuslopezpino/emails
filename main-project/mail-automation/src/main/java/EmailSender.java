import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void main(String[] args) {
        try {
            // Cargar las propiedades desde el classpath usando ClassLoader y Files
            Properties props = new Properties();
            InputStream input = EmailSender.class.getClassLoader().getResourceAsStream("config.properties");

            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar el archivo config.properties");
                return;
            }

            // Cargar el archivo de propiedades
            props.load(input);

            // Leer las propiedades del archivo config.properties
            final String username = props.getProperty("mail.username");
            final String password = props.getProperty("mail.password");

            // Crear la sesión con autenticación
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Leer el archivo HTML desde la carpeta de resources
            String htmlContent = readHtmlFromClasspath("invite_template.html");

            // Crear un objeto MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Cambia por tu email
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("destinatario@example.com") // Cambia por el destinatario
            );
            message.setSubject("We Will Build a Free Website for Your Business");

            // Establecer el contenido del mensaje con HTML leído del archivo
            message.setContent(htmlContent, "text/html");

            // Enviar el correo
            Transport.send(message);

            System.out.println("Correo enviado exitosamente");

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    // Método para leer el contenido HTML desde el classpath
    private static String readHtmlFromClasspath(String fileName) throws IOException {
        // Cargar el archivo desde el classpath
        try (InputStream inputStream = EmailSender.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("No se pudo encontrar el archivo " + fileName);
            }

            return new String(inputStream.readAllBytes(), "UTF-8");
        }
    }
}
