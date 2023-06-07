package org.agilelovers.server.email;

import org.agilelovers.server.user.UserEmailDocument;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

public class EmailUtil {

    /**
     * Utility method to send simple HTML email
     *
     * @param session
     * @param toEmail
     * @param body
     */
    public static void sendEmail(Session session, String toEmail, String body, UserEmailDocument emailConfig) {
        try {
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(emailConfig.getEmail()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setText(body, "UTF-8");

            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
