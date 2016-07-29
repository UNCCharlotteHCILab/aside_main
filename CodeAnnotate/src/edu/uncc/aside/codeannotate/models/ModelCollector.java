package edu.uncc.aside.codeannotate.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.corext.refactoring.code.flow.InOutFlowAnalyzer;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.visitors.AddModelVisitor;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
import edu.uncc.aside.codeannotate.visitors.RemoveModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @author Mahmoud Mohammadi (mmoham12 at uncc dot edu)
 */
public class ModelCollector extends Model {

	private IProject project;

	private List<Path> paths = null;
	private List<InputValidationPoint> ivPoints = null;
	private List<OutputEncodingPoint> oePoints = null;
	private List<SQLInjectionPoint> siPoints = null;

	public static ModelCollector getInstance(IProject project) {

		ModelCollector collector = ModelRegistry
				.getPathCollectorForProject(project);
		
		if (collector == null) {
			collector = new ModelCollector(project);
		}

		return collector;

	}

	public ModelCollector(IProject project) {
		this.project = project;
		if (paths == null) {
			paths = Collections.synchronizedList(new ArrayList<Path>());
		}
		//MM
		if (ivPoints == null) {
			ivPoints = Collections.synchronizedList(new ArrayList<InputValidationPoint>());
		}
		
		ModelRegistry.registerPathCollector(this);
	}

	public IProject getProject() {
		return project;
	}

	public void addPath(Path path) {
		if (this.contains(path))
			return;
		synchronized (paths) {
			
			paths.add(path);
			
			path.accept(AddModelVisitor.getInstance());
			
						
			fireEvent("addPath", null, path);
		}
	}

	public void removePath(Path path) {
		if (this.contains(path)) {
			synchronized(paths){
				path.accept(RemoveModelVisitor.getInstance());
				paths.remove(path);
				fireEvent("removePath", null, path);	
		}}
	}
	
	public void addInputValidation(InputValidationPoint point) {
		if (this.contains(point))
			return;
		synchronized (ivPoints) {
			
			ivPoints.add(point);
			
			point.accept(AddModelVisitor.getInstance());
			
			fireEvent("addInputValidation", null, point);
		}
	}

	public void removeInputValidation(InputValidationPoint point) {
		if (this.contains(point)) {
			synchronized(ivPoints){

				point.accept(RemoveModelVisitor.getInstance());

				ivPoints.remove(point);
				fireEvent("removeInputValidation", null, point);	
			}
		}
	}
	
	public void removeAllInputValidation() {
		
			synchronized(ivPoints){
				
				
				for (InputValidationPoint point : ivPoints) {
					point.accept(RemoveModelVisitor.getInstance());
				}
				ivPoints.clear();
				
				fireEvent("removeInputValidation", null, null);	
			}
		
	}
	
public void replacePoint(InputValidationPoint point) {
		
		InputValidationPoint _point = getPoint(point);
		synchronized (ivPoints) {
			if (_point != null)
				ivPoints.remove(_point);
			ivPoints.add(point);
		}
	}
	private boolean contains(InputValidationPoint point) {
		if (this.getPoint(point) != null)
			return true;
		return false;
	}

	private InputValidationPoint getPoint(InputValidationPoint point) {
		for (InputValidationPoint p :  ivPoints) {
			if (point.equalsTo(p))
				return p;
		}
		return null;
	}
	public void signalRemove(Path path){
		path.accept(RemoveModelVisitor.getInstance());
		fireEvent("removePath", null, path);	
	}

	private boolean contains(Path path) {
		if (this.getPath(path) != null)
			return true;
		return false;
	}

	private Path getPath(Path path) {
		for (Path p : paths) {
			if (path.equalsTo(p))
				return p;
		}
		return null;
	}

	@Override
	public void accept(IModelVisitor visitor) {
		visitor.visit(this);
	}

	public List<Path> getAllPaths() {
		return paths;
	}
	public List<InputValidationPoint> getAllInputValidationPoints() {
		return ivPoints;
	}
	
	public List<OutputEncodingPoint> getAllOutputEncodingPoints() {
		return oePoints;
	}
	
	public List<SQLInjectionPoint> getAllSqlInjectionPoints() {
		return siPoints;
	}
	
	public List<Model> getAllModels() {
		List<Model> allModels = new ArrayList<Model>(); 
		allModels.addAll(paths);
		allModels.addAll(ivPoints);
	//	allModels.addAll(oePoints);
	//	allModels.addAll(siPoints);
		
		return allModels;
	}

	@Override
	public Model getParent() {
		return null;// this should happen in any situation
	}

	@Override
	protected List<Model> buildChildren() {
		List<Model> children = Collections
				.synchronizedList(new ArrayList<Model>());
		for (Path path : getAllPaths())
			children.add(path);
		return children;
	}

	public void replacePath(Path path) {
		Path _path = getPath(path);
		synchronized (paths) {
			if (_path != null)
				paths.remove(_path);
			paths.add(path);
		}
	}
}
