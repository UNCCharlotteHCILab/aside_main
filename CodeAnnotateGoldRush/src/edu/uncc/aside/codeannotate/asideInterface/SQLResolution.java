package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

import edu.uncc.aside.codeannotate.Plugin;

public class SQLResolution implements IMarkerResolution, IMarkerResolution2 {

	String resolutionType;
	String label;
	
    SQLResolution(String label) {
       this.label = label;
    }
    public String getLabel() {
       return label;
    }
    public void run(IMarker marker) {
       MessageDialog.openInformation(null, "SQL Statement Demo", "This quick-fix is not yet implemented");
    	System.out.println("Resolution run");

    }
	@Override
	public String getDescription() {
		return "When using a PreparedStatement, the SQL statement is precompiled so the DBMS does not compile"
				+ " the statement when executed.  <p><p>In other words, the statement and the data are not presented "
				+ "on the same line.  By separating the two, the DBMS does not execute the data as its own statement.  "
				+ "This prevents unintended queries";
	}
	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
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
