package edu.uncc.aside.codeannotate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
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
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.utils.InterfaceUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;
import edu.uncc.aside.utils.MarkerAndAnnotationUtil;

/*
 * Very useful utility class, currently copied from LapsePlus
 */
public class Utils {
	/**
	 * @return
	 */
	public static Set<IProject> getActiveProjects() {
		
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		Set<IProject> activeProjects= new HashSet<IProject>();
		
		for (IProject p : allProjects){
			if(p.getName().equalsIgnoreCase("RemoteSystemsTempFiles") || 
	    			p.getName().equalsIgnoreCase("Servers")  
	    			|| p.getName().equalsIgnoreCase("Server") ){
	    		continue;
	    	}
		    if(p.isOpen())
			        activeProjects.add(p);
     	}
		return activeProjects;
	}
	
/*	
	public static class RetrievalImplementation 
	{
	  int n;
	  boolean valueSet=false ;

	   static void readFromCSVFile()
	  {
	    
	    String csvFile = "AnnotationCSV.csv";
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		
		
			String fileName="AccountsServlet.java";
			IResource theResource = null;
		//	MakerManagement.setAnnotationFromCSVFile( Plugin.ANNOTATION_QUESTION,"AccountServlet.java", 1000,20);
			for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects() ){
				if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
				{

					IJavaProject javaProject = JavaCore.create(p);
					if(javaProject == null)
						continue;
					try{
						IPackageFragment[] fragments = javaProject
								.getPackageFragments();

						for (IPackageFragment fragment : fragments) {
							//	ICompilationUnit[] units = fragment.getCompilationUnits();
							for (ICompilationUnit unit : fragment.getCompilationUnits()) {
								if (unit.getElementName().equals(fileName))
								{
									 theResource = unit.getResource();
									break;
								}
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			InterfaceUtil.createMarker(Plugin.ANNOTATION_QUESTION, 1500, 1520, theResource,"");
			
			try {
					
				for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects() ){
					if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
					{
						
						br = new BufferedReader(new FileReader(p.getLocation().makeAbsolute() + "/"+ csvFile));
						while ((line = br.readLine()) != null) {

					        // use comma as separator
						String[] annotation = line.split(csvSplitBy);

						System.out.println("Annotation [annotationMarkerName= " + annotation[0] 
								 				+ " , fileName=" + annotation[1]
								 				+ " , markerStart=" + annotation[2]
								 				+ " , highlightingLength =" + annotation[3]
								 				+ " , annotatedText=" + annotation[4]
								 			//	+ " , randomId=" + annotation[5] + "]"
								 				);
						
						
						IJavaProject javaProject = JavaCore.create(p);
						if(javaProject == null)
							continue;
						try{
							IPackageFragment[] fragments = javaProject
									.getPackageFragments();

							for (IPackageFragment fragment : fragments) {
								//	ICompilationUnit[] units = fragment.getCompilationUnits();
								for (ICompilationUnit unit : fragment.getCompilationUnits()) {
									if (unit.getElementName().equals(annotation[1]))
									{
										 theResource = unit.getResource();
										break;
									}
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						int Start = Integer.parseInt(annotation[2].trim());
						int End= Start +  Integer.parseInt(annotation[3].trim());
						
						InterfaceUtil.createMarker(annotation[0], Start,End, theResource,"");
					}
						
					
				}
				
				//setAnnotationFromCSVFile(annotation[0],annotation[1],annotation[2],annotation[3],annotation[4],annotation[5]);
			//	MakerManagement.setAnnotationFromCSVFile( annotation[0],annotation[1],annotation[2],annotation[3]);
			}

		
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			

		System.out.println("Done");
		
		//valueSet = false;
	  //  notify();
	  }

	 
	}
*/
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

		ASTParser parser = ASTParser.newParser(AST.JLS8);
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

	public static void markSensitiveOperation(MethodInvocation mi, IResource resource,
			CompilationUnit cu) {
		int char_start, length;
		try {
			// First, gotta check whether there is a marker for the method
			// invocation
			IMarker[] markers = resource.findMarkers(
					PluginConstants.MARKER_ROOT_TYPE, true, IResource.DEPTH_INFINITE);
			
			
			for (IMarker marker : markers) {
				
				char_start = marker.getAttribute(IMarker.CHAR_START, -1);
				
				length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;
				// Second, if there is one, then move on; if not, create one.
				
				if (char_start == mi.getStartPosition()
						&& length == mi.getLength())
				{
					if(marker.getType().equalsIgnoreCase(PluginConstants.MARKER_ANNOTATION_REQUEST) ||
							marker.getType().equalsIgnoreCase(PluginConstants.MARKER_ANNOTATION_CHECKED)||
							marker.getType().equalsIgnoreCase(PluginConstants.MARKER_GREEN_CHECK_BOX))
						return;
				}
					
			}
			Map<String, Object> markerAttributes = new HashMap<String, Object>();	
			markerAttributes.put(IMarker.CHAR_START,mi.getStartPosition() );
			markerAttributes.put(IMarker.CHAR_END, mi.getStartPosition()+ mi.getLength() );
			markerAttributes.put(IMarker.LINE_NUMBER,	mi.getStartPosition());
			markerAttributes.put(IMarker.MESSAGE,"Where is the corresponding authentication process?");
			markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			
			IMarker questionMarker = MarkerAndAnnotationUtil
					.addMarker(resource,
							markerAttributes, PluginConstants.MARKER_ANNOTATION_REQUEST);

			
			
			/*
			IMarker questionMarker= InterfaceUtil.createMarker(
					resource
					,PluginConstants.ANNOTATION_QUESTION
					,mi.getStartPosition() 
					,mi.getStartPosition()+ mi.getLength() 
					,cu.getLineNumber(mi.getStartPosition())
					,IMarker.SEVERITY_WARNING
					,IMarker.PRIORITY_HIGH
					,"Where is the corresponding authentication process?", "0", "0"										
					);
					*/
			
			/*
			IMarker questionMarker = resource
					.createMarker(Plugin.ANNOTATION_QUESTION);

			questionMarker.setAttribute(IMarker.CHAR_START,
					mi.getStartPosition());
			questionMarker.setAttribute(IMarker.CHAR_END, mi.getStartPosition()
					+ mi.getLength());
			questionMarker.setAttribute(IMarker.MESSAGE,
					);
			
			questionMarker.setAttribute(IMarker.LINE_NUMBER,
					cu.getLineNumber(mi.getStartPosition()));
			
			questionMarker.setAttribute(IMarker.SEVERITY,
					IMarker.SEVERITY_WARNING);
			questionMarker
					.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			*/
			if( questionMarker != null)
				InterfaceUtil.prepareAnnotationRequest(questionMarker, resource);

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static void createQuestionMarker(ASTNode node, IResource resource,
			CompilationUnit unit) throws CoreException {
		String message = "What is the corresponding authentication?";

		Map<String, Object> markerAttributes = new HashMap<String, Object>();	
		markerAttributes.put(IMarker.CHAR_START,node.getStartPosition() );
		markerAttributes.put(IMarker.CHAR_END,node.getStartPosition() + node.getLength() );
		markerAttributes.put(IMarker.LINE_NUMBER,	unit.getLineNumber(node.getStartPosition()));
		markerAttributes.put(IMarker.MESSAGE,message);
		markerAttributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		markerAttributes.put(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		
		IMarker questionCheckedMarker = MarkerAndAnnotationUtil
				.addMarker(resource,
						markerAttributes, PluginConstants.MARKER_ANNOTATION_REQUEST);

		/*
		IMarker questionCheckedMarker = resource
				.createMarker(PluginConstants.ANNOTATION_QUESTION);

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
				*/
		InterfaceUtil.prepareAnnotationRequest(questionCheckedMarker, resource);
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
	

	public static ICompilationUnit getCompilationUnitOf(IResource resource) {
		IFile file = (IFile) resource.getAdapter(IFile.class);
		return (ICompilationUnit) JavaCore.create(file);
	}

	public static void removeMarkersOnPath(Path path) {
		if(path == null)
			return;
		AccessControlPoint sesnsitiveSink = path.getSensitiveOperation();
		
		removeAnnotationMarkerOfPoint(sesnsitiveSink);
		
		List<AccessControlPoint> checks = path.getChecks();
		
		for (AccessControlPoint check : checks) {
			removeAnnotationMarkerOfPoint(check);
		}

	}

	public static void removeAnnotationMarkerOfPoint(AccessControlPoint point) {
		ASTNode node = point.getNode();
		IResource resource = point.getResource();

		try {
			int char_start, length;

			IMarker[] allMarkers = resource.findMarkers(
					PluginConstants.MARKER_ROOT_TYPE, true, IResource.DEPTH_INFINITE);
			
			for (IMarker marker : allMarkers) {

				if( marker.getType().equals(PluginConstants.MARKER_ANNOTATION_REQUEST)
						|| marker.getType().equals(PluginConstants.MARKER_ANNOTATION_CHECKED)
						|| marker.getType().equals(PluginConstants.MARKER_ANNOTATION_ANSWER)
						)
				{
					char_start = marker.getAttribute(IMarker.CHAR_START, -1);
					length = marker.getAttribute(IMarker.CHAR_END, -1) - char_start;

					if (char_start == node.getStartPosition()
							&& length == node.getLength()) {
						
						InterfaceUtil.removeMarker(marker);
					}
				}

			}
			
			/*
			IMarker[] questionMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_REQUEST, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_CHECKED, false,
					IResource.DEPTH_ONE);
			IMarker[] answerMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_ANSWER, false, IResource.DEPTH_ONE);

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
			*/

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	public static boolean isAssociatedWithMarker(ASTNode node,
			IResource resource) {

		try {
			int char_start, length;

			IMarker[] questionMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_REQUEST, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_CHECKED, false,
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
					PluginConstants.MARKER_ANNOTATION_REQUEST, false, IResource.DEPTH_ONE);
			IMarker[] checkedMarkers = resource.findMarkers(
					PluginConstants.MARKER_ANNOTATION_CHECKED, false,
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
