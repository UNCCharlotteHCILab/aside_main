package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class ValidationResolution implements IMarkerResolution,
		IMarkerResolution2 {

	String resolutionType;
	String label;
	
    ValidationResolution(String label, String resolutionType) {
       this.label = label;
       this.resolutionType = resolutionType;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
       MessageDialog.openInformation(null, "Validation Demo", "This quick-fix is not yet implemented");
    	System.out.println("Resolution run");

    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		String description = "";
		switch(resolutionType){
		case "alphabet":
			description = "Alphabetical characters and numbers only";
			break;
		case "http":
			description = "HTTP code";
			break;
		case "url":
			description = "URL";
			break;
		case "credit":
			description = "credit";
			break;
		case "email":
			description = "email";
			break;
		case "ssn":
			description = "ssn";
			break;
		default:
			description = "i should put something here";
		}

		return description;
	}
	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
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
