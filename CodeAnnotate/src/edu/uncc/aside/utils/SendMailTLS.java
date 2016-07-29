package edu.uncc.aside.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.sis.aside.AsidePlugin;

public class SendMailTLS {
	private final static String ESAPI_CONFIG_DIR_NAME = "ESAPI-Lib";
	private final static String PROJECT_SRC_PATH = "src";
	private final static String LOG_DIR = "/.metadata/.plugins/edu.uncc.sis.aside/aside.log";
	
	public static void sendLogFile(IProject project, String userIDInputByUser){
		String logFileFullPath = null;
		logFileFullPath = getLogFileFullPath(project);
		
		 String userIDFromSystem = System.getProperty("user.name");
		 String twoUserIDs = userIDFromSystem;
		 
		final String username = "Gmail_Username";
		final String password = "Gmail_Password";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		System.out.println("reached position 0");
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
        System.out.println("reached position 1");
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("asideuncc@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("asideuncc@gmail.com"));
		
			System.out.println("reached position 2");	

			
			
			//get the time stamp, user name, hostname
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String dateStr = dateFormat.format(date);
			
			
		/*	java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			System.out.println(â€�Hostname of local machine: â€� + localMachine.getHostName());*/

			String filename = twoUserIDs + "_" + dateStr + "_aside.log";
			
			message.setSubject(twoUserIDs + "_" + dateStr);
			
			// Create the message part 
			BodyPart messageBodyPart1 = new MimeBodyPart();

			// Fill the message
			messageBodyPart1.setText("");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart1);

			
			String userHome = System.getProperty("user.home");
			System.out.println("user.home " + userHome);
			String userDir = System.getProperty("user.dir");
			System.out.println("user.dir = " + userDir);
			// Part two is attachment
			BodyPart messageBodyPart2 = new MimeBodyPart();
			
			
			
			DataSource source = new FileDataSource(logFileFullPath);  //modified
			System.out.println("logFileFullPath = " + logFileFullPath.toString());
			System.out.println("reached position 3");
			messageBodyPart2.setDataHandler(new DataHandler(source));
			System.out.println("reached position 4");
			
			messageBodyPart2.setFileName(filename);
			multipart.addBodyPart(messageBodyPart2);

			// Put parts in message
			message.setContent(multipart);
            System.out.println("reached postion 5");
			// Send the message
			Transport.send(message);
			
			System.out.println("Done");
		
			
		} catch (MessagingException e) {
			System.out.println("come into the exception here");
			throw new RuntimeException(e);
		}
	}
	//need to be modified later~~~~~~~~~~~~
	public static String getLogFileFullPath(IProject project){
		String logFileFullPath = null;
		
		Bundle bundle = Platform.getBundle(PluginConstants.PLUGIN_ID);
		Path path = new Path(IPath.SEPARATOR + ESAPI_CONFIG_DIR_NAME);
		URL fileURL = FileLocator.find(bundle, path, null);
		if (fileURL == null) {
			System.err.println("cannot locate log file directory");
			return null;
		}
		
        System.out.println("FileLocator.find(bundle, path, null)--fileURL==" + fileURL.toString());
        
		InputStream is = null;
		try {

			URL localFileURL = FileLocator.toFileURL(fileURL);

			String sourcePath = localFileURL.getFile();
			File file = new File(sourcePath);
			if (file.exists() && file.isDirectory()) {
				File[] sourceFiles = file.listFiles();
				for (int i = 0; i < sourceFiles.length; i++) {
             
					File target = sourceFiles[i];
					String fileName = target.getName();

					if (target.isFile()) {
						//fileName.endsWith(".jar")
						if (true) {
							is = new BufferedInputStream(new FileInputStream(
									target.getAbsolutePath()));
							/*IFile destination = project
									.getFile(IPath.SEPARATOR + PROJECT_WEBCONTENT_PATH);*/
							//System.out.println("project.getFullPath() = " + );
							
							IFolder folder = project.getFolder(PROJECT_SRC_PATH);
							String tmpPathStr = folder.getRawLocationURI().getPath().toString();
							String delimiter = "/";
							String[] temp = null;
							String resultStr = "";
							temp = tmpPathStr.split(delimiter);
							System.out.println("temp length = " + temp.length);
							for(int t = 0; t < temp.length - 2; t++)
							{
								if(t != 0)
								resultStr += "/" + temp[t];
								else
									resultStr = temp[t];
							}
							resultStr = resultStr + LOG_DIR;
							logFileFullPath = resultStr;
							System.out.println("logFileFullPath = " + logFileFullPath);
							
							return logFileFullPath;
						}
					} else if (target.isDirectory()) {
						/*if (fileName.equalsIgnoreCase("esapi")
								|| fileName.equalsIgnoreCase(".esapi")) {
							//IFolder destination = fProject.getFolder(fileName); // this one may need modification
							//added Mar. 2
							//IFolder tmp = fProject.getFolder(fileName);
							IFolder destination = fProject.getFolder(IPath.SEPARATOR + PROJECT_WEBINF_PATH + IPath.SEPARATOR + fileName);
							String tmp = IPath.SEPARATOR + PROJECT_WEBINF_PATH + IPath.SEPARATOR + fileName;
							//System.out.println("line 126 destination = " + destination + " tmp" + tmp);
							/////
							
							if (!destination.exists()) {
								try {
									destination.create(true, true, null);
								} catch (CoreException e) {
									continue;
								}
							}
							copyDirectory(target, tmp, is, monitor);
						}*/
					}
				}

			} else {
				System.out.println("log file does not exist");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}*/
		return null;
	}
}