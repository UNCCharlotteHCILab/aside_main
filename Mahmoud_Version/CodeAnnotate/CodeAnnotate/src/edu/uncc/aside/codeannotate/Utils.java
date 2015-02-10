package edu.uncc.aside.codeannotate;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.uncc.aside.codeannotate.XMLConfig.SinkDescription;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.Point;


/*
 * Very useful utility class, currently copied from LapsePlus
 */
public class Utils {

	public static class ExprUnitResource {
		Expression expr;
		CompilationUnit cu;
		IResource resource;

		public ExprUnitResource(Expression expr, CompilationUnit cu,
				IResource resource) {
			this.expr = expr;
			this.cu = cu;
			this.resource = resource;
		}

		public CompilationUnit getCompilationUnit() {
			return cu;
		}

		/**
		 * This is either a MethodInvocation or a ClassInstanceCreation.
		 * */
		public Expression getExpression() {
			return expr;
		}

		public IResource getResource() {
			return resource;
		}

		public String toString() {
			if (expr != null && resource != null && cu != null)
				return expr.toString() + " in " + resource.getName()
						+ " at Line "
						+ cu.getLineNumber(expr.getStartPosition());
			else
				return "";
		}
	}

	public static class ExprUnitResourceMember extends ExprUnitResource {
		private IMember member;

		public ExprUnitResourceMember(Expression expr, CompilationUnit cu,
				IResource resource, IMember member) {
			super(expr, cu, resource);
			this.member = member;
		}

		public IMember getMember() {
			return member;
		}

		public String toString() {
			return super.toString();
		}
	}

	public static class MethodDeclarationUnitPair {
		MethodDeclaration method;
		CompilationUnit cu;
		IResource resource;
		IMember member;

		public MethodDeclarationUnitPair(MethodDeclaration method,
				CompilationUnit cu, IResource resource, IMember member) {
			this.method = method;
			this.cu = cu;
			this.resource = resource;
			this.member = member;
		}

		public CompilationUnit getCompilationUnit() {
			return cu;
		}

		public MethodDeclaration getMethod() {
			return method;
		}

		public IResource getResource() {
			return resource;
		}

		public IMember getMember() {
			return member;
		}

		public String toString() {
			if (method != null && member != null && resource != null)
				return method.toString() + " in " + resource.getName()
						+ " at Line "
						+ cu.getLineNumber(method.getStartPosition());
			else
				return "";
		}
	}

	public static MethodDeclaration getParentMethodDeclaration(ASTNode node) {
		if (node == null)
			return null;
		ASTNode parent = node.getParent();
		if (parent == null || parent.getNodeType() == ASTNode.COMPILATION_UNIT)
			return null;

		if (parent.getNodeType() == ASTNode.METHOD_DECLARATION)
			return (MethodDeclaration) parent;
		else
			return getParentMethodDeclaration(parent);

	}

	public static IMethod convertMethodDecl2IMethod(
			MethodDeclaration methodDecl, IResource resource) {

		try {
			ICompilationUnit iCompilationUnit = JavaCore
					.createCompilationUnitFrom((IFile) resource);
			int startPos = methodDecl.getStartPosition();
			IJavaElement element = iCompilationUnit.getElementAt(startPos);
			if (element instanceof IMethod) {
				return (IMethod) element;
			}
			return null;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CompilationUnit getCompilationUnit(ICompilationUnit unit) {

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);

		return (CompilationUnit) parser.createAST(null);
	}

	// TODO: hard coded for now
	public static boolean isEntranceMethod(String new_accessor_id) {
		if (new_accessor_id.equals("execute")
				|| new_accessor_id.equals("doPost")
				|| new_accessor_id.equals("doGet")
				|| new_accessor_id.equals("processRequest")
				|| new_accessor_id.equals("onSubmit")
				|| new_accessor_id.equals("referenceData")
				|| new_accessor_id.equals("setUpSSLSocket")) {
		//	System.out.println("entry=" + new_accessor_id);
			return true;
		}
		return false;
	}

	// TODO: this is a simplified version
	public static boolean isEntranceMethodDeclaration(MethodDeclaration node) {
		String name = node.getName().getIdentifier();
		return isEntranceMethod(name);
	}

	public static void markAccessor(MethodInvocation mi, IResource resource,
			CompilationUnit cu) {

		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			IMarker[] markers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			int char_start, length;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == mi.getStartPosition()
						&& length == mi.getLength())
					return;
			}

			IMarker questionMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION);

			questionMarker.setAttribute(IMarker.CHAR_START,
					mi.getStartPosition());
			questionMarker.setAttribute(IMarker.CHAR_END, mi.getStartPosition()
					+ mi.getLength());
			questionMarker.setAttribute(IMarker.MESSAGE,
					"Where is the corresponding authentication process?");
			questionMarker.setAttribute(IMarker.LINE_NUMBER,
					cu.getLineNumber(mi.getStartPosition()));
			questionMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_WARNING);
			questionMarker
					.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static void createQuestionMarker(ASTNode node, IResource resource,
			CompilationUnit unit) throws CoreException {
		String message = "What is the corresponding authentication?";

		IMarker questionCheckedMarker = resource
				.createMarker(Plugin.ANNOTATION_QUESTION);

		questionCheckedMarker.setAttribute(IMarker.CHAR_START,
				node.getStartPosition());
		questionCheckedMarker.setAttribute(IMarker.CHAR_END,
				node.getStartPosition() + node.getLength());
		questionCheckedMarker.setAttribute(IMarker.MESSAGE, message);
		questionCheckedMarker.setAttribute(IMarker.LINE_NUMBER,
				unit.getLineNumber(node.getStartPosition()));
		questionCheckedMarker.setAttribute(IMarker.SEVERITY,
				IMarker.SEVERITY_WARNING);
		questionCheckedMarker.setAttribute(IMarker.PRIORITY,
				IMarker.PRIORITY_HIGH);
	}

	public static boolean isMethodInvocationOfInterest(MethodInvocation node,
			Collection<SinkDescription> accessors) {

		IMethodBinding binding = node.resolveMethodBinding();
		if (binding == null)
			return false;
		ITypeBinding typeBinding = binding.getDeclaringClass();
		if (typeBinding == null)
			return false;
		String methodName = binding.getName();
		String className = typeBinding.getQualifiedName();
		for (SinkDescription accessor : accessors) {
			if (accessor.getMethodName().equals(className + "." + methodName)
					&& accessor.getTypeName().equals(className)) {
				System.out.println("sensitive operation = " + accessor.getMethodName().toString());
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param target
	 *            the accessor node of a path stored in a path collector.
	 * @return whether the target is still in the compilation unit as it was.
	 */
	

	public static ICompilationUnit compilationUnitOfInterest(IResource resource) {
		IFile file = (IFile) resource.getAdapter(IFile.class);
		return (ICompilationUnit) JavaCore.create(file);
	}

	public static void removeMarkersOnPath(Path path) {
		if(path == null)
			return;
		Point accessor = path.getAccessor();
		removeMarkerOnPoint(accessor);
		List<Point> checks = path.getChecks();
		for (Point check : checks) {
			removeMarkerOnPoint(check);
		}

	}

	public static void removeMarkerOnPoint(Point point) {
		ASTNode node = point.getNode();
		IResource resource = point.getResource();

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);
			IMarker[] answerMarkers = resource.findMarkers(
					Plugin.ANNOTATION_ANSWER, false, IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					marker.delete();
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					marker.delete();
				}

			}

			for (IMarker marker : answerMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					marker.delete();
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static boolean isAssociatedWithMarker(ASTNode node,
			IResource resource) {

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					return true;
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					return true;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static IMarker getAssociatedMarker(ASTNode node, IResource resource) {

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					Plugin.ANNOTATION_QUESTION_CHECKED, false,
					IResource.DEPTH_ONE);

			for (IMarker marker : questionMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					return marker;
				}

			}

			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					return marker;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}
}
