package edu.uncc.aside.codeannotate.models;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public interface IPoint {

public abstract ASTNode getNode();
	
	public abstract Model getParent();
	public abstract String toString();
	public abstract String getLineNumberText();
	
}
