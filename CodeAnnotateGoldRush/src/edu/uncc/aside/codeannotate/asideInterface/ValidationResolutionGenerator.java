package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class ValidationResolutionGenerator implements
		IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker mk) 
	{
		//handle highlighting
		InterfaceUtil.clearAndSetHighlighting(3, mk);
		
		//get marker code for faking a Validation for ui study, 1 is the first Validation, 2 is the second
		
		int markerIdentifier = mk.getAttribute("markerIdentifier", -1);
		
		
	       try {
	          Object problem = mk.getAttribute("WhatsUp");
	          
	          problem = "";
	          
	          return new IMarkerResolution[] 
	        {
	        	new ValidationTitleResolution("*****ASIDE POTENTIAL SECURITY VULNERABLITY*****" + problem, markerIdentifier),
	        	new ValidationResolution("Allow Alphabetical Characters and Numbers", "alphabet"),
	        	new ValidationResolution("Allow Minimal HTTP Characters", "http"),
	        	new ValidationResolution("Allow URL Characters", "url"),
	        	new ValidationResolution("Allow Credit Card Numbers", "credit"),
	        	new ValidationResolution("Allow EMail Addresses", "email"),
	        	new ValidationResolution("Allow Social Security Numbers", "ssn")
	        	        		  
	          };
	       }
	       catch (CoreException e) 
	       {
	          return new IMarkerResolution[0];
	       }
	       
	    }

	@Override
	public boolean hasResolutions(IMarker arg0) 
	{
		// TODO Auto-generated method stub
		return true;
	}	

}
