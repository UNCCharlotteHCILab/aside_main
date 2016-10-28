package edu.uncc.aside.codeannotate.visitors;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import edu.uncc.aside.codeannotate.PluginConstants;
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
// This method just logs the adding, removing and changing of the markers!
		if (delta == null) 
			return false;
		

		IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();

		for (IMarkerDelta markerDelta : markerDeltas) {

			String markerType = markerDelta.getType();
			
			IResource resource = markerDelta.getResource();

		//	if (markerType.equals(PluginConstants.ANNOTATION_QUESTION)) {
				
				switch (markerDelta.getKind()) {
				
				case IResourceDelta.ADDED:
					System.err.println(markerType + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(markerType + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(markerType + " CHANGED " + resource.getName());
					break;
				}
			/* Commented by Mahmoud	
			} else 
				if (markerType.equals(PluginConstants.PluginConstants.MARKER_ANNOTATION_CHECKED)) {
				switch (markerDelta.getKind()) {
				case IResourceDelta.ADDED:
					System.err.println(markerType + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(markerType + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(markerType + " CHANGED " + resource.getName());
					break;
				}
			} else if (markerType.equals(PluginConstants.ANNOTATION_ANSWER)) {
				switch (markerDelta.getKind()) {
				case IResourceDelta.ADDED:
					System.err.println(markerType + " ADDED " + resource.getName());
					break;
				case IResourceDelta.REMOVED:
					System.err.println(markerType + " REMOVED " + resource.getName());
					break;
				case IResourceDelta.CHANGED:
					System.err.println(markerType + " CHANGED " + resource.getName());
					break;
				}
			}
			*/
		}
		return true;
	}

}
