package edu.uncc.aside.codeannotate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.ArrayDeque;



import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

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
public class PathFinder {

	private static Map<IProject, PathFinder> projectFinderMap = null;

	private static IProject selectProject;
	private IJavaProject javaProject;
	private PathCollector pathCollector;	
	private List<Path> paths;
	
	private ArrayDeque<String> pathStack ;
	
	

	private PathFinder() {
	}

	public static PathFinder getInstance(IProject project) {
		PathFinder projectFinder = null;
		if (projectFinderMap == null) {
			projectFinderMap = Collections
					.synchronizedMap(new HashMap<IProject, PathFinder>());
		} else {
			projectFinder = projectFinderMap.get(project);
		}

		if (projectFinder == null) {
			projectFinder = new PathFinder();

			projectFinderMap.put(project, projectFinder);
		}
		selectProject = project;
		return projectFinder;

	}

	public void run(IProgressMonitor monitor) {
		Collection<SinkDescription> accessors = XMLConfig
				.readSinks(Plugin.SENSITIVE_ACCESSORS_CONFIG);

		
			monitor.beginTask(
					"Finding paths...",
					accessors.size());
			pathCollector = ModelRegistry
					.getPathCollectorForProject(selectProject);

			if (pathCollector == null) {
				pathCollector = new PathCollector(selectProject);
			}

			paths = pathCollector.getAllPaths();

			if (paths == null)
				paths = Collections.synchronizedList(new ArrayList<Path>());

			// TODO: This, right now, mimics the implementation of LapsePlus in
			// order to have a quick prototype to test the design idea
			javaProject = JavaCore.create(selectProject);

			int j=0;
			
			pathStack = new ArrayDeque<String>();
			Plugin.callGraph = new  ArrayList<Object>();
			
			for (SinkDescription accessor : accessors) {
				String accessor_id = accessor.getID();
				
				
				pathStack.addFirst(accessor_id);
				
				System.err.println("\n========================== New Accessor :(" + ++j + ") " + accessor_id);
				Collection<?> callers = CallerFinder.findCallers(monitor,
						accessor_id, javaProject, false);
				
			//	System.err.println(" number of callers = " + callers.size() );
				int i= 0;

				for (Object caller : callers) {
			//		System.err.println("--------Start of Process of  Caller # " + ++i + "  {");
					
					process(caller, i+ "" );
					
					
			//		System.err.println("------------End of Process of new Caller # " + i + "  }");
					if(! pathStack.isEmpty())
						
						pathStack.removeFirst();
				}
				
				if(! pathStack.isEmpty())
					pathStack.removeFirst();
				

				if (monitor.isCanceled()) {
					return;
				}
			}
		 
		projectFinderMap.put(selectProject, this);
	}

	private void process(Object caller, String pc) {
		
				
		int indent= pc.split("-").length * 20;
		
		 String ind = new String(new char[indent]).replace('\0', ' ');

	//	System.err.println(ind + "MM.." + pc);
		
		if (caller instanceof Utils.ExprUnitResourceMember) {

			Utils.ExprUnitResourceMember pair = (Utils.ExprUnitResourceMember) caller;
			IResource resource = ((Utils.ExprUnitResourceMember) caller)
					.getResource();

			Expression expr = pair.getExpression();

			if (expr.getNodeType() != ASTNode.METHOD_INVOCATION) {
				System.err.println("expression is not a method invocation");
				return;
			}
			
			MethodInvocation mi = (MethodInvocation) expr;
			MethodDeclaration enclosingMethodDeclaration = Utils
					.getParentMethodDeclaration(expr);
			
			IMethodBinding methodBinding = enclosingMethodDeclaration
					.resolveBinding();
			
			if (methodBinding == null) {
				System.err.println("bindings cannot be resolved");
				return;
			}
			ITypeBinding typeBinding = methodBinding.getDeclaringClass();
			String qualifiedName = Binding2JavaModel
					.getFullyQualifiedName(typeBinding);
			
			IMethod method = Utils.convertMethodDecl2IMethod(
					enclosingMethodDeclaration, resource);
			
			IResource mResource;
			try {
				mResource = method.getUnderlyingResource();
				if (mResource == null) {
					System.err.println("Check here...");
					return;
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
				return;
			}

			String new_accessor_id = qualifiedName + "."
					+ method.getElementName();
			
			/* Mahmoud */
		//	System.err.println(ind +" Called By --> " + new_accessor_id) ;
			
			Point tmp = new Point(pair.getExpression(),
					pair.getCompilationUnit(), resource);
			
			String callPoint = tmp.getResource().getName()+"( Line # "+ 
					tmp.getUnit().getLineNumber(tmp.getNode().getStartPosition() )+ " )"  ;
			
		//	System.out.println(ind +"Calling Point :" + callPoint +  " = "+ tmp.getNode().toString());
		//pathStack.addFirst(new_accessor_id ); //+ " @ " + callPoint);
			
			pathStack.addFirst(tmp.getNode().toString());

			/*
			 * if this method is an entrance method such as doGet/doPost in Java
			 * Servlet, the statement that has Expr should be marked
			 */
			if (Utils.isEntranceMethod(method.getElementName())) {
				
				Utils.markAccessor(mi, resource, pair.getCompilationUnit());
				/*
				 * build a path from the entrance method to caller, but first,
				 * we need to check to see whether an existing path instance of
				 * such exists, so now, the question is what data structure to
				 * be used to manage all the existing paths.
				 */
			//	System.out.println(ind +"--->  Entrance Found:");
				
				Point accessor = new Point(pair.getExpression(),
						pair.getCompilationUnit(), resource);
				Point entrance = new Point(enclosingMethodDeclaration,
						Utils.getCompilationUnit(method.getCompilationUnit()),
						mResource);
				Path path = hasPath(entrance, accessor);
				
				
				if (path == null) {
					
					path = new Path(entrance, accessor, null);
					callPoint = accessor.getResource().getName()+"( Line # "+ 
							accessor.getUnit().getLineNumber(accessor.getNode().getStartPosition() )+ " )" + " = "+ accessor.getNode().toString();					
				//	System.out.println(ind +  callPoint);
					
				//	pathStack.addFirst(callPoint);
					
				//	System.out.println(ind +"MM..: " + accessor.toString() ) ;
				//	accessor.getResource().getName() + accessor.getUnit().getLineNumber(0)
				//	System.out.println("MM..: " + entrance.getResource().getName()+ "..>"+ entrance.getNode().toString() ) ;
					pathCollector.addPath(path);
					
					System.err.println(ind +"************* New Path # " + pathCollector.getAllPaths().size());
					String callPath=" ";
					
					Plugin.callGraph.add(pathStack.clone());
					
					while(! pathStack.isEmpty())
					{
						callPath +=" ==>"+ "{ " + pathStack.removeFirst() + " } ";
						
					}
					
					System.out.println(callPath);
				}
				else
					{
				//	System.err.println(ind +"!! Path Exists !!");
					
					pathStack.removeFirst();
					}

			} // EntrancePoint
			else {
							
				// Otherwise, find out the methods that call this method
				Collection<?> callers = CallerFinder.findCallers(null,
						new_accessor_id, javaProject, false);
				
			//	System.out.println(ind +" No Entrance. Number of callers of : " + new_accessor_id + "  =  " + callers.size());
				
				int i=0;
				
				for (Object _caller : callers) {
					//System.out.println("Processing caller in :" + _caller.toString());
					i++;
				//	System.err.println(ind +"--------Start of Process of  Caller # " + pc + "->"+ i + "  {");
					
					process(_caller , pc + "-"+ i);
					
				//	System.err.println(ind +"--------End of Process of  Caller # " + pc + "->"+ i + "  }");
					
					if(! pathStack.isEmpty())
						pathStack.removeFirst();
				}
				//System.out.println(ind +" ---------- End of callers of # " + pc + "( " + new_accessor_id + " )");
				
			}

		} else if (caller instanceof Utils.MethodDeclarationUnitPair) {
			Utils.MethodDeclarationUnitPair pair = (Utils.MethodDeclarationUnitPair) caller;

		} else {
			return;
		}
	}

	private Path hasPath(Point entrance, Point accessor) {

		for (Path path : paths) {
			if (path.getAccessor().equalsTo(accessor)
					&& path.getEntrance().equalsTo(entrance))
				return path;

		}
		return null;

	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public PathCollector getPathCollector() {
		return pathCollector;
	}

	public ArrayList<Path> getPathsRelatedToUnit(ICompilationUnit unit) {

		ArrayList<Path> pathsOfInterest = new ArrayList<Path>();

		if (paths != null) {
			Set<ICompilationUnit> units = null;
			for (Path path : paths) {
				units = path.getCompilationUnitsOfInterest();
				if (units != null && units.contains(unit)) {
					pathsOfInterest.add(path);
				}
			}
		}

		return pathsOfInterest;
	}

}
