package edu.uncc.aside.codeannotate.presentations;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.dialogs.ViewLabelProvider;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class AnnotationTreeLabelProvider extends ColorDecoratingLabelProvider {

	public AnnotationTreeLabelProvider() {
		super(new ViewLabelProvider(null, null), null);
	}

	@Override
	public void dispose() {
		Map<ImageDescriptor, Image> imageCache = Plugin.imageCache;
		for (Iterator<Image> i = imageCache.values().iterator(); i.hasNext();) {
			(i.next()).dispose();
		}

		imageCache.clear();
	}

	@Override
	public Image getImage(Object element) {
		ImageDescriptor descriptor = null;

		if (element == null) {
			descriptor = Plugin.getImageDescriptor("owasp.gif");
		} else if (element instanceof Path) {
			descriptor = Plugin.getImageDescriptor("red.jpeg");
		} else if (element instanceof Point) {
			descriptor = Plugin.getImageDescriptor("green.jpeg");
		} else {
			descriptor = Plugin.getImageDescriptor("devil.jpeg");
		}
		// obtain the cached image corresponding to the descriptor
		Image image = Plugin.imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}
		return image;
	}

	@Override
	public String getText(Object element) {

		String text = "The current input for this view is not clear.";

		if (element == null)
			return text;

		if (element instanceof Path) {
			text = ((Path) element).getPathID();
		} else if (element instanceof Point) {
			text = ((Point) element).getPointID();
		}
		return text;
	}

}
