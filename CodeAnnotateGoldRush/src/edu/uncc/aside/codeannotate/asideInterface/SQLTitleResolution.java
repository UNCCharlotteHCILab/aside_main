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
		String description = "This code uses external data to create a dynamic SQL statement. Attackers could manipulate this data to run their own SQL commands.  This is known as an SQL injection attack."
				+ "<p><p>To fix this vulnerability, change this dynamic SQL statement to a prepared SQL statement, which cannot be manipulated by an attacker. Choose \"Read More\" for details on creating prepared SQL statements.";
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
