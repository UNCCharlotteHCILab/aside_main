package edu.uncc.aside.asideInterface;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.InterfaceUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.sis.aside.markers.*;
//MM For Annotation Request Marker
	public class QuestionResolutionGenerator  implements
			IMarkerResolutionGenerator, IMarkerResolutionGenerator2
	{
		public IMarkerResolution[] getResolutions(IMarker marker) 
		
		{
			
			Random rand = new Random();
			int  n = rand.nextInt(9999) + 1000;
			String randomId=Integer.toString(n);
			
			ICompilationUnit fCompilationUnit = MarkerAndAnnotationUtil.getCompilationUnit(marker);
			
			//handle highlighting
			InterfaceUtil.clearAndSetHighlighting(2, marker,randomId);
			
			//fake vulnerabilities
			InterfaceUtil.fakeVulnerabilities();
			
			
		       try {
		          Object problem = marker.getAttribute("WhatsUp");
		          //making "problem" an empty string. normally it would be from the marker
		          problem = "";
		          
		          //MM Not all item required. Based on the marker type this list should be changed. 
		          return new IMarkerResolution[] 
		        {
		             //new QuestionTitleResolution("**********" + Plugin.pluginName + " Annotation Request**********"+problem),
		        	new TitleMarkerResolution(marker, "annotation", ""+ problem),		        	
		        	
		        	new AnnotateNowResolution("41-Add Annotation", PluginConstants.MARKER_ANNOTATION_REQUEST,randomId),
		        //	new QuestionResolution("42-Delete Annotation"+problem),
		        	
		        	new IgnoreMarkerResolution(
		    				fCompilationUnit, PluginConstants.INPUT_IGNORE_RANK_NUM, "annotation"),
		        	new ReadMoreResolution(marker, "annotation", ""+ problem)
		        	
		         //    new QuestionResolution("403-Modify Annotation"+problem),
		         //    new AnnotationRequestReadMore("404-Read More"+problem),
		             
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
