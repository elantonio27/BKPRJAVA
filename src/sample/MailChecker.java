package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class MailChecker {
    private Properties props;
    private MailChecker MailUtils;

    public ObservableList<Mailobject> getAllMails() throws IOException, MessagingException {
/*
        Session session = MailUtils.getGMailSession(
                JOptionPane.showInputDialog( "user" ),
                JOptionPane.showInputDialog( "pass" ) );

 */
        ObservableList<Mailobject> dlmMails= FXCollections.observableArrayList();
        Session session = getGMailSession("testbarkeeper@gmail.com","Jackcarver12");
        Folder inbox = openPop3InboxReadOnly( session );
        printAllTextPlainMessages( inbox );

        if(inbox.getMessages()!=null)
        {
            for (Message m : inbox.getMessages())
            {
                Mailobject mo = new Mailobject();
                mo.setSubject(m.getSubject());
                mo.setDate(m.getSentDate());
                mo.setSender(Arrays.toString(m.getFrom()));
                dlmMails.add(mo);
            }
        }
        else
        {
            System.out.println("Sie haben keine Mails");
        }
        printAllTextPlainMessages(inbox);
        closeInbox( inbox );
        return dlmMails;
    }

    public static Session getGMailSession(String user, String pass )
    {
        final Properties props = new Properties();

        // Zum Empfangen
        props.setProperty( "mail.pop3.host", "pop.gmail.com" );
        props.setProperty( "mail.pop3.user", user );
        props.setProperty( "mail.pop3.password", pass );
        props.setProperty( "mail.pop3.port", "995" );
        props.setProperty( "mail.pop3.auth", "true" );
        props.setProperty( "mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory" );

        // Zum Senden
        props.setProperty( "mail.smtp.host", "smtp.gmail.com" );
        props.setProperty( "mail.smtp.auth", "true" );
        props.setProperty( "mail.smtp.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.port", "465" );
        props.setProperty( "mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );

        return Session.getInstance( props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( props.getProperty( "mail.pop3.user" ),
                        props.getProperty( "mail.pop3.password" ) );
            }
        } );
//    session.setDebug( true );
    }
    // ...


    public static Folder openPop3InboxReadOnly( Session session )
            throws MessagingException
    {
        Store store = session.getStore( "pop3" );
        store.connect();

        Folder folder = store.getFolder( "INBOX" );
        folder.open( Folder.READ_ONLY );

        return folder;
    }
    public static void closeInbox( Folder folder ) throws MessagingException
    {
        folder.close( false );
        folder.getStore().close();
    }
    public static   void printAllTextPlainMessages( Folder folder )
            throws MessagingException, IOException
    {
        for ( Message m : folder.getMessages() )
        {
            System.out.println( "\nNachricht:" );
            System.out.println( "Von: " + Arrays.toString(m.getFrom()) );
            System.out.println( "Betreff: " + m.getSubject() );
            System.out.println( "Gesendet am: " + m.getSentDate() );
            System.out.println( "Content-Type: " +
                    new ContentType( m.getContentType() ) );

            if ( m.isMimeType( "text/plain" ) )
                System.out.println( m.getContent() );
        }
    }


    public static void sendGMX() throws MessagingException
    {
        String sender = "bkpr@gmx.de";
        String password = "my.password";
        String receiver = "my-receiver@gmail.com";

        Properties properties = new Properties();

        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "mail.gmx.net");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.user", sender);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.starttls.enable", "true");

        Session mailSession = Session.getInstance(properties, new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(properties.getProperty("mail.smtp.user"),
                        properties.getProperty("mail.smtp.password"));
            }
        });

        Message message = new MimeMessage(mailSession);
        InternetAddress addressTo = new InternetAddress(receiver);
        message.setRecipient(Message.RecipientType.TO, addressTo);
        message.setFrom(new InternetAddress(sender));
        message.setSubject("The subject");
        message.setContent("This is the message content", "text/plain");
        Transport.send(message);
    }
}
