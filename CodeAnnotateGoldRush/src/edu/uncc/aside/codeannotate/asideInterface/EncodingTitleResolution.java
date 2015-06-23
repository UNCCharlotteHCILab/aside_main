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
	int markerId = -1;
	
    EncodingTitleResolution(String label, int markerId) {
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
		
		descriptionString = "This code uses the println method from the PrintWriter to output data to the application. "
				+ "If this output is not encoded, attackers can exploit this vulnerability and insert malicious code into the application."
				+ "<p><p>Use the options below to generate code to encode output data for different kinds of content."
				+ "<p><p>Go to https://www.owasp.org/index.php/Data_Validation for more information.";
		
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
