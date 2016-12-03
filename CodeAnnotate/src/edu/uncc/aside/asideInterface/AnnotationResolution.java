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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;

/*
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.ASIDEMarkerAndAnnotationUtil;
import edu.uncc.sis.aside.utils.Converter;
*/

public class AnnotationResolution implements IMarkerResolution,
IMarkerResolution2{
	

	String label;
    AnnotationResolution(String label) {
       this.label = label;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
       MessageDialog.openInformation(null, "Green Diamond Demo", "This quick-fix is not yet implemented");
    	System.out.println("Resolution run");
 
    	/*
    	IWorkbench theWorkbench = Plugin.getDefault().getWorkbench();
    	IWorkbenchWindow activeWindow = theWorkbench.getActiveWorkbenchWindow();
    	IWorkbenchPage activePage = activeWindow.getActivePage();
    	
    	
    	IWorkbench theWorkbench = Plugin.getDefault().getWorkbench();
		   IWorkbenchWindow[] workbenchWindows = theWorkbench.getWorkbenchWindows();
		   Display display = Plugin.getDefault().getWorkbench().getDisplay();
		   Cursor cursor = new Cursor(display, SWT.CURSOR_CROSS);
		   
		   for(int i = 0; i < workbenchWindows.length; i++)
		   {
			   Shell currentShell = workbenchWindows[i].getShell();
			   currentShell.setCursor(cursor);
		   }
		   */

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