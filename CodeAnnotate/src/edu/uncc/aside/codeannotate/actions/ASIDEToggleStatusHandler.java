

package edu.uncc.aside.codeannotate.actions;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
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
import edu.uncc.aside.codeannotate.listeners.CodeAnnotateDocumentEditListener;
import edu.uncc.aside.utils.MakerManagement;
import edu.uncc.sis.aside.auxiliary.core.TestRunOnAllProjects;

/**
 * 
 * @author Mahmoud  (mmoham12 at uncc dot edu)
 * 
 */
public class ASIDEToggleStatusHandler extends AbstractHandler {
	
	private static final Logger logger = Plugin.getLogManager().getLogger(
			TestRunOnAllProjects.class.getName());

	private IWorkbenchPart targetPart;
	IProject selectProject = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Command command = event.getCommand();
	     boolean oldValue = HandlerUtil.toggleCommandState(command);
	     
	     
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

		if(  oldValue ){ // ASIDE is going to be turned OFF
			
			// Turn ASIDE Off.
			Plugin.setAllowed(false);

			//Turn ASIDE Off after asking from the developer
			//Removing All Markers and annotation and only keep the inserted code

			IJavaProject javaProject = JavaCore.create(selectProject);

			MakerManagement.deleteProjectMarkers(javaProject ,Plugin.ROOT_MARKER);

			

			//get current date time with Date()
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			logger.info(dateFormat.format(new Date()) + " : " + Plugin.getUserId() + " turned ASIDE off and ALL markers removed in the project " + 
					javaProject.getElementName().toUpperCase() +". Inserted codes are kept unchanged.");

			return null;
		}
		else { // ASIDE is going to be turned ON


			Plugin.setAllowed(true);

			return null;
		}
	}

	public class MountListenerJob extends Job {

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
}
