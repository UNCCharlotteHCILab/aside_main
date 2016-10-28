package edu.uncc.aside.codeannotate.models;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.uncc.aside.codeannotate.visitors.IModelVisitor;

public class SQLInjectionPoint extends Point implements IPoint {

	public SQLInjectionPoint(ASTNode _node, CompilationUnit unit,
			IResource resource) {
		super(_node, unit, resource);
		
	}

	@Override
	public String toString() {
		
		return "\nInput Validation warning is done through \n\n   " + node.toString() + "\n\nin " + resource.getName() + " at Line "
				+ unit.getLineNumber(getStartOffset());
	}

	@Override
	public String getLineNumberText() {
		
		return "Line " + unit.getLineNumber(getStartOffset()) + " in "
				+ resource.getName();
	}
	
	
	public String getVulnerabilityText() {
		
		return "Input Validation Warning at Line " + unit.getLineNumber(node.getStartPosition()) + " of  "
				+ resource.getName();
	}
	
	
	@Override
	public void accept(IModelVisitor visitor) {
		// This does nothing for Point
		visitor.visit(this);
	}

	@Override
	public Model getParent() {
		
			return ModelRegistry.getPathCollectorForProject(this.resource.getProject());

	}
	

	@Override
	protected List<Model> buildChildren() {
		return null;
	}
	

}
