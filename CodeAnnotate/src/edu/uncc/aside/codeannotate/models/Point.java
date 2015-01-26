package edu.uncc.aside.codeannotate.models;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class Point extends Model {
	private ASTNode node;
	private CompilationUnit unit;
	private IResource resource;

	private Path parent;

	public Point(ASTNode _node, CompilationUnit unit, IResource resource) {

		if (node == null || unit == null || resource == null) {
//			System.err.println("NULL for Constructing a Point");
		}

		this.setNode(_node);
		this.setUnit(unit);
		this.setResource(resource);
		
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStartPosition();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
	}

	public void setParent(Path path) {
		parent = path;
	}

	public ASTNode getNode() {
		return node;
	}

	public void setNode(ASTNode node) {
		this.node = node;
	}

	public CompilationUnit getUnit() {
		return unit;
	}

	public void setUnit(CompilationUnit unit) {
		this.unit = unit;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public boolean equalsTo(Object object) {
		if (!(object instanceof Point))
			return false;
		Point another = (Point) object;
		ASTNode anotherNode = another.getNode();
		CompilationUnit anotherUnit = another.getUnit();
		IResource anotherResource = another.getResource();
		if (anotherResource.getFullPath().toString()
				.equals(resource.getFullPath().toString())) {
			if (anotherUnit.getJavaElement().getElementName()
					.equals(unit.getJavaElement().getElementName())) {
				if (anotherNode.getNodeType() == node.getNodeType()) {
					if (anotherNode.getStartPosition() == node
							.getStartPosition()
							&& anotherNode.getLength() == node.getLength()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	
	
	public String toString() {
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStartPosition();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
		return "\nPartial/Full access control check is done through \n\n   " + node.toString() + "\n\nin " + resource.getName() + " at Line "
				+ unit.getLineNumber(startOffset);
	}

	public String getPointID() {
		int startOffset = -1;
		Object startProperty = node.getProperty(Plugin.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStartPosition();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(Plugin.ASIDE_NODE_PROP_START, startOffset);
		}
		
		return "Line " + unit.getLineNumber(startOffset) + " in "
				+ resource.getName();
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