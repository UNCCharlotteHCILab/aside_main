package edu.uncc.sis.aside.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.AuthenCenter;
import edu.uncc.aside.utils.ConsentForm;
import edu.uncc.aside.utils.Converter;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.visitors.MethodDeclarationVisitor;

/**
 * Application Security IDE Plugin (ASIDE)
 * 
 * @author Jing Xie (jxie2 at uncc dot edu) <a href="http://www.uncc.edu/">UNC
 *         Charlotte</a>
 */
public class ManuallyLaunchAsideOnTargetAction implements IObjectActionDelegate {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			ManuallyLaunchAsideOnTargetAction.class.getName());

	private IAction targetAction;
	private IWorkbenchPart targetWorkbench;

	//MM List of all Markers per Method per Java File
	private static Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>> projectMarkerMap = null;

	public ManuallyLaunchAsideOnTargetAction() {
		super();
	}

	@Override
	public void run(IAction action) {


		String userIDFromSystem = System.getProperty("user.name");

		if(AuthenCenter.hasPermission(userIDFromSystem)){
			Plugin.getDefault().setAllowed(true);
		}

		if(Plugin.getDefault().isAllowed()){
			System.out.println("manually run " + Plugin.PLUGIN_NAME + "");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			//System.out.println("in Manuallylsdfjalsj");
			//get current date time with Date()
			Date date = new Date();
			logger.info(dateFormat.format(date)+ " " + "User clicked Run " + Plugin.PLUGIN_NAME + " from the context menu to launch " + Plugin.PLUGIN_NAME + "");

			// scan the selected target's project presentation
			if (targetWorkbench == null) {
				return;
			}

			ISelectionProvider selectionProvider = targetWorkbench.getSite()
					.getSelectionProvider();

			if (selectionProvider == null) {
				return;
			}

			ISelection selection = selectionProvider.getSelection();

			if (selection != null && !selection.isEmpty()
					&& selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;

				Object firstElement = structuredSelection.getFirstElement();

				try {
					IProject project = null;
					IJavaProject javaProject = null;
					
					if (firstElement != null && firstElement instanceof IResource) {
						
						IResource resource = (IResource) firstElement;
						project = resource.getProject();
						javaProject = JavaCore.create(project);
						
					} else if (firstElement != null
							&& firstElement instanceof IJavaElement) {
						IResource resource = (IResource) ((IJavaElement) firstElement)
								.getAdapter(IResource.class);
						if (resource != null) {
							project = resource.getProject();
							javaProject = JavaCore.create(project);
						}
					}
					if (project != null && javaProject != null) {


						// setup a new Job thread
						Plugin.getDefault().setProject(project);

						ESAPIConfigurationJob job = new ESAPIConfigurationJob(
								"ESAPI Configuration", project, javaProject);
						job.scheduleInteractive();
						
						inspectOnProject(javaProject);

					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}

			// At last, set the CompliationParticipant active
			if (!Plugin.getDefault().getSignal()) {
				Plugin.getDefault().setSignal(true);
			}
			System.out.println("Manually launch aside finished!");
		}
	}
	public static void inspectOnProject(IJavaProject project)
			throws JavaModelException, CoreException {

		if (project == null || project.getElementName().equalsIgnoreCase("RemoteSystemsTempFiles"))
			return;
		

		System.out.println("project.getElementName()==" +project.getElementName());
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		
		Date date = new Date();
		logger.info(dateFormat.format(date) + " : " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " is inspecting project: " + project.getElementName());
		/*/MM
		if(project != null){ // && project.getElementName()!="RemoteSystemsTempFiles"
			try{
				project.getCorrespondingResource().deleteMarkers(
						PluginConstants.ROOT_MARKER, true,
						IResource.DEPTH_INFINITE);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		*/
		projectMarkerMap = Plugin.getDefault().getMarkerIndex(project);

		//MM projectMarkerMap =list of the markers for each function in this Java file.
		if (projectMarkerMap == null) {
			projectMarkerMap = new HashMap<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>>();
		}

		//MM fragments = Java Packages in a project
		IPackageFragment[] packageFragmentsInProject = project
				.getPackageFragments();		
		
		for (IPackageFragment fragment : packageFragmentsInProject) {
			
			ICompilationUnit[] units = fragment.getCompilationUnits();
			for (ICompilationUnit unit : units) {

				//MM Run analysis for each Java file
				Map<MethodDeclaration, ArrayList<IMarker>> fileMap = inspectOnJavaFile(unit);
				
				projectMarkerMap.put(unit, fileMap);
			}
		}

		// At last, put it back to Aside Plugin
		Plugin.getDefault().setMarkerIndex(project, projectMarkerMap);
		
		date = new Date();
		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " finished inspecting project: " + project.getElementName());
	}

	public static Map<MethodDeclaration, ArrayList<IMarker>> inspectOnJavaFile(
			ICompilationUnit unit) {
		
		addInspectOnFileLog(unit);

		CompilationUnit astRoot = Converter.parse(unit);
		// PreferencesSet set = new PreferencesSet(true, true, false, new
		// String[0]);
		MethodDeclarationVisitor declarationVisitor = new MethodDeclarationVisitor(
				astRoot, null, unit, null);
		
		return declarationVisitor.process();

	}

	private static void addInspectOnFileLog(ICompilationUnit unit) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		
		Date date = new Date();
	    logger.info(dateFormat.format(date) + " : " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " starts inspecting java file: " + unit.getElementName());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart workbench) {
		this.targetAction = action;
		this.targetWorkbench = workbench;

	}

}
