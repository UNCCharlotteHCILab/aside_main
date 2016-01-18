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
	String markerId;
	
    ValidationResolution(String label, String resolutionType, String markerId) {
       this.label = label;
       this.resolutionType = resolutionType;
       this.markerId = markerId;
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
		String inputDescription = "";
		String variableName = "input";
		switch(resolutionType){
		case "alphabet":
			inputDescription = "alphabetical characters and numbers";
			break;
		case "http":
			inputDescription = "characters used in HTTP code: alphanumeric and special characters .-/=_!$*?@";
			break;
		case "url":
			inputDescription = "appropriate protocol-host-port format";
			break;
		case "credit":
			inputDescription = "credit card numbers in the form of 'xxxx.xxxx.xxxx.xxxx'";
			break;
		case "email":
			inputDescription = "email addresses in the form of 'foo@foo.foo'";
			break;
		case "ssn":
			inputDescription = "social security numbers in the form of 'xxx-xx-xxxx'";
			break;
		default:
			inputDescription = "";
		}
		if(markerId.equals("inputValidation1")){
			variableName = "\"accountName\"";
		}else if(markerId.equals("inputValidation3")){
			variableName = "\"newBalance\"";
		}
		
		String description =  "<p><p>This will generate code to ensure that " + variableName + " only contains "
				+ inputDescription + ". All other characters will throw an exception."
				+ "<p><p>The generated code uses getValidInput method from the Enterprise Security API (ESAPI). Go to LINK for more information.";
		
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
