package edu.uncc.aside.codeannotate.presentations;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.ISharedImages;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CheckUndoResolution implements IMarkerResolution2 {

	private IProject project;
	private IResource resource;
	private Path path = null; 
	private AccessControlPoint check = null;
	
	private final static String LABEL = "1. Click me to undo this check";
	
	@Override
	public String getLabel() {	
		return LABEL;
	}

	@Override
	public void run(IMarker marker) {
		
		resource = marker.getResource();
		project = resource.getProject();
		
		retrievePathAndCheckFromMarker(marker);
		
		if(path != null && check != null){
			replaceAccessorMarkerOnPath(path);
			path.removeCheck(check);
		}
		
		try {
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void retrievePathAndCheckFromMarker(IMarker marker) {
		
		IFile file = (IFile) resource.getAdapter(IFile.class);

		ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);

		if (unit == null) {
			return;
		}

		CompilationUnit astRoot = Utils.getCompilationUnit(unit);
		int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
		int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
		int length = charEnd - charStart;

		NodeFinder finder = new NodeFinder(astRoot, charStart, length);

		ASTNode node = finder.getCoveringNode();
		AccessControlPoint nodePoint = new AccessControlPoint(node, astRoot, resource);
		
		ModelCollector modelCollector = ModelRegistry
				.getPathCollectorForProject(project);
		List<Path> paths = modelCollector.getAllPaths();
		List<AccessControlPoint> checks = null;
		for (Path _path : paths) {
			checks = _path.getChecks();
			for(AccessControlPoint _check : checks){
				if(_check.equalsTo(nodePoint)){
					check = _check;
					path = _path;
					return;
				}
			}
		}

	}

	private void replaceAccessorMarkerOnPath(Path path) {
		AccessControlPoint accessor = path.getSensitiveOperation();
		ASTNode node = accessor.getNode();
		IResource resource = accessor.getResource();
		CompilationUnit unit = accessor.getUnit();
		
		try {
			// First, gotta check whether there is a marker for the accessor node
			int char_start, length;
			
			IMarker[] questionMarkers = resource.findMarkers(PluginConstants.MARKER_ANNOTATION_REQUEST,
					false, IResource.DEPTH_ONE);
			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStartPosition()
						&& length == node.getLength()){
					return;
				}
					
			}
			
			IMarker[] markers = resource.findMarkers(PluginConstants.MARKER_ANNOTATION_CHECKED,
					false, IResource.DEPTH_ONE);
			
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStartPosition()
						&& length == node.getLength()){
					marker.delete();
					Utils.createQuestionMarker(node, resource, unit);
					return;
				}
					
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getDescription() {
		
		return PluginConstants.CHECK_UNDO_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		
		return Plugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_UNDO);
	}
}
