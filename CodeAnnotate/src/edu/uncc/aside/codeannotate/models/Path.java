package edu.uncc.aside.codeannotate.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.uncc.aside.codeannotate.PluginConstants;
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

	private AccessControlPoint entrance;
	private AccessControlPoint sensitiveOperation;
	private List<AccessControlPoint> checks;
	//MM
	private List<AccessControlPoint> annotations;

	private Set<ICompilationUnit> compilationUnits = new HashSet<ICompilationUnit>();

	public Path(AccessControlPoint entrance, AccessControlPoint sensitiveOperation, List<AccessControlPoint> _checks) {
		
		this.entrance = entrance;
		this.sensitiveOperation = sensitiveOperation;
		
		compilationUnits.add(Utils.getCompilationUnitOf(entrance
				.getResource()));
		
		//MM Added
		compilationUnits.add(Utils.getCompilationUnitOf(sensitiveOperation
				.getResource()));

		checks = _checks;
		if (checks == null) {
			checks = Collections.synchronizedList(new ArrayList<AccessControlPoint>());
		} else {
			for (AccessControlPoint check : checks) {
								
				System.err.println("MMM Next Check...");
				System.err.println(check.toString());
				
				compilationUnits.add(Utils
						.getCompilationUnitOf(check.getResource()));
			}
		}
	}

	public Path() {

	}

	public AccessControlPoint getEntrance() {
		return entrance;
	}

	public void setEntrance(AccessControlPoint entrance) {
		this.entrance = entrance;
	}

	public AccessControlPoint getSensitiveOperation() {
		return sensitiveOperation;
	}

	public void setSensitiveOperation(AccessControlPoint accessor) {
		this.sensitiveOperation = accessor;
	}

	public List<AccessControlPoint> getChecks() {
		if (checks == null)
			checks = Collections.synchronizedList(new ArrayList<AccessControlPoint>());
		return checks;
	}

	//MM
	public List<AccessControlPoint> getAnnotations() {
		if (annotations == null)
			annotations = Collections.synchronizedList(new ArrayList<AccessControlPoint>());
		return annotations;
	}
	public void setChecks(List<AccessControlPoint> checks) {
		this.checks = checks;
	}

	//MM
	public boolean containsAnnotation(AccessControlPoint annotation) {

		if (this.getAnnotation(annotation) != null)
			return true;
		return false;
	}
	
	public boolean containsCheck(AccessControlPoint check) {

		if (this.getCheck(check) != null)
			return true;
		return false;
	}

	public AccessControlPoint getAnnotation(AccessControlPoint annotation) {

		if (annotations == null)
			return null;

		for (AccessControlPoint p : annotations) {
			if (p.equalsTo(annotation))
				return p;
		}

		return null;
	}
	public AccessControlPoint getCheck(AccessControlPoint check) {

		if (checks == null)
			return null;

		for (AccessControlPoint p : checks) {
			if (p.equalsTo(check))
				return p;
		}

		return null;
	}

	public boolean equalsTo(Object object) {

		if (!(object instanceof Path))
			return false;

		Path another = (Path) object;
		if (another.getSensitiveOperation().equals(sensitiveOperation)
				&& another.getEntrance().equals(entrance))
			return true;

		return false;
	}

	public String toString() {
		
		int startOffset = sensitiveOperation.getStartOffset();
				/*MM -1;
		Object startProperty = sensitiveOperation.getNode().getProperty(
				PluginConstants.ASIDE_NODE_PROP_START);
		
		if (startProperty == null) {
			startOffset = sensitiveOperation.getNode().getStartPosition();
		} else {
			startOffset = Integer.parseInt(startProperty.toString());
		}
*/
		return "\nThe method invocation \n\n   "
				+ sensitiveOperation.getNode().toString()
				+ "\n\nat Line "
				+ sensitiveOperation.getUnit().getLineNumber(startOffset)
				+ " in "
				+ sensitiveOperation.getResource().getName()
				+ " is detected to be accessing potentially senstive/privileged data of the application without explicit access control checks. " +
				"\n\nIf you already have code that does the proper check, please annotate the code that can be evaluated to be true or false (e.g. the condition test of a if statement) by first selecting and then pressing Ctrl+0; " +
				"\n\nIf you do not yet have such code checking the right to access the sensitive data, it is STRONGLY encouraged that you take the time to make up the miss.";
	}

	public String getPathID() {
		return "Access Control Request for " + sensitiveOperation.getLineNumberText();
	//entrance.getLineNumberText() + " => " + sensitiveOperation.getLineNumberText();
	}
	//MM
	public void addAnnotation(AccessControlPoint annotation) {

		if (this.containsAnnotation(annotation))
			return;
		
		synchronized (annotations) {
			
			annotations.add(annotation);
			
			//MM Adding Listener including Plugin.getDefault().getAnnotationView() 
			annotation.accept(AddModelVisitor.getInstance());
			//MM Firing the Listener including Plugin.getDefault().getAnnotationView() 
			fireEvent("addAnnotation", null, annotation);
		}
	}
	public void removeAnnotation(AccessControlPoint annotation) {
		if (this.containsAnnotation(annotation)) {
			synchronized (checks) {
				
				annotation.accept(RemoveModelVisitor.getInstance());
				//MM Removing Listener including Plugin.getDefault().getAnnotationView()
				annotations.remove(annotation);
				//MM Firing the Listener including Plugin.getDefault().getAnnotationView()
				fireEvent("removeAnnotation", null, annotation);
			}
		}
	}
	public void addCheck(AccessControlPoint check) {

		if (this.containsCheck(check))
			return;
		
		synchronized (checks) {
			
			checks.add(check);
			
			//MM Adding Listener including Plugin.getDefault().getAnnotationView() 
			check.accept(AddModelVisitor.getInstance());
			//MM Firing the Listener including Plugin.getDefault().getAnnotationView() 
			fireEvent("addCheck", null, check);
		}
	}

	public void removeCheck(AccessControlPoint check) {
		if (this.containsCheck(check)) {
			synchronized (checks) {
				
				check.accept(RemoveModelVisitor.getInstance());
				//MM Removing Listener including Plugin.getDefault().getAnnotationView()
				checks.remove(check);
				//MM Firing the Listener including Plugin.getDefault().getAnnotationView()
				fireEvent("removeCheck", null, check);
			}
		}
	}

	public IProject getProject() {
		
		return sensitiveOperation.getResource().getProject();
	}

	@Override
	public void accept(IModelVisitor visitor) {
		
		visitor.visit(this);
	}

	@Override
	public Model getParent() {
		return ModelRegistry.getPathCollectorForProject(getProject());

	}

	@Override
	protected List<Model> buildChildren() {
		List<Model> children = Collections
				.synchronizedList(new ArrayList<Model>());
		for (AccessControlPoint check : checks)
			children.add(check);
		
		return children;
	}

	public Set<ICompilationUnit> getCompilationUnitsOfInterest() {
		return compilationUnits;
	}
}
