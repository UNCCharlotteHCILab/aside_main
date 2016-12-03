package edu.uncc.aside.codeannotate;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IAnnotationImageProvider;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class AnnotationImageProvider implements IAnnotationImageProvider {

		private final static String UNKNOWN = "unknown";

	/*
	 * 0-argument constructor as required by extension point
	 */
	public AnnotationImageProvider() {
		super();
	}

	@Override
	public ImageDescriptor getImageDescriptor(String imageDescritporId) {
		
		ImageDescriptor descriptor = null;
		
		if (imageDescritporId.equals(PluginConstants.MARKER_ANNOTATION_REQUEST)) {
			descriptor = Plugin.getImageDescriptor("red.jpeg");

		} else if (imageDescritporId.equals(PluginConstants.MARKER_ANNOTATION_ANSWER)) {
			descriptor = Plugin.getImageDescriptor("green.jpeg");

		} else if(imageDescritporId.equals(PluginConstants.MARKER_ANNOTATION_CHECKED)){
			descriptor = Plugin.getImageDescriptor("yellow.jpeg");
			
		}else{
			descriptor = Plugin.getImageDescriptor("uncc.jpg");
		}

		return descriptor;
	}

	@Override
	public String getImageDescriptorId(Annotation annotation) {

		String type = annotation.getType();
		
		if (type.equals(PluginConstants.MARKER_ANNOTATION_REQUEST)) {
			return PluginConstants.MARKER_ANNOTATION_REQUEST;
		} else if (type.equals(PluginConstants.MARKER_ANNOTATION_ANSWER)) {
			return PluginConstants.MARKER_ANNOTATION_ANSWER;
		} else if(type.equals(PluginConstants.MARKER_ANNOTATION_CHECKED)){
			return PluginConstants.MARKER_ANNOTATION_CHECKED;
		}else {
			return UNKNOWN;
		}

	}

	@Override
	public Image getManagedImage(Annotation annotation) {
		String type = annotation.getType();
		
		ImageDescriptor descriptor = getImageDescriptor(type);
		
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}
		return image;
	}
}