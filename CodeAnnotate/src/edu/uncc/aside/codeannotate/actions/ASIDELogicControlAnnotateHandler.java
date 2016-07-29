package edu.uncc.aside.codeannotate.actions;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.CallerFinder;
import edu.uncc.aside.codeannotate.asideInterface.InterfaceUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class ASIDELogicControlAnnotateHandler extends AbstractHandler {

	private IEditorPart target;
	private IProject project;
	private IResource file;
	private CompilationUnit astRoot;
	private ASTNode node;
	private Shell shell;
	private Map<String, Path> map;
	private AccessControlPoint annotatedControlLogic;

	private Path annotationPath;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		target = HandlerUtil.getActiveEditor(event);
		shell = HandlerUtil.getActiveShell(event);
		ISelection object = HandlerUtil.getCurrentSelection(event);

		// The current path to be annotated with a check
		annotationPath = Plugin.annotationPath;

		if (annotationPath == null) {
			popupDialogWarning();
			return null;
		}

		if (object instanceof ITextSelection) {
			ITextSelection tSelection = (ITextSelection) object;
			
			IEditorInput input = target.getEditorInput();
			IJavaElement element = JavaUI.getEditorInputJavaElement(input);

			try {
				file = element.getUnderlyingResource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			IJavaProject javaProject = element.getJavaProject();
			project = javaProject.getProject();

			
			process(tSelection);

		}

		return object;
	}

	private void process(ITextSelection tSelection) {

		int char_start = tSelection.getOffset();
		int length = tSelection.getLength();
		astRoot = getASTRoot();
		node = NodeFinder.perform(astRoot, char_start, length);

		if (node == null)
			return;
		int nodeType = node.getNodeType();
		
		ITypeBinding binding = null;
		String fullyQualifiedName = null;
		
		switch (nodeType) {
		case ASTNode.ASSERT_STATEMENT:
			// read articles about Java assertion and Spring security assertion
			break;
		case ASTNode.SIMPLE_NAME:
			SimpleName sn = (SimpleName) node;
			binding = sn.resolveTypeBinding();
			fullyQualifiedName = binding.getQualifiedName();
			if (fullyQualifiedName.equals("boolean")) {
				annotatedControlLogic = new AccessControlPoint(node, astRoot, file);
				annotatePath();
			}
			break;
		case ASTNode.METHOD_INVOCATION:
			MethodInvocation mi = (MethodInvocation) node;
			IMethodBinding mBinding = mi.resolveMethodBinding();
			binding = mBinding.getReturnType();
			fullyQualifiedName = binding.getQualifiedName();
	//		if (fullyQualifiedName.equals("boolean")) {
				annotatedControlLogic = new AccessControlPoint(node, astRoot, file);
				annotatePath();
	//		}
			break;
		case ASTNode.INFIX_EXPRESSION:
			InfixExpression infixExpression = (InfixExpression) node;
			binding = infixExpression.resolveTypeBinding();
			fullyQualifiedName = binding.getQualifiedName();
			if (fullyQualifiedName.equals("boolean")) {
				if( infixExpression.getParent().getNodeType() == ASTNode.IF_STATEMENT)
				{
					// Mahmoud
					IfStatement ifNode = (IfStatement)infixExpression.getParent();
					//	is.getExpression();
					
						AccessControlPoint accessor= annotationPath.getSensitiveOperation();
						
						ASTNode parent_accsr= accessor.getNode().getParent();	
						/*
						 Find Call Graph
						 if( IFStatement.ParentMethod is_in CallGraph) AND
						 if( IFStatement.THEN_Statement Reaches the Sensitive Operation( accessor) )
						 * */
						
						ArrayDeque<String> element ;
						 String f,e;
						for (int i = 0; i < Plugin.callGraph.size(); i++) {
						    element = (ArrayDeque<String>)Plugin.callGraph.get(i); // element = a call path from SSO to Entry point
							
						   f=  element.getFirst();
						   e = element.getLast();
						   if( f.equalsIgnoreCase(accessor.getNode().toString()))
						   {
							   // Found The corresponding call graph. Now check whether the If statement can reach the accessor
							  Statement then= ifNode.getThenStatement();
							  
						   
						   }
						}
								
						
						Boolean found = false;
						while( ! found && parent_accsr != null)
						{
							if( parent_accsr.getNodeType() == ASTNode.IF_STATEMENT && 
								ifNode.getStartPosition() == parent_accsr.getStartPosition() &&
								ifNode.getLength() == parent_accsr.getLength())
							{
								found = true;
							}
							else	
							{
								parent_accsr = parent_accsr.getParent();
								if(parent_accsr.getNodeType() == ASTNode.METHOD_DECLARATION)
								{
									// find the callers
									/* Collection<?> callers = CallerFinder.findCallers(monitor,
											accessor_id, javaProject, false); */
								}
								
							}
						
						}
						if(found)
						{
							node = (ASTNode)ifNode.getExpression();
							
							annotatedControlLogic = new AccessControlPoint(node, astRoot, file);
							
							annotatePath();
						}
						else
						{
							System.err.println("There is an error...");
						}
					/* previous code	
					annotatedControlLogic = new Point(node, astRoot, file);
				
					annotatePath();
					*/
				}
			}
			
			break;
		
		case ASTNode.IF_STATEMENT :
			
			break;
		default:
			System.out
					.println("the selected code piece is not recognized by CodeAnnotate...");
			break;

		}
	}

	private void annotatePath() {
		removeInformationControl();
		
		if (annotationPath.containsCheck(annotatedControlLogic))
			return;
		
		annotationPath.addCheck(annotatedControlLogic);
		
		annotatedControlLogic.setParent(annotationPath);

		markControlLogic();
		replaceAccessorMarkerOnPath(annotationPath);
		// TODO: how to manage path and its control logic annotations: UI->View,
		// background infrastructure?
	}

	private void markControlLogic() {

		try {
			// First, gotta check whether there is a marker for the node
			if (file == null)
				return;
			IMarker[] markers = file.findMarkers(PluginConstants.MARKER_ANNOTATION_ANSWER,
					false, IResource.DEPTH_ONE);
			int char_start = -1, length = 0;
			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStartPosition()
						&& length == node.getLength())
					return;
			}

			Map<String, Object> markerAttributes = new HashMap<String, Object>();			
			
			markerAttributes.put(IMarker.CHAR_START,
					node.getStartPosition());
			markerAttributes.put(IMarker.CHAR_END, node.getStartPosition()
					+ node.getLength());
			markerAttributes.put(IMarker.MESSAGE,
					"This is an annotated access control logic.");
			markerAttributes.put(IMarker.LINE_NUMBER,
					astRoot.getLineNumber(node.getStartPosition()));
			markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			
			IMarker answerMarker = MarkerAndAnnotationUtil
					.addMarker(astRoot,
							markerAttributes, PluginConstants.MARKER_ANNOTATION_ANSWER);

		/*	
			IMarker answerMarker = file.createMarker(PluginConstants.ANNOTATION_ANSWER);

			answerMarker.setAttribute(IMarker.CHAR_START,
					node.getStartPosition());
			answerMarker.setAttribute(IMarker.CHAR_END, node.getStartPosition()
					+ node.getLength());
			answerMarker.setAttribute(IMarker.MESSAGE,
					"This is an annotated access control logic.");
			answerMarker.setAttribute(IMarker.LINE_NUMBER,
					astRoot.getLineNumber(node.getStartPosition()));
			answerMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			answerMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			*/
			
			InterfaceUtil.prepareAnnotationRequest(answerMarker, null);

		} catch (CoreException e) {
			e.printStackTrace();
		}

		
	}

	private void replaceAccessorMarkerOnPath(Path path) {
		AccessControlPoint accessor = path.getSensitiveOperation();
		ASTNode node = accessor.getNode();
		IResource resource = accessor.getResource();
		CompilationUnit unit = accessor.getUnit();

		try {
			// First, gotta check whether there is a marker for the accessor
			// node
			int char_start, length;

			IMarker[] checkedMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_CHECKED, false,
					IResource.DEPTH_ONE);
			for (IMarker marker : checkedMarkers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					return;
				}

			}

			IMarker[] markers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_REQUEST, false, IResource.DEPTH_ONE);

			for (IMarker marker : markers) {
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				if (char_start == node.getStartPosition()
						&& length == node.getLength()) {
					marker.delete();
					createCheckedMarker(node, resource, unit, path.getChecks());
					return;
				}

			}

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void createCheckedMarker(ASTNode node, IResource resource,
			CompilationUnit unit, List<AccessControlPoint> checks) throws CoreException {
		String message = "Access control checks are at "
				+ unit.getLineNumber(node.getStartPosition());
		
		Map<String, Object> markerAttributes = new HashMap<String, Object>();	
		markerAttributes.put(IMarker.CHAR_START,
				node.getStartPosition());
		markerAttributes.put(IMarker.CHAR_END, node.getStartPosition()
				+ node.getLength());
		markerAttributes.put(IMarker.MESSAGE,	message);
		markerAttributes.put(IMarker.LINE_NUMBER,
				unit.getLineNumber(node.getStartPosition()));
		markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
		markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		
		IMarker questionCheckedMarker = MarkerAndAnnotationUtil
				.addMarker(resource,
						markerAttributes, PluginConstants.MARKER_ANNOTATION_CHECKED);

		/*
		IMarker questionCheckedMarker = resource
				.createMarker(PluginConstants.PluginConstants.MARKER_ANNOTATION_CHECKED);

		questionCheckedMarker.setAttribute(IMarker.CHAR_START,
				node.getStartPosition());
		questionCheckedMarker.setAttribute(IMarker.CHAR_END,
				node.getStartPosition() + node.getLength());
		questionCheckedMarker.setAttribute(IMarker.MESSAGE, message);
		questionCheckedMarker.setAttribute(IMarker.LINE_NUMBER,
				unit.getLineNumber(node.getStartPosition()));
		questionCheckedMarker.setAttribute(IMarker.SEVERITY,
				IMarker.SEVERITY_INFO);
		questionCheckedMarker.setAttribute(IMarker.PRIORITY,
				IMarker.PRIORITY_HIGH);
		*/
		
		InterfaceUtil.prepareAnnotationRequest(questionCheckedMarker, resource);

	}

	private CompilationUnit getASTRoot() {
		IEditorInput input = target.getEditorInput();
		IJavaElement jElement = JavaUI.getEditorInputJavaElement(input);

		ICompilationUnit cu = (ICompilationUnit) jElement;

		return Utils.getCompilationUnit(cu);
	}

	private void popupDialogWarning() {
		MessageDialog
				.openInformation(
						shell,
						"" + Plugin.PLUGIN_NAME + " Code Annotation",
						"Please choose a sensitive information access point that is marked by a red flag.");

	}

	private void removeInformationControl(){
		
		IInformationControl control = Plugin.currentInformationControl;
		if (control != null)
			control.dispose();
		Plugin.currentInformationControl = null;
	}
}
