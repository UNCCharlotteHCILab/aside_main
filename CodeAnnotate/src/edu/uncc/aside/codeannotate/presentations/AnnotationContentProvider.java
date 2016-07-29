package edu.uncc.aside.codeannotate.presentations;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.models.InputValidationPoint;
//import edu.uncc.aside.codeannotate.models.InputValidationsCollector;
import edu.uncc.aside.codeannotate.models.Model;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @author Mahmoud Mohammadi (mmoham12 at uncc dot edu)
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
			
			if (oldInput instanceof ModelCollector) {
				ModelCollector collector = (ModelCollector) oldInput;
				
				List<Path> paths = collector.getAllPaths();
				if (!paths.isEmpty()) {
					
					synchronized(paths){
						Iterator<Path> it = paths.iterator();
						while(it.hasNext()){
							Path child = it.next();
							
							// Calls listener property change method for each child
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
			List<AccessControlPoint> checks = path.getChecks();
			if (!checks.isEmpty()) {
				synchronized(checks){
					Iterator<AccessControlPoint> it = checks.iterator();
					while(it.hasNext()){
						AccessControlPoint child = it.next();
						
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
		return getElementsOf(obj);
		
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
// Return more elements ( Tree nodes) for element that has children and not leaves.
	public Object[] getElementsOf(Object obj) {
		
		if (obj instanceof ModelCollector) {
			ModelCollector collector = (ModelCollector) obj;
			ModelCollector _collector = ModelRegistry
					.getPathCollectorForProject(collector.getProject());

			return _collector.getAllModels().toArray();
		}

		if (obj instanceof Path) {
			Path path = (Path) obj;
			ModelCollector parent = (ModelCollector) path.getParent();
			ModelCollector collector = ModelRegistry
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
	public boolean hasChildren(Object obj) {

		if (obj == null) {
			return false;
		}
		
		Object[] kids =getElementsOf(obj);
		
		if ( kids == null || kids.length ==0)
			return false;
		
		return true;
		
	}

}
