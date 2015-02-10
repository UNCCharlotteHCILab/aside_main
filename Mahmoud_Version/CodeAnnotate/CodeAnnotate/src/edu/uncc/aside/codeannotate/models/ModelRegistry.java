package edu.uncc.aside.codeannotate.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import edu.uncc.aside.codeannotate.visitors.AddModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class ModelRegistry implements PropertyChangeListener {

	private static ModelRegistry instance;

	private ModelRegistry() {
	}

	private static Map<IProject, PathCollector> pathCollectors = Collections
			.synchronizedMap(new HashMap<IProject, PathCollector>());

	public static ModelRegistry getInstance() {
		if (instance == null)
			instance = new ModelRegistry();

		return instance;
	}

	public static PathCollector getPathCollectorForProject(IProject project) {
		if (pathCollectors == null)
			return null;

		return pathCollectors.get(project);
	}

	public static void registerPathCollector(PathCollector collector) {
		collector.accept(AddModelVisitor.getInstance());
		synchronized (pathCollectors) {
			pathCollectors.put(collector.getProject(), collector);
		}
	}

	public static Collection<PathCollector> getAllRegisteredChildren() {
		return pathCollectors.values();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		PathCollector pathCollector;
		Path path;

		String propertyName = event.getPropertyName();

		Object source = event.getSource();

		if (source instanceof PathCollector) {
			pathCollector = (PathCollector) source;

			if (propertyName.equals("addPath")) {
				synchronized (pathCollectors) {
					pathCollectors.put(pathCollector.getProject(),
							pathCollector);
				}
			} else if (propertyName.equals("removePath")) {
				synchronized (pathCollectors) {
					pathCollectors.put(pathCollector.getProject(),
							pathCollector);
				}
			}

		} else if (source instanceof Path) {
			path = (Path) source;
			pathCollector = (PathCollector) path.getParent();

			if (propertyName.equals("addCheck")) {
				synchronized (pathCollectors) {
					pathCollectors.put(pathCollector.getProject(),
							pathCollector);
				}
			} else if (propertyName.equals("removeCheck")) {

				pathCollector.replacePath(path);
				synchronized (pathCollectors) {
					pathCollectors.put(pathCollector.getProject(),
							pathCollector);
				}
			}

		} else {
			/*
			 * unexpected problem, best practice to log it, but oh well, I am
			 * lazy, so I save it for later.
			 */
		}

	}

}
