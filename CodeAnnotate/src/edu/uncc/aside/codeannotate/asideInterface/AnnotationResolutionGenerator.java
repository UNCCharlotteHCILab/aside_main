package edu.uncc.aside.codeannotate.asideInterface;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;

	public class AnnotationResolutionGenerator  implements
			IMarkerResolutionGenerator, IMarkerResolutionGenerator2
	{
		public IMarkerResolution[] getResolutions(IMarker mk) 
		{
			Random rand = new Random();
			int  n = rand.nextInt(9999) + 1000;
			String randomId=Integer.toString(n);
			
			System.out.println(randomId);
			
			
			//handle highlighting
			InterfaceUtil.clearAndSetHighlighting(0, mk,randomId);
			
			
		       try {
		          Object problem = mk.getAttribute("WhatsUp");
		          //making "problem" an empty string. normally it would be from the marker
		          problem = "";
		          
		          //get the matching request marker
		          
		          int markerIndex = mk.getAttribute("markerIndex", -1);
		          IMarker matchingRequestMarker = VariablesAndConstants.annotationRequestMarkers[markerIndex];
		          
		          return new IMarkerResolution[] 
		        {
		             new AnnotationCodeResolution("**********ASIDE Annotation**********"+problem, matchingRequestMarker),
		             new AnnotationDeleteResolution("301-Delete Annotation"+problem),
		             new AnnotationResolution("302-Modify Annotation"+problem),
		             new AnnotationReadMore("303-Read More"+problem),
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
