package edu.uncc.aside.codeannotate.presentations;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.models.Model;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class AnnotationContentProvider implements ITreeContentProvider {
	private static Object[] EMPTY_ARRAY = new Object[0];
	private TreeViewer viewer;

	public AnnotationContentProvider() {

	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;

		if (oldInput != null && Plugin.getDefault().getAnnotationView() != null) {
			if (oldInput instanceof PathCollector) {
				PathCollector collector = (PathCollector) oldInput;
				
				List<Path> paths = collector.getAllPaths();
				if (!paths.isEmpty()) {
					
					synchronized(paths){
						Iterator<Path> it = paths.iterator();
						while(it.hasNext()){
							Path child = it.next();
							child.removePropertyChangeListener(Plugin.getDefault()
									.getAnnotationView());
						}
					}
					
				}

				collector.removePropertyChangeListener(Plugin.getDefault()
						.getAnnotationView());
			}

		} else if (oldInput instanceof Path && Plugin.getDefault().getAnnotationView() != null) {
			Path path = (Path) oldInput;
			List<Point> checks = path.getChecks();
			if (!checks.isEmpty()) {
				synchronized(checks){
					Iterator<Point> it = checks.iterator();
					while(it.hasNext()){
						Point child = it.next();
						child.removePropertyChangeListener(Plugin.getDefault()
								.getAnnotationView());
					}
				}
				
			}

			path.removePropertyChangeListener(Plugin.getDefault()
					.getAnnotationView());
		}
	}

	@Override
	public Object[] getChildren(Object obj) {

		if (obj instanceof PathCollector) {
			PathCollector collector = (PathCollector) obj;
			PathCollector _collector = ModelRegistry
					.getPathCollectorForProject(collector.getProject());

			return _collector.getAllPaths().toArray();
		}

		if (obj instanceof Path) {
			Path path = (Path) obj;
			PathCollector parent = (PathCollector) path.getParent();
			PathCollector collector = ModelRegistry
					.getPathCollectorForProject(parent.getProject());

			Path _parent = path;
			for (Path kid : collector.getAllPaths()) {
				if (kid.equalsTo(path)) {
					_parent = kid;
					break;
				}
			}
			return _parent.getChecks().toArray();
		}

		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	@Override
	public Object getParent(Object obj) {

		if (obj instanceof Model) {
			Model _model = (Model) obj;
			return _model.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof PathCollector) {

			PathCollector collector = (PathCollector) obj;
			collector = ModelRegistry.getPathCollectorForProject(collector
					.getProject());
			
			List<Path> children = collector.getAllPaths();
			if (children == null || children.isEmpty())
				return false;

			return true;
		} else if (obj instanceof Path) {
			Path path = (Path) obj;
			PathCollector parent = (PathCollector) path.getParent();
			PathCollector collector = ModelRegistry
					.getPathCollectorForProject(parent.getProject());
			for (Path kid : collector.getAllPaths()) {
				if (kid.equalsTo(path)) {
					List<Point> children = kid.getChecks();
					if (children == null || children.isEmpty())
						return false;
					return true;
				}
			}

		}
		return false;
	}

}
