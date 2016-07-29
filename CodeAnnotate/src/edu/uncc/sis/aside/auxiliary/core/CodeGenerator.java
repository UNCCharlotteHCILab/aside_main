package edu.uncc.sis.aside.auxiliary.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import edu.uncc.aside.ast.ASTBuilder;
import edu.uncc.aside.ast.ASTResolving;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.domainmodels.ASTRewriteAndTracking;
import edu.uncc.sis.aside.domainmodels.ASTRewriteAndTryCatchFlag;
import edu.uncc.sis.aside.visitors.FindNodeListVisitor;

public class CodeGenerator {

	private static CodeGenerator instance = null;

	private CodeGenerator() {
		super();
	}

	public static CodeGenerator getInstance() {

		if (instance == null) {

			synchronized (CodeGenerator.class) {
				if (instance == null)
					instance = new CodeGenerator();
			}
		}

		return instance;
	}

	/**
	 * There are essentially four cases: 1. ... String string = a.b(); ...
	 * 
	 * 2. String s; ... s = a.b(); ...
	 * 
	 * 3. ... c.d(a.b(), Object s); ...
	 * 
	 * 4. ... f = c.d(a.b(), Object s); ...
	 * 
	 * The correct format of validation is:
	 * 
	 * try{ ESAPI.validator.getValidInput("context", input, "input type",
	 * MAX_LENTGH, allowNull); }catch(InputValidationException e){
	 * 
	 * }catch(IntrusionException e){
	 * 
	 * }
	 * 
	 * 
	 */

	public void OldOnegenerateValidationRoutine(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType) {
		if (inputType == null || inputType.equals("")) {
			inputType = "UNKNOWN";
		}
        //IMarker marker
		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit importEdits = null, textEdits = null;
        //newly added
		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		
		/*
		 * Syntactic checking against regular expressions
		 */

		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			//System.out.println("returned from within if (statement == null)");
			return;
		}

		StructuralPropertyDescriptor location = statement.getLocationInParent();
//newly deleted
//		if (location == null || location.isChildProperty()
//				|| !location.isChildListProperty()) {
//			System.out.println("returned from within if (location == null || location.isChildProperty()|| !location.isChildListProperty())");
//			return;
//		}

		try {
			if (statement instanceof ExpressionStatement) {
//updated code add here. more cases, including multiple method invocations as arguments, and multiple layers of invocation.
//e.g. bug report case 3, for(Transaction transaction:ds.getTransactionByAccount(request.getParameter("transactionAccount"))){..}
				
				ExpressionStatement expressionStatement = (ExpressionStatement) statement;
				Expression expression = expressionStatement.getExpression();
				ASTNode parent = node.getParent();

				/* String s; ... s = a.b(); ... */
				//modified on oct 30
				if (parent == expression && parent instanceof Assignment) {
					Assignment assignment = (Assignment) parent;

					Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
                     
					fASTRewrite = _refactoringCode(fCompilationUnit, fASTRewrite, ast, inputType,statement,
							node);   //passed the node into it for my implementation
					textEdits = fASTRewrite.rewriteAST();
					
					textEdits.apply(document, TextEdit.CREATE_UNDO
							| TextEdit.UPDATE_REGIONS);
					
					
					///newly added
					//maybe the assignment is different?
//					fASTRewrite = _embraceCodeWithTryStatement(fASTRewrite, ast, inputType,
//							statement, node, location); //passed the node into it.
//					textEdits = fASTRewrite.rewriteAST();
//					
//					textEdits.apply(document, TextEdit.CREATE_UNDO
//							| TextEdit.UPDATE_REGIONS);					
					
					///
					fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
					fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
					fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
					importEdits = fImportRewrite.rewriteImports(null);
					
					importEdits.apply(document, TextEdit.CREATE_UNDO
							| TextEdit.UPDATE_REGIONS);

					int importLength = importEdits.getLength();
					CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
							document);
//copyAnnotation is for , since fImportRewrite add some imports here, so it would change the location of code,
					//cause that the annotation is not in the same position as its target node. so you need to
					//consider the offset, and make them at the same position . that is the aim of copyAnnotation method.
					//importLength
					//bugs. annotatedStatementsMap is empty, copyAnnotation is no use.
					MarkerAndAnnotationUtil.copyAnnotation(annotatedStatementsMap, newRoot,
							importLength);
                    //System.out.println("processed within if (parent == expression && parent instanceof Assignment)");
					return;
				}

				/* ... c.d(a.b(), ...); ... */
				if (parent == expression && parent instanceof MethodInvocation) {

					ArrayList<ITrackedNodePosition> list = new ArrayList<ITrackedNodePosition>();
					fASTRewrite = _refactoringCode2(fASTRewrite, ast,
							inputType, statement, node, location);
					textEdits = fASTRewrite.rewriteAST();

					fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
					fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
					fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
					importEdits = fImportRewrite.rewriteImports(null);
					textEdits.apply(document, TextEdit.CREATE_UNDO
							| TextEdit.UPDATE_REGIONS);
					importEdits.apply(document, TextEdit.CREATE_UNDO
							| TextEdit.UPDATE_REGIONS);

					int importLength = importEdits.getLength();
					CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
							document);

					if (!list.isEmpty()) {
						ITrackedNodePosition trackPosition = list.get(0);
						MarkerAndAnnotationUtil.copyAnnotation(trackPosition, node, newRoot,
								importLength);
					}
					//System.out.println("processed within if (parent == expression && parent instanceof MethodInvocation)");
					return;
				}

				/* Object s; ... s = c.d(a.b(), ...); ... */
				//bug report, case 2, the if statement here is wrong.
				ASTNode grandNode = parent.getParent();
				if (grandNode != null && grandNode == expression
						&& parent instanceof MethodInvocation) {
					System.out.println("bug report case 2. processed within if (grandNode != null && grandNode == expression&& parent instanceof MethodInvocation)");
					return;
				}
			}

			if (statement instanceof VariableDeclarationStatement) {
//bugs here, if there is comments in the code like 
				/*comments
				 * fsadfksj
				*/
				//it will cast exceptions
				VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.getParent();

				if (fragment == null) {
					return;
				}
				Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
				ArrayList<ITrackedNodePosition> originalNodePositionList = new ArrayList<ITrackedNodePosition>();
				/* ... String string = a.b(); ... */
				fASTRewrite = _refactoringCode(fCompilationUnit, fASTRewrite, ast, inputType,statement,
						node);  //passed the node into it for my implementation
				textEdits = fASTRewrite.rewriteAST();
				fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
				fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
				fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
				textEdits.apply(document, TextEdit.CREATE_UNDO
						| TextEdit.UPDATE_REGIONS);
				importEdits = fImportRewrite.rewriteImports(null);

				importEdits.apply(document, TextEdit.CREATE_UNDO
						| TextEdit.UPDATE_REGIONS);

				int importLength = importEdits.getLength();
				CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
						document);

				MarkerAndAnnotationUtil.copyAnnotation(annotatedStatementsMap, newRoot, importLength);

				if (!originalNodePositionList.isEmpty()) {
					ITrackedNodePosition trackPosition = originalNodePositionList
							.get(0);
					MarkerAndAnnotationUtil.copyAnnotation(trackPosition, node, newRoot, importLength);
				}
//System.out.println("if (statement instanceof VariableDeclarationStatement)");
				return;
			}
//bug report case 3, the following code runs. because it does not belong to all the above conditions.
			// we should include these conditions.
			Shell shell = Plugin.getDefault().getShell();
			MessageBox messageBox = new MessageBox(shell, SWT.OK
					| SWT.ICON_WARNING);
			messageBox.setText("FYI:");
			messageBox
					.setMessage("case 3 in bug report. " + Plugin.PLUGIN_NAME + " currently does not handle this case, would you mind writing your own validatoin/encoding routine?");
			messageBox.open();

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}
	public ASTNode generateValidationRoutine(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType) {
		//document, root, fImportRewrite, ast, node, key
		
			ASTNode newNode = null; // the node created and put into the validation code.
		if (inputType == null || inputType.equals("")) {
			inputType = "UNKNOWN";
		}
		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			System.out.println("returned from within if (statement == null) in method generateValidationRoutine");
			return null;
		}

		StructuralPropertyDescriptor location = statement.getLocationInParent();

		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit importEdits = null, textEdits = null;
        //newly added
		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		//IMarker marker = 
		try{
		Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
        
				
		//replace node with esapi validation code
		
		fASTRewrite = _refactoringCode(fCompilationUnit, fASTRewrite, ast, inputType, statement,
			         node);   //passed the node into it for my implementation
		textEdits = fASTRewrite.rewriteAST();
		
		textEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		////////////
//		ASTRewrite secondASTRewrite = ASTRewrite.create(ast);
//		fASTRewrite =  _refactoringCode(fCompilationUnit, secondASTRewrite, ast, inputType, statement,
//		         node);//passed the node into it.
//		textEdits = fASTRewrite.rewriteAST();
//		
//		textEdits.apply(document, TextEdit.CREATE_UNDO
//				| TextEdit.UPDATE_REGIONS);	
		////////////

		//put the body of the methodDeclaration input a tryStatement
//				fASTRewrite = _embraceCodeWithTryStatement(fASTRewrite, ast, inputType,
//						statement, node, location); //passed the node into it.
//				textEdits = fASTRewrite.rewriteAST();
//				
//				textEdits.apply(document, TextEdit.CREATE_UNDO
//						| TextEdit.UPDATE_REGIONS);	
         //imports
		fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
		importEdits = fImportRewrite.rewriteImports(null);
		
		importEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);

		int importLength = importEdits.getLength();
		CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
				document);
//copyAnnotation is for , since fImportRewrite add some imports here, so it would change the location of code,
		//cause that the annotation is not in the same position as its target node. so you need to
		//consider the offset, and make them at the same position . that is the aim of copyAnnotation method.
		//importLength
		//bugs. annotatedStatementsMap is empty, copyAnnotation is no use.
		MarkerAndAnnotationUtil.copyAnnotation(annotatedStatementsMap, newRoot,
				importLength);
       // System.out.println("newly done) in generateValidationRoutine");
		return newNode;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//
		/*
		 * Syntactic checking against regular expressions
		 */

		Statement st = ASTResolving.findParentStatement(node);
		if (st == null) {
			System.out.println("returned from within if (st == null) in generateValidationRoutine");
			return null;
		}
//newly deleted
//		if (location == null || location.isChildProperty()
//				|| !location.isChildListProperty()) {
//			System.out.println("returned from within if (location == null || location.isChildProperty()|| !location.isChildListProperty())");
//			return;
//		}
		return newNode;
	

	}
	
	public Integer[] generateValidationCodeAndAddASIDE_Flag(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType, String returnTypeOfMethodDeclarationStr) {
		
		int realLineNum = 0;
		int mStart = 0;
		int mLength = 0;
		Integer[] results = new Integer[4];
		//document, root, fImportRewrite, ast, node, key
		MethodDeclaration testDeclaration = ASTResolving.findParentMethodDeclaration(node);
		AST testAST = node.getAST();
			ASTNode newNode = null; // the node created and put into the validation code.
		if (inputType == null || inputType.equals("")) {
			inputType = "UNKNOWN";
		}
		Statement statement = ASTResolving.findParentStatement(node);
//		if (statement == null) {
//			System.out.println("returned from within if (statement == null) in generateValidationCodeAndAddASIDE_Flag");
//			return null;
//		}

		//StructuralPropertyDescriptor location = statement.getLocationInParent();
        if(!(node instanceof Expression)){
        	System.out.println("!node instanceof Expression");
        	return null;
        }
		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit importEdits = null, textEdits = null;
        //newly added
		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		//IMarker marker = 
		try{
		Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
        Object ob;
        /////create a cloned node
        Document doc = new Document("import java.util.List;\nclass X {}\n");
        ASTParser parserForClone = ASTParser.newParser(AST.JLS3);
        parserForClone.setSource(doc.get().toCharArray());
        CompilationUnit cuForClone = (CompilationUnit) parserForClone.createAST(null);
        cuForClone.recordModifications();
        
        AST ASTForClone = cuForClone.getAST();
        
		ASTNode cloneNode = ASTNode.copySubtree(ASTForClone, node); 
		//System.out.println("cloneNode "+cloneNode);
		/////		
		//replace node with esapi validation code
		int nodeStartPosition = node.getStartPosition();
		int nodeLength = node.getLength();
		ITrackedNodePosition itrackNodePosition = fASTRewrite.track(node);
		ASTRewriteAndTryCatchFlag astRewriteAndTryCatchFlag = null;
		astRewriteAndTryCatchFlag = _refactoringCodeAndAddASIDE_Flag(fCompilationUnit, fASTRewrite, ast, inputType, statement,
			         node, returnTypeOfMethodDeclarationStr);   //passed the node into it for my implementation
	    fASTRewrite = astRewriteAndTryCatchFlag.getAstRewrite();
	    results[3] = astRewriteAndTryCatchFlag.getJustGeneratedWhichTryCatch();
		
		textEdits = fASTRewrite.rewriteAST();
		
		textEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		
		/////
		
//		IEditorPart tmpPart = JavaUI
//				.openInEditor(fCompilationUnit, true, true);
//		if (tmpPart == null) {
//			return null;
//		}
//
//		IEditorInput tmpInput = tmpPart.getEditorInput();
//
//		if (tmpInput == null)
//			return null;
//
//		IDocument newDocument = JavaUI.getDocumentProvider()
//				.getDocument(tmpInput);
		
		//ASTNode EsapiNode = NodeFinder.perform(newAstRoot, nodeStartPosition, nodeLength);
		CompilationUnit newAstRoot = ASTResolving.createQuickFixAST(
				fCompilationUnit, null);
		NodeFinder nodeFinder = new NodeFinder(newAstRoot, nodeStartPosition, nodeLength);
		ASTNode EsapiNode = nodeFinder.perform(newAstRoot, nodeStartPosition, nodeLength);
		//System.out.println("EsapiNode"+EsapiNode);
		if(EsapiNode == null){
			System.out.println("EsapiNode == null in generateValidationCodeAndAddASIDE_Flag");
			}
		//System.out.println(EsapiNode+" ccccc "+ cloneNode);
		
		FindNodeListVisitor visitor = new FindNodeListVisitor(EsapiNode, cloneNode);
		if(cloneNode==null)
			System.out.println("cloneNode==null in generateValidationCodeAndAddASIDE_Flag");
		visitor.process();
		LinkedList<ASTNode> CandidateNodeList = visitor.getMatchedNodeList();
		ASTNode matchNode;
		matchNode = MarkerAndAnnotationUtil.findTheRightNodeFromList(CandidateNodeList, nodeStartPosition);
		
		int itrackStartPosition = itrackNodePosition.getStartPosition();
		int itrackLength = itrackNodePosition.getLength();
		//the matched node found
		int lineNumBeforeImportEdits = 0;
		if (matchNode != null) {
			MarkerAndAnnotationUtil.createAnnotationAtPosition(fCompilationUnit, matchNode);
			
			//add a line comment before the position?
			//This part need to be implemented-----------------------------------
			Statement tmpSta = ASTResolving.findParentStatement(matchNode);
			mStart = tmpSta.getStartPosition();
			mLength = tmpSta.getLength();
			
			System.out.println("itrackStartPosition " +itrackStartPosition);
			System.out.println("itrackLength "+itrackLength);
			if(mStart==itrackStartPosition && mLength==itrackLength)
				System.out.println("same");
			//addCommentsBeforeStatement(fCompilationUnit, mStart, mLength);
		    lineNumBeforeImportEdits = newAstRoot.getLineNumber(mStart);
			//System.out.println("lineNumBeforeImportEdits="+lineNumBeforeImportEdits);			
			
		}
		//System.out.println("MatchedNode"+matchNode);
		if(matchNode == null){
			System.out.println("MatchedNode == null in generateValidationCodeAndAddASIDE_Flag");
			}
//		/////////
	
		////////////
//		ASTRewrite secondASTRewrite = ASTRewrite.create(ast);
//		fASTRewrite =  _embraceCodeWithTryStatement(secondASTRewrite, ast, inputType, statement,
//		         node);//passed the node into it.
//		textEdits = fASTRewrite.rewriteAST();
//		
//		textEdits.apply(document, TextEdit.CREATE_UNDO
//				| TextEdit.UPDATE_REGIONS);	
		////////////

		//put the body of the methodDeclaration input a tryStatement
		//ASTRewrite secondASTRewrite = ASTRewrite.create(ast);
//		fASTRewrite = _embraceCodeWithTryStatement(fASTRewrite, ast, inputType,
//		statement, node, location); //passed the node into it.
//		textEdits = fASTRewrite.rewriteAST();
//			
//		textEdits.apply(document, TextEdit.CREATE_UNDO
//						| TextEdit.UPDATE_REGIONS);	
         //imports
		//---------------note Jan. 28, I cut this part and paste to some place above.
		fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
		importEdits = fImportRewrite.rewriteImports(null);
		
		importEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);

		int importLength = importEdits.getLength();
		//Since Aside only generate 3 lines of Imports, so the real line number of the generated esapi statement
		//wil be added with 3. if Imports have already been generated, then no Imports will be added, so the added line num is 0.
		int addedLineNum = 0;
		if(importLength != 0)
			addedLineNum = 3;
		
		//System.out.println("importLength: "+importLength);
		CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
				document);
				
//copyAnnotation is for , since fImportRewrite add some imports here, so it would change the location of code,
		//cause that the annotation is not in the same position as its target node. so you need to
		//consider the offset, and make them at the same position . that is the aim of copyAnnotation method.
		//importLength
		//bugs. annotatedStatementsMap is empty, copyAnnotation is no use.
		MarkerAndAnnotationUtil.copyAnnotation(annotatedStatementsMap, newRoot,importLength);
		//add a line comment before the position?
		
		//addCommentsBeforeStatement(fCompilationUnit, matchNode);
		
//System.out.println("newly done) in generateValidationCodeAndAddASIDE_Flag");
		//newly added on Jan. 20
		//--------------open file stream and write comments like a String
		
		int realStartPosition = mStart + importLength;
	
		realLineNum = lineNumBeforeImportEdits + addedLineNum;
		//AddCommentsThroughFileStream(fCompilationUnit, realLineNum);
		//System.out.println("realLineNum="+realLineNum);
		
		results[0]=realLineNum;
		results[1]=realStartPosition;
		results[2]=mLength;
	
        //--------------
		//fCompilationUnit.get
		return results;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return results;

	}
	
	public ASTNode generateTryCode(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType) {
		//document, root, fImportRewrite, ast, node, key

			ASTNode newNode = null; // the node created and put into the validation code.
		if (inputType == null || inputType.equals("")) {
			inputType = "UNKNOWN";
		}
		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			System.out.println("returned from within if (statement == null) in generateTryCode");
			return null;
		}

		StructuralPropertyDescriptor location = statement.getLocationInParent();

		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit importEdits = null, textEdits = null;
        //newly added
		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		//IMarker marker = 
		try{
		Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
        
				
		//replace node with esapi validation code
		
		fASTRewrite = _embraceCodeWithTryStatement(fASTRewrite, ast, inputType, statement,
			         node);   //passed the node into it for my implementation
	
				
		textEdits = fASTRewrite.rewriteAST();
		
		textEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		
		/////
		
		////////////
//		ASTRewrite secondASTRewrite = ASTRewrite.create(ast);
//		fASTRewrite =  _refactoringCode(fCompilationUnit, secondASTRewrite, ast, inputType, statement,
//		         node);//passed the node into it.
//		textEdits = fASTRewrite.rewriteAST();
//		
//		textEdits.apply(document, TextEdit.CREATE_UNDO
//				| TextEdit.UPDATE_REGIONS);	
		////////////

		//put the body of the methodDeclaration input a tryStatement
		//ASTRewrite secondASTRewrite = ASTRewrite.create(ast);
//		fASTRewrite = _embraceCodeWithTryStatement(fASTRewrite, ast, inputType,
//		statement, node, location); //passed the node into it.
//		textEdits = fASTRewrite.rewriteAST();
//			
//		textEdits.apply(document, TextEdit.CREATE_UNDO
//						| TextEdit.UPDATE_REGIONS);	
         //imports
		fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
		fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
		importEdits = fImportRewrite.rewriteImports(null);
		
		importEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);

		int importLength = importEdits.getLength();
		CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
				document);
//copyAnnotation is for , since fImportRewrite add some imports here, so it would change the location of code,
		//cause that the annotation is not in the same position as its target node. so you need to
		//consider the offset, and make them at the same position . that is the aim of copyAnnotation method.
		//importLength
		//bugs. annotatedStatementsMap is empty, copyAnnotation is no use.
		MarkerAndAnnotationUtil.copyAnnotation(annotatedStatementsMap, newRoot,importLength);
//System.out.println("newly copy done)");
		return newNode;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//
		/*
		 * Syntactic checking against regular expressions
		 */

		Statement st = ASTResolving.findParentStatement(node);
		if (st == null) {
			System.out.println("returned from within if (st == null) in generateTryCode");
			return null;
		}
//newly deleted
//		if (location == null || location.isChildProperty()
//				|| !location.isChildListProperty()) {
//			System.out.println("returned from within if (location == null || location.isChildProperty()|| !location.isChildListProperty())");
//			return;
//		}
		return newNode;
	

	}
	
	
	
	private ASTRewrite _embraceCodeWithTryStatement(ASTRewrite fASTRewrite, AST ast,
			String inputType, Statement statement, ASTNode node) {
//bugs, what is location for?
		
		MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		//methodToBeReplaced.getStructuralProperty(Block.STATEMENTS_PROPERTY);
		TryStatement tryStatement = ast.newTryStatement();
		Block tmpBlock = methodToBeReplaced.getBody();
		
		
		
		StringLiteral inputTypeArg = ast.newStringLiteral();
		inputTypeArg.setLiteralValue(inputType);
		// inputTypeArg.setLiteralValue(ESAPI_INPUT_TYPE);
		
	//MM	LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
	//MM			ESAPI_TRY_COMMENT, ASTNode.LINE_COMMENT);
		
		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				methodToBeReplaced.getBody(),
				Block.STATEMENTS_PROPERTY);//b
        Block copyOfBlock = ast.newBlock();
		
		List<Statement> statementInBlock = methodToBeReplaced.getBody().statements();
		ASTNode tmpAstNode;
		for(Iterator<Statement> it = statementInBlock.iterator();it.hasNext();){
						
						tmpAstNode = it.next();
								Statement st = (Statement) fASTRewrite.createCopyTarget(tmpAstNode);
								//System.out.println(tmpAstNode);
								copyOfBlock.statements().add(st);  
						  
					}
	      	
		tryStatement.setBody(copyOfBlock);
		CatchClause validationExceptionClause = ast.newCatchClause();
		SingleVariableDeclaration veFormalParameter = ast
				.newSingleVariableDeclaration();
		veFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
		veFormalParameter.setType(ast.newSimpleType(ast
				.newName(PluginConstants.VALIDATION_EXCEPTION_TYPE)));

		validationExceptionClause.setException(veFormalParameter);

		Block veClauseBody = ast.newBlock();
		validationExceptionClause.setBody(veClauseBody);

		CatchClause intrusionExceptionClause = ast.newCatchClause();
		SingleVariableDeclaration ieFormalParameter = ast
				.newSingleVariableDeclaration();
		ieFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
		ieFormalParameter.setType(ast.newSimpleType(ast
				.newName(PluginConstants.INTRUSION_EXCEPTION_TYPE)));

		intrusionExceptionClause.setException(ieFormalParameter);

		Block ieClauseBody = ast.newBlock();
		intrusionExceptionClause.setBody(ieClauseBody);

		tryStatement.catchClauses().add(validationExceptionClause);
		tryStatement.catchClauses().add(intrusionExceptionClause);
				
//		for(Iterator i=methodToBeReplaced.getBody().statements().iterator();i.hasNext();){
//		     fListRewrite.remove((ASTNode)i.next(), null);
//		}
		
		fListRewrite.insertAt(tryStatement, 0, null);

		return fASTRewrite;
	}
	private void addCommentsBeforeStatement(ICompilationUnit fCompilationUnit, int nodeStartPosition, int nodeLength){
		CompilationUnit newAstRoot = ASTResolving.createQuickFixAST(
				fCompilationUnit, null);
		NodeFinder nodeFinder = new NodeFinder(newAstRoot, nodeStartPosition, nodeLength);
		ASTNode node = nodeFinder.perform(newAstRoot, nodeStartPosition, nodeLength);
		
		AST ast = node.getAST();
		MethodDeclaration declaration = ASTResolving
				.findParentMethodDeclaration(node);
try{
		if (declaration == null) {
			return;
		}

		Block body = declaration.getBody();

ASTRewrite fASTRewrite = ASTRewrite.create(ast);

		IEditorPart part = JavaUI.openInEditor(fCompilationUnit, true, true);
		if (part == null) {
			return;
		}

		IEditorInput input = part.getEditorInput();

		if (input == null)
			return;

		IDocument document = JavaUI.getDocumentProvider()
				.getDocument(input);
		
		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			System.out.println("returned from within if (st == null) in addcomment");
			return;
		}
		if(!statement.getAST().equals(ast))
		{
			System.out.println("!statement.getAST().equals(ast)");
			return;
		}
	
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				PluginConstants.ESAPI_COMMENT, ASTNode.LINE_COMMENT);
		
		//MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);

		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				body,
				Block.STATEMENTS_PROPERTY);
//insert before the statement
		fListRewrite.insertFirst(notice, null);
		fListRewrite.insertLast(notice, null);
fListRewrite.insertBefore(statement,notice,   null);

//newly added
//Statement placeHolder= (Statement) fASTRewrite.createStringPlaceholder("//mycomment", ASTNode.EMPTY_STATEMENT);
//fListRewrite.insertBefore(placeHolder, statement,  null);


/////////
//fListRewrite.insertAt(notice, index, null);
TextEdit textEdits = null;

	textEdits = fASTRewrite.rewriteAST();

	textEdits.apply(document, TextEdit.CREATE_UNDO
			| TextEdit.UPDATE_REGIONS);
}catch(Exception e){
	e.printStackTrace();
}
	
}
	private ASTRewriteAndTryCatchFlag _refactoringCodeAndAddASIDE_Flag(ICompilationUnit fCompilationUnit, ASTRewrite fASTRewrite, AST ast,
			String inputType, Statement statement,ASTNode node, String returnTypeOfMethodDeclarationStr) {

		ASTRewriteAndTryCatchFlag astRewriteAndTryCatchFlag = new ASTRewriteAndTryCatchFlag();
		MethodInvocation esapiValidator = ast.newMethodInvocation();
		esapiValidator.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
		esapiValidator.setName(ast.newSimpleName(PluginConstants.ESAPI_VALIDATOR));

		MethodInvocation esapiValidation = ast.newMethodInvocation();
		esapiValidation.setExpression(esapiValidator);
		esapiValidation.setName(ast
				.newSimpleName(PluginConstants.ESAPI_VALIDATOR_GETVALIDINPUT));

		List<ASTNode> arguments = esapiValidation.arguments();

		StringLiteral contextArg = ast.newStringLiteral();
		contextArg.setLiteralValue(PluginConstants.ESAPI_CONTEXT_PLACEHOLDER);
		arguments.add(0, contextArg);
		Expression tmpAssignmentLeft = null;
		
		
		StringLiteral inputTypeArg = ast.newStringLiteral();
		inputTypeArg.setLiteralValue(inputType);
		// inputTypeArg.setLiteralValue(ESAPI_INPUT_TYPE);
		

		NumberLiteral lengthArg = ast.newNumberLiteral(PluginConstants.ESAPI_DEFAULT_LENGTH);
		

		BooleanLiteral allowNullArg = ast.newBooleanLiteral(false);
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				PluginConstants.ESAPI_COMMENT, ASTNode.LINE_COMMENT);
		
		MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);

		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				methodToBeReplaced.getBody(),
				Block.STATEMENTS_PROPERTY);

		//newly added
		ExpressionStatement esapiValidationStatement = ast.newExpressionStatement(esapiValidation);
		arguments.add(1, (Expression)fASTRewrite.createCopyTarget(node));
			//arguments.add(1, fASTRewrite.createCopyTarget(esapiValidator));
			arguments.add(2, inputTypeArg);
			arguments.add(3, lengthArg);
			arguments.add(4, allowNullArg);
			if(node==null)
				System.out.println(esapiValidationStatement+" wrong in _refactoringCodeAndAddASIDE_Flag");
			//System.out.println(esapiValidationStatement+" "+ASIDEMarkerAndAnnotationUtil.getASIDE_Flag(fASTRewrite.createCopyTarget(node)));
			//fListRewrite.insertAfter( esapiValidationStatement,statement, null);
			
		fASTRewrite.replace(node, esapiValidation, null);
		//fListRewrite.insertBefore(notice, statement, null);
		//here to paste tryStatement code
		//currently, only deals with methodInvocation case
		int justGeneratedWhichTryCatch = 0;
//		if(hasValidationTryStatement((MethodInvocation)node,statement)==false)
//		{
			//four possible return values, 0(need to generate both validation and intrusion exception), 
			//1(no need to generate neither), 2(need to generate ValidationException), 3(need to generate IntrusionException)
		 int inWhichException = inWhichExceptionTryCatch((MethodInvocation)node);		
			if(inWhichException != 1){
	//////////start

			Statement st = ASTResolving.findParentStatement(node);
			if (st == null) {
				System.out.println("returned from within if (st == null) in _refactoringCodeAndAddASIDE_Flag");
				return null;
			}

			TryStatement tryStatement = ast.newTryStatement();
			Block tmpBlock = methodToBeReplaced.getBody();
			inputTypeArg.setLiteralValue(inputType);
		
	        Block copyOfBlock = ast.newBlock();
			
			List<Statement> statementInBlock = methodToBeReplaced.getBody().statements();
			ASTNode tmpAstNode;
			for(Iterator<Statement> it = statementInBlock.iterator();it.hasNext();){
							
							tmpAstNode = it.next();
									Statement stmt = (Statement) fASTRewrite.createCopyTarget(tmpAstNode);
									//System.out.println(tmpAstNode);
									copyOfBlock.statements().add(stmt);  
							  
						}
		      	
			tryStatement.setBody(copyOfBlock);
			//newly added Feb. 24
			ReturnStatement returnStatementInCatch = ast.newReturnStatement();
			ReturnStatement returnStatementInCatch2 = ast.newReturnStatement();
			if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[0])){
				BooleanLiteral returnExpressionInCatch = ast.newBooleanLiteral(false);
				BooleanLiteral returnExpressionInCatch2 = ast.newBooleanLiteral(false);
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[1])){
				//return ;
				returnStatementInCatch.setExpression(null);
				returnStatementInCatch2.setExpression(null);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[2])){
				//return 0;
				NumberLiteral returnExpressionInCatch = ast.newNumberLiteral("0");
				NumberLiteral returnExpressionInCatch2 = ast.newNumberLiteral("0");
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[3])){
				//return null;
				NullLiteral returnExpressionInCatch = ast.newNullLiteral();
				NullLiteral returnExpressionInCatch2 = ast.newNullLiteral();
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}
			
			CatchClause validationExceptionClause = ast.newCatchClause();
			SingleVariableDeclaration veFormalParameter = ast
					.newSingleVariableDeclaration();
			veFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
			veFormalParameter.setType(ast.newSimpleType(ast
					.newName(PluginConstants.VALIDATION_EXCEPTION_TYPE)));

			validationExceptionClause.setException(veFormalParameter);
            
			Block veClauseBody = ast.newBlock();
			veClauseBody.statements().add(returnStatementInCatch);
			validationExceptionClause.setBody(veClauseBody);

			CatchClause intrusionExceptionClause = ast.newCatchClause();
			SingleVariableDeclaration ieFormalParameter = ast
					.newSingleVariableDeclaration();
			ieFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
			ieFormalParameter.setType(ast.newSimpleType(ast
					.newName(PluginConstants.INTRUSION_EXCEPTION_TYPE)));

			intrusionExceptionClause.setException(ieFormalParameter);
            Block ieClauseBody = ast.newBlock();
			ieClauseBody.statements().add(returnStatementInCatch2);
			intrusionExceptionClause.setBody(ieClauseBody);
			if(inWhichException == 0){
			tryStatement.catchClauses().add(validationExceptionClause);
			tryStatement.catchClauses().add(intrusionExceptionClause);
			justGeneratedWhichTryCatch = 1;
			}else if(inWhichException == 2){
				tryStatement.catchClauses().add(validationExceptionClause);
				justGeneratedWhichTryCatch = 2;
			}else if(inWhichException == 3){
				tryStatement.catchClauses().add(intrusionExceptionClause);
				justGeneratedWhichTryCatch = 3;
			}else{
				System.out.println("errors in _refactoringCodeAndAddASIDE_Flag");
				justGeneratedWhichTryCatch = 0;
				return null;
			}		
			for(Iterator i=methodToBeReplaced.getBody().statements().iterator();i.hasNext();){
			     fListRewrite.remove((ASTNode)i.next(), null);
			}
			fListRewrite.insertAt(notice, 0, null);
			fListRewrite.insertAfter(tryStatement, notice, null);
			/////end
			
		}
		//tmp end here
		//}
		//	fASTRewrite.replace(node, ASIDEMarkerAndAnnotationUtil.createNodeCopyAndAnnotation(fCompilationUnit, fASTRewrite, node), null);
		astRewriteAndTryCatchFlag.setAstRewrite(fASTRewrite);
		astRewriteAndTryCatchFlag.setJustGeneratedWhichTryCatch(justGeneratedWhichTryCatch);
		return astRewriteAndTryCatchFlag;
	}
	private boolean hasValidationTryStatement(MethodInvocation targetNode,
			Statement statement) {
		
		MethodDeclaration methodDeclaration = ASTResolving.findParentMethodDeclaration(statement);
	   Statement targetStatement = null;
       Block block = methodDeclaration.getBody();
       List<Statement> statements = block.statements();
       if(statements.isEmpty() || statements.size()==0){
    	   System.out.println("statements.isEmpty() || statements.size()==0 in hasValidationTryStatement");
    	   return false;
       }
    	   targetStatement = statements.get(0);
    	   
			if (!(targetStatement instanceof TryStatement)) {
				//System.out.println("!(targetStatement instanceof TryStatement) in hasValidationTryStatement");
				return false;
			}

			TryStatement targetTryStatement = (TryStatement) targetStatement;
			List<CatchClause> catchList = targetTryStatement.catchClauses();
			if (catchList.size() < 2){
				System.out.println("catchList.size() < 2 in hasValidationTryStatement");
				return false;}
			boolean validation = false, intrusion = false;
			for (CatchClause clause : catchList) {
				SingleVariableDeclaration exception = clause.getException();
				Type type = exception.getType();
				ITypeBinding binding = type.resolveBinding();
				if (binding != null) {
					String qualifiedName = binding.getQualifiedName();
					if (qualifiedName
							.equals("org.owasp.esapi.errors.ValidationException")) {
						validation = true;
					} else if (qualifiedName
							.equals("org.owasp.esapi.errors.IntrusionException")) {
						intrusion = true;
					}
				}
			}

			if (validation && intrusion)
				return true;
			else
				return false;
	

	}
	
	//four possible return values, 0(need to generate both validation and intrusion exception), 
	//1(no need to generate neither), 2(need to generate ValidationException), 3(need to generate IntrusionException)
	//added March. 3rd, judge if the node is within a Exception or RuntimeException try catch block
	//if it is in a ValidationException, return 3, if it is in a IntrusionException or RuntimeException, return 2;
	//if it is in both ValidationException and IntrusionException, 
	//or both ValidationException and RuntimeException, or in an Exception block, return 1;
	//else return 0
	private int isWhichTypeExceptionTryCatch(TryStatement targetStatement){
		if(targetStatement == null)
			   return 0;
		   else if(targetStatement instanceof TryStatement){
	      
				TryStatement targetTryStatement = (TryStatement) targetStatement;
				List<CatchClause> catchList = targetTryStatement.catchClauses();
				if (catchList.size() < 1){
					System.out.println("catchList.size() < 1 in isWhichTypeExceptionTryCatch");
					return 0;
				}
				else if(catchList.size() == 1){
					CatchClause clause = catchList.get(0);
					SingleVariableDeclaration exception = clause.getException();
					Type type = exception.getType();
					ITypeBinding binding = type.resolveBinding();
					if (binding != null) {
						String qualifiedName = binding.getQualifiedName();
						if (qualifiedName
								.equals("java.lang.Exception")) {
							return 1;
						} else if (qualifiedName
								.equals("java.lang.RuntimeException")) {
							return 2;
						} else if (qualifiedName
								.equals("org.owasp.esapi.errors.IntrusionException")) {
							return 2;
						} else if (qualifiedName
								.equals("org.owasp.esapi.errors.ValidationException")) {
							return 3;
						} 
					}
				}
				else if(catchList.size() >= 2){
					boolean isException = false;
					boolean isRuntimeException = false;
					boolean isValidationException = false;
					boolean isIntrusionException = false;
					for(int i = 0; i < catchList.size(); i++){
					CatchClause clause = catchList.get(i);
					SingleVariableDeclaration exception = clause.getException();
					Type type = exception.getType();
					ITypeBinding binding = type.resolveBinding();
					if (binding != null) {
						String qualifiedName = binding.getQualifiedName();
						if (qualifiedName
								.equals("java.lang.Exception")) {
							isException = true;
						} else if (qualifiedName
								.equals("java.lang.RuntimeException")) {
							isRuntimeException = true;
						} else if (qualifiedName
								.equals("org.owasp.esapi.errors.ValidationException")) {
							isValidationException = true;
						} else if (qualifiedName
								.equals("org.owasp.esapi.errors.IntrusionException")) {
							isIntrusionException = true;
						} 
					}
					}
					if(isException == true){
						return 1;
					} else if(isValidationException == true){
						if(isRuntimeException == true || isIntrusionException == true)
							return 1;
						else
							return 3;
					} else if(isRuntimeException == true || isIntrusionException == true){
						return 2;
					}
					}
				}
		   else{
			   return 0;
		   }
		return 0;
}
	//four possible return values, 0(need to generate both validation and intrusion exception), 
		//1(no need to generate neither), 2(need to generate ValidationException), 3(need to generate IntrusionException)
	private int inWhichExceptionTryCatch(MethodInvocation targetNode) {
		Statement targetStatement = null;
		targetStatement = ASTResolving.findParentTryStatement(targetNode);
		int result = 0;
		boolean hasValidationException = false;
		boolean hasIntrusionException = false;
		while(targetStatement != null && targetStatement instanceof TryStatement){
			result = isWhichTypeExceptionTryCatch((TryStatement)targetStatement);
			if(result == 1){
				return 1;
			}else if(result == 2){
				hasIntrusionException = true;
			}else if(result == 3){
				hasValidationException = true;
			}
			targetStatement = ASTResolving.findParentTryStatement(targetStatement);			
		}
		if(hasValidationException == true && hasIntrusionException == true)
			return 1;
		else if(hasIntrusionException == true)
			return 2;
		else if(hasValidationException == true)
			return 3;
		else
			return 0;
    }
	
	//here node is the string like request.getParameter(..)?
	private ASTRewrite _refactoringCode(ICompilationUnit fCompilationUnit, ASTRewrite fASTRewrite, AST ast,
			String inputType, Statement statement,ASTNode node) {
//bugs, what is location for?
		//MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		//MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		//methodToBeReplaced.getStructuralProperty(Block.STATEMENTS_PROPERTY);
	
		MethodInvocation esapiValidator = ast.newMethodInvocation();
		esapiValidator.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
		esapiValidator.setName(ast.newSimpleName(PluginConstants.ESAPI_VALIDATOR));

		MethodInvocation esapiValidation = ast.newMethodInvocation();
		esapiValidation.setExpression(esapiValidator);
		esapiValidation.setName(ast
				.newSimpleName(PluginConstants.ESAPI_VALIDATOR_GETVALIDINPUT));

		List<ASTNode> arguments = esapiValidation.arguments();

		StringLiteral contextArg = ast.newStringLiteral();
		contextArg.setLiteralValue(PluginConstants.ESAPI_CONTEXT_PLACEHOLDER);
		arguments.add(0, contextArg);
		Expression tmpAssignmentLeft = null;
		
		
		StringLiteral inputTypeArg = ast.newStringLiteral();
		inputTypeArg.setLiteralValue(inputType);
		// inputTypeArg.setLiteralValue(ESAPI_INPUT_TYPE);
		

		NumberLiteral lengthArg = ast.newNumberLiteral(PluginConstants.ESAPI_DEFAULT_LENGTH);
		

		BooleanLiteral allowNullArg = ast.newBooleanLiteral(false);
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				PluginConstants.ESAPI_COMMENT, ASTNode.LINE_COMMENT);
		
		MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);

		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				methodToBeReplaced.getBody(),
				Block.STATEMENTS_PROPERTY);
		
//		if (statement instanceof VariableDeclarationStatement) {
//			
//							VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.getParent();
//							ListRewrite fListRewrite = fASTRewrite.getListRewrite(
//							fragment,
//							VariableDeclarationFragment.	.STATEMENTS_PROPERTY);
//		}
//		else if(statement instanceof ExpressionStatement ){
//								ExpressionStatement expressionStatement = (ExpressionStatement) statement;
//								Expression expression = expressionStatement.getExpression();
//								ASTNode parent = node.getParent();
//
//								if (parent == expression && parent instanceof Assignment) {
//									ListRewrite fListRewrite = fASTRewrite.getListRewrite(
//											expressionStatement.,
//											Expression.	.STATEMENTS_PROPERTY);
//									
//								}
//		}
//		
		//methodToBeReplaced.copytmpVariableDeclarationFragment
		//methodToBeReplaced.getBody().copySubtree(target, node)
		//ASIDEMarkerAndAnnotationUtil.	
		//	arguments.add(1, fASTRewrite.createCopyTarget(node)); //needrevised
		//newly added
		ExpressionStatement esapiValidationStatement = ast.newExpressionStatement(esapiValidation);
		arguments.add(1, (Expression)fASTRewrite.createCopyTarget(node));
			//arguments.add(1, fASTRewrite.createCopyTarget(esapiValidator));
			arguments.add(2, inputTypeArg);
			arguments.add(3, lengthArg);
			arguments.add(4, allowNullArg);
			if(node==null)
				System.out.println(esapiValidationStatement + "wrong in _refactoringCode");
			fListRewrite.insertBefore( esapiValidationStatement,statement, null);
		//fASTRewrite.replace(node, esapiValidation, null);
		fListRewrite.insertBefore(notice, esapiValidationStatement, null);
		//	fASTRewrite.replace(node, ASIDEMarkerAndAnnotationUtil.createNodeCopyAndAnnotation(fCompilationUnit, fASTRewrite, node), null);
		return fASTRewrite;
	}

	private ASTRewrite _refactoringCode2(ASTRewrite fASTRewrite, AST ast,
			String inputType, Statement statement, ASTNode coveringNode,
			StructuralPropertyDescriptor location) {
		/* ... c.d(a.b(), ...); ... */
		//example: 
		//c.hey(ren.copy(),cd.my());
		//parent == expression && parent instanceof MethodInvocation

MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);

ListRewrite fListRewrite = fASTRewrite.getListRewrite(
		methodToBeReplaced.getBody(),
		Block.STATEMENTS_PROPERTY);

		ExpressionStatement originalStatement = (ExpressionStatement)statement;
		Expression originalExpression = originalStatement.getExpression();
		MethodInvocation originalMethodInvocation = (MethodInvocation)originalExpression;
		
		Expression outerExpression = originalMethodInvocation.getExpression(); //c, simpleName
		SimpleName outerMethodName = originalMethodInvocation.getName(); //hey, simpleName
		List originalArguments = originalMethodInvocation.arguments();
		//System.out.println("origExpres "+originalExpression);
		MethodInvocation nowMethodInvocation = ast.newMethodInvocation();
		ExpressionStatement nowStatement = ast.newExpressionStatement(nowMethodInvocation);
		nowMethodInvocation.setExpression((Expression) fASTRewrite.createCopyTarget(outerExpression));
		nowMethodInvocation.setName((SimpleName) fASTRewrite.createCopyTarget(outerMethodName));
		List nowArguments = nowMethodInvocation.arguments();
		
		TryStatement tryStatement = ast.newTryStatement();

		MethodInvocation esapiValidator = ast.newMethodInvocation();
		esapiValidator.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
		esapiValidator.setName(ast.newSimpleName(PluginConstants.ESAPI_VALIDATOR));

		MethodInvocation esapiValidation = ast.newMethodInvocation();
		esapiValidation.setExpression(esapiValidator);
		esapiValidation.setName(ast
				.newSimpleName(PluginConstants.ESAPI_VALIDATOR_GETVALIDINPUT));

		StringLiteral contextArg = ast.newStringLiteral();
		contextArg.setLiteralValue(PluginConstants.ESAPI_CONTEXT_PLACEHOLDER);
		List<ASTNode> arguments = esapiValidation.arguments();
		arguments.add(0, contextArg);
		
		arguments.add(1, fASTRewrite.createCopyTarget(coveringNode));

		StringLiteral inputTypeArg = ast.newStringLiteral();
		inputTypeArg.setLiteralValue(inputType);
		arguments.add(2, inputTypeArg);

		NumberLiteral lengthArg = ast.newNumberLiteral(PluginConstants.ESAPI_DEFAULT_LENGTH);
		arguments.add(3, lengthArg);

		BooleanLiteral allowNullArg = ast.newBooleanLiteral(false);
		arguments.add(4, allowNullArg);
					
		ASTNode tmpNode;
		int i=0;
		for(Iterator it = originalArguments.iterator();it.hasNext();){
			tmpNode = (ASTNode) it.next();
			if(tmpNode.equals(coveringNode)){
				nowArguments.add(i, esapiValidation);
			}else{
				nowArguments.add(i, fASTRewrite.createCopyTarget(tmpNode));
			}
			i++;
		}
		
		
		Block tryBlock = ast.newBlock();
		ASTNode tmpAstNode;
		List<Statement> statementInBlock = methodToBeReplaced.getBody().statements();
		for(Iterator<Statement> it = statementInBlock.iterator();it.hasNext();){
						
						tmpAstNode = it.next();
						//if(tmpAstNode instanceof LineComment)
						//System.out.println(tmpAstNode.toString());
						  if(tmpAstNode.equals(statement))
						  {
							  //copyOfBody.statements().add((ASTNode)notice);
							  //validationStatement = ast.newTryStatement();
							  
							  //Statement st = (Statement)fASTRewrite.createCopyTarget(validationStatement); 
							  tryBlock.statements().add(nowStatement);
								
								//targetStatement = st;
							
						  }
						  else{
								Statement st = (Statement) fASTRewrite.createCopyTarget(tmpAstNode);
								tryBlock.statements().add(st);  
						  }
					}
		
		tryStatement.setBody(tryBlock);

		CatchClause validationExceptionClause = ast.newCatchClause();
		SingleVariableDeclaration veFormalParameter = ast
				.newSingleVariableDeclaration();
		veFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
		veFormalParameter.setType(ast.newSimpleType(ast
				.newName(PluginConstants.VALIDATION_EXCEPTION_TYPE)));

		validationExceptionClause.setException(veFormalParameter);

		Block veClauseBody = ast.newBlock();
//		LineComment ve = (LineComment) fASTRewrite.createStringPlaceholder(
//				"//TODO: Auto-generated by ASIDE", ASTNode.LINE_COMMENT);
//		veClauseBody.statements().add(ve);
		validationExceptionClause.setBody(veClauseBody);

		CatchClause intrusionExceptionClause = ast.newCatchClause();
		SingleVariableDeclaration ieFormalParameter = ast
				.newSingleVariableDeclaration();
		ieFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
		ieFormalParameter.setType(ast.newSimpleType(ast
				.newName(PluginConstants.INTRUSION_EXCEPTION_TYPE)));
///bugs here
		//validationExceptionClause.setException(ieFormalParameter);
		intrusionExceptionClause.setException(ieFormalParameter);
		
		Block ieClauseBody = ast.newBlock();
//		LineComment ie = (LineComment) fASTRewrite.createStringPlaceholder(
//				"//TODO: Auto-generated by ASIDE", ASTNode.LINE_COMMENT);
//		ieClauseBody.statements().add(ie);
		intrusionExceptionClause.setBody(ieClauseBody);

		tryStatement.catchClauses().add(validationExceptionClause);
		tryStatement.catchClauses().add(intrusionExceptionClause);

//		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
//				ESAPI_COMMENT, ASTNode.LINE_COMMENT);
//
//		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
//				ASTResolving.getParent(statement, ASTNode.BLOCK),
//				Block.STATEMENTS_PROPERTY);

		//fListRewrite.insertAfter(tryStatement, statement, null);
		//fListRewrite.insertBefore(notice, tryStatement, null);

		//Block ifThen = ast.newBlock();
		//ifThen.statements().add(fASTRewrite.createCopyTarget(statement));
		for(Iterator itt=methodToBeReplaced.getBody().statements().iterator();itt.hasNext();){
		     fListRewrite.remove((ASTNode)itt.next(), null);
		}
		
		fListRewrite.insertAt(tryStatement, 0, null);

		return fASTRewrite;
	}


	
	public ITrackedNodePosition generateEncodingRoutine(IDocument document,
			ImportRewrite fImportRewrite, AST ast,
			MethodDeclaration declaration, Expression node, String methodName)
			throws IllegalArgumentException, CoreException,
			MalformedTreeException, BadLocationException {

		ICompilationUnit fCompilationUnit = fImportRewrite.getCompilationUnit();

		

		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit textEdits = null;

		Statement statement = ASTResolving.findParentStatement(node);

//		if (!(statement instanceof ExpressionStatement)) {
//			return null;
//		}

//		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
//		Expression expression = expressionStatement.getExpression();
//
//		ASTNode parent = node.getParent();
//		if (parent == null || parent != expression) {
//			return null;
//		}
		
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				PluginConstants.ESAPI_COMMENT,
				ASTNode.LINE_COMMENT);		
		ListRewrite listRewrite = fASTRewrite.getListRewrite(
				ASTResolving.getParent(statement, ASTNode.BLOCK),
				Block.STATEMENTS_PROPERTY);
		
		Expression copyOfCoveredNode = (Expression) fASTRewrite
				.createCopyTarget(node);
		int nodeType = node.getNodeType();

		MethodInvocation replacement = ast.newMethodInvocation();
//		 ITrackedNodePosition position = null;
		MethodInvocation _expression = ast.newMethodInvocation();
		_expression.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
		_expression.setName(ast.newSimpleName(PluginConstants.ESAPI_ENCODER));
//need to be changed
	    replacement.setExpression(_expression);
		replacement.setName(ast.newSimpleName(methodName));

		/*List<Expression> arguments = replacement.arguments();
		arguments.add(0, copyOfCoveredNode);*/
		
		///newly added Aug 30
		String t = "fds";
		//System.out.println(t);
		System.out.println("fsdf" + t + "fsdffff" + t+ "ffffffffffff" + String.valueOf(true));
		InfixExpression resultInfixEx = ast.newInfixExpression();
		ITrackedNodePosition replacementPositionTracking = null;
		
		if(node instanceof InfixExpression){
			
			System.out.println("copyOfCoveredNode is instanceof InfixExpression");
			InfixExpression tmpInfixEx = (InfixExpression)node;
			if(tmpInfixEx.getOperator().equals(org.eclipse.jdt.core.dom.InfixExpression.Operator.PLUS)){
				System.out.println("tmpInfixEx.getOperator().equals(org.eclipse.jdt.core.dom.InfixExpression.Operator.PLUS)");
				Expression leftExpr = tmpInfixEx.getLeftOperand();
				System.out.println("leftExpr "+ leftExpr);
				Expression rightExpr = tmpInfixEx.getRightOperand();
				Expression tmpEx = null;
				Object tmpObj = null;
				//process leftOperand
				// !(leftExpr instanceof InfixExpression) is used to handle 
				//case out.println("" + "" + session.getValue("sesname"));
				//where leftExpr 
				if((MarkerAndAnnotationUtil.isBasicConstant(leftExpr)) || (leftExpr instanceof InfixExpression)){
                    //constant, then just keep it as it is
					
					//add this one with the result expression
					Expression copyOfLeftExpr = (Expression) fASTRewrite
							.createCopyTarget(leftExpr);
					resultInfixEx.setLeftOperand(copyOfLeftExpr);
					

				}else{ //not constant, then embrace it with esapi encoding code
					//MethodInvocation replaceEx = ast.newMethodInvocation();
					MethodInvocation tmpReplacement = ast.newMethodInvocation();
					MethodInvocation _express = ast.newMethodInvocation();
					_express.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
					_express.setName(ast.newSimpleName(PluginConstants.ESAPI_ENCODER));
			
					tmpReplacement.setExpression(_express);
					tmpReplacement.setName(ast.newSimpleName(methodName));
					//need to be changed
					Expression copyOfLeftExpr = (Expression) fASTRewrite
							.createCopyTarget(leftExpr);
					List<Expression> replaceArguments = tmpReplacement.arguments();
					replaceArguments.add(0, copyOfLeftExpr);
                    //add this one with the result expression
					resultInfixEx.setLeftOperand(tmpReplacement);
				}
				//set operator
				resultInfixEx.setOperator(org.eclipse.jdt.core.dom.InfixExpression.Operator.PLUS);
				//process rightOperand
				if((MarkerAndAnnotationUtil.isBasicConstant(rightExpr))){//constant, then just keep it as it is
					//add this one with the result expression\
					Expression copyOfRightExpr = (Expression) fASTRewrite
							.createCopyTarget(rightExpr);
					resultInfixEx.setRightOperand(copyOfRightExpr);
					
				}else{ //not constant, then embrace it with esapi encoding code
					//MethodInvocation replaceEx = ast.newMethodInvocation();
					MethodInvocation tmpReplacement = ast.newMethodInvocation();
					MethodInvocation _express = ast.newMethodInvocation();
					_express.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
					_express.setName(ast.newSimpleName(PluginConstants.ESAPI_ENCODER));
			
					tmpReplacement.setExpression(_express);
					tmpReplacement.setName(ast.newSimpleName(methodName));
					//need to be changed
					Expression copyOfRightExpr = (Expression) fASTRewrite
							.createCopyTarget(rightExpr);
					List<Expression> replaceArguments = tmpReplacement.arguments();
					replaceArguments.add(0, copyOfRightExpr);
                    //add this one with the result expression
					resultInfixEx.setRightOperand(tmpReplacement);
				}
				
				//process extended Operands
				if(tmpInfixEx.hasExtendedOperands()){
					List extendedOperands = tmpInfixEx.extendedOperands();
					for(int i = 0; i < extendedOperands.size(); i++){
						tmpObj = extendedOperands.get(i);
						if(tmpObj instanceof Expression){
						tmpEx = (Expression) tmpObj;
						if(MarkerAndAnnotationUtil.isBasicConstant(tmpEx)){//constant, then just keep it as it is
							
							//add this one with the result expression
							Expression copyOfTmpEx = (Expression) fASTRewrite
									.createCopyTarget(tmpEx);
							resultInfixEx.extendedOperands().add(copyOfTmpEx);
							
						}else{ //not constant, then embrace it with esapi encoding code
							//MethodInvocation replaceEx = ast.newMethodInvocation();
							
							MethodInvocation tmpReplacement = ast.newMethodInvocation();
							MethodInvocation _express = ast.newMethodInvocation();
							_express.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
							_express.setName(ast.newSimpleName(PluginConstants.ESAPI_ENCODER));
					
							tmpReplacement.setExpression(_express);
							tmpReplacement.setName(ast.newSimpleName(methodName));
							//need to be changed
							Expression copyOfTmpEx = (Expression) fASTRewrite
									.createCopyTarget(tmpEx);
							List<Expression> replaceArguments = tmpReplacement.arguments();
							replaceArguments.add(0, copyOfTmpEx);
							resultInfixEx.extendedOperands().add(tmpReplacement);

						}
						
						}else{
							System.err.println("tmpObj instanceof Expression is false in CodeGenerator for infixExpression");
							return null;
						}
						
					}
				}
				System.out.println("resultInfixEx = " + resultInfixEx);
			/*	//replacement.setExpression();
				replacement.setName(ast.newSimpleName(methodName));
				List<Expression> replaceArguments = replacement.arguments();
				replaceArguments.add(0, resultInfixEx);*/
				
			}
			replacementPositionTracking = fASTRewrite.track(resultInfixEx);
			fASTRewrite.replace(node, resultInfixEx, null);
		}else if(MarkerAndAnnotationUtil.isBasicConstant(node)){
			System.out.println("strange cases in CodeGenerator.java");
		}
		else{
			MethodInvocation replace = ast.newMethodInvocation();
//			 ITrackedNodePosition position = null;
			MethodInvocation _express = ast.newMethodInvocation();
			_express.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
			_express.setName(ast.newSimpleName(PluginConstants.ESAPI_ENCODER));

			replace.setExpression(_express);
			replace.setName(ast.newSimpleName(methodName));

			List<Expression> args = replace.arguments();
			args.add(0, copyOfCoveredNode);
	        //newly added
			replacementPositionTracking = fASTRewrite.track(replace);
			fASTRewrite.replace(node, replace, null);
		
		}
		
		textEdits = fASTRewrite.rewriteAST();
		
		textEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		

		return replacementPositionTracking;
	}
	public void insertEncodingImports(IDocument document,
			ImportRewrite fImportRewrite){

		TextEdit importEdits = null;
		fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
		try {
			importEdits = fImportRewrite.rewriteImports(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			importEdits.apply(document, TextEdit.CREATE_UNDO
					| TextEdit.UPDATE_REGIONS);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean generateSpecialOutputValidationCode(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType, String returnTypeOfMethodDeclarationStr) {
		//document, root, fImportRewrite, ast, node, key
		ASTNode matchNode = null;
			ASTNode newNode = null; // the node created and put into the validation code.
			CompilationUnit thirdAstRoot = null;
		if (inputType == null || inputType.equals("")) {
			inputType = "UNKNOWN";
		}
		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			System.out.println("returned from within if (statement == null) in generateSpecialOutputValidationCode");
			return false;
		}
		int hasGeneratedWhichException = 0; 
		StructuralPropertyDescriptor location = statement.getLocationInParent();

		ASTRewrite fASTRewrite = ASTRewrite.create(ast);
		TextEdit textEdits = null;
        //newly added
		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		int esapiNodeLength = 0;
		boolean hasEsapiTryCatch = true;
		int tmpOffset = 0;
		int ESAPI_Comment_flag = 1; //which ESAPI comment to use; 1 means use PluginConstants.ESAPI_COMMENTS, 0 means use PluginConstants.ESAPI_SIMPLE_COMMENT

		int returnStartPosition = 0;
		int returnLength = 0 ;
		MethodDeclaration methodDeclarationStayIn = null;
		String esapi_comment = "";
		try{
			
		Map<ITrackedNodePosition, ArrayList<ASTNode>> annotatedStatementsMap = new HashMap<ITrackedNodePosition, ArrayList<ASTNode>>();
        Object ob;
        
		//replace the node with esapi validation code(but have not add tryStatement), and record the location
		//of the esapi validation code in replacementPositionTracking
        ITrackedNodePosition replacementPositionTracking = null;
       /* if(node instanceof MethodInvocation){
           int inWhichExceptionFlag = inWhichExceptionTryCatch((MethodInvocation)node);
           if(inWhichExceptionFlag != 1)
        	   esapi_comment = PluginConstants.ESAPI_COMMENTS;
           else 
        	   esapi_comment = PluginConstants.ESAPI_SIMPLE_COMMENT;
        }*/
        esapi_comment = PluginConstants.ESAPI_SIMPLE_COMMENT;
        
		replacementPositionTracking = generateSpecialOutputValidationRoutine(document,
				astRoot,fImportRewrite, ast,
				node, inputType);   //passed the node into it for my implementation
		
		if(replacementPositionTracking == null){
			System.out.println("replacementPositionTracking == null in generateSpecialOutputValidationCode");
			return false;
		}
		//System.out.println("SpecialOutputValidation---replacementPositionTracking start="+replacementPositionTracking.getStartPosition() + " length="+replacementPositionTracking.getLength());
		//ASIDEMarkerAndAnnotationUtil.createAnnotationAtPosition(fCompilationUnit, replacementPositionTracking.getStartPosition(), replacementPositionTracking.getLength());
		//////find the esapi validation node, and create a clone node for it, for future use.
		//start from here is the finding of just generated esapi code
		
		//add comments before the statement the node belongs to
				CompilationUnit newestAstRoot = ASTResolving.createQuickFixAST(fCompilationUnit, null);
				
				NodeFinder tmpNodeFinder = new NodeFinder(newestAstRoot, replacementPositionTracking.getStartPosition(), replacementPositionTracking.getLength());
				
				ASTNode theEsapiNode = tmpNodeFinder.perform(newestAstRoot, replacementPositionTracking.getStartPosition(), replacementPositionTracking.getLength());
				
				Statement statementBelongTo = ASTResolving.findParentStatement(theEsapiNode);
				
				int realLineNum = newestAstRoot.getLineNumber(statementBelongTo.getStartPosition());
				//add comments with IDoc
//				 IWorkbench wb = PlatformUI.getWorkbench();
//				   IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//				   IWorkbenchPage page = win.getActivePage();
//				   IEditorPart tmpPart = page.getActiveEditor();
//				   if (!(tmpPart instanceof AbstractTextEditor))
//				      return false;
//				   ITextEditor editor = (ITextEditor)tmpPart;
//				   IDocumentProvider dp = editor.getDocumentProvider();
//				   IDocument doc = dp.getDocument(editor.getEditorInput());
				  
				tmpOffset = statementBelongTo.getStartPosition();//doc.getLineOffset(doc.getNumberOfLines()-4);
				   IEditorPart part = JavaUI
							.openInEditor(fCompilationUnit, true, true);
					
				   if (part == null) {
						return false;
					}
					IEditorInput input = part.getEditorInput();
					
					if (input == null)
						return false;
					IDocument doc = JavaUI.getDocumentProvider()
							.getDocument(input);
				   try {
					   
					doc.replace(tmpOffset, 0, esapi_comment);
					
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				   int esapiCommentLength = esapi_comment.length();
		
		///////................
		CompilationUnit newAstRoot = ASTResolving.createQuickFixAST(
							fCompilationUnit, null);
		//find the statementStayIn after inserting the esapi comment
		NodeFinder nodeFinder = new NodeFinder(newAstRoot, theEsapiNode.getStartPosition() + esapiCommentLength, theEsapiNode.getLength());
	
		ASTNode esapiNode = nodeFinder.perform(newAstRoot, theEsapiNode.getStartPosition() + esapiCommentLength, theEsapiNode.getLength());
		//System.out.println("EsapiNode"+esapiNode);
		if(esapiNode == null){
			System.out.println("esapiNode == null in generateSpecialOutputValidationCode");
		}
		int esapiNodeStartPosition = esapiNode.getStartPosition();
		//int esapiNodeLength = esapiNode.getLength();
		
	    //matchNode = esapiNode;
	    
//        Document docForClone = new Document("import java.util.List;\nclass X {}\n");
//        ASTParser parserForClone = ASTParser.newParser(AST.JLS3);
//        parserForClone.setSource(docForClone.get().toCharArray());
//        CompilationUnit cuForClone = (CompilationUnit) parserForClone.createAST(null);
//        cuForClone.recordModifications();
        
        //AST ASTForClone = cuForClone.getAST();
		ASTRewriteAndTracking astRewriteAndTracking;
	    ITrackedNodePosition validationReturnPostion;
	    ITrackedNodePosition intrusionReturnPosition;
		
	    Statement tmpStatement = ASTResolving.findParentStatement(esapiNode);
		//hasEsapiTryCatch = hasValidationTryStatement((MethodInvocation)esapiNode,tmpStatement);
		//four possible return values, 0(need to generate both validation and intrusion exception), 
		//1(no need to generate neither), 2(need to generate ValidationException), 3(need to generate IntrusionException)
		int inWhichException = 2;
		
		if(esapiNode instanceof MethodInvocation)
			inWhichException = inWhichExceptionTryCatch((MethodInvocation)esapiNode);
		
		if(inWhichException == 0)
		    hasGeneratedWhichException = 1;
		
		else if(inWhichException == 2)
			hasGeneratedWhichException = 2;
		
		else if(inWhichException == 3)
			hasGeneratedWhichException = 3;
		//if(hasEsapiTryCatch == false){
			
		if(inWhichException != 1){
			//remove the annotation on the old esapiNode, because after adding tryStatement, the position is different.
			//we will attach annotation on its new position
			MarkerAndAnnotationUtil.removeAnnotationAtPosition(fCompilationUnit,
					esapiNode);
			
			AST secondAst = newAstRoot.getAST();
			ASTRewrite secondfASTRewrite = ASTRewrite.create(secondAst);
		
		methodDeclarationStayIn = ASTResolving.findParentMethodDeclaration(esapiNode);    
		
		astRewriteAndTracking = generateTry(document, fCompilationUnit,secondfASTRewrite, secondAst,
				inputType, esapiNode, returnTypeOfMethodDeclarationStr, hasGeneratedWhichException);
		
		validationReturnPostion = astRewriteAndTracking.getFirstNodePosition();
		
		intrusionReturnPosition = astRewriteAndTracking.getSecondNodePosition();
		
		if(hasGeneratedWhichException == 1 || hasGeneratedWhichException == 2){
			
			returnStartPosition = validationReturnPostion.getStartPosition();
			returnLength = validationReturnPostion.getLength();
			
		}else if(hasGeneratedWhichException == 3){
			
			returnStartPosition = intrusionReturnPosition.getStartPosition();
			returnLength = intrusionReturnPosition.getLength();
		}
		

			}
			}catch(Exception e){
			e.printStackTrace();
		}
		
		  //modified Mar. 7
		   //int newOffset = tmpOffset + esapiCommentLength;
		   boolean hasInsertCommentForCatchReturn = false;
		   //hasEsapiTryCatch == false represents that just generate try catch, so we need to insert comment
		   //if(hasEsapiTryCatch == false){
			 //hasGeneratedWhichException is a flag denoting which exception statements have been generated
				//1 denotes both validationException and IntrusionException have been generated, 2 denotes 
				//only generated ValidationException, 3 denotes only generated IntrusionException
				if(hasGeneratedWhichException == 1 || hasGeneratedWhichException == 2 || hasGeneratedWhichException == 3)
			   hasInsertCommentForCatchReturn = insertCommentForCatchReturn(fCompilationUnit, returnStartPosition, returnLength, hasGeneratedWhichException);
		   //}
		   if(hasInsertCommentForCatchReturn == false){
			   System.out.println("Comments for CatchReturn have not been inserted!");
		   }
		   try{
			TextEdit importEdits = null;
		    fImportRewrite.addImport(PluginConstants.ESAPI_IMPORT);
			fImportRewrite.addImport(PluginConstants.ESAPI_VALIDATION_EXCEPTION_IMPORT);
			fImportRewrite.addImport(PluginConstants.ESAPI_INTRUSION_EXCEPTION_IMPORT);
			importEdits = fImportRewrite.rewriteImports(null);
			
			importEdits.apply(document, TextEdit.CREATE_UNDO
					| TextEdit.UPDATE_REGIONS);

			int importLength = importEdits.getLength();
			
			CompilationUnit newRoot = ASTBuilder.getASTBuilder().parse(
					document);
		   }catch(Exception e){
				e.printStackTrace();
			}
		//ASIDEMarkerAndAnnotationUtil.insertComments(fCompilationUnit, realLineNum, PluginConstants.ESAPI_COMMENTS);
        return true;
		
	}
	//hasGeneratedWhichException is a flag denoting which exception statements have been generated
	//1 denotes both validationException and IntrusionException have been generated, 2 denotes 
	//only generated ValidationException, 3 denotes only generated IntrusionException
	public boolean insertCommentForCatchReturn(ICompilationUnit fCompilationUnit, int returnStartPosition, int returnLength,  int hasGeneratedWhichException){
		//added Feb. 25, insert comments before the return statements for two catch blocks
		   
		   CompilationUnit astRootForReturn = ASTResolving.createQuickFixAST(
					fCompilationUnit, null);
		   
		   AST ast = astRootForReturn.getAST();
		   ASTRewrite fASTRewrite = ASTRewrite.create(ast);
			NodeFinder nodeFinderForReturn = new NodeFinder(astRootForReturn, returnStartPosition, returnLength);
			ASTNode coveringNode = nodeFinderForReturn.getCoveringNode();
			if(coveringNode == null){
				System.out.println("error in coveringNode = nodeFinderForReturn.getCoveringNode()--coveringNode == null");
				//return false;
			}
			MethodDeclaration methodDeclarationStayIn = ASTResolving.findParentMethodDeclaration(coveringNode);
			
			if(methodDeclarationStayIn == null){
				System.out.println("coveringNode = " + coveringNode);
				System.out.println("methodDeclarationStayIn == null in insertCommentForCatchReturn");
				return false;
			}
		    Block blockStayIn = methodDeclarationStayIn.getBody();
		    Statement targetStatement;
		       List<Statement> statements = blockStayIn.statements();
		       if(statements.isEmpty() || statements.size() == 0){
		    	   System.out.println("statements.isEmpty() || statements.size()==0 in insertCommentForCatchReturn");
		    	   return false;
		       }
		    	   targetStatement = statements.get(0);
		    	   
					if (!(targetStatement instanceof TryStatement)) {
						System.out.println("!(targetStatement instanceof TryStatement) in insertCommentForCatchReturn");
						return false;
					}

					TryStatement targetTryStatement = (TryStatement) targetStatement;
					List<CatchClause> catchList = targetTryStatement.catchClauses();
					if (catchList.size() < 1){
						System.out.println("catchList.size() < 1 in insertCommentForCatchReturn");
						return false;
					}
					boolean hasValidationException = false, hasIntrusionException = false;
					CatchClause validationClause = null;
					CatchClause intrusionClause = null;
					//has generated both exceptions statements
					if(hasGeneratedWhichException == 1){
						hasValidationException = true;
						hasIntrusionException = true;
						validationClause = catchList.get(0);
						intrusionClause = catchList.get(1);
					}else if(hasGeneratedWhichException == 2){
						hasValidationException = true;
						validationClause = catchList.get(0);
					}else if(hasGeneratedWhichException == 3){
						hasIntrusionException = true;
						intrusionClause = catchList.get(0);	
					}else{
						System.out.println("error in insertCommentForCatchReturn of CodeGenerator");
					}
					
					if(hasValidationException == true){
						ListRewrite fListRewrite = fASTRewrite.getListRewrite(validationClause.getBody(), Block.STATEMENTS_PROPERTY);
						Statement firstStatement = null;
						if(validationClause.getBody().statements().size() == 1)
						   firstStatement = (Statement) validationClause.getBody().statements().get(0);
						if(firstStatement == null || !(firstStatement instanceof ReturnStatement)){
							System.out.println("error in insertComment to trycatch--firstStatement == null || !(firstStatement instanceof ReturnStatement)");
						}
						LineComment validationNotice = (LineComment) fASTRewrite.createStringPlaceholder(
								PluginConstants.VALIDATION_EXCEPTION_COMMENTS, ASTNode.LINE_COMMENT);
						fListRewrite.insertBefore(validationNotice, firstStatement, null);
	                    }
						if(hasIntrusionException == true){
						ListRewrite fListRewriteIntrusion = fASTRewrite.getListRewrite(intrusionClause.getBody(), Block.STATEMENTS_PROPERTY);
						Statement firstStatementIntrusion = null;
						if(intrusionClause.getBody().statements().size() == 1)		
						firstStatementIntrusion = (Statement) intrusionClause.getBody().statements().get(0);
						if(firstStatementIntrusion == null || !(firstStatementIntrusion instanceof ReturnStatement)){
							System.out.println("error in insertComment to trycatch--firstStatementIntrusion == null || !(firstStatement instanceof ReturnStatement)");
						}
						LineComment intrusionNotice = (LineComment) fASTRewrite.createStringPlaceholder(
								PluginConstants.INTRUTION_EXCEPTION_COMMENTS, ASTNode.LINE_COMMENT);
						fListRewriteIntrusion.insertBefore(intrusionNotice, firstStatementIntrusion,  null);
						}
						IEditorPart part = null;
						try {
							part = JavaUI
									.openInEditor(fCompilationUnit, true, true);
						} catch (PartInitException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (JavaModelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (part == null) {
							return false;
						}
						IEditorInput input = part.getEditorInput();
						if (input == null)
							return false;
						IDocument document = JavaUI.getDocumentProvider()
								.getDocument(input);
						TextEdit textEdits = null;
						try {
							textEdits = fASTRewrite.rewriteAST();
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							textEdits.apply(document, TextEdit.CREATE_UNDO
									| TextEdit.UPDATE_REGIONS);
						} catch (MalformedTreeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
			return true;
	}
	//hasGeneratedWhichException == 1 denotes that need to generate both, 2 denotes that need to generate ValidationException
	//3 denotes that need to generate IntrusionException
	public ASTRewriteAndTracking generateTry(IDocument document, ICompilationUnit fCompilationUnit, ASTRewrite fASTRewrite, AST ast,
			String inputType, ASTNode node, String returnTypeOfMethodDeclarationStr, int hasGeneratedWhichException){
		ASTRewriteAndTracking astRewriteAndTracking = new ASTRewriteAndTracking();
		ITrackedNodePosition trackValidationReturnStatement = null;
		ITrackedNodePosition trackIntrusionReturnStatement = null;	
		
		Statement statement = ASTResolving.findParentStatement(node);
		MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				 PluginConstants.ESAPI_TRY_COMMENT, ASTNode.LINE_COMMENT);
		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				methodToBeReplaced.getBody(),
				Block.STATEMENTS_PROPERTY);
	
		
//		if(hasValidationTryStatement((MethodInvocation)node,statement)==false)
//		{
	//////////start
           if(hasGeneratedWhichException != 0){
			Statement st = ASTResolving.findParentStatement(node);
			if (st == null) {
				System.out.println("returned from within if (st == null) in generateTry");
				return null;
			}

			TryStatement tryStatement = ast.newTryStatement();
			Block tmpBlock = methodToBeReplaced.getBody();
			//inputTypeArg.setLiteralValue(inputType);
		
	        Block copyOfBlock = ast.newBlock();
			
			List<Statement> statementInBlock = methodToBeReplaced.getBody().statements();
			ASTNode tmpAstNode;
				
			for(Iterator<Statement> it = statementInBlock.iterator();it.hasNext();){
							
							        tmpAstNode = it.next();
									Statement stmt = (Statement) fASTRewrite.createCopyTarget(tmpAstNode);
									//System.out.println(tmpAstNode);
									copyOfBlock.statements().add(stmt);  
							  
						}
		      	
			tryStatement.setBody(copyOfBlock);
			//newly added Feb. 24
			ReturnStatement returnStatementInCatch = ast.newReturnStatement();
			ReturnStatement returnStatementInCatch2 = ast.newReturnStatement();
			
			trackValidationReturnStatement = fASTRewrite.track(returnStatementInCatch);
			trackIntrusionReturnStatement = fASTRewrite.track(returnStatementInCatch2);
			
			if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[0])){
				BooleanLiteral returnExpressionInCatch = ast.newBooleanLiteral(false);
				BooleanLiteral returnExpressionInCatch2 = ast.newBooleanLiteral(false);
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[1])){
				//return ;
				returnStatementInCatch.setExpression(null);
				returnStatementInCatch2.setExpression(null);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[2])){
				//return 0;
				NumberLiteral returnExpressionInCatch = ast.newNumberLiteral("0");
				NumberLiteral returnExpressionInCatch2 = ast.newNumberLiteral("0");
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}else if(returnTypeOfMethodDeclarationStr.equals(PluginConstants.returnTypeCategories[3])){
				//return null;
				NullLiteral returnExpressionInCatch = ast.newNullLiteral();
				NullLiteral returnExpressionInCatch2 = ast.newNullLiteral();
				returnStatementInCatch.setExpression(returnExpressionInCatch);
				returnStatementInCatch2.setExpression(returnExpressionInCatch2);
			}
			CatchClause validationExceptionClause = ast.newCatchClause();
			SingleVariableDeclaration veFormalParameter = ast
					.newSingleVariableDeclaration();
			veFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
			veFormalParameter.setType(ast.newSimpleType(ast
					.newName(PluginConstants.VALIDATION_EXCEPTION_TYPE)));

			validationExceptionClause.setException(veFormalParameter);

			Block veClauseBody = ast.newBlock();
			veClauseBody.statements().add(returnStatementInCatch);
			validationExceptionClause.setBody(veClauseBody);

			CatchClause intrusionExceptionClause = ast.newCatchClause();
			SingleVariableDeclaration ieFormalParameter = ast
					.newSingleVariableDeclaration();
			ieFormalParameter.setName(ast.newSimpleName(PluginConstants.EXCEPTION_VARIABLE_NAME));
			ieFormalParameter.setType(ast.newSimpleType(ast
					.newName(PluginConstants.INTRUSION_EXCEPTION_TYPE)));

			intrusionExceptionClause.setException(ieFormalParameter);

			Block ieClauseBody = ast.newBlock();
			ieClauseBody.statements().add(returnStatementInCatch2);
			intrusionExceptionClause.setBody(ieClauseBody);
            if(hasGeneratedWhichException == 1){
			tryStatement.catchClauses().add(validationExceptionClause);
			tryStatement.catchClauses().add(intrusionExceptionClause);
            }else if(hasGeneratedWhichException == 2){
            	tryStatement.catchClauses().add(validationExceptionClause);
            }else if(hasGeneratedWhichException == 3){
            	tryStatement.catchClauses().add(intrusionExceptionClause);
            }else{
            	System.out.println("errors in generateTry in CodeGenerator.java");
            	return null;
            }
					
			for(Iterator i=methodToBeReplaced.getBody().statements().iterator();i.hasNext();){
			     fListRewrite.remove((ASTNode)i.next(), null);
			}
			
	//MM Second comment not required
			fListRewrite.insertAt(notice, 0, null);
			
			fListRewrite.insertAfter(tryStatement, notice, null);
		}/////end
		//}
        
        TextEdit textEdits = null;
		try {
			textEdits = fASTRewrite.rewriteAST();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		try {
			textEdits.apply(document, TextEdit.CREATE_UNDO
					| TextEdit.UPDATE_REGIONS);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		
		astRewriteAndTracking.setAstRewrite(null);
		astRewriteAndTracking.setFirstNodePosition(trackValidationReturnStatement);
		astRewriteAndTracking.setSecondNodePosition(trackIntrusionReturnStatement);
		return astRewriteAndTracking;
	}
	public ITrackedNodePosition generateSpecialOutputValidationRoutine(IDocument document,
			CompilationUnit astRoot, ImportRewrite fImportRewrite, AST ast,
			ASTNode node, String inputType) throws IllegalArgumentException, CoreException,
			MalformedTreeException, BadLocationException {
//bugs, what is location for?
		//MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		//MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);
		//methodToBeReplaced.getStructuralProperty(Block.STATEMENTS_PROPERTY);
	//////
		Statement statement = ASTResolving.findParentStatement(node);
		if (statement == null) {
			System.out.println("returned from within if (statement == null) in generateSpecialOutputValidationRoutine");
			return null;
		}

        ASTRewrite fASTRewrite = ASTRewrite.create(ast);

		ICompilationUnit fCompilationUnit = (ICompilationUnit)astRoot.getJavaElement();
		//////
		MethodInvocation esapiValidator = ast.newMethodInvocation();
		esapiValidator.setExpression(ast.newSimpleName(PluginConstants.ESAPI));
		esapiValidator.setName(ast.newSimpleName(PluginConstants.ESAPI_VALIDATOR));

		MethodInvocation esapiValidation = ast.newMethodInvocation();
		esapiValidation.setExpression(esapiValidator);
		esapiValidation.setName(ast
				.newSimpleName(PluginConstants.ESAPI_VALIDATOR_GETVALIDINPUT));

		List<ASTNode> arguments = esapiValidation.arguments();

		StringLiteral contextArg = ast.newStringLiteral();
		contextArg.setLiteralValue(PluginConstants.ESAPI_CONTEXT_PLACEHOLDER);
		arguments.add(0, contextArg);
		Expression tmpAssignmentLeft = null;
		//newly added, track the validation code, store its position
		ITrackedNodePosition replacementPositionTracking = fASTRewrite.track(esapiValidation); 
		///
		
		StringLiteral inputTypeArg = ast.newStringLiteral();
		inputTypeArg.setLiteralValue(inputType);
		// inputTypeArg.setLiteralValue(ESAPI_INPUT_TYPE);
		

		NumberLiteral lengthArg = ast.newNumberLiteral(PluginConstants.ESAPI_DEFAULT_LENGTH);
		

		BooleanLiteral allowNullArg = ast.newBooleanLiteral(false);
		LineComment notice = (LineComment) fASTRewrite.createStringPlaceholder(
				PluginConstants.ESAPI_COMMENT, ASTNode.LINE_COMMENT);
		
		MethodDeclaration methodToBeReplaced = ASTResolving.findParentMethodDeclaration(statement);

		ListRewrite fListRewrite = fASTRewrite.getListRewrite(
				methodToBeReplaced.getBody(),
				Block.STATEMENTS_PROPERTY);

	
		//ExpressionStatement esapiValidationStatement = ast.newExpressionStatement(esapiValidation);
		if(!(node instanceof Expression)){
			System.out.println("!(node instanceof Expression) in generateSpecialOutputValidationRoutine!");
			return null;
		}
		arguments.add(1, (Expression)fASTRewrite.createCopyTarget(node));
			//arguments.add(1, fASTRewrite.createCopyTarget(esapiValidator));
			arguments.add(2, inputTypeArg);
			arguments.add(3, lengthArg);
			arguments.add(4, allowNullArg);
			
			//System.out.println(esapiValidation+" "+ASIDEMarkerAndAnnotationUtil.getASIDE_Flag(fASTRewrite.createCopyTarget(node)));
			//fListRewrite.insertAfter( esapiValidationStatement,statement, null);
			
		fASTRewrite.replace(node, esapiValidation, null);
		//fListRewrite.insertBefore(notice, statement, null);
		//here to paste tryStatement code
		//currently, only deals with methodInvocation case
	
		//	fASTRewrite.replace(node, ASIDEMarkerAndAnnotationUtil.createNodeCopyAndAnnotation(fCompilationUnit, fASTRewrite, node), null);
		TextEdit importEdits = null, textEdits = null;
		textEdits = fASTRewrite.rewriteAST();
		importEdits = fImportRewrite.rewriteImports(null);
		textEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		importEdits.apply(document, TextEdit.CREATE_UNDO
				| TextEdit.UPDATE_REGIONS);
		
		return replacementPositionTracking;
	}


	
//some methods, such as geAttachedAnnotation have been moved to ASIDEMarkerAndAnnotationUtil
	
}