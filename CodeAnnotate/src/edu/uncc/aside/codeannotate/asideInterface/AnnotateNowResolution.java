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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.AnnotationTypeLookup;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;

/*
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;
*/

public class AnnotateNowResolution implements IMarkerResolution,
IMarkerResolution2{
	

	String label;
	String markerType = "";
	String rendomId= "";
	
    AnnotateNowResolution(String label, String incomingType, String randomId) {
       this.label = label;
       this.markerType = incomingType;
       this.rendomId=randomId;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
      // MessageDialog.openInformation(null, "Entering Highlight Mode", "Please highlight and hit CTRL+Zero");
    	System.out.println("Resolution run");
    	//allow the annotating to work
    	VariablesAndConstants.isAnnotatingNow = true;
    	
    	//save marker so it can be changed later
    	VariablesAndConstants.currentIndex = marker.getAttribute("markerIndex", -1);
    	
    	
    	//now create selection listener to detect when they select text for an annotation and assign the listener to the page
    	TextSelectionListener theListener = new TextSelectionListener();
    	VariablesAndConstants.currentSelectionListener = theListener;
    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(theListener);
    	
    	/* Doesn't seem to work. maybe this can change annotations rather than replace them
    	
    	RGB theRGB = new RGB(0,0,99);
		AnnotationTypeLookup atl = EditorsUI.getAnnotationTypeLookup();
		AnnotationPreferenceLookup apl = EditorsUI.getAnnotationPreferenceLookup();
		AnnotationPreference ap = null;
		ap = apl.getAnnotationPreference(
				atl.getAnnotationType(marker));
				ap.setHighlightPreferenceValue(false);
    	*/
    	
    	
    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		 return edu.uncc.aside.codeannotate.asideInterface.VariablesAndConstants.ADD_ANNOTATION_DESC;
	}
	@Override
	public Image getImage() {
		
		ImageDescriptor descriptor;
		
		if(markerType.equals("yellow.question"))
		{
			descriptor = Plugin
					.getImageDescriptor("yellowQuestion.png");
		}
		else if(markerType.equals("red.flag"))
		{
			descriptor = Plugin
					.getImageDescriptor("redFlag.png");
			
		}
		else if(markerType.equals("green.check"))
		{
			descriptor = Plugin
					.getImageDescriptor("greenCheck.png");
			
		}
		else
		{
			descriptor = Plugin
					.getImageDescriptor("yellowQuestion.png");
		}
		
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return image;
		
	}
 }