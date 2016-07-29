package edu.uncc.sis.aside.visitors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.uncc.aside.ast.ASTResolving;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.models.InputValidationPoint;
//import edu.uncc.aside.codeannotate.models.InputValidationsCollector;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.aside.utils.Converter;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.domainmodels.TrustBoundaryRepository;
import edu.uncc.sis.aside.preferences.PreferencesSet;

import org.apache.commons.lang3.*;
/**
 * 
 * @author Jun Zhu (jzhu16 at uncc dot edu) Jing Xie (jxie2 at uncc dot edu) <a href="http://www.uncc.edu/">UNC
 *         Charlotte</a>
 * 
 */
public class MethodInvocationVisitor extends ASTVisitor {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			MethodInvocationVisitor.class.getName());

	private ASTMatcher astMatcher;

	private MethodDeclaration acceptor;

	private ArrayList<IMarker> markersList;

	private IProject project;

	private ICompilationUnit cu;

	private CompilationUnit astRoot;

	private PreferencesSet prefSet;
	
	//MM
	private ModelCollector modelCollector;

	// Expression: ArrayAccess && MethodInvocation
	private ArrayList<Expression> taintedListSources = new ArrayList<Expression>();

	// Expression: SimpleName && MethodInvocation
	private ArrayList<Expression> taintedMapSources = new ArrayList<Expression>();
 
	/**
	 * Constructor
	 * 
	 * @param prefSet
	 */
	public MethodInvocationVisitor(MethodDeclaration node,
			ArrayList<IMarker> existingAsideMarkers, ICompilationUnit cu,
			PreferencesSet prefSet) {
		super();
		acceptor = node;
		this.cu = cu;

		this.prefSet = prefSet;

		if (existingAsideMarkers != null && !existingAsideMarkers.isEmpty()) {
			MarkerAndAnnotationUtil
					.clearStaleMarkers(existingAsideMarkers);
		}

		if (markersList == null) {
			markersList = new ArrayList<IMarker>();
		}

		astRoot = ASTResolving.findParentCompilationUnit(node);

		IJavaProject javaProject = cu.getJavaProject();
		if (javaProject != null) {
			project = (IProject) javaProject.getAdapter(IProject.class);
		}

		astMatcher = Plugin.getDefault().getASTMatcher();
		
		this.modelCollector = ModelRegistry
				.getPathCollectorForProject(project);

		if (modelCollector == null) {
			modelCollector = new ModelCollector(project);
		}
	}

	public ArrayList<IMarker> process() {
		if (acceptor != null)
			acceptor.accept(this);
		
		return markersList;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		
		IMethodBinding methodBinding = node.resolveMethodBinding();
		
		

	//	System.out.println("MM visit(MethodInvocation");
		if (methodBinding == null) {
			return false;
		}
        
		ITypeBinding returnTypeBinding = methodBinding.getReturnType();

		/**
		 * untrusted input flows into a program via the return value of a method
		 * invocation.
		 */
		if (returnTypeBinding == null) {
			return false;
		}
		String qualifiedName = returnTypeBinding.getQualifiedName();
		boolean isMethodInvocation = true;
		
		//System.out.println("MM TrustBoundaryRepository 1");
		
		// To check whether the method is defined in the trust boundary list
		boolean tainted_as_defined_by_trust_boundary = TrustBoundaryRepository
				.getHandler(project, prefSet).isExist(methodBinding,
						isMethodInvocation, qualifiedName);
		
		//System.out.println("MM TrustBoundaryRepository 2");
		String[] attrTypes = TrustBoundaryRepository.getHandler(project,
				prefSet).getAttrTypes();
		
		//System.out.println("MM TrustBoundaryRepository 3");
		 String ruleNameBelongTo =  TrustBoundaryRepository.getHandler(project,
					prefSet).getRuleNameBelongTo();
		 
		// System.out.println("MM TrustBoundaryRepository 4 ");
		 String typeNameBelongTo =  TrustBoundaryRepository.getHandler(project,
					prefSet).getTypeNameBelongTo(); 
		 
		// System.out.println("MM findParentStatement");
		Statement parentStatement = ASTResolving.findParentStatement(node);

		/* the marker to be created and attached to the corresponding node */
		IMarker marker = null;

		Map<String, Object> markerAttributes = new HashMap<String, Object>();

		int lineNumber = astRoot.getLineNumber(node.getStartPosition());
       
		/*
		 * aside marker attributes which will be used for the corresponding
		 * marker resolutions
		 */
			
		markerAttributes.put(IMarker.LINE_NUMBER, lineNumber);		
		markerAttributes.put("edu.uncc.sis.aside.marker.flow",	Converter.arrayToString(attrTypes));
	    //the trustboundary rule that the methodInvocation belongs to, 
		
		markerAttributes.put("edu.uncc.sis.aside.marker.typeNameBelongTo", typeNameBelongTo);
		markerAttributes.put("edu.uncc.sis.aside.marker.ruleNameBelongTo", ruleNameBelongTo);

		
		
		//newly added Feb. 24
		MethodDeclaration parentMethodDeclaration = ASTResolving.findParentMethodDeclaration(node);
		
		String returnTypeStr = MarkerAndAnnotationUtil.getReturnTypeStr(parentMethodDeclaration);
				
		markerAttributes.put("edu.uncc.sis.aside.marker.returnTypeOfMethodDeclarationBelongTo"
				, returnTypeStr);
		

		//System.out.println("MM Before tainted_as_defined_by_trust_boundary" );
		
		if (tainted_as_defined_by_trust_boundary) {
			
		//	System.out.println("MM After tainted_as_defined_by_trust_boundary" );
			/*
			 * The method invocation under examination matches one of the trust
			 * boundary, next, check the location of this method invocation
			 */
			//should delete this if 
//			if (ASIDEMarkerAndAnnotationUtil
//					.isControlConditionStatementWithBlock(parentStatement))
//				return false;
			//should delete this if 
//			if (ASIDEMarkerAndAnnotationUtil
//					.isControlConditionStatementWithBlock(parentStatement
//							.getParent()))
//				return false;
			//special case deals with the case that the untrusted input is an 
			//argument of the esapi validation method invocation
			
			//MM It is needed to make a Point Object here.
			
			if (MarkerAndAnnotationUtil.specialCase(node))
				return false;
			//should delete this if 
//			if (ASIDEMarkerAndAnnotationUtil.isConvertedToNumericType(node))
//				return false;
			//only use annotation to judge
			
			if (MarkerAndAnnotationUtil.hasAnnotationAtPosition(cu, node))
				{
				//System.out.println("ASIDEMarkerAndAnnotationUtil.hasAnnotationAtPosition in methodinvocation");
				return false;
				}
//			System.out.println("in MethodInvocation "+ASIDEMarkerAndAnnotationUtil.getASIDE_Flag(node));
//			if(ASIDEMarkerAndAnnotationUtil.hasASIDE_FlagAtNode(node))
//				return false;
			
			//should deleted this if
//			if (ASIDEMarkerAndAnnotationUtil
//					.hasASIDEGeneratedValidationRoutine(cu, node,
//							parentStatement))
//				return false;
            
			/**
			 * If there are more cases where ASIDE should not report the node as
			 * introducing untrusted input in to the program.
			 */

			if (qualifiedName.equals("java.lang.String")) {
				
				if(!MarkerAndAnnotationUtil.isDirectlyEmbracedByValidationMethodInvocation(node))
				{
				int start = node.getStartPosition();
				int length = node.getLength();
				int end = start + length;

				/*
				 * before creating a marker for the node, check and see whether
				 * there is a residue marker at the position. If yes, delete it.
				 */

				deleteResidueMarkerForNode(node);

				markerAttributes.put(IMarker.CHAR_START, start);
				markerAttributes.put(IMarker.CHAR_END, end);
				markerAttributes.put(IMarker.MESSAGE, PluginConstants.TOOL_TIP);
				markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				
				markerAttributes.put("edu.uncc.sis.aside.marker.validationType", "String");
				
				//Adding Input Validation Marker
				marker = MarkerAndAnnotationUtil.addMarker(astRoot,
						markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

				
				// here we have to update the ASIDE view as well. 
				
				InputValidationPoint point= new InputValidationPoint
						((ASTNode)node,this.astRoot,this.cu.getResource());
				
				modelCollector.addInputValidation(point);
				
			//	marker.getResource()
				
			//	System.out.println("MM Before Add Marker");
				
				markersList.add(marker);
				  
			    //get current date time with Date()
				addInputValidationLog(node, lineNumber);
			}
			}

			if (qualifiedName.equals("int")) {
				if(!MarkerAndAnnotationUtil.isDirectlyEmbracedByValidationMethodInvocation(node))
				{
				/**
				 * integers are treated differently as strings since most
				 * strings require only syntactic checking while integers
				 * involve semantic checking, for instance, the upper and lower
				 * bound of the integer.
				 * 
				 * However, an external validation API may provide such checking
				 * against a string. For example,
				 * 
				 * ESAPI.validator.getValidInteger("context", "input", maxValue,
				 * minValue, allowNull);
				 * 
				 * Thus, ASIDE needs re-design to better cope with the above
				 * situation. But for the study, since we do not provide any
				 * default trust boundary rules that have return values of
				 * integer type, this if block will not be used at all.
				 */
				int start = node.getStartPosition();
				int length = node.getLength();
				int end = start + length;

				// TODO: deleteResidueMarkerForNode(node)

				markerAttributes.put(IMarker.CHAR_START, start);
				markerAttributes.put(IMarker.CHAR_END, end);
				markerAttributes
						.put(IMarker.MESSAGE,
								PluginConstants.TOOL_TIP);
				markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				markerAttributes.put(
						"edu.uncc.sis.aside.marker.validationType", "int");
				
				
				
				
				InputValidationPoint point= new InputValidationPoint
						((ASTNode)node,this.astRoot,this.cu.getResource());
				
				modelCollector.addInputValidation(point);
				
				byte[] data =SerializationUtils.serialize(point);
				
				markerAttributes.put("model.point", data);
				
				marker = MarkerAndAnnotationUtil.addMarker(astRoot,
						markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);
				
				
				
				markersList.add(marker);
				  
			    addInputValidationLog(node, lineNumber);

			}
			}

			if (qualifiedName.equals("void")) {
				/*
				 * This node is being examined for output encoding need
				 * 
				 * The specified argument node should be of a java.lang.String
				 * type. If the node is of InfixExpress, do further analysis to
				 * identify the variable portion
				 */

				int[] arguments = TrustBoundaryRepository.getHandler(project,
						prefSet).getArguments();
				ArrayList<IMarker> argumentMarkerList = new ArrayList<IMarker>();
				List<Expression> argumentNodeList = node.arguments();
                //here 
				for (int index : arguments) {
					Expression argumentNode = argumentNodeList.get(index);

					/*
					 * check the node type for: InfixExpression, SimpleName,
					 * MethodInvocation; although there are more possibilities,
					 * for demo purpose, we consider only these three types.
					 * Correspondingly, when it comes to fix the issue by
					 * generating code for the source of untrusted input, we
					 * need to consider these three types of node
					 */

					//					int nodeType = argumentNode.getNodeType();
					//
					//					switch (nodeType) {
					//
					//					case ASTNode.SIMPLE_NAME:
					int argCharStart = argumentNode.getStartPosition();

					int argLength = argumentNode.getLength();

					/*
					 * Check the validity of each argument node to eliminate
					 * false positives, such as String constant, integer
					 * constant, boolean constant, etc
					 */
					//
					//						if (!ASIDEMarkerAndAnnotationUtil.hasAnnotationAtPosition(
					//								cu, argumentNode) && !ASIDEMarkerAndAnnotationUtil.isFalsePositive(
					//										argumentNode, argCharStart, argLength, cu))

					if (!MarkerAndAnnotationUtil.hasAnnotationAtPosition(
							cu, argumentNode) )
						//						if (!ASIDEMarkerAndAnnotationUtil.hasASIDE_FlagAtNode(
						//								argumentNode) )

					{
						/*
						 * before create a Marker for a particular node, remember to
						 * delete any residue marker associated with this node
						 */
						//newly added, determine whether the argument node output is encoding method invocation
						//if yes, it means the argument has been encoded
						if(!MarkerAndAnnotationUtil.isEncodingOrValidationMethodInvocation(argumentNodeList.get(index)))
						{
							if(!MarkerAndAnnotationUtil.isConstant(argumentNode)){

								deleteResidueMarkerForNode(argumentNode);

								markerAttributes.put(IMarker.CHAR_START, argCharStart);
								markerAttributes.put(IMarker.CHAR_END, argCharStart
										+ argLength);
								markerAttributes.put(IMarker.MESSAGE,	PluginConstants.TOOL_TIP);

								markerAttributes.put(
										"edu.uncc.sis.aside.marker.targetPosition", index);
								markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
								markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
								
								marker = MarkerAndAnnotationUtil.addMarker(astRoot,
										markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

								argumentMarkerList.add(marker);
							}
						}

					}	


				}


				if (!argumentMarkerList.isEmpty()) {
					
					markersList.addAll(argumentMarkerList);
					
					addInputValidationLog(node, lineNumber);
					
				}
			}
            //newly added for only warning case
			if (qualifiedName.equals("java.sql.ResultSet")) {
				if(!MarkerAndAnnotationUtil.isDirectlyEmbracedByValidationMethodInvocation(node))
				{
					int start = node.getStartPosition();
					int length = node.getLength();
					int end = start + length;

					/*
					 * before creating a marker for the node, check and see whether
					 * there is a residue marker at the position. If yes, delete it.
					 */

					deleteResidueMarkerForNode(node);

					markerAttributes.put(IMarker.CHAR_START, start);
					markerAttributes.put(IMarker.CHAR_END, end);
					markerAttributes.put(IMarker.MESSAGE,
							PluginConstants.TOOL_TIP);

					markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					
					
					markerAttributes.put(
							"edu.uncc.sis.aside.marker.validationType", "ResultSet");

					marker = MarkerAndAnnotationUtil.addMarker(astRoot,
							markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

				
					markersList.add(marker);

					addInputValidationLog(node, lineNumber);
				}
			}
			if (qualifiedName.equals("boolean")) {
				if(!MarkerAndAnnotationUtil.isDirectlyEmbracedByValidationMethodInvocation(node))
				{
					int start = node.getStartPosition();
					int length = node.getLength();
					int end = start + length;

					/*
					 * before creating a marker for the node, check and see whether
					 * there is a residue marker at the position. If yes, delete it.
					 */

					deleteResidueMarkerForNode(node);

					markerAttributes.put(IMarker.CHAR_START, start);
					markerAttributes.put(IMarker.CHAR_END, end);
					markerAttributes
					.put(IMarker.MESSAGE,
							PluginConstants.TOOL_TIP);

					markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					
					
					markerAttributes.put(
							"edu.uncc.sis.aside.marker.validationType", "boolean");
					marker = MarkerAndAnnotationUtil.addMarker(astRoot,
							markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

					
					markersList.add(marker);


					addInputValidationLog(node, lineNumber);
				}
			}

			
			
			/*
			 * The method invocation returns a Map instance that contains
			 * untrusted data.
			 */
			if (PluginConstants.JAVA_MAP_TYPES.contains(qualifiedName)) {
				taintedMapSources.add(node);
				StructuralPropertyDescriptor location = node
						.getLocationInParent();
				
				if (location.isChildProperty()) {
					ASTNode parent = node.getParent();
					
					if (parent.getNodeType() == ASTNode.ASSIGNMENT) {
						
						if (node.getLocationInParent() == Assignment.RIGHT_HAND_SIDE_PROPERTY) {
							
							Assignment assignmentParent = (Assignment) parent;
							Expression assignmentExpression = assignmentParent
									.getLeftHandSide();
							
							taintedMapSources.add(assignmentExpression);
						}
					}

					if (parent.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
						
						if (node.getLocationInParent() == VariableDeclarationFragment.INITIALIZER_PROPERTY) {
							
							VariableDeclarationFragment variableDeclarationParent = (VariableDeclarationFragment) parent;
							SimpleName variable = variableDeclarationParent
									.getName();
							
							taintedMapSources.add(variable);
						}
					}

				}
			}

			/*
			 * The method invocation returns a List instance that contains
			 * untrusted data.
			 */
			if (PluginConstants.JAVA_LIST_TYPES.contains(qualifiedName)) {
				taintedListSources.add(node);
				StructuralPropertyDescriptor location = node
						.getLocationInParent();
				
				if (location.isChildProperty()) {
					
					ASTNode parent = node.getParent();
					
					if (parent.getNodeType() == ASTNode.ASSIGNMENT) {
						
						if (node.getLocationInParent() == Assignment.RIGHT_HAND_SIDE_PROPERTY) {
							Assignment assignmentParent = (Assignment) parent;
							Expression assignmentExpression = assignmentParent
									.getLeftHandSide();
							
							taintedListSources.add(assignmentExpression);
						}
					}

					if (parent.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
					
						if (node.getLocationInParent() == VariableDeclarationFragment.INITIALIZER_PROPERTY) {
							
							VariableDeclarationFragment variableDeclarationParent = (VariableDeclarationFragment) parent;
							SimpleName variable = variableDeclarationParent
									.getName();
							taintedListSources.add(variable);
						}
					}

				}
			}

		} else if (!tainted_as_defined_by_trust_boundary) {
//else if (!tainted_as_defined_by_trust_boundary){} loop is used to process return types map list, the control
			//expression else if is only used to eliminate some work of compilers, for performance sake, no real
			//control logic aim
			/*
			 * TODO: I have not finished examining this since my summer study does
			 * not require this functionality
			 */
			if (taintedMapSources.isEmpty()) {
				return true;
			}
            //should delete this if 
//			if (ASIDEMarkerAndAnnotationUtil
//					.isControlConditionStatementWithBlock(parentStatement))
//				return false;
			//should delete this if
//			if (ASIDEMarkerAndAnnotationUtil
//					.isControlConditionStatementWithBlock(parentStatement
//							.getParent()))
//				return false;

			if (MarkerAndAnnotationUtil.specialCase(node))
				return false;

			int start = node.getStartPosition();
			int length = node.getLength();
			int end = start + length;

			if (!MarkerAndAnnotationUtil.hasAnnotationAtPosition(cu, node)) {
			//if (!ASIDEMarkerAndAnnotationUtil.hasASIDE_FlagAtNode(node)) {
				if (!qualifiedName.equals("int")) {
					Expression methodExpression = node.getExpression();
					SimpleName methodName = node.getName();
					// Object value = Map.get("key");
					
					if (methodExpression != null
							&& methodName.getIdentifier() != null
							&& methodName.getIdentifier().equals("get")) {
						
						int invokeExpressionType = methodExpression
								.getNodeType();
						
						switch (invokeExpressionType) {
						case ASTNode.METHOD_INVOCATION:

							MethodInvocation target = (MethodInvocation) methodExpression;

							if (MarkerAndAnnotationUtil.isTainted(target,
									taintedMapSources, astMatcher)) {

								if (!MarkerAndAnnotationUtil
										.specialCase(target)) {

									markerAttributes.put(IMarker.CHAR_START,
											start);
									markerAttributes.put(IMarker.CHAR_END, end);
									// TODO: make message more concrete and
									// meaningful
									markerAttributes
									.put(IMarker.MESSAGE,
											PluginConstants.TOOL_TIP);

									markerAttributes
									.put("edu.uncc.sis.aside.marker.validationType",
											"String");
									markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
									markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
									
									
									marker = MarkerAndAnnotationUtil
											.addMarker(astRoot,
													markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

									
									markersList.add(marker);

									addInputValidationLog(node, lineNumber);

								}
							}
							break;
						case ASTNode.SIMPLE_NAME:
							if (MarkerAndAnnotationUtil.isTainted(
									(SimpleName) methodExpression,
									taintedMapSources)) {

								markerAttributes.put(IMarker.CHAR_START, start);
								markerAttributes.put(IMarker.CHAR_END, end);
								markerAttributes
								.put(IMarker.MESSAGE,
										PluginConstants.TOOL_TIP);

								markerAttributes
								.put("edu.uncc.sis.aside.marker.validationType",
										"String");
								markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
								markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
								
								
								marker = MarkerAndAnnotationUtil
										.addMarker(astRoot, markerAttributes, PluginConstants.MARKER_INPUT_VALIDATION);

								markersList.add(marker);

								addInputValidationLog(node, lineNumber);

							}
							break;
						default:
							break;
						}
					}

				}

				if (qualifiedName.equals("int")) {
					// TODO
				}
			}

			return true;
		}

		// record the location of this method invocation in its ancestor
		return false;
	}

	private void addInputValidationLog(MethodInvocation node,
			int lineNumber) 
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " found an input validation vulnerability at line " + lineNumber
				+ ".<code>" + node.toString() + "<code> in Java File <<"
				+ astRoot.getJavaElement().getElementName()
				+ ">> in Project [" + project.getName() + "]");
		
	
	}

	/*
	 * check and see whether there is a residue marker at the position. If yes,
	 * delete it.
	 */
	private void deleteResidueMarkerForNode(ASTNode node) {
		
		int start = node.getStartPosition();
		int length = node.getLength();
		int end = start + length;
		int linenumber = astRoot.getLineNumber(start);

		try {
			IFile file = (IFile) cu.getUnderlyingResource();
			
			IMarker[] markers = file.findMarkers(
					PluginConstants.MARKER_INPUT_VALIDATION, false,
					IResource.DEPTH_ONE);

			for (IMarker marker : markers) {

				int mstart = marker.getAttribute(IMarker.CHAR_START, -1);
				int mend = marker.getAttribute(IMarker.CHAR_END, -1);
				int mlinenumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);

				if (mstart == start && mend == end && mlinenumber == linenumber) {
					marker.delete();
				}

			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void endVisit(MethodInvocation node) {

		super.endVisit(node);
	}

	public ArrayList<Expression> getTaintedListSources() {
		return taintedListSources;
	}

}