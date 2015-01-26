package edu.uncc.aside.codeannotate.presentations;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CodeAnnotateControlMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2 {

	public final static String TARGET_MARKER_TYPE = "CodeAnnotate.annotationAnswer";

	private IMarkerResolution[] NO_RESOLUTION = new IMarkerResolution[0];
	private IMarkerResolution undoResolution;

	public CodeAnnotateControlMarkerResolutionGenerator() {

	}

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		if (hasResolutions(marker)) {
			IMarkerResolution[] resolutions = new IMarkerResolution[1];

			undoResolution = new CheckUndoResolution();
			resolutions[0] = undoResolution;

			return resolutions;
		} else {
			return NO_RESOLUTION;
		}
	}

	@Override
	public boolean hasResolutions(IMarker marker) {
		try {
			String type = marker.getType();
			if (type.equals(TARGET_MARKER_TYPE))
				return true;
			else
				return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}

}
