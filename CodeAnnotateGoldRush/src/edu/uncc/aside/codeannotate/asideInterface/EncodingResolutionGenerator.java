package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class EncodingResolutionGenerator implements IMarkerResolutionGenerator,
		IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker mk) 
	{
		//handle highlighting
		InterfaceUtil.clearAndSetHighlighting(3, mk);
		
		
		int markerIdentifier = mk.getAttribute("markerIdentifier", -1);
		
		
	       try {
	          Object problem = mk.getAttribute("WhatsUp");
	          
	          problem = "";
	          
	          return new IMarkerResolution[] 
	        {
	        	new EncodingTitleResolution("*Potential Security Vulnerability Detected*" + problem, markerIdentifier),
	        	new EncodingResolution("Encode for URL", "url"),
	        	new EncodingResolution("Encode for HTML", "html")
	        	        		  
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
