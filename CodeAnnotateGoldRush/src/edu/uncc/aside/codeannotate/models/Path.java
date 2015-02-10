package edu.uncc.aside.codeannotate.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.visitors.AddModelVisitor;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
import edu.uncc.aside.codeannotate.visitors.RemoveModelVisitor;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public class Path extends Model {

	private Point entrance;
	private Point accessor;
	private List<Point> checks;

	private Set<ICompilationUnit> compilationUnitsOfInterest = new HashSet<ICompilationUnit>();

	public Path(Point entrance, Point accessor, List<Point> _checks) {
		this.entrance = entrance;
		compilationUnitsOfInterest.add(Utils.compilationUnitOfInterest(entrance
				.getResource()));
		this.accessor = accessor;
		compilationUnitsOfInterest.add(Utils.compilationUnitOfInterest(entrance
				.getResource()));

		checks = _checks;
		if (checks == null) {
			checks = Collections.synchronizedList(new ArrayList<Point>());
		} else {
			for (Point check : checks) {
				/* Mahmoud */
				System.err.println(check.toString());
				System.err.println("MMM Next...");
				
				compilationUnitsOfInterest.add(Utils
						.compilationUnitOfInterest(check.getResource()));
			}
		}
	}

	public Path() {

	}

	public Point getEntrance() {
		return entrance;
	}

	public void setEntrance(Point entrance) {
		this.entrance = entrance;
	}

	public Point getAccessor() {
		return accessor;
	}

	public void setAccessor(Point accessor) {
		this.accessor = accessor;
	}

	public List<Point> getChecks() {
		if (checks == null)
			checks = Collections.synchronizedList(new ArrayList<Point>());
		return checks;
	}

	public void setChecks(List<Point> checks) {
		this.checks = checks;
	}

	public boolean containsCheck(Point check) {

		if (this.getCheck(check) != null)
			return true;
		return false;
	}

	public Point getCheck(Point check) {

		if (checks == null)
			return null;

		for (Point p : checks) {
			if (p.equalsTo(check))
				return p;
		}

		return null;
	}

	public boolean equalsTo(Object object) {

		if (!(object instanceof Path))
			return false;

		Path another = (Path) object;
		if (another.getAccessor().equals(accessor)
				&& another.getEntrance().equals(entrance))
			return true;

		return false;
	}

	public String toString() {
		int startOffset = -1;
		Object startProperty = accessor.getNode().getProperty(
				Plugin.ASIDE_NODE_PROP_START);
		if (startProperty == null) {
			startOffset = accessor.getNode().getStartPosition();
		} else {
			startOffset = Integer.parseInt(startProperty.toString());
		}

		return "\nThe method invocation \n\n   "
				+ accessor.getNode().toString()
				+ "\n\nat Line "
				+ accessor.getUnit().getLineNumber(startOffset)
				+ " in "
				+ accessor.getResource().getName()
				+ " is detected to be accessing potentially senstive/privileged data of the application without explicit access control checks. " +
				"\n\nIf you already have code that does the proper check, please annotate the code that can be evaluated to be true or false (e.g. the condition test of a if statement) by first selecting and then pressing Ctrl+0; " +
				"\n\nIf you do not yet have such code checking the right to access the sensitive data, it is STRONGLY encouraged that you take the time to make up the miss.";
	}

	public String getPathID() {
		return entrance.getPointID() + " => " + accessor.getPointID();
	}

	public void addCheck(Point check) {

		if (this.containsCheck(check))
			return;
		synchronized (checks) {
			checks.add(check);
			check.accept(AddModelVisitor.getInstance());
			fireEvent("addCheck", null, check);
		}
	}

	public void removeCheck(Point check) {
		if (this.containsCheck(check)) {
			synchronized (checks) {
				check.accept(RemoveModelVisitor.getInstance());
				checks.remove(check);
				fireEvent("removeCheck", null, check);
			}
		}
	}

	@Override
	public void accept(IModelVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Model getParent() {
		return ModelRegistry.getPathCollectorForProject(accessor.getResource()
				.getProject());

	}

	@Override
	protected List<Model> buildChildren() {
		List<Model> children = Collections
				.synchronizedList(new ArrayList<Model>());
		for (Point check : checks)
			children.add(check);
		return children;
	}

	public Set<ICompilationUnit> getCompilationUnitsOfInterest() {
		return compilationUnitsOfInterest;
	}
}
