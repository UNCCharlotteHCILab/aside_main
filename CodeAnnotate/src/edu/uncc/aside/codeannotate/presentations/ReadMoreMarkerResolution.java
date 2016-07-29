package edu.uncc.aside.codeannotate.presentations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.ISharedImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class ReadMoreMarkerResolution implements IMarkerResolution2 {

	private final static String HOST_URL = "http://hci.sis.uncc.edu/aside/";
	private final static String PAGE_TITLE = "CodeAnnotate More Information";
	private final static String PAGE_TIP = "";
	private final static String BROWSER_ID = "CodeAnnotate";

	private final static String LABEL = "1. Click me to read more about this warning.";
	private final static String ACESS_CONTROL = "accessControl";

	private IWorkbenchBrowserSupport browserSupport;
	private IWebBrowser browser;

	public ReadMoreMarkerResolution() {
	
	}

	@Override
	public String getLabel() {

		return LABEL;
	}

	@Override
	public void run(IMarker marker) {

		Plugin.getDefault().getWorkbench().getDisplay()
				.asyncExec(new Runnable() {

					@Override
					public void run() {

						try {
							browserSupport = Plugin.getDefault().getWorkbench().getBrowserSupport();
							browser = browserSupport.createBrowser(
									IWorkbenchBrowserSupport.AS_VIEW, BROWSER_ID, PAGE_TITLE,
									PAGE_TIP);
							URL url = new URL(HOST_URL + ACESS_CONTROL);

							browser.openURL(url);
							URLConnection connection = url.openConnection();
							connection.setDoOutput(true);

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (PartInitException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

	}

	@Override
	public String getDescription() {

		return PluginConstants.READ_MORE_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		ImageDescriptor descriptor = Plugin.getImageDescriptor("uncc.jpg");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return Plugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP);
	}

}
