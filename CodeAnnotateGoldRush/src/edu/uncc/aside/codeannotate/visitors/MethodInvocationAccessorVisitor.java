package edu.uncc.aside.codeannotate.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.XMLConfig;
import edu.uncc.aside.codeannotate.XMLConfig.SinkDescription;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class MethodInvocationAccessorVisitor extends ASTVisitor {

	private Collection<SinkDescription> accessors;
	private ASTNode target;
	private IProject projectOfInterest;
	private ICompilationUnit unitOfInterest;
	private IResource resource;
	private PathCollector collector;
	private int lineChangedStartOffset;
	private int nextStartOffset;

	public MethodInvocationAccessorVisitor(ASTNode node,
			ArrayList<Path> pathsOfInterest, ICompilationUnit unit,
			IProject project, int lineChangedStartOffset, int nextStartOffset) {
		target = node;
		this.lineChangedStartOffset = lineChangedStartOffset;
		this.nextStartOffset = nextStartOffset;
		accessors = XMLConfig.readSinks(Plugin.SENSITIVE_ACCESSORS_CONFIG);
		projectOfInterest = project;
		unitOfInterest = unit;
		collector = ModelRegistry.getPathCollectorForProject(projectOfInterest);
		try {
			resource = unitOfInterest.getUnderlyingResource();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean preVisit2(ASTNode _node) {

		int start = _node.getStartPosition();
		int end = start + _node.getLength();

		if (end < lineChangedStartOffset || start > nextStartOffset)
			return false;

		if (_node.getNodeType() == ASTNode.METHOD_INVOCATION) {
			MethodInvocation node = (MethodInvocation) _node;

			/* I visit Mahmoud*/ 
			if (Utils.isMethodInvocationOfInterest(node, accessors)) {

				MethodDeclaration parent = Utils
						.getParentMethodDeclaration(node);
				IMethod method = Utils.convertMethodDecl2IMethod(parent,
						resource);
				IResource mResource;
				try {
					mResource = method.getUnderlyingResource();
					if (mResource == null) {
						System.err.println("Check here...");
						return false;
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
					return false;
				}
				if(parent != null)
					System.err.println("MM.. NOT Entrance Method " + parent.toString());
				
				if (Utils.isEntranceMethodDeclaration(parent)) {

					Utils.markAccessor(node, resource,
							Utils.getCompilationUnit(unitOfInterest));
					Point accessor = new Point(node,
							Utils.getCompilationUnit(unitOfInterest), resource);
					Point entrance = new Point(parent,
							Utils.getCompilationUnit(method
									.getCompilationUnit()), mResource);

					final Path path = new Path(entrance, accessor, null);
					
					collector.addPath(path);
					
					System.out.println("Just created and added a new path "
							+ path.getPathID());
				} else {
					// TODO Then what should I do???
					
				}
			} 
		} 
		return true;
	}

	public void process() {
		target.accept(this);
	}

}
