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
	String markerId;
	
    ValidationTitleResolution(String label, String markerId) {
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
		if(markerId.equals("inputValidation1")){
			descriptionString = "The variable \"accountName\" is external user data read from the getParameter method from the HttpServletRequest. ";
		}else if(markerId.equals("inputValidation3")){
			descriptionString = "The variable \"newBalance\" is external user data read from the getParameter method from the HttpServletRequest. ";
		}else{
			descriptionString = "The data read from request.getParameter is external user data read from the HttpServletRequest. ";
		}
		descriptionString += "If the input is not validated, attackers can exploit this vulnerability and insert malicious code (known as Cross Site Scripting)."
				+ "<p><p>Use the options below to generate code that validates this input or click Read More for more information.";
		
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
