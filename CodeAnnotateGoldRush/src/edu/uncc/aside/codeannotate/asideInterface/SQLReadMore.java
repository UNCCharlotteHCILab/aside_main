package edu.uncc.aside.codeannotate.asideInterface;

import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import edu.uncc.aside.codeannotate.Plugin;

public class SQLReadMore implements IMarkerResolution, IMarkerResolution2 {

	String label;
    SQLReadMore(String label) {
       this.label = label;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker)
    {
    	 try 
    	 {
    		 String lineText = VariablesAndConstants.methodNames[marker.getAttribute("markerIndex", -1)];
    		 IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(1, "myId", "ASIDE More Information", "ASIDE More Information");
    		 URL url = new URL("https://50e55cd9aea1a61d3f1742e818435f8ba71d1cc5.googledrive.com/host/0BzEfllwR2DGafkFLWV9RckQtUzBleXcxbHZTX0M1d0h5VXJwTzJwWU5ILUE2TmR6Y2NFUFU/ASIDE%20SQL.html");
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
		return "Go to a web page to learn why you are seeing this marker and what you can do to fix it";
		//return edu.uncc.aside.codeannotate.asideInterface.VariablesAndConstants.READ_MORE_DESC;
	}
	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		ImageDescriptor descriptor = Plugin
				.getImageDescriptor("redQuestion.png");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return image;
	}
 }
