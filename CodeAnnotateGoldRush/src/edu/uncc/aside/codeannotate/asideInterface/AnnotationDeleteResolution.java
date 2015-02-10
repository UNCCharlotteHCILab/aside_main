package edu.uncc.aside.codeannotate.asideInterface;

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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;

/*
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;
*/

public class AnnotationDeleteResolution implements IMarkerResolution,
IMarkerResolution2{
	

	String label;
    AnnotationDeleteResolution(String label) {
       this.label = label;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
    	//this is a request to delete an annotation
    	//first delete the annotation marker
    	int markerIndex = -1;
    	int secondIndex = -1;
    	try
    	{
    		markerIndex = marker.getAttribute("markerIndex", -1);
    		secondIndex = marker.getAttribute("secondIndex", -1);
    		marker.delete();
    		VariablesAndConstants.annotationMarkers[markerIndex][secondIndex] = null; //so checkForVulnerabilities will work
    	}
    	catch(Exception e)
    	{
    		System.out.println("exception deleting marker");
    	}
    	
    	//then find the corresponding marker and deal with it if no more annotations remain.
    	if(InterfaceUtil.hasAnnotationsRemaining(markerIndex) == false)
    	{
    		InterfaceUtil.changeMarker("yellow.question", markerIndex, 0, 0);
    	}
    	
    	InterfaceUtil.checkForVulnerabilities();

    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return edu.uncc.aside.codeannotate.asideInterface.VariablesAndConstants.ANNOTATION_DELETE_DESC;
	}
	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		ImageDescriptor descriptor = Plugin
				.getImageDescriptor("greenDiamondSmall.png");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return image;
	}
 }
