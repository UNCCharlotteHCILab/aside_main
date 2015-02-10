package edu.uncc.aside.codeannotate.listeners;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import edu.uncc.aside.codeannotate.visitors.CodeAnnotateMarkerChangeVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CodeAnnotateMarkerChangeListener implements
		IResourceChangeListener {

	private static CodeAnnotateMarkerChangeListener listener;

	private CodeAnnotateMarkerChangeVisitor fVisitor;

	private CodeAnnotateMarkerChangeListener() {
		fVisitor = new CodeAnnotateMarkerChangeVisitor();
	}

	public static CodeAnnotateMarkerChangeListener getListener() {
		if (listener == null)
			listener = new CodeAnnotateMarkerChangeListener();
		return listener;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		/*
		 * This only happens when I save the the modification of resources, for
		 * instance, a file.
		 */
		IResourceDelta delta = event.getDelta();
		if (delta != null && event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				delta.accept(fVisitor);
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
	}

}
