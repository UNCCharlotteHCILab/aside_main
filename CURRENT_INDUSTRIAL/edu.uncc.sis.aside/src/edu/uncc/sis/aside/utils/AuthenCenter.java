package edu.uncc.sis.aside.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.*;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;

public class AuthenCenter {
	
	
	
	
	/*This function was in use in spring of 2013. It has been commented out because the code no longer checks the email for student ids.
	 * it has been replaced with an alternative function of the same name
	 
	public static boolean hasPermission(String userId) {
		List<String> participantsList = getParticipantsList();
		for(int i = 0; i < participantsList.size(); i++){
			System.out.println(participantsList.get(i) + " " + userId);
			if(participantsList.get(i).equals(userId)){
				System.out.println("User is authenticated " + userId);
				return true;
			}
		}
			return false;
	}
	*/
	
	public static boolean hasPermission(String userId) {
		return true; //aside always has permission to run, regardless of userid. may need to modify for certain studies.
	}
	
	
	public static List<String> getParticipantsList() {
		List<String> userIdList = new LinkedList<String>();
		userIdList.add("Tyler");
		userIdList.add("testuser2");
		return userIdList;
	}
	

	
	/* This function was used in the main version in spring of 2013. However, it has been commented out since the list of participants is no longer 
	 * loaded from an email inbox. An alternative function (above) is used instead.
	public static List<String> getParticipantsList() {
		
		
		// get the most recent email with subject "UNCC ASIDE userId list"
		String requiredSubject = PluginConstants.USERIDLIST_EMAIL_SUBJECT;
		String subject = "";
		List<String> userIdList = new LinkedList<String>();

		final String username = "Gmail_Username";
		final String password = "Gmail_Password";

		IMAPFolder folder = null;
		Store store = null;
		Flag flag = null;
		try {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);

			try {
				store = session.getStore("imaps");
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				store.connect("imap.googlemail.com", username, password);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				folder = (IMAPFolder) store.getFolder("inbox");
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // This doesn't work for other email account
				// folder = (IMAPFolder) store.getFolder("inbox"); This works
				// for both email account

			if (!folder.isOpen())
				try {
					folder.open(Folder.READ_WRITE);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			Message[] messages = folder.getMessages();
			System.out.println("No of Messages : " + folder.getMessageCount());
			System.out.println("No of Unread Messages : "
					+ folder.getUnreadMessageCount());
			System.out.println(messages.length);
			String strs = "";
			for (int i = 0; i < messages.length; i++) {

				System.out
						.println("*****************************************************************************");
				System.out.println("MESSAGE " + (i + 1) + ":");
				Message msg = messages[i];
				// System.out.println(msg.getMessageNumber());
				// Object String;
				// System.out.println(folder.getUID(msg)

				subject = msg.getSubject();
				String sender = msg.getFrom()[0].toString();
				System.out.println("sender = " + sender);
				
				if(PluginConstants.ALLOWED_USERID_EMAIL_SENDER.contains(sender)){
				if (subject.equals(requiredSubject)) {
					System.out.println("Subject: " + subject);
				    Object content = msg.getContent();  
				    if (content instanceof String)  
				    {  
				        String body = (String)content;  
				        System.out.println("string " + body);
				    }  
				    else if (content instanceof Multipart)  
				    {  
				        Multipart mp = (Multipart)content;  
				        strs = mp.getBodyPart(0).getContent().toString();
				       
				    }  
				    break;
				}
				}
			}
			String[] userIds = strs.split("\n");
			for(int i = 0; i < userIds.length; i++){
				if(userIds[i].length() > 0){
				System.out.print(userIds[i].substring(0, userIds[i].length()-1));
				userIdList.add(userIds[i].substring(0, userIds[i].length()-1));
			}
			}
			
			
		} catch (Exception e) {
			System.err.println("Exception occured in AuthenCenter.java");
		} finally {
			if (folder != null && folder.isOpen()) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (store != null) {
					try {
						store.close();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
		return userIdList;
		
		
	}
	*/
}
