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
public class Old_odeAnnotateMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2 {

	public final static String TARGET_MARKER_TYPE_1 = "CodeAnnotate.annotationQuestion";
	public final static String TARGET_MARKER_TYPE_2 = "CodeAnnotate.annotationQuestionChecked";

	private IMarkerResolution[] NO_RESOLUTION = new IMarkerResolution[0];
	private IMarkerResolution ignoreResolution;
	private IMarkerResolution readMoreResolution;
	IMarkerResolution annotationResolution;

	/*
	 * public 0-argument constructor required by extension point definition
	 */
	public Old_odeAnnotateMarkerResolutionGenerator() {
		ignoreResolution = new IgnoreMarkerResolution();
	}

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {

		if (hasResolutions(marker)) {
			IMarkerResolution[] resolutions = new IMarkerResolution[3];

			readMoreResolution = new Old_ReadMoreMarkerResolution();
			annotationResolution = new AnnotationResolution();
			resolutions[0] = readMoreResolution;
			resolutions[1] = annotationResolution;								
			resolutions[2] = ignoreResolution;

			return resolutions;
		} else {
			return NO_RESOLUTION;
		}

	}

	@Override
	public boolean hasResolutions(IMarker marker) {

		try {
			String type = marker.getType();
			if (type.equals(TARGET_MARKER_TYPE_1)
					|| type.equals(TARGET_MARKER_TYPE_2))
				return true;
			else
				return false;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

	}

}
