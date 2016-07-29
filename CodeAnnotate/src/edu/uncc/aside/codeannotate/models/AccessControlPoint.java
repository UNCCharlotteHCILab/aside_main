package edu.uncc.aside.codeannotate.models;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @author Mahmoud Mohammadi ( mmoham12 at unccc dot edu)
 */
public class AccessControlPoint extends Point implements IPoint {
	
	private Path parent;

	public AccessControlPoint(ASTNode _node, CompilationUnit unit, IResource resource) {

		super(_node,unit,resource);
		
		
	}

	
	public void setParent(Path parent) {
		this.parent =  parent;
	}
	
	
	@Override
	public String toString() {
		
		return "\nPartial/Full access control check is done through \n\n   " + node.toString() + "\n\nin " + resource.getName() + " at Line "
				+ unit.getLineNumber(getStartOffset());
	}

	@Override
	public String getLineNumberText() {
		
		return "Access Control Request in Line " + unit.getLineNumber(getStartOffset()) + " of "
				+ resource.getName();
	}
	
	@Override
	public int getLineNumber() {
		
		return unit.getLineNumber(getStartOffset());
	}

	@Override
	public void accept(IModelVisitor visitor) {
		// This does nothing for Point
		visitor.visit(this);
	}

	@Override
	public Model getParent() {
		return parent;
	}

	@Override
	protected List<Model> buildChildren() {
		return null;
	}
}