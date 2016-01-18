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
				
		String markerIdentifier = null;
		try {
			markerIdentifier = (String) mk.getAttribute("markerIdentifier");
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	       try {
	          Object problem = mk.getAttribute("WhatsUp");
	          
	          problem = "";
	          
	          return new IMarkerResolution[] 
	        {
	        	new ValidationTitleResolution("*Potential Security Vulnerability Detected*" + problem, markerIdentifier),
	        	new ValidationResolution("Allow Only Letters and Numbers", "alphabet", markerIdentifier),
	        	new ValidationResolution("Allow Only Minimal HTTP Characters", "http", markerIdentifier),
	        	new ValidationResolution("Allow Only URL Characters", "url", markerIdentifier),
	        	new ValidationResolution("Allow Only Credit Card Numbers", "credit", markerIdentifier),
	        	new ValidationResolution("Allow Only Email Addresses", "email", markerIdentifier),
	        	new ValidationResolution("Allow Only Social Security Numbers", "ssn", markerIdentifier),
	        	new ValidationReadMore("Read More", markerIdentifier)
	        	        		  
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