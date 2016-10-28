package edu.uncc.aside.actions;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uncc.aside.codeannotate.PathFinder;
import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.listeners.DocumentEditListener;
import edu.uncc.aside.utils.InterfaceUtil;
import edu.uncc.sis.aside.auxiliary.core.RunAnalysis;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @Modified by Mahmoud ( mmoham12 at uncc dot edu )
 */
public class Old_ASIDECodeAnnotateHandler extends AbstractHandler {
	
	private static final Logger logger = Plugin.getLogManager().getLogger(
			RunAnalysis.class.getName());
	
	private IWorkbenchPart targetPart;
	IProject selectProject = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		targetPart = HandlerUtil.getActivePart(event);
		
		IWorkbenchPartSite site = targetPart.getSite();
		ISelectionProvider selectionProvider = site.getSelectionProvider();
		
		if (selectionProvider == null)
			return null;
		
		ISelection selection = selectionProvider.getSelection();
		
		if (selection == null)
			return null;
		
		if (selection instanceof IStructuredSelection) {

			IStructuredSelection sSelection = (IStructuredSelection) selection;

			if (sSelection.isEmpty())
				return null;

			Object fElement = sSelection.getFirstElement();

			if (fElement != null && fElement instanceof IResource) {
				IResource element = (IResource) fElement;
				selectProject = element.getProject();
			}

		} else if (selection instanceof ITextSelection) {
			// Technically, this should not happen.
			ITextSelection tSelection = (ITextSelection) selection;

		}

		if (selectProject == null)
			return null;

		if( Plugin.isAllowed()){
			
			//Turn ASIDE Off after asking from the developer
			//Removing All Markers and annotation and keep the inserted code
			Plugin.setAllowed(false);
			
			IJavaProject javaProject = JavaCore.create(selectProject);
			
			InterfaceUtil.deleteWorkspaceMarkers(PluginConstants.MARKER_ROOT_TYPE);
			
					
			 //get current date time with Date()
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
		    logger.info(dateFormat.format(new Date()) + " : " + Plugin.getUserId() + " turned ESIDE off and ALL markers removed in the project " + 
		    		javaProject.getElementName().toUpperCase() +". Inserted codes are kept unchanged.");

			return null;
		}
		/*
		 * Use a Job to attach a {@link DocumentEditListener} to
		 * each and every IDocument that is related to a ICompilationUnit in the
		 * selected project
		 * I visit Mahmoud
		 */
		Job job = new MountListenerJob("Mount listener to Java file",
				JavaCore.create(selectProject));
		
		job.setPriority(Job.INTERACTIVE);
		job.schedule();

		/* Delegates all heavy lifting to {@link PathFinder} */
		Job heavy_job = new Job("Finding paths in Project: " + selectProject.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try{
					Plugin.getDefault().getWorkbench().getDisplay()
							.asyncExec(new Runnable() {

								@Override
								public void run() {
									
									PathFinder.getInstance(selectProject).run(monitor);
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

		return null;
	}

	public class MountListenerJob extends Job {

		IJavaProject projectOfInterest;
		IBufferChangedListener listener;
		ArrayList<ICompilationUnit> totalUnits;

		public MountListenerJob(String name, IJavaProject project) {
			super(name);
			projectOfInterest = project;
			
			listener = new DocumentEditListener();
			
			totalUnits = new ArrayList<ICompilationUnit>();
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
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
}
