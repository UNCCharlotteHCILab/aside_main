package edu.uncc.sis.aside.markers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;

import org.eclipse.core.runtime.FileLocator;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.uncc.aside.asideInterface.VariablesAndConstants;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.InterfaceUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.aside.utils.Converter;
import edu.uncc.sis.aside.Old_AsidePlugin;

public class TitleMarkerResolution implements IMarkerResolution,
IMarkerResolution2{
	
	private static final Logger logger = Plugin.getLogManager().getLogger(
			SyntacticValidationResolution.class.getName());

//	private ICompilationUnit fCompilationUnit;
	private String fInputType;
	private IMarker fMarker;
//	private IProject fProject;
	private String readMoreType;
	/**
	 * Constructor for ReadMoreResolution
	 * @param problem TODO
	 * @param validationRule
	 */
	public TitleMarkerResolution(IMarker marker, String readMoreType, String problem) {
		super();
	//	fCompilationUnit = cu;
		fInputType = PluginConstants.ReadMore;
		fMarker = marker;
		//fProject = project;
		this.readMoreType = readMoreType;
	}

	@Override
	public String getDescription() {
		String instruction = "", description = "", moreInfo = "";
		String content = "";
		// First Option : Read more examples
		if(this.readMoreType.equals("input")){
			
			description =" This line of code reads untrusted data. If the input is not validated before it is used, attackers can potentially insert malicious code into the program. This can lead to Cross Site Scripting or other security problems.";
			instruction ="";
			moreInfo =" Use the options below to insert code that validates this input, or Read more information about this vulnerability and how to fix it.";
			
		
		}else if(this.readMoreType.equals("output")){
			instruction = "";
			description = " This code uses the println method from the PrintWriter object to output data to the application. If this is not sanitized, attackers can insert arbitrary malicious code which will be executed when this data is displayed. This attack is known as a Cross-Site-Scripting attack.";
			moreInfo = " Use the options below to generate code to sanitize data, or Read more information about this vulnerability and how to fix it.";
			
		}else if(this.readMoreType.equals("annotation")){
			description =" This involves sensitive information and could possibly be an access control vulnerability if access control code is not in place.";
			instruction ="";
			moreInfo =" Please annotate the access control code for this this sensitive operation by double clicking on Annotate Now and highlighting the code.";
					
		}else if(this.readMoreType.equals("sql")){
			description =" This code uses external data to create a dynamic SQL statement. Attackers could manipulate this data to run their own SQL commands.  This is known as an SQL injection attack.";
			instruction ="";
			moreInfo =" To fix this vulnerability, change this dynamic SQL statement to a prepared SQL statement, which cannot be manipulated by an attacker. Choose Read More for details on creating prepared SQL statements.";
					
		}
		return  description + "<p><p>" + moreInfo;
	}

	@Override
	public Image getImage() {
        
        
		return Plugin.getImageDescriptor("devil.png") 
				.createImage();
	}

	@Override
	public String getLabel() {
		if(this.readMoreType.equals("input")){
			return  "10-" + Plugin.PLUGIN_NAME + " Unvalidated Input Vulnerability"; //"101 - Input Validation: Explanations and Examples";
		}
		else if(this.readMoreType.equals("output")){
			return "20-" + Plugin.PLUGIN_NAME + " Unsanitized Output Vulnerability";
		}
		else if(this.readMoreType.equals("sql")){
			return "30-" + Plugin.PLUGIN_NAME + " SQL Injection Vulnerability";
		}
		else if(this.readMoreType.equals("annotation")){
			return "40-" + Plugin.PLUGIN_NAME + " Annotation Request";
		}else
			return "50-" + Plugin.PLUGIN_NAME + " Unvalidated Input Vulnerability"; //"101 - Input Validation: Explanations and Examples";
	}

	@Override
	public void run(IMarker marker) {
		
//		try 
//   	 {
//   		 URL url;
//   		 String lineText = VariablesAndConstants.methodNames[marker.getAttribute("markerIndex", -1)];
//   		 System.out.println(lineText);
//   		 IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(1, "myId", "" + Plugin.pluginName + " More Information", "" + Plugin.pluginName + " More Information");
//   		 if(this.readMoreType.equals("input")){
//   			 url = new URL("https://cd881bde7e4e09b16cb85cc7a0d5e747929b7864.googledrive.com/host/0BzEfllwR2DGafnVyZEV2ODhEQ1p0ekoyZ1I5anZ6aURHbkJoZFFTeXQ2NldBbFotV0hNMlE/Validation.html");
//   		 }else if(this.readMoreType.equals("output")){
//			 url = new URL("https://e93c6c1b3c43bf644cdfce81f94611601da37359.googledrive.com/host/0BzEfllwR2DGafm5MbjlfaWZGSDFZUE1qeWZZVHRjWl9SdDdQX3ZwU2NfZ0hFN01ISGt0Qjg/Encoding.html");
//   		 }else{
//   			 url = new URL("https://cd881bde7e4e09b16cb85cc7a0d5e747929b7864.googledrive.com/host/0BzEfllwR2DGafnVyZEV2ODhEQ1p0ekoyZ1I5anZ6aURHbkJoZFFTeXQ2NldBbFotV0hNMlE/Validation3.html");
//   		 }
//   		 webBrowser.openURL(url);
//   		 URLConnection urlConnection = url.openConnection();
//   		 urlConnection.setDoOutput(true);
//		 } 
//   	catch (Exception e) 
//   	{
//			e.printStackTrace();
//		}	    
		// TODO Auto-generated method stub
		
		//String ruleNameBelongTo="";
	//	IProject project = MarkerAndAnnotationUtil.getProjectFromICompilationUnit(fCompilationUnit);
		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  
	    //get current date time with Date()
		//Date date = new Date();
	   	}
		/*
		try {
			
			ruleNameBelongTo = (String)marker.getAttribute("edu.uncc.sis.aside.marker.ruleNameBelongTo");
			System.out.println("ruleNameBelongTo " + ruleNameBelongTo);
			
			//String url;
			//IWorkbenchSupport.createBrowser("myId").openURL(url); 
			
			// Create query string
            String param1Value = ruleNameBelongTo;
            String param2Value = "";
			String queryString = "param1=" +
			   URLEncoder.encode(param1Value, "UTF-8");
			queryString += "&param2=" +
			   URLEncoder.encode(param2Value, "UTF-8");

			// Make connection

			
			OutputStreamWriter out = new OutputStreamWriter(
			   urlConnection.getOutputStream());

			// Write query string to request body

			out.write(queryString);
			out.flush();

			// Read the response

			BufferedReader in = new BufferedReader(
			   new InputStreamReader(urlConnection.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
			   System.out.println(line);
			}
			out.close();
			in.close(); 

			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
	}

