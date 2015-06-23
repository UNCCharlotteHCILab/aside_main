package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class EncodingResolution implements IMarkerResolution,
		IMarkerResolution2 {

	String resolutionType;
	String label;
	
    EncodingResolution(String label, String resolutionType) {
       this.label = label;
       this.resolutionType = resolutionType;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
       MessageDialog.openInformation(null, "Encoding Demo", "This quick-fix is not yet implemented");
    	System.out.println("Resolution run");

    }
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		String methodName = "";
		String methodAction = "";
		switch(resolutionType){
		case "url":
			methodName = "encodeForURL";
			methodAction = "URL encoding (percent encoding)";
			break;
		case "html":
			methodName = "encodeForHTML";
			methodAction = "HTML encoding";
			break;
		}
		String description = "Encode the output using the Enterprise Security API."
				+ "<p><p>The " + methodName +  " method of the Encoder interface performs "
				+ methodAction + " on the given output."
				+ "<p><p>Go to LINK for documentation.";
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
