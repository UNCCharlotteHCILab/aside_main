package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class SQLTitleResolution implements IMarkerResolution,
		IMarkerResolution2 {

	String label;
	int markerId = -1;
	
    SQLTitleResolution(String label, int markerId) {
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
		String description = "This code uses external data to create a dynamic SQL statement. "
				+ "This input data could contain an SQL command that executes unwanted actions (known as an SQL injection attack)."
				+ "<p><p>Change this dynamic SQL statement to a Prepared Statement.  Choose Read More to find out how.";
		 return description;
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