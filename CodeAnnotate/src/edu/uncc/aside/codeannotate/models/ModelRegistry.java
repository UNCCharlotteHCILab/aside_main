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

	private static Map<IProject, ModelCollector> modelCollectors = Collections
			.synchronizedMap(new HashMap<IProject, ModelCollector>());
	
	//MM
	/*
	private static Map<IProject, InputValidationsCollector> inputValidationsCollectors = Collections
			.synchronizedMap(new HashMap<IProject, InputValidationsCollector>());
*/
	public static ModelRegistry getInstance() {
		if (instance == null)
			instance = new ModelRegistry();

		return instance;
	}

	//MM
	/*
	public static InputValidationsCollector getInputValidationsCollectorForProject(IProject project) {
		if (inputValidationsCollectors == null)
			return null;

		return inputValidationsCollectors.get(project);
	}
	*/
	
	public static ModelCollector getPathCollectorForProject(IProject project) {
		if (modelCollectors == null)
			return null;

		return modelCollectors.get(project);
	}

	public static void registerPathCollector(ModelCollector collector) {

		collector.accept(AddModelVisitor.getInstance());

		synchronized (modelCollectors) {
			modelCollectors.put(collector.getProject(), collector);
		}
	}
	/*
	public static void registerInputValidationsCollector(InputValidationsCollector collector) {

		collector.accept(AddModelVisitor.getInstance());

		synchronized (inputValidationsCollectors) {
			inputValidationsCollectors.put(collector.getProject(), collector);
		}
	}
	*/

	public static Collection<ModelCollector> getAllRegisteredChildren() {
		return modelCollectors.values();
	}

	//MM gets called after firing the events in e.g., addPath method
	@Override
	public void propertyChange(PropertyChangeEvent event) {

		ModelCollector modelCollector;
		Path path;
		InputValidationPoint point;
	//	InputValidationsCollector pointCollector;

		String propertyName = event.getPropertyName();

		Object source = event.getSource();

		if (source instanceof ModelCollector) {
			modelCollector = (ModelCollector) source;

			if (propertyName.equals("addPath")) {
				synchronized (modelCollectors) {
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}
			} else if (propertyName.equals("removePath")) {
				
				synchronized (modelCollectors) {
					//MM replaces the new modelCollector
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}
			}

		} else if (source instanceof Path) {
			path = (Path) source;

			modelCollector = (ModelCollector) path.getParent();

			if (propertyName.equals("addCheck")) {
				synchronized (modelCollectors) {
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}
			} else if (propertyName.equals("addAnnotation")) {

				synchronized (modelCollectors) {
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}
			} else if (propertyName.equals("removeCheck")) {
//MM replace the old Path having check with new one without the removed check
				modelCollector.replacePath(path);
				
				synchronized (modelCollectors) {
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}
			}
			else if (propertyName.equals("removeAnnotation")) {

				//MM replace the old Path having the Annotation with new one without the removed Annotation
				modelCollector.replacePath(path);
				
				synchronized (modelCollectors) {
					modelCollectors.put(modelCollector.getProject(),
							modelCollector);
				}		
			}
		}else 
			//MM Input validation points firing
		if (source instanceof InputValidationPoint) {
			point = (InputValidationPoint) source;

			modelCollector = (ModelCollector) point.getParent();

			if (propertyName.equals("addInputValidation")) {
					synchronized (modelCollectors) {
						modelCollectors.put(modelCollector.getProject(),
								modelCollector);
					}
						

				} else if (propertyName.equals("removeInputValidation")) {
					
					modelCollector.replacePoint(point);
					
					synchronized (modelCollectors) {
						modelCollectors.put(modelCollector.getProject(),
								modelCollector);
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
