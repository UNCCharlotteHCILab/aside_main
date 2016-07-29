package edu.uncc.sis.aside.markers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.sis.aside.AsidePlugin;

public class IgnoreMarkerResolution implements IMarkerResolution,
		IMarkerResolution2 {

	private static final Logger logger = Plugin.getLogManager()
	.getLogger(IgnoreMarkerResolution.class.getName());
	
	private ICompilationUnit fCompilationUnit;
	private int rankNum;
	private String type;
	
	public IgnoreMarkerResolution(ICompilationUnit fCompilationUnit, int rankNum, String type){
		super();
		this.fCompilationUnit = fCompilationUnit;
		this.rankNum = rankNum;
		this.type = type;
	}
	
	@Override
	public String getDescription() {
		
		String content = "";
		String end = "";
		if(type.equals("input"))
		end = "Follow the \"Input Validation: Explanations and Examples\" link for more info about why this method invocation needs validation.";
		
		else if(type.equals("output"))
		end = "Follow the \"Output Encoding: Explanations and Examples\" link for more info about why this method invocation needs encoding.";	
		
		else if(type.equals("sql"))
			end = "Follow the \"Output Encoding: Explanations and Examples\" link for more info about why this method invocation needs encoding.";	
		
		else if(type.equals("annotation"))
			end = "Follow the \"Output Encoding: Explanations and Examples\" link for more info about why this method invocation needs encoding.";	

			
		String instruction = "-- Double click selection to remove " + Plugin.PLUGIN_NAME + " warning --";
		String description = "While " + Plugin.PLUGIN_NAME + " has found a potential vulnerability, you would like to disable the warning and treat this issue as non-vulnerable.";
		content = instruction + "<p><p>" + description + "<p><p>" + end;
		return content;
	}

	@Override
	public Image getImage() {
		
		return Plugin.getImageDescriptor("devil.png")
		.createImage();
	}

	@Override
	public String getLabel() {
		String start = "";
		String label = ""; // - 
		
		if(type.equals("input"))
		{
			start ="1";
			label = "-Disable Input Validation";
		}	
			else if(type.equals("output"))
			{
				start ="2";
				label = "-Disable Output Encoding";
			}
			else if(type.equals("sql"))
			{
				start ="3";
				label = "-Disable SQL Injection";
			}
			else if(type.equals("annotation"))
			{
				start ="4";
				label = "-Disable Annotation";
			}
		return start + this.rankNum + label;
	}

	@Override
	public void run(IMarker marker) {

		IProject project = MarkerAndAnnotationUtil.getProjectFromICompilationUnit(fCompilationUnit);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  
	    addLog(marker, project, dateFormat);

		MarkerAndAnnotationUtil.replaceMarkerWithAnnotation(fCompilationUnit, marker);
	}

	private void addLog(IMarker marker, IProject project, DateFormat dateFormat) {
		//get current date time with Date()
		Date date = new Date();
	    logger.info(dateFormat.format(date)+ " " + Plugin.getUserId() + " chose to Ignore This for the warning at " 
		        + marker.getAttribute(IMarker.LINE_NUMBER, -1) + " in java file <<"
				+ fCompilationUnit.getElementName()
				+ ">> in Project ["
				+ project.getName() + "]");
	}
	
	
}
