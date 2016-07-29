package edu.uncc.aside.codeannotate.models;

import java.io.Serializable;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.visitors.IModelVisitor;

public  class Point  extends Model implements Serializable{
	
	protected ASTNode node;
	protected CompilationUnit unit;
	protected IResource resource;

	//protected PathCollector parent;
	
	Point(ASTNode _node, CompilationUnit unit, IResource resource) {
		
		if (node == null || unit == null || resource == null) {
			System.err.println("NULL for Constructing a Point");
		}
		this.node = _node;
		this.unit = unit;
		this.resource = resource;
				
	}
	
	
	public int getStartOffset()
	{
		int startOffset = -1;
		Object startProperty = node.getProperty(PluginConstants.ASIDE_NODE_PROP_START);
		if(startProperty == null)
		{
		startOffset = node.getStartPosition();	
		}else{
			startOffset = Integer.parseInt(startProperty.toString());
			node.setProperty(PluginConstants.ASIDE_NODE_PROP_START, startOffset);
		}
		return startOffset;
	}
	
	public int getLineNumber() {
		
		
		return unit.getLineNumber(node.getStartPosition());
	}
	/*
	public String getVulnerabilityText() {
		
		return "Input Validation Warning at Line " + unit.getLineNumber(node.getStartPosition()) + " of  "
				+ resource.getName();
	}
	*/
	
	public boolean equalsTo(Object object) {
		if (!(object instanceof InputValidationPoint))
			return false;
		
		InputValidationPoint another = (InputValidationPoint) object;
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

/*
	@Override
	public ASTNode getNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CompilationUnit getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLineNumberText() {
		// TODO Auto-generated method stub
		return null;
	}
*/
	@Override
	public void accept(IModelVisitor visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public Model getParent() {
		// TODO Auto-generated method stub
		return ModelRegistry.getPathCollectorForProject(this.resource.getProject());

	}

	@Override
	protected List<Model> buildChildren() {
		// TODO Auto-generated method stub
		return null;
	}


	

}
