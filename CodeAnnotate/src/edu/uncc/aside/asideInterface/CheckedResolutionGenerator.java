package edu.uncc.aside.asideInterface;


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

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.InterfaceUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.sis.aside.markers.IgnoreMarkerResolution;
import edu.uncc.sis.aside.markers.ReadMoreResolution;

//The resolution generator for the green checks
	public class CheckedResolutionGenerator  implements
			IMarkerResolutionGenerator, IMarkerResolutionGenerator2
	{
		public IMarkerResolution[] getResolutions(IMarker marker) 
		{
			
			Random rand = new Random();
			int  n = rand.nextInt(9999) + 1000;
			String randomId=Integer.toString(n);
			
			ICompilationUnit fCompilationUnit = MarkerAndAnnotationUtil.getCompilationUnit(marker);
			
			
			//handle highlighting
			InterfaceUtil.clearAndSetHighlighting(1, marker,randomId);
			
		       try {
		          Object problem = marker.getAttribute("WhatsUp");
		          //making "problem" an empty string. normally it would be from the marker
		          problem = "";
		          
		          return new IMarkerResolution[] 
		          {
		         //     new CheckedTitleResolution("**********" + Plugin.PLUGIN_NAME + " Bound Annotation Request**********"+problem),
		         //     new AnnotateNowResolution("Add Annotation" + problem, PluginConstants.MARKER_ANNOTATION_CHECKED,randomId),
		         //     new CheckedDeleteResolution("Delete All Associated Annotations"+problem),
		         //    new CheckedResolution("Modify Annotation Binding"+problem),
		         //    new CheckedReadMore("Read More"+problem),
		             
		             new ReadMoreResolution(marker, "annotation", ""+ problem),		
		             new AnnotateNowResolution("41-Add Annotation", PluginConstants.MARKER_ANNOTATION_CHECKED,randomId),
		             new CheckedDeleteResolution("42-Delete All Associated Annotations"),			        	
			         new IgnoreMarkerResolution(
			    				fCompilationUnit, PluginConstants.INPUT_IGNORE_RANK_NUM, "annotation")
			
		             
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