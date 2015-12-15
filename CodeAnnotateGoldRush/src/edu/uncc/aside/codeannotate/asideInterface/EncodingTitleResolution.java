package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class EncodingTitleResolution implements IMarkerResolution,
		IMarkerResolution2 {

	String label;
	String markerId;
	
    EncodingTitleResolution(String label, String markerId) {
       this.label = label;
       this.markerId = markerId;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
       MessageDialog.openInformation(null, "Red Devil Demo", "This quick-fix is not yet implemented");
    	System.out.println("Resolution run");

    }
	@Override
	public String getDescription() {		
		String descriptionString = "";
		String outputType = "data";
		if(markerId.equals("outputEncoding1")){
			outputType = "\"currentUser.getUsername()\"";
		}else if(markerId.equals("outputEncoding2")){
			outputType = "\"account.toString()\"";
		}
		descriptionString = "This code uses the println method from the PrintWriter to output data returned by " + outputType + " to the application. "
				+ "If the data returned by " + outputType + " is not encoded, attackers can exploit this vulnerability and insert malicious code into the application "
				+ "(known as Cross-Site Scripting)."
				+ "<p><p>Use the options below to generate code to encode data from" + outputType + " or click Read More for more information.";
		
		 return descriptionString;
	}
	@Override
	public Image getImage() {
		
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