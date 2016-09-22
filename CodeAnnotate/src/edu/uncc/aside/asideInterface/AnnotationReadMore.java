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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;

/*
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;
*/

public class AnnotationReadMore implements IMarkerResolution,
IMarkerResolution2{
	

	String label;
    AnnotationReadMore(String label) {
       this.label = label;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker)
    {
    	 try 
    	 {
    		 IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(1, "myId", "" + Plugin.PLUGIN_NAME + " More Information", "" + Plugin.PLUGIN_NAME + " More Information");
    		 URL url = Platform.getBundle(PluginConstants.PLUGIN_ID).getEntry("files\\ASIDE_AccessControl.html");
    		// URL url = new URL("https://3a3f55c7a88ea8fa30228719dabc9ba137fbf338.googledrive.com/host/0B_sYP_Y3om2XZ2RaZUttQXp0dEE/ASIDE_AccessControl.html");
    		 webBrowser.openURL(url);
    		 URLConnection urlConnection = url.openConnection();
    		 urlConnection.setDoOutput(true);
		 } 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}	    

    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return edu.uncc.aside.asideInterface.VariablesAndConstants.PLACEHOLDER_TEXT;
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