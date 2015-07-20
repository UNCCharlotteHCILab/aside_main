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
	String markerId;
	
    EncodingResolution(String label, String resolutionType, String markerId) {
       this.label = label;
       this.resolutionType = resolutionType;
       this.markerId = markerId;
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
		String variableName = "output";
		switch(resolutionType){
		case "URL":
			methodName = "encodeForURL";
			methodAction = "URL encoding (percent encoding)";
			break;
		case "HTML":
			methodName = "encodeForHTML";
			methodAction = "HTML encoding";
			break;
		}
		if(markerId.equals("outputEncoding1")){
			variableName = "\"currentUser.getUsername()\"";
		}else if(markerId.equals("outputEncoding2")){
			variableName = "\"account.toString()\"";
		}
		String description = "<p><p>This will add code to encode/abstract data returned by " +variableName+ " to ensure that any injected commands "
				+ "aren't run when loading a page's " + resolutionType + ".<p><p>The generated code uses the "
				+ methodName + " method from the Enterprise Security API (ESAPI) to perform " + methodAction + ". "
				+ "Go to LINK for documentation.";
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
