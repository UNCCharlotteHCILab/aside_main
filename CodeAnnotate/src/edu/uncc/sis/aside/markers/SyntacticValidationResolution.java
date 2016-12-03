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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
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

public class SyntacticValidationResolution implements IMarkerResolution,
		IMarkerResolution2 {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			SyntacticValidationResolution.class.getName());

	private ICompilationUnit fCompilationUnit;
	private String fInputType; //MM Menu Item Id in the future
	private IMarker fMarker;
//	private IProject fProject;

	/**
	 * Constructor for SyntacticValidationResolution
	 * 
	 * @param cu
	 * @param validationRule
	 */
	SyntacticValidationResolution(ICompilationUnit cu, IMarker marker,
			String inputType) {
		super();
		fCompilationUnit = cu;
		fInputType = inputType;
		fMarker = marker;
	//	fProject = project;
		
		//MM Preparing the label, icon and description from the config file
	}

	@Override
	public String getDescription() {
		String instruction, description = "", info = "", content = "";
		String end = "";
		Map<String, Object> markerAttributes= new HashMap<String, Object>();
		try {
			markerAttributes = fMarker.getAttributes();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instruction = "--Double click this option to insert code--"; //"-- Double click selection to auto-generate validation code --";
		info ="The generated code uses the getValidInput method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
		end =""; // "Follow the \"Input Validation: Explanations and Examples\" link for more info about why this method invocation needs validation.";
		//title = "Selection of this rule will add the following validation routine to your code:";
		if (fInputType == null){
			description = "Description about this rule is not available";
			content = description;
		}
		StringBuffer buf = new StringBuffer();

		if(fInputType.equals("SafeString")){
			description ="This quick fix will insert code to ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' only contains letters and numbers. All other characters will throw an exception.";
			// "getParameter(value) is typically used to obtain external user data which is quite vulnerable to exploitations through the use of questionable entries. Use this method to limit input from an outside source to alphabetical characters and numbers only.";
			// info = "Note, special characters are not allowed (&gt;.@&, etc) and if they are entered, an exception will be thrown that must be handled by the developer (e.g., place in the validation catch response.sendRedirect(\"Login.jsp\"); and an accompanying message to the user that their attempt was unsuccessful)."; 
			//ScreenShot

			//"Note, if a non-valid character is entered (>.@&, etc), an exception is thrown which should be handled by the developer in the catch block (e.g., response.sendRedirect(\"Login.jsp\"); and a message to the user that their login attempt was unsuccessful).";
			// content = instruction + "<p><p>" + description + "<p><p>" + info + "<p><p>" + end;    

		}else if(fInputType.equals("HTTPParameterValue")){
			description = "This quick fix will insert code to ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' only contains characters used in HTTP code: alphanumeric and special characters .-/=_!$*?@. All other characters will throw an exception."; 
			//"getParameter(value) is typically used to obtain external user data which can be exploited with malicious injections. Use this method to ensure that input to be used in HTTP code is limited to alphanumeric characters and the special characters .-/+=_ !$*?@.";
			//info = "The generated code uses the getValidInput method from the Enterprise Security API (ESAPI). Go to Read More for more information on this and other ways to fix this vulnerability.";
			//"Note, if a non-valid character is entered, an exception is thrown which should be handled by the developer in the catch block (e.g., response.sendRedirect(\"Login.jsp\"); and a message to the user that their login attempt was unsuccessful).";
			//content = instruction + "<p><p>" + description + "<p><p>" + info + "<p><p>" + end;
		}

		else if(fInputType.equals("URL")){
			description = "Ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' follow appropriate protocol-host-port format and the characters involved. If non-allowed characters are found, an exception will be thrown that must be handled by the developer (e.g., place in the validation catch response.sendRedirect(\"Login.jsp\"); and an accompanying message to the user that their attempt was unsuccessful).";


		}
		else if(fInputType.equals("CreditCard")){
			description = "Ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' will only contains credit card number in the form of xxxx.xxxx.xxxx.xxxx. Anything else will throw an exception that must be handled by the developer (e.g., place in the validation catch response.sendRedirect(\"Login.jsp\"); and an accompanying message to the user that their attempt was unsuccessful).";



		}else if(fInputType.equals("Email")){
			description = "Ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' will only contains addresses in the form of foo@foo.foo . Anything else will throw an exception that must be handled by the developer (e.g., place in the validation catch response.sendRedirect(\"Login.jsp\"); and an accompanying message to the user that their attempt was unsuccessful).";

		}else if(fInputType.equals("SSN")){
			description = "Ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' will only contains numbers in the form of xxx-xx-xxxx. Anything else will throw an exception that must be handled by the developer (e.g., place in the validation catch response.sendRedirect(\"Login.jsp\"); and an accompanying message to the user that their attempt was unsuccessful).";

		}	
		else if(fInputType.equals("FileName")){
			description = "Ensure that '" +markerAttributes.get("edu.uncc.sis.aside.marker.variableName")+"' will only contains filenames consisting of valid filename characters, followed by a dot and a file extension. All other characters or formats will throw an exception.";

		}
		content = content = instruction + "<p><p>" + description + "<p><p>" + info + "<p><p>" + end;
		buf.append(content);
		return buf.toString();
	}

	@Override
	public Image getImage() {
/*
		if (fInputType.equalsIgnoreCase("URL")
				|| fInputType.equalsIgnoreCase("email")) {
			return Plugin.getImageDescriptor("devil.png")
					.createImage();
		}
*/
		return Plugin.getImageDescriptor("redCheckmark.png")
				.createImage();
	}

	@Override
	public String getLabel() {
		String labelStr = "Filter HttpServletPath";
		if(fInputType.equals("SafeString"))
			labelStr= "101- Allow Only Alphabetical Characters and Numbers"; //102 - 
	/*	else if(fInputType.equals("HttpServletPath"))
			labelStr= "Filter HttpServletPath";*/
		else if(fInputType.equals("HTTPParameterValue"))
			labelStr= "102- Allow Only Minimal HTTP Characters"; //103 - 
		else if(fInputType.equals("URL"))
			labelStr= "103- Allow Only URL Characters"; //104 - 
		else if(fInputType.equals("CreditCard"))
			labelStr= "104- Allow Only Credit Card Numbers"; //105 - 
		else if(fInputType.equals("Email"))
			labelStr= "105- Allow Only Email Addresses"; //106 - 
		else if(fInputType.equals("SSN"))
			labelStr= "106- Allow Only Social Security Number"; //107 - 
		else if(fInputType.equals("FileName"))
			labelStr= "107- Allow Only Valid File Names"; //107 - 
		return labelStr;
	}

	@Override
	public void run(IMarker marker) {

		IProject project = MarkerAndAnnotationUtil.getProjectFromICompilationUnit(fCompilationUnit);
		   DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			  
		  //MM   addResolutionLog(marker, project, dateFormat);
		    try {
				String returnTypeOfMethodDeclarationStr = (String) marker.getAttribute("edu.uncc.sis.aside.marker.returnTypeOfMethodDeclarationBelongTo");
				
				CompilationUnit astRoot = ASTResolving.createQuickFixAST(
						fCompilationUnit, null);
				
				ImportRewrite fImportRewrite = ImportRewrite.create(astRoot, true);

				int offset = (int) fMarker.getAttribute(IMarker.CHAR_START, -1);
				int length = (int) fMarker.getAttribute(IMarker.CHAR_END, -1)
						- offset;

				ASTNode node = NodeFinder.perform(astRoot, offset, length);
				if (node == null) {
					System.out.println("node is null after NodeFinder in SpecialOutputValidationResolution");
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
	//generateValidationRoutine returns the new node of request.getParameter() embraced by the validation code
				int nodeStartPosition = node.getStartPosition();
				int nodeLength = node.getLength();
				
				ASTNode newNode;
				if(node==null)
					System.out.println("node is null in SyntacticValidation. start="+offset+" length="+length+"in generateSpecialOutputValidationRoutine");
//				newNode = CodeGenerator.getInstance().generateValidationRoutine(document,
//						astRoot, fImportRewrite, ast, node, fInputType);
				///////////
//				ITrackedNodePosition replacementPositionTracking = null;
				MarkerAndAnnotationUtil.deleteMarkerAtPosition(marker);
				
				boolean result = CodeGenerator.getInstance().generateSpecialOutputValidationCode(document,
						astRoot, fImportRewrite, ast, node, fInputType, returnTypeOfMethodDeclarationStr);
				
				if(result==false){
					System.out.println("generateSpecialOutputValidationCode does not run properly!");
				}

				new AsideScanOneICompilationUnit(project, fCompilationUnit);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MalformedTreeException e) {
				e.printStackTrace();
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

	}

	private void addResolutionLog(IMarker marker, IProject project,
			DateFormat dateFormat) {
		//get current date time with Date()
		Date date = new Date();
		logger.info(dateFormat.format(date)+ " " + Plugin.getUserId() + " chose input validation rule <validation>" + fInputType
				+ "<validation> at "
				+ marker.getAttribute(IMarker.LINE_NUMBER, -1) + " in java file <<"
				+ fCompilationUnit.getElementName()
				+ ">> in Project ["
				+ project.getName() + "]");
	}


}