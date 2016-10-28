package edu.uncc.aside.asideInterface;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.utils.InterfaceUtil;

/*
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;
*/

public class AnnotationCodeResolution implements IMarkerResolution,
IMarkerResolution2{
	

	String label;
	IMarker callingMarker = null;
    AnnotationCodeResolution(String label, IMarker incomingMarker) {
       this.label = label;
       this.callingMarker = incomingMarker;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) 
    {
    	//get the request marker and navigate to it
    	int markerIndex = marker.getAttribute("markerIndex", -1);
    	IMarker associatedRequestMarker = VariablesAndConstants.annotationRequestMarkers[markerIndex];
    	
    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    	IWorkbenchPage page = window.getActivePage();
    	try 
    	{
    		
    		IDE.openEditor(page, associatedRequestMarker);
    		
    		//the below line is how to open a page without a marker on older versions. the line beneath it is how to open a page without a marker on newer versions.
    		//page.openEditor(new FileEditorInput(file), desc.getId());
			//IDE.openEditor(page, file, true);
    		
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}

    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return InterfaceUtil.getResolutionDescription(this.callingMarker);
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