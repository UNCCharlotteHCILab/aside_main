package edu.uncc.aside.codeannotate.presentations;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.ISharedImages;

import edu.uncc.aside.codeannotate.Constants;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class IgnoreMarkerResolution implements IMarkerResolution2 {

	IResource resource;
	IProject project;
	
	private final static String LABEL = "3. Click me to remove this warning";
	
	@Override
	public String getLabel() {

		return LABEL;
	}

	@Override
	public void run(IMarker marker) {

		resource = marker.getResource();
		project = resource.getProject();

		Path path = retrivePathFromMarker(marker);
		Utils.removeMarkersOnPath(path);
		
		PathCollector pathCollector = ModelRegistry
				.getPathCollectorForProject(project);
		pathCollector.removePath(path);
	}

	@Override
	public String getDescription() {

		return Constants.IGNORE_MARKER_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		ImageDescriptor descriptor = Plugin.getImageDescriptor("ignore.png");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return Plugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
	}

	/**
	 * Retrieves the Path that is identified by the marker.
	 * 
	 * @param marker
	 * @return
	 */
	private Path retrivePathFromMarker(IMarker marker) {

		IFile file = (IFile) resource.getAdapter(IFile.class);

		ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);

		if (unit == null) {
			return null;
		}

		CompilationUnit astRoot = Utils.getCompilationUnit(unit);
		int charStart = marker.getAttribute(IMarker.CHAR_START, -1);
		int charEnd = marker.getAttribute(IMarker.CHAR_END, -1);
		int length = charEnd - charStart;

		NodeFinder finder = new NodeFinder(astRoot, charStart, length);

		ASTNode node = finder.getCoveringNode();
		Point nodePoint = new Point(node, astRoot, resource);

		return getPathByAccessor(nodePoint);

	}

	private Path getPathByAccessor(Point nodePoint) {

		if (nodePoint == null)
			return null;

		PathCollector pathCollector = ModelRegistry
				.getPathCollectorForProject(project);
		List<Path> paths = pathCollector.getAllPaths();
		Point accessor = null;
		for (Path path : paths) {
			accessor = path.getAccessor();
			if (accessor.equalsTo(nodePoint)) {
				return path;
			}
		}

		return null;
	}

}
