package edu.uncc.sis.aside.markers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.asideInterface.VariablesAndConstants;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;

public class ReadMoreResolution implements IMarkerResolution,
IMarkerResolution2{
	
	private static final Logger logger = Plugin.getLogManager().getLogger(
			SyntacticValidationResolution.class.getName());

	private ICompilationUnit fCompilationUnit;
	private String fInputType;
	private IMarker fMarker;
	private IProject fProject;
	private String readMoreType;
	/**
	 * Constructor for ReadMoreResolution
	 * 
	 * @param cu
	 * @param validationRule
	 */
	public ReadMoreResolution(ICompilationUnit cu, IMarker marker, IProject project, String readMoreType) {
		super();
		fCompilationUnit = cu;
		fInputType = PluginConstants.ReadMore;
		fMarker = marker;
		fProject = project;
		this.readMoreType = readMoreType;
	}

	@Override
	public String getDescription() {
		String instruction = "", description = "", moreInfo = "";
		String content = "";
		
		if(this.readMoreType.equals("input")){
			instruction = "-- Double click the \"Input Validation: Explanations and Examples\" link for an in-depth review of these issues";
		description = "Security issues occur when malicious characters make their way into applications, programs, systems and databases (to name a few). Therefore, it is imperative to validate anything that is externally obtained prior to doing anything with the external input.";
		moreInfo = "Follow the \"Input Validation: Explanations and Examples\" link for more info about why this method invocation needs validation.";
		content = instruction + "<p><p>" + description + "<p><p>" + moreInfo;
		}else if(this.readMoreType.equals("output")){
			instruction = "-- Double click the \"Output Encoding: Explanations and Examples\" link for an in-depth review of these issues.";
			description = "Security issues occur when malicious characters make their way into the output. Therefore, it is imperative to encode anything that is externally obtained prior to sending it to a browser.";
			moreInfo = "Follow the \"Output Encoding: Explanations and Examples\" link for more info about why this method invocation needs encoding.";
			content = instruction + "<p><p>" + description + "<p><p>" + moreInfo;
		}else{
			content = "";
		}
		return content;
	}

	@Override
	public Image getImage() {
        
        
		return Plugin.getImageDescriptor("devil.png")
				.createImage();
	}

	@Override
	public String getLabel() {
		if(this.readMoreType.equals("input")){
			return "101 - Input Validation: Explanations and Examples";
			}
		else if(this.readMoreType.equals("output")){
			return "101 - Output Encoding: Explanations and Examples";
		}else
			return "101 - Input Validation: Explanations and Examples";
	}

	@Override
	public void run(IMarker marker) {
		
//		try 
//   	 {
//   		 URL url;
//   		 String lineText = VariablesAndConstants.methodNames[marker.getAttribute("markerIndex", -1)];
//   		 System.out.println(lineText);
//   		 IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(1, "myId", "ASIDE More Information", "ASIDE More Information");
//   		 if(this.readMoreType.equals("input")){
//   			 url = new URL("https://cd881bde7e4e09b16cb85cc7a0d5e747929b7864.googledrive.com/host/0BzEfllwR2DGafnVyZEV2ODhEQ1p0ekoyZ1I5anZ6aURHbkJoZFFTeXQ2NldBbFotV0hNMlE/ASIDE%20Validation.html");
//   		 }else if(this.readMoreType.equals("output")){
//			 url = new URL("https://e93c6c1b3c43bf644cdfce81f94611601da37359.googledrive.com/host/0BzEfllwR2DGafm5MbjlfaWZGSDFZUE1qeWZZVHRjWl9SdDdQX3ZwU2NfZ0hFN01ISGt0Qjg/ASIDE%20Encoding.html");
//   		 }else{
//   			 url = new URL("https://cd881bde7e4e09b16cb85cc7a0d5e747929b7864.googledrive.com/host/0BzEfllwR2DGafnVyZEV2ODhEQ1p0ekoyZ1I5anZ6aURHbkJoZFFTeXQ2NldBbFotV0hNMlE/ASIDE%20Validation3.html");
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
		
		String ruleNameBelongTo="";
		IProject project = ASIDEMarkerAndAnnotationUtil.getProjectFromICompilationUnit(fCompilationUnit);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  
	    //get current date time with Date()
		Date date = new Date();
	   
	    try {
	    	 URL url;
	    String queryUrlStr = PluginConstants.HostUrl;
	   // IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("myId");
	    int constantValue = 1<<1;
	    //System.out.println("constantValue= "+constantValue);
	    IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(constantValue, "myId", "ASIDE More Information", "ASIDE More Information");	    
	    //ruleNameBelongTo = (String)marker.getAttribute("edu.uncc.sis.aside.marker.ruleNameBelongTo");
	    //System.out.println(ruleNameBelongTo);
	    //for the demo of Feb. 17, since there are two explanation pages available
	    //I only use the type name (inputValidation, outputEncoding) in the url, called typeNameBelongTo
	    //for the future, I pass two parameters (ruleNameBelongTo, typeNameBelongTo) to open "readMore" page.
	    String urlStr = "";//queryUrlStr+ruleNameBelongTo; //URLEncoder.encode(ruleNameBelongTo, "UTF-8");
	    //added Feb. 16, for server 
	    //urlStr = "http://hci.sis.uncc.edu/aside/index";
	    ruleNameBelongTo = (String)marker.getAttribute("edu.uncc.sis.aside.marker.typeNameBelongTo");
	    
	    
	    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " chose ReadMore option to read the webpage of <webpageType>" + ruleNameBelongTo + "<webpageType> for the warning " + "at line "
				+ marker.getAttribute(IMarker.LINE_NUMBER, -1) + " in java file <<"
				+ fCompilationUnit.getElementName()
				+ ">> in Project ["
				+ project.getName() + "]");
	    
	    String ruleTypeUrlParam = Converter.ruleTypeToUrlParam(ruleNameBelongTo);
	    //System.out.println("ruleTypeUrlParam-=-"+ruleTypeUrlParam);
	    String userID = Plugin.getUserId();
	    if(userID.equals("anonymizedUser")){
	    	urlStr = queryUrlStr + ruleTypeUrlParam + ".jsp";
	    }else
	    urlStr = queryUrlStr + ruleTypeUrlParam + ".jsp?userName=" + userID;
	    //System.out.println("urlStr = " + urlStr);
	    if(this.readMoreType.equals("output")){
			 url = new URL("https://bf01edb9cef99332ecbb51e2b7d6bd6e0ea6e715.googledrive.com/host/0B_sYP_Y3om2XaVVManJKcU1xVHc/ASIDE_Output_Encoding.html");
		 }else if(this.readMoreType.equals("input")){
			 url = new URL("https://5b1d4b79290586caa52348cf1f0035de64b53bf3.googledrive.com/host/0B_sYP_Y3om2XSDB0aUMwSTRTQUE/ASIDE_Input_Validation.html");
		 }
		 else
		 {
			 url = new URL("https://1b11f44f746ed3e929e864ba53f92c994760f743.googledrive.com/host/0B_sYP_Y3om2XTEpXRVhuVENuUkk/ASIDE_SQL_Statement.html");
		 }
		 webBrowser.openURL(url);
		 URLConnection urlConnection = url.openConnection();
		 urlConnection.setDoOutput(true);
	    //URL url = new URL(urlStr);
	    
	    //webBrowser.openURL(url);
		//URLConnection urlConnection = url.openConnection();
		//urlConnection.setDoOutput(true);
		//added Aug 30th
		/*
	   
		String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
		OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
		wr.write(data);*/
		//
		/*
		 *  OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		 *  wr.write(data);
    wr.flush();

    // Get the response
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
        // Process line...
    }
    wr.close();
    rd.close();
    */
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

}