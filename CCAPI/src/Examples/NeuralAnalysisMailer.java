/*
 * Created on 28.03.2005
 * 
 * GPL protected. Author: Ulrich Staudinger
 *  
 */
package Examples;

import java.util.*;
import javax.mail.*;

import javax.mail.internet.*;
import javax.activation.*;


public class NeuralAnalysisMailer {

	public static void postMail(String recipient, String subject,
			String message, String from) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.activestocks.de");

		Session session = Session.getDefaultInstance(props);

		Message msg = new MimeMessage(session);

		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress addressTo = new InternetAddress(recipient);
		msg.setRecipient(Message.RecipientType.TO, addressTo);

		msg.setSubject(subject);
		
		
		
		
//		MimeBodyPart text = new MimeBodyPart();
//		text.setText( message );
//		text.setHeader( "MIME-Version" , "1.0" );
//		text.setHeader( "Content-Type" , text.getContentType() );
//
//		content.addBodyPart( text );

		
		BodyPart messageBodyPart = new MimeBodyPart();
		String htmlText = "<b>(c) Ulrich Staudinger</b><br>";
//		  "<img src=\"cid:file1\"><br>"+
//		  "<img src=\"cid:file2\"><br>"+
//		  "<img src=\"cid:file3\"><br></html>";
		messageBodyPart.setContent(htmlText, "text/html");
		
		MimeMultipart multipart = new MimeMultipart("related");
		multipart.addBodyPart(messageBodyPart);
		
		
		// attach the figures. 
		DataSource fileDataSource1 = new FileDataSource( "c:/figure1.png" );
		MimeBodyPart file1 = new MimeBodyPart();
		file1.setDataHandler( new DataHandler(fileDataSource1) );
		file1.setHeader("Content-ID","<file1>");
		file1.setFileName( "NN1.png" );     // gibt dem Anhang einen Namen		
		multipart.addBodyPart( file1 );
		
		DataSource fileDataSource2 = new FileDataSource( "c:/figure2.png" );
		MimeBodyPart file2 = new MimeBodyPart();
		file2.setDataHandler( new DataHandler(fileDataSource2) );
		file2.setFileName( "NN2.png" );     // gibt dem Anhang einen Namen	
		file2.setHeader("Content-ID","file2");
		multipart.addBodyPart( file2 );

		DataSource fileDataSource3 = new FileDataSource( "c:/figure3.png" );
		MimeBodyPart file3 = new MimeBodyPart();
		file3.setDataHandler( new DataHandler(fileDataSource2) );
		file3.setFileName( "NN3.png" );     // gibt dem Anhang einen Namen		
		file3.setHeader("Content-ID","file3");
		
		
		multipart.addBodyPart( file3 );

		
		
		
		msg.setContent( multipart );
	
		
		
		Transport.send(msg);
		
	}

	public static void main(String[] args) {
		System.out.println("About to send data ....");
		try{
			postMail("us@activestocks.de","NN Analyse "+(new Date()), "(c) Ulrich Staudinger", "webmaster@activestocks.de");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}