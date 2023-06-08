package org.agilelovers.server.common;

import org.agilelovers.server.email.returned.ReturnedEmailDocument;
import org.agilelovers.server.email.config.UserEmailConfigDocument;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    /**
     * Utility method to send simple HTML email
     *
     * @param session
     * @param toEmail
     * @param body
     */
    public static ReturnedEmailDocument sendEmail(Session session, String toEmail, String body, UserEmailConfigDocument emailConfig, String entirePrompt) {
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
            String errorMessage = "Failed to send email. Email configuration error: " + e.getMessage();
            return ReturnedEmailDocument.builder()
                    .userId(emailConfig.getUserID())
                    .entirePrompt(entirePrompt)
                    .confirmationOfEmailSent(errorMessage)
                    .build();
        }

        return ReturnedEmailDocument.builder()
                .userId(emailConfig.getUserID())
                .entirePrompt(entirePrompt)
                .confirmationOfEmailSent("Email was successfully sent")
                .build();

    }
}
