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
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.popup.actions.ManuallyLaunchAsideOnTargetAction;
import edu.uncc.sis.aside.utils.ConsentForm;

public class TestRunOnAllProjects {
	private static final Logger logger = Plugin.getLogManager().getLogger(
			TestRunOnAllProjects.class.getName());
	
	public void runOnAllProjects(){
		
			IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			Set<IProject> activeProjects= new HashSet<IProject>();
			for (IProject p : allProjects){
			    if(p.isOpen())
				        activeProjects.add(p);
	     	}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			  
		    //get current date time with Date()
			Date date = new Date();
		    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " ASIDE starts to inspect all the projects in the workspace");

			for(IProject project : activeProjects){
			
				if(project == null || project.getName()=="RemoteSystemsTempFiles")
					continue;
				System.out.println("projectname = " + project.getName());
				if(project.getName().equals("Servers") || project.getName().equals("servers") || project.getName().equals("Server") || project.getName().equals("server")){
					continue;
				}
				IJavaProject javaProject = JavaCore.create(project);
				if(javaProject == null || project.getName()=="RemoteSystemsTempFiles")
					continue;
			
				//break; //for now, only run on one project
			
				Job jobCodeAnnotate = new MountListenerJob("Mount listener to Java file",
						JavaCore.create(project));
				jobCodeAnnotate.setPriority(Job.INTERACTIVE);
				jobCodeAnnotate.schedule();
				
				//code for i/o

				Plugin.getDefault().setProject(project);
				
				ESAPIConfigurationJob job = new ESAPIConfigurationJob(
						"ESAPI Configuration", project, javaProject);
				//job.scheduleInteractive();
				job.scheduleShort();
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
				
				
				
				
				/* Delegates all heavy lifting to {@link PathFinder} */
				Job heavy_job = new Job("Finding paths in Project: " + project.getName()) {

					@Override
					protected IStatus run(final IProgressMonitor monitor) {
						try{
							Plugin.getDefault().getWorkbench().getDisplay()
									.asyncExec(new Runnable() {

										@Override
										public void run() {
											PathFinder.getInstance(project).run(monitor);
										}

									});
						}finally{
							monitor.done();
						}
						return Status.OK_STATUS;
					}

				};
				heavy_job.setPriority(Job.LONG);
				heavy_job.schedule();
				
				
				
				
			
			}
			date = new Date();
		    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " ASIDE finished inspect all the projects in the workspace");
		
	}

}

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

