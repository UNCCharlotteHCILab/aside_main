package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class ValidationTitleResolution implements IMarkerResolution,
		IMarkerResolution2 {

	String label;
	int markerId = -1;
	
    ValidationTitleResolution(String label, int markerId) {
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
		
		descriptionString = "This code uses the getParameter method from the HttpServletRequest to access external user data.  "
				+ "If the input is not validated attackers can exploit this vulnerability and insert malicious code."
				+ "<p><p>View the options below to discover input validation techniques for specific types of allowed input data"
				+ "<p><p>Go to https://www.owasp.org/index.php/Data_Validation for more information.";
		
		 return descriptionString;
	}
	@Override
	public Image getImage() {
		
		ImageDescriptor descriptor = Plugin
				.getImageDescriptor("devil.png");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}
		return image;
	}
}
