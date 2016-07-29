package edu.uncc.sis.aside.auxiliary.core;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferChangedListener;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.uncc.aside.codeannotate.PathFinder;
import edu.uncc.aside.codeannotate.Plugin;
//import edu.uncc.aside.codeannotate.actions.ASIDECodeAnnotateHandler.MountListenerJob;
import edu.uncc.aside.codeannotate.listeners.CodeAnnotateDocumentEditListener;
import edu.uncc.aside.utils.ConsentForm;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.popup.actions.ManuallyLaunchAsideOnTargetAction;

public class RunAnalysisOnAllProjects {
	private static final Logger logger = Plugin.getLogManager().getLogger(
			RunAnalysisOnAllProjects.class.getName());
	
	public void runOnAllProjects(){
		
			IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			
			Set<IProject> activeProjects= new HashSet<IProject>();
			
			for (IProject p : allProjects){
			    if(p.isOpen())
				        activeProjects.add(p);
	     	}
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		  
		   
			Date date = new Date();
		    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " starts to inspect all the projects in the workspace");

		    // Running jobs for all active projects in the current workspace
		    for(final IProject project : activeProjects){

		    	System.out.println("projectname = " + project.getName());

		    	if(project.getName().equalsIgnoreCase("RemoteSystemsTempFiles") || 
		    			project.getName().equalsIgnoreCase("Servers")  
		    			|| project.getName().equalsIgnoreCase("Server") ){
		    		continue;
		    	}
		    	IJavaProject javaProject = JavaCore.create(project);

		    	if(javaProject == null )
		    		continue;

		    	
		    	// ESAPI Configuration ( short job)  		    	
		    	runESAPIPreparation(project);
		    	
		    	//Run Input Validation analysis using trust boundary configuration 
		    	runInputValidationAnalysis(project);

		    	// Access Control OR Path Finding Job ( long job)  
		    	runAccessControlAnalysis(project);
		    	
		    	// Document Changer listeners for Java files are set
		    	runAccessControlChangeListeners(project);

		    } // For All Active Projects= For each project we would have 3 jobs.
			
			
			date = new Date();
		    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " finished inspect all the projects in the workspace");
		
 }

	private Job runAccessControlAnalysis(final IProject project) {
		
		System.out.println("Finding Access Control Paths: " + project.getName());		    	

		Job jobAccessControl = new Job("Access Control: " + project.getName()) {


			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try{
					
					Plugin.getDefault().getWorkbench().getDisplay()
					.asyncExec(new Runnable() {

						@Override
						public void run() {
							// Actual Code to run for path finding = Job's body 
							PathFinder.getInstance(project).run(monitor);
						}

					});
				}finally{
					monitor.done();
				}
				return Status.OK_STATUS;
			}

		};
		
		jobAccessControl.setPriority(Job.LONG);
		jobAccessControl.schedule();
		
		return jobAccessControl;
	}

	private void runInputValidationAnalysis(IProject project) {
		
		IJavaProject javaProject = JavaCore.create(project);
		
		System.out.println("Finding Input Validation : " + project.getName());
		
		try {
			
			ManuallyLaunchAsideOnTargetAction.inspectOnProject(javaProject);
			//System.out.println("test on all projects finished");

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		}
	}

	private void runESAPIPreparation(final IProject project) {
		
		Plugin.getDefault().setProject(project);
		
		ESAPIConfigurationJob job = new ESAPIConfigurationJob(
				"ESAPI Configuration", project,  JavaCore.create(project));		    	
		job.scheduleShort();
		
	}

	private void runAccessControlChangeListeners(final IProject project) {
		
		Job jobCodeAnnotate = new MountListenerJob("Mount listener to Java file",
				JavaCore.create(project));

		jobCodeAnnotate.setPriority(Job.INTERACTIVE);

		jobCodeAnnotate.schedule();
		
	}

} //Class

class MountListenerJob extends Job {

	IJavaProject projectOfInterest;
	IBufferChangedListener listener;
	ArrayList<ICompilationUnit> totalUnits;

	public MountListenerJob(String name, IJavaProject project) {
		super(name);
		projectOfInterest = project;
		
		listener = new CodeAnnotateDocumentEditListener();
		
		totalUnits = new ArrayList<ICompilationUnit>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			// Getting the compilation units of the project
			monitor.beginTask(
					"Mounting a CodeAnnotateDocumentEditListener to a Java file",
					numberOfJavaFiles(projectOfInterest));

			for (ICompilationUnit unit : totalUnits) {

				if (unit == null || !unit.exists())
					continue;

				if (!unit.isOpen()) {
					unit.open(monitor);
				}

				unit.becomeWorkingCopy(monitor);

				IBuffer buffer = (unit).getBuffer();
				
				if (buffer != null) {
					
					//Assign a document listener to each java  file
					buffer.addBufferChangedListener(listener);
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private int numberOfJavaFiles(IJavaProject project)
			throws JavaModelException {

		int count = 0;
		IPackageFragment[] fragments = projectOfInterest
				.getPackageFragments();
		
		for (IPackageFragment fragment : fragments) {
			ICompilationUnit[] units = fragment.getCompilationUnits();
			for (ICompilationUnit unit : units) {
				totalUnits.add(unit);
				count++;
			}
		}

		return count;
	}
}

