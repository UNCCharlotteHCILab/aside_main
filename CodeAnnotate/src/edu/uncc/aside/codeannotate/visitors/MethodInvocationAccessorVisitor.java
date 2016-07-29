package edu.uncc.aside.codeannotate.visitors;

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

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.XMLConfig;
import edu.uncc.aside.codeannotate.XMLConfig.SinkDescription;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
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
	private ModelCollector modelCollector;
	private int lineChangedStartOffset;
	private int nextLineStartingOffset;

	public MethodInvocationAccessorVisitor(ASTNode node,
			ICompilationUnit unit, IProject project,
			int lineChangedStartOffset, int nextLineStartingOffset) {
		
		target = node;
		this.lineChangedStartOffset = lineChangedStartOffset;
		this.nextLineStartingOffset = nextLineStartingOffset;
		// List of Security sensitive operations . Should be loaded only once for all vistirs and nodes
		accessors = XMLConfig.readSinks(PluginConstants.SENSITIVE_OPERATIONS_CONFIG_FILE);
		
		projectOfInterest = project;
		unitOfInterest = unit;
		
		modelCollector = ModelRegistry.getPathCollectorForProject(projectOfInterest);
		
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

		if (end < lineChangedStartOffset || start > nextLineStartingOffset)
			return false;

		if (_node.getNodeType() == ASTNode.METHOD_INVOCATION) {
			MethodInvocation node = (MethodInvocation) _node;

			/* I visit Mahmoud*/ 
			// Is node one of the sensitive operations?
			if (Utils.isMethodInvocationOfInterest(node, accessors)) {

				//Getting parent node of this method declaration 
				MethodDeclaration parent = Utils
						.getParentMethodDeclaration(node);
				
				IMethod parentMethod = Utils.convertMethodDecl2IMethod(parent,
						resource);
				
				IResource mResource;
				try {
					mResource = parentMethod.getUnderlyingResource();
					
					if (mResource == null) {
						System.err.println("Check here...");
						return false;
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
					return false;
				}
				
			//	if(parent != null)
				//	System.err.println("MM.. The parent is NOT an Entrance Method " + parent.toString());
				//MM Is the parent of the a sensitive operation is one of the entrance points( Post, Get ,...)
				if (Utils.isEntranceMethodDeclaration(parent)) {

					Utils.markSensitiveOperation(node, resource,
							Utils.getCompilationUnit(unitOfInterest));
					
					//MM Point for sensitive operation( database.execquery(),...)
					AccessControlPoint accessor = new AccessControlPoint(node,
							Utils.getCompilationUnit(unitOfInterest), resource);
					
					//MM Point for entrance method ( doGet, doPost ,...
					AccessControlPoint entrance = new AccessControlPoint(parent,
							Utils.getCompilationUnit(parentMethod
									.getCompilationUnit()), mResource);

					//MM Third parameter which is list of checks is empty
					final Path path = new Path(entrance, accessor, null);
					
					
					modelCollector.addPath(path);
					
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
