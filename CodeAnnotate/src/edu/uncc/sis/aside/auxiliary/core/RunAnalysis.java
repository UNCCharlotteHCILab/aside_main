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
import edu.uncc.aside.codeannotate.Utils;
//import edu.uncc.aside.actions.ASIDECodeAnnotateHandler.MountListenerJob;
import edu.uncc.aside.codeannotate.listeners.DocumentEditListener;
import edu.uncc.aside.utils.ConsentForm;
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.popup.actions.ManuallyLaunchAsideOnTargetAction;

public class RunAnalysis {
	private static final Logger logger = Plugin.getLogManager().getLogger(
			RunAnalysis.class.getName());
	RunAnalysis instance =null;
	
	RunAnalysis (){
		if ( instance == null)
			instance = new RunAnalysis();
		
	}
	public RunAnalysis getInstance()
	{
		if ( instance == null)
			instance = new RunAnalysis();
		return instance;
	}
	public static void runAllAnalysisOnAllProjects(){
		
				
			Set<IProject> activeProjects= Utils.getActiveProjects();
			
			addLog();

		    // Running jobs for all active projects in the current workspace
		    for(final IProject project : activeProjects){

		    	System.out.println("projectname = " + project.getName());
		    	
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
		    	runChangeListernersJob(project);

		    } // For All Active Projects= For each project we would have 3 jobs.
			
			
		    addLog();
				
 }
	/**
	 * @return
	 */
	private static DateFormat addLog() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");		  
   
		Date date = new Date();
		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " " + Plugin.PLUGIN_NAME + " starts to inspect all the projects in the workspace");
		return dateFormat;
	}

	public static Job runAccessControlAnalysis(final IProject project) {
		
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

	public static void runInputValidationAnalysis(IProject project) {
		
		IJavaProject javaProject = JavaCore.create(project);
		
		System.out.println("Finding Input Validation : " + project.getName());
		
		try {
			
			ManuallyLaunchAsideOnTargetAction.inspectOnProject(javaProject);
		

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		}
	}

	public static void runESAPIPreparation(final IProject project) {
		
		Plugin.getDefault().setProject(project);
		
		// Copies files and folders needed for ESAPI. 
		ESAPIConfigurationJob job = new ESAPIConfigurationJob(
				"ESAPI Configuration", project,  JavaCore.create(project));		    	
		job.scheduleShort();
		
	}

	public static void runChangeListernersJob(final IProject project) {
		
		Job job = new ChangeListernersJob("Mount listener to Java file",
				JavaCore.create(project));

		job.setPriority(Job.INTERACTIVE);

		job.schedule();
		
	}

} //Class

class ChangeListernersJob extends Job {

	IJavaProject projectOfInterest;
	IBufferChangedListener listener;
	ArrayList<ICompilationUnit> totalUnits;

	public ChangeListernersJob(String name, IJavaProject project) {
		super(name);
		projectOfInterest = project;
		
		listener = new DocumentEditListener();
		
		totalUnits = new ArrayList<ICompilationUnit>();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			// Getting the compilation units of the project
			monitor.beginTask(
					"Mounting a DocumentEditListener to a Java file",
					numberOfJavaFiles(projectOfInterest));

			for (ICompilationUnit unit : totalUnits) {

				if (unit == null || !unit.exists())
					continue;

				if (!unit.isOpen()) {
					unit.open(monitor);
				}

				unit.becomeWorkingCopy(monitor);

				//MM Internal buffer of each Compilation Unit
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

