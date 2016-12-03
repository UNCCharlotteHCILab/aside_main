package edu.uncc.sis.aside.markers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.uncc.aside.ast.ASTResolving;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.auxiliary.core.AsideScanOneICompilationUnit;
import edu.uncc.sis.aside.auxiliary.core.CodeGenerator;

public class EncodingResolution implements IMarkerResolution,
		IMarkerResolution2 {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			EncodingResolution.class.getName());
	private IProject project;
	private ICompilationUnit fCompilationUnit;
	private CompilationUnit astRoot;
	private String fStrategyType;
	private String esapiEncoderMethodName;
	private IMarker fMarker;

	public EncodingResolution(ICompilationUnit cu, String strategyType, IMarker marker) {
		super();
		fCompilationUnit = cu;
		fStrategyType = strategyType;
		esapiEncoderMethodName = "encodeFor" + fStrategyType;
		fMarker=marker;
		IJavaProject javaProject = cu.getJavaProject();
		if (javaProject != null) {
			project = (IProject) javaProject.getAdapter(IProject.class);
		}
	}

	@Override
	public String getDescription() {
		StringBuffer buf = new StringBuffer();
		String instruction = "-- Double click selection to auto-generate encoding method --";
		String info = "", description = "", content = "";
	    String end = "Follow the \"Output Encoding: Explanations and Examples\" link for more info about why this method invocation needs encoding.";
	    
	    Map<String, Object> markerAttributes= new HashMap<String, Object>();
		try {
			markerAttributes = fMarker.getAttributes();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    if(fStrategyType.equals(PluginConstants.ENCODING_TYPES[0])){
	    	description = "This will add code to encode/abstract data retured by \""+ markerAttributes.get("edu.uncc.sis.aside.marker.ouputArgumentName")+ "\" to ensure that any injected commands to Cascading Style Sheet or Style tag aren't run when loading a page";
			info = "The generated code uses the encodeForCSS method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
		}else if(fStrategyType.equals(PluginConstants.ENCODING_TYPES[1])){
			description = "This will add code to encode/abstract data retured by \""+ markerAttributes.get("edu.uncc.sis.aside.marker.ouputArgumentName")+ "\" to ensure that any injected commands to HTML body aren't run when loading a page";
			info = "The generated code uses the encodeForHTML method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
		}else if(fStrategyType.equals(PluginConstants.ENCODING_TYPES[2])){
			description = "This will add code to encode/abstract data retured by \""+ markerAttributes.get("edu.uncc.sis.aside.marker.ouputArgumentName")+ "\" to ensure that any injected commands to HTML attributes aren't run when loading a page";
			info = "The generated code uses the encodeForHTMLAttr method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
		}else if(fStrategyType.equals(PluginConstants.ENCODING_TYPES[3])){
			description = "This will add code to encode/abstract data retured by \""+ markerAttributes.get("edu.uncc.sis.aside.marker.ouputArgumentName")+ "\" to ensure that any injected commands to javascripts aren't run when loading a page";
			info = "The generated code uses the encodeForJS method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
		}

		content = instruction + "<p><p>" + description + "<p><p>" + info + "<p><p>" + end;
		buf.append(content);
		
		return buf.toString();		
	}

	@Override
	public Image getImage() {
		return Plugin.getImageDescriptor("redCheckmark.png").createImage();
	}

	@Override
	public String getLabel() {
		if(fStrategyType.equals("HTML"))
			return "101-HTML Encoder";
		else if(fStrategyType.equals("HTMLAttribute"))
		    return "102-HTML Attribute Encoder";
		else if(fStrategyType.equals("JavaScript"))
		    return "103-JavaScript Encoder";
		else if(fStrategyType.equals("CSS"))
		    return "104-CSS Encoder";
		else
			return "";
	}

	@Override
	public void run(IMarker marker) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  
	    //get current date time with Date()
		Date date = new Date();
	    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " chose output encoding rule " + fStrategyType + " at "
				+ marker.getAttribute(IMarker.LINE_NUMBER, -1)
				+ " in java file <<"
				+ fCompilationUnit.getElementName() + ">> in Project [" + project.getName() + "]");
		try {
			astRoot = ASTResolving.createQuickFixAST(fCompilationUnit, null);
			ImportRewrite fImportRewrite = ImportRewrite.create(astRoot, true);

			int offset = (int) marker.getAttribute(IMarker.CHAR_START, -1);
			int length = (int) marker.getAttribute(IMarker.CHAR_END, -1)
					- offset;

			ASTNode node = NodeFinder.perform(astRoot, offset, length);

			if (!(node instanceof Expression)) {
				return;
			}

			MethodDeclaration declaration = ASTResolving
					.findParentMethodDeclaration(node);

			if (declaration == null) {
				return;
			}

			Block body = declaration.getBody();

			AST ast = body.getAST();

			IEditorPart part = JavaUI
					.openInEditor(fCompilationUnit, true, true);

			if (part == null) {
				return;
			}

			IEditorInput input = part.getEditorInput();

			if (input == null)
				return;

			IDocument document = JavaUI.getDocumentProvider()
					.getDocument(input);
			//System.out.println("node start="+offset + " length="+length);
			
			/*
			 * replace the marker with annotation to signify that this node has
			 * been validated
			 */
//			ASIDEMarkerAndAnnotationUtil.replaceMarkerWithAnnotation(
//					fCompilationUnit, marker);
			Statement statementStayIn = ASTResolving.findParentStatement(node);
			int statementStartPosition = statementStayIn.getStartPosition();
			MarkerAndAnnotationUtil.deleteMarkerAtPosition(marker);
			
			ITrackedNodePosition replacementPositionTracking = null;
			replacementPositionTracking = CodeGenerator.getInstance().generateEncodingRoutine(document,
					fImportRewrite, ast, declaration, (Expression) node,
					esapiEncoderMethodName);
			
//			if(replacementPositionTracking == null){
//				System.out.println("replacementPositionTracking == null in EncodingResolution");
//				return;
//			}
			//System.out.println("replacementPositionTracking start="+replacementPositionTracking.getStartPosition() + " length="+replacementPositionTracking.getLength());
			MarkerAndAnnotationUtil.createAnnotationAtPosition(fCompilationUnit, replacementPositionTracking.getStartPosition(), replacementPositionTracking.getLength());
			
//			int startPosition = replacementPositionTracking.getStartPosition();
//			int nodeLength = replacementPositionTracking.getLength();
//			CompilationUnit newAstRoot = ASTResolving.createQuickFixAST(
//					fCompilationUnit, null);
//			ASTNode tmpNode = NodeFinder.perform(newAstRoot, startPosition, nodeLength);
//			Statement tmpStatement = ASTResolving.findParentStatement(tmpNode);
//			if(tmpStatement == null){
//				System.out.println("tmpStatement == null in EncodingResolution!");
//			}
//			int realLineNum = newAstRoot.getLineNumber(tmpStatement.getStartPosition());
//			int mStartPosition = tmpStatement.getStartPosition();
			//insert comments with IDocument
//			 IWorkbench wb = PlatformUI.getWorkbench();
//			   IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//			   IWorkbenchPage page = win.getActivePage();
//			   IEditorPart tmpPart = page.getActiveEditor();
//			   if (!(tmpPart instanceof AbstractTextEditor))
//			      return;
//			   ITextEditor editor = (ITextEditor)tmpPart;
//			   IDocumentProvider dp = editor.getDocumentProvider();
//			   IDocument doc = dp.getDocument(editor.getEditorInput());
			   int tmpOffset = statementStartPosition;//doc.getLineOffset(doc.getNumberOfLines()-4);
			   try {
				document.replace(tmpOffset, 0, PluginConstants.ESAPI_ENCODING_COMMENTS);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CompilationUnit newAstRoot = ASTResolving.createQuickFixAST(fCompilationUnit, null);
				ImportRewrite newImportRewrite = ImportRewrite.create(newAstRoot, true);
				CodeGenerator.getInstance().insertEncodingImports(document, newImportRewrite);
						
			IProject fProject = MarkerAndAnnotationUtil.getProjectFromICompilationUnit(fCompilationUnit);
			
			new AsideScanOneICompilationUnit(fProject, fCompilationUnit);
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

}
