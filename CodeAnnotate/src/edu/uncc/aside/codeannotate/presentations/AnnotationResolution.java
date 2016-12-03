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
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;

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
public class AnnotationResolution implements IMarkerResolution2 {

	public final static String LABEL = "2. Click me to annotate a control logic";

	private IProject project;
	private IResource resource;

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void run(IMarker marker) {
		IInformationControl control = showPresenter();
		resource = marker.getResource();
		project = resource.getProject();

		Path path = retrivePathFromMarker(marker);
		switchAnnotationPath(path, control);

	}

	@Override
	public String getDescription() {

		return PluginConstants.ANNOTATION_RESOLUTION_DESC;
	}

	@Override
	public Image getImage() {
		ImageDescriptor descriptor = Plugin
				.getImageDescriptor("annotation.jpeg");
		Image image = Plugin.imageCache.get(descriptor);

		if (image == null) {
			image = descriptor.createImage();
			Plugin.imageCache.put(descriptor, image);
		}

		return image;
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
		AccessControlPoint nodePoint = new AccessControlPoint(node, astRoot, resource);

		return getPathByAccessor(nodePoint);

	}

	private Path getPathByAccessor(AccessControlPoint nodePoint) {

		if (nodePoint == null)
			return null;

		ModelCollector modelCollector = ModelRegistry
				.getPathCollectorForProject(project);
		List<Path> paths = modelCollector.getAllPaths();
		AccessControlPoint accessor = null;
		for (Path path : paths) {
			accessor = path.getSensitiveOperation();
			if (accessor.equalsTo(nodePoint)) {
				return path;
			}
		}

		return null;
	}

	private void switchAnnotationPath(Path path, IInformationControl control) {

		Plugin.annotationPath = path;
		Plugin.currentInformationControl = control;
	}

	private IInformationControl showPresenter() {
		int maxWidth = 400, maxHeight = 200;
		Display display = Plugin.getDefault().getWorkbench().getDisplay();
		Shell shell = display.getActiveShell();
		org.eclipse.swt.graphics.Point shell_location = shell.getLocation();
		Rectangle bounds = shell.getBounds();
		int x = shell_location.x + bounds.width - maxWidth;
		int y = shell_location.y + bounds.height - maxHeight;

		IInformationControl control = Plugin.currentInformationControl;

		if (control == null)
			control = new DefaultInformationControl(shell, false);

		control.setLocation(new org.eclipse.swt.graphics.Point(x, y));
		control.setSize(maxWidth, maxHeight);
		control.setSizeConstraints(maxWidth, maxHeight);

		control.setBackgroundColor(new Color(display, new RGB(255, 192, 203)));
		control.setForegroundColor(new Color(display, new RGB(0, 0, 0)));
		control.setVisible(true);
		control.setInformation("<p><p/><p>To complete annotating, please select a boolean condition check that needs to be performed before accessing data in database or other data storage. </p> " +
				"<p>Next, press <b><i>Ctrl+0</i></b> to complete the process. </p><br/>");

		return control;

	}
}
