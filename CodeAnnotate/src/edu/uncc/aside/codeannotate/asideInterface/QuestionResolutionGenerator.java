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

	public class QuestionResolutionGenerator  implements
			IMarkerResolutionGenerator, IMarkerResolutionGenerator2
	{
		public IMarkerResolution[] getResolutions(IMarker mk) 
		{
			Random rand = new Random();
			int  n = rand.nextInt(9999) + 1000;
			String randomId=Integer.toString(n);
			
			//handle highlighting
			InterfaceUtil.clearAndSetHighlighting(2, mk,randomId);
			
			//fake vulnerabilities
			InterfaceUtil.fakeVulnerabilities();
			
			
		       try {
		          Object problem = mk.getAttribute("WhatsUp");
		          //making "problem" an empty string. normally it would be from the marker
		          problem = "";
		          
		          return new IMarkerResolution[] 
		        {
		             //new QuestionTitleResolution("**********ASIDE Annotation Request**********"+problem),
		             new QuestionResolution("302-Delete Annotation"+problem),
		             new QuestionResolution("303-Modify Annotation"+problem),
		             new AnnotationRequestReadMore("304-Read More"+problem),
		             new AnnotateNowResolution("301-Annotate Now"+problem, "yellow.question",randomId),
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
