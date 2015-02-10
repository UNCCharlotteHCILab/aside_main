package edu.uncc.aside.codeannotate.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.visitors.AddModelVisitor;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
import edu.uncc.aside.codeannotate.visitors.RemoveModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class PathCollector extends Model {

	private IProject project;

	private List<Path> paths = null;

	public static PathCollector getInstance(IProject project) {

		PathCollector collector = ModelRegistry
				.getPathCollectorForProject(project);
		if (collector == null) {
			collector = new PathCollector(project);
		}

		return collector;

	}

	public PathCollector(IProject project) {
		this.project = project;
		if (paths == null) {
			paths = Collections.synchronizedList(new ArrayList<Path>());
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
