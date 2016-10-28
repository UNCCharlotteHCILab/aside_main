package edu.uncc.sis.aside.builder;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.utils.ESAPIConfigProcess;
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.visitors.MethodDeclarationVisitor;
import edu.uncc.sis.aside.visitors.MethodInvocationVisitor;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu) <a href="http://www.uncc.edu/">UNC
 *         Charlotte</a>
 * 
 */
public class AsideCompilationParticipant extends CompilationParticipant {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			AsideCompilationParticipant.class.getName());

	private static CompilationUnit cuBeforeReconcile = null;

	private static ASTMatcher astMatcher = null;

	// List of markers per method per compilation units.
	private static Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>> projectMarkerMap = null;

	private int counter = 0;

	/*
	 * 0-argument constructor as required by extension point
	 */
	public AsideCompilationParticipant() {

		if (astMatcher == null) {
			astMatcher = Plugin.getDefault().getASTMatcher();
		}

	}

	@Override
	public int aboutToBuild(IJavaProject project) {

		return READY_FOR_BUILD;
	}

	@Override
	public boolean isActive(IJavaProject project) {

		/*
		 * attach a hidden aside file and an audit file to the project if the
		 * project does not have one yet
		 */

		try {

			IProject resourceProject = (IProject) project
					.getAdapter(IProject.class);
			if (resourceProject != null) {

				String description = "Project: " + resourceProject.getName()
						+ "\nLocation: "
						+ resourceProject.getLocation().toOSString() + "\n";
				System.out.println(description);
				

				IFile aside_detection = resourceProject.getFile(".aside");
				

				if (aside_detection == null || !aside_detection.exists()) {
					aside_detection.create(
							new ByteArrayInputStream(description.getBytes()),
							false, null);
					ResourceAttributes asideInfoAttributes = new ResourceAttributes();
					asideInfoAttributes.setHidden(true);
					asideInfoAttributes.setReadOnly(false);
					aside_detection.setResourceAttributes(asideInfoAttributes);
				}

				/*
				 * IFile asideLog = resourceProject.getFile("aside.audit");
				 * 
				 * if (asideLog == null || !asideLog.exists()) {
				 * asideLog.create(new ByteArrayInputStream(description
				 * .getBytes()), false, null); ResourceAttributes
				 * asideLogAttributes = new ResourceAttributes();
				 * asideLogAttributes.setHidden(false);
				 * asideLogAttributes.setReadOnly(true);
				 * aside_detection.setResourceAttributes(asideLogAttributes); }
				 */
			}
		} catch (CoreException e) {
		}

		return true;
	}

	@Override
	public void reconcile(ReconcileContext context) {

		// Avoid reconciling: This is reset in
		// ManuallyLaunchAsideOnTargetAction.java to avoid unnecessary
		// reconciling at the beginning of launching Eclipse
		// if (counter == 0 && !Plugin.getDefault().getSignal()) {
		// return;
		// }
		if(Plugin.isAllowed()){

		counter++;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		
		Date date = new Date();
		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " starts RECONCILING... " + counter);

		try {
			// javaElementDelta = List of Changes in AST 
			
			IJavaElementDelta javaElementDelta = context.getDelta();

			if (javaElementDelta == null) {
				System.out
				.println("javaElementDelta == null in AsideCompilationParticipant");
				return;
			}
			IJavaProject project = javaElementDelta.getElement()
					.getJavaProject();


			if (project == null) {
				System.out
				.println("project == null in AsideCompilationParticipant");
				return;
			}


			//newly added April 9
			String compatible_resourceDirectory =  "src" + IPath.SEPARATOR + "esapi";

			String resourceDirectory = ".esapi"; 

			IProject resourceProject = (IProject) project.getAdapter(IProject.class);

			if (resourceProject != null) {

				IFolder folder = resourceProject.getFolder(compatible_resourceDirectory);

				URI locationUri = null;
				if (folder.exists()) {
					locationUri = folder.getRawLocationURI();
				} else {
					folder = resourceProject.getFolder(resourceDirectory);
					if (folder.exists()) {
						locationUri = folder.getRawLocationURI();
					}
				}
				if(locationUri == null){
					ESAPIConfigProcess esapiConfig = new ESAPIConfigProcess(
							"ESAPI Configuration", resourceProject, project);
					esapiConfig.run();

				}
			}
			/////////////////
			
			projectMarkerMap = Plugin.getDefault().getMarkerIndex(project);

			if (projectMarkerMap == null) {
				projectMarkerMap = new HashMap<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>>();
			}

			int kind = javaElementDelta.getKind();

			/* consider changes on one java file only */
			if (kind != IJavaElementDelta.CHANGED) {
				 System.out.println("kind != IJavaElementDelta.CHANGED in AsideCompilationParticipant");
				return;
			}

			int flags = javaElementDelta.getFlags();
			// Description of Flags:
			if ((flags & IJavaElementDelta.F_CONTENT) != 0
					|| (flags & IJavaElementDelta.F_AST_AFFECTED) != 0) {
				
				System.out.println("MM After F_AST_AFFECTED");
				
				CompilationUnit cuAfterReconcile = context.getAST8();

				if (cuAfterReconcile == null) {
					return;
				}

				ICompilationUnit cu = (ICompilationUnit) cuAfterReconcile
						.getJavaElement().getPrimaryElement();

				//markersMap = List of the markers of the compilation unit.				
				Map<MethodDeclaration, ArrayList<IMarker>> markersMap = projectMarkerMap.get(cu);

				// First Time Reconciling. Usually when the file opens
				if (cuBeforeReconcile == null) { 
				
					// inspect on this compilation unit AST
				//	System.out.println("MM cuAST_BeforeReconcile");
					
					// Finding Vulnerabilities in changed AST in method declarations and then add corresponding markers
					MethodDeclarationVisitor methodDeclarationVisitor = new MethodDeclarationVisitor(
							cuAfterReconcile, markersMap, cu,
							null);
					markersMap = methodDeclarationVisitor.process();

					if (markersMap == null) {
						System.out
								.println("markersMap == null in AsideCompilationParticipant");
						
						markersMap = new HashMap<MethodDeclaration, ArrayList<IMarker>>();
					}

					// Adding found vulnerabilities as markers
					projectMarkerMap.put(cu, markersMap);

					/*
					 * store the AST after this reconcile process for next round
					 * reconcile
					 */

					cuBeforeReconcile = cuAfterReconcile;

					Plugin.getDefault().setMarkerIndex(project,
							projectMarkerMap);

					return;

				} 
				// Reconciling request after the first time a file gets opened
				else {

					if (astMatcher.match(cuBeforeReconcile,	cuAfterReconcile)) {
						// no change on the structure of the ICompilationUnit
						// When a file is opened for the second time there is no change in its AST structure
						// Two ASTs : cuAST_BeforeReconcile and cuAST_AfterReconcile are equal
						
						return;
					}

					/*
					 * Compare ASTs before and after reconciling, locate the
					 * method declaration within which the change happened
					 */

					@SuppressWarnings("unchecked")
					List<TypeDeclaration> types = cuAfterReconcile.types();

					if (types.isEmpty()) {
						// return; ?
					}

					if (markersMap == null) {
						markersMap = new HashMap<MethodDeclaration, ArrayList<IMarker>>();
					}

					Set<MethodDeclaration> methodsBefore = markersMap.keySet();

					Map<MethodDeclaration, ArrayList<IMarker>> newMarkersMap = new HashMap<MethodDeclaration, ArrayList<IMarker>>();

					// type is a class in a compilation unit
					for (TypeDeclaration type : types) {
						
						//The methods in the AST 
						MethodDeclaration[] methodsAfter = type.getMethods();

						for (MethodDeclaration methodAfter : methodsAfter) {
							
							MethodDeclaration matched = getMatchInSet(methodAfter,
									methodsBefore);

							//Not a new Mthod. If there is a method found in both ASTs of Class( before and after a change happened)						
							if (matched != null) {
								//Update its markers
								newMarkersMap.put(matched, markersMap.get(matched));
								
								markersMap.remove(matched);

							} else 
								//A new method has been added to the class
								if (matched == null) {
								
								MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor(
										methodAfter, null, cu, null);
								
							//	System.out.println("MM Before process ");
								
								// Get the lis of the markers for this new method
								ArrayList<IMarker> markers = methodInvocationVisitor.process();

								newMarkersMap.put(methodAfter, markers);
							}

						}

					}// For all classes in a compilation  unit

					// clear out all markers of last scanning before the change
					if (!markersMap.isEmpty()) {

						Collection<ArrayList<IMarker>> values = markersMap
								.values();

						for (ArrayList<IMarker> value : values) {
							if (value != null && !value.isEmpty()) {
								for (IMarker marker : value) {
									if (marker.exists()) {
									//marker.delete();
									}
								}
							}
						}
					}

					projectMarkerMap.put(cu, newMarkersMap);
					/*
					 * store the AST after this reconcile process for next round
					 * reconcile
					 */
					cuBeforeReconcile = cuAfterReconcile;
				}

			}

			Plugin.getDefault().setMarkerIndex(project, projectMarkerMap);
			date = new Date();
	//		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.pluginName + " finished RECONCILING... " + counter);

		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
}
	}

	private MethodDeclaration getMatchInSet(MethodDeclaration matchee,
			Set<MethodDeclaration> pool) {

		for (MethodDeclaration member : pool) {

			if (astMatcher.match(matchee, member)) {
				return member;
			}
		}

		return null;
	}
	

}
