package edu.uncc.aside.codeannotate.visitors;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import edu.uncc.aside.codeannotate.Plugin;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CodeAnnotateMarkerChangeVisitor implements IResourceDeltaVisitor {

	public CodeAnnotateMarkerChangeVisitor() {
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {

		if (delta == null) 
			return false;
		

		IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();

		for (IMarkerDelta markerDelta : markerDeltas) {

			String type = markerDelta.getType();
			IResource resource = markerDelta.getResource();

			if (type.equals(Plugin.ANNOTATION_QUESTION)) {
				switch (markerDelta.getKind()) {
				case IResourceDelta.ADDED:
					System.err.println(type + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(type + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(type + " CHANGED " + resource.getName());
					break;
				}
			} else if (type.equals(Plugin.ANNOTATION_QUESTION_CHECKED)) {
				switch (markerDelta.getKind()) {
				case IResourceDelta.ADDED:
					System.err.println(type + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(type + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(type + " CHANGED " + resource.getName());
					break;
				}
			} else if (type.equals(Plugin.ANNOTATION_ANSWER)) {
				switch (markerDelta.getKind()) {
				case IResourceDelta.ADDED:
					System.err.println(type + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(type + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(type + " CHANGED " + resource.getName());
					break;
				}
			}
		}
		return true;
	}

}
