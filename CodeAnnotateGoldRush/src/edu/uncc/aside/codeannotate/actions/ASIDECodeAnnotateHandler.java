package edu.uncc.aside.codeannotate.actions;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import edu.uncc.aside.codeannotate.asideInterface.InterfaceUtil;
import edu.uncc.aside.codeannotate.asideInterface.VariablesAndConstants;
import edu.uncc.aside.codeannotate.listeners.CodeAnnotateDocumentEditListener;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public class ASIDECodeAnnotateHandler extends AbstractHandler {

	private IWorkbenchPart targetPart;
	IProject selectProject = null;
 
	/*@Override
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

		
		 /* Use a Job to attach a {@link CodeAnnotateDocumentEditListener} to
		 /* each and every IDocument that is related to a ICompilationUnit in the
		 /* selected project
		 /* I visit Mahmoud 
		 
		Job job = new MountListenerJob("Mount listener to Java file",
				JavaCore.create(selectProject));
		job.setPriority(Job.INTERACTIVE);
		job.schedule();

		 //Delegates all heavy lifting to {@link PathFinder} 
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
	*/
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Code Annotate Command Run");
		
		int charPositions[] = new int[2];
		
		targetPart = HandlerUtil.getActivePart(event);
		//IPath thePath = new Path("iTrust/WebRoot/auth/admin/addHCP.java");
		//IPath thePath = new Path("iTrust/src/edu/ncsu/csc/itrust/action/AddHCPAction.java");
		//IProject iTrust = ResourcesPlugin.getWorkspace().getRoot().getProject("ITrust");
		IProject GoldRush = ResourcesPlugin.getWorkspace().getRoot().getProject("GoldRush");
		
		
		
		
		 /* This code runs when code annotate is run when the project is right clicked and code annotate is run
		 * Each block dsiplays a warning or annotation request where we want it to.
		 * We are not using all of these requests and warnings for the current ITrust study, but the extras are left
		 * here in case of future studies. The extras can be commented out if desired.*/
		 
		
		
		
		
		IPath thePath = new Path("src/uncc/goldrush/servlet/AccountsServlet.java");
		IFile theFile = GoldRush.getFile(thePath);
        
		thePath = new Path("src/uncc/goldrush/servlet/AccountsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(52 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
       
		thePath = new Path("src/uncc/goldrush/servlet/AccountsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(54 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/AccountsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(67 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/LoginServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(44 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/LoginServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(46 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/LoginServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(49 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/Trainer.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(45 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/Trainer.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(50 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransactionsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(46 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransactionsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(46 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransactionsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(48 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
	
		thePath = new Path("src/uncc/goldrush/servlet/TransactionsServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(57 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(64 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(64 , theFile);
		InterfaceUtil.createMarker("red.flag.box", charPositions[0], charPositions[1], theFile); 
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(70  , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(99 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(103 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(105 , theFile);
		InterfaceUtil.createMarker("red.flag.box", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(107 , theFile);
		InterfaceUtil.createMarker("red.devil", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("src/uncc/goldrush/servlet/TransferServlet.java");
		theFile = GoldRush.getFile(thePath);
		charPositions = InterfaceUtil.getCharStartFromLineNumber(109 , theFile);
		InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		
		/*
		IPath thePath = new Path("WebRoot/auth/admin/addPHA.java");
		IFile theFile = iTrust.getFile(thePath);
		
		thePath = new Path("WebRoot/auth/admin/addPHA.java");
	    theFile = iTrust.getFile(thePath);
	    charPositions = InterfaceUtil.getCharStartFromLineNumber(26,theFile);
	    InterfaceUtil.createMarker("red.flag.box", charPositions[0], charPositions[1], theFile);
		
		thePath = new Path("WebRoot/auth/admin/addHCP.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(34,theFile);
        InterfaceUtil.createMarker("red.flag.box", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/admin/addER.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(35,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/admin/editNDCodes.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(47,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/hcp/editPatientList.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(46,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/hcp/editAppt.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(113,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/hcp/laborDeliveryReport.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(106,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/er/information.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(23,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/er/information.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(19,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/pha/adverseEventChart.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(25,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
        
        thePath = new Path("WebRoot/auth/patient/viewMessageInbox.java");
        theFile = iTrust.getFile(thePath);
        charPositions = InterfaceUtil.getCharStartFromLineNumber(69,theFile);
        InterfaceUtil.createMarker("yellow.question", charPositions[0], charPositions[1], theFile);
		*/
		
        //now prepare the contextual text for vulnerabilities
        IMarker firstMarker = VariablesAndConstants.annotationRequestMarkers[0];
		IMarker secondMarker = VariablesAndConstants.annotationRequestMarkers[1];
		
		//now set the marker ids so that they can be identified for the faked warning messages
		try
		{
			firstMarker.setAttribute("markerIdentifier", 1);
			secondMarker.setAttribute("markerIdentifier", 2);
		}
		catch(Exception e)
		{
			System.out.println("Exception setting marker id's for vulnerabilities");
		}
		
		
		
		
		
		/*
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
		
		 
		
		 * Use a Job to attach a {@link CodeAnnotateDocumentEditListener} to
		 * each and every IDocument that is related to a ICompilationUnit in the
		 * selected project
		 * I visit Mahmoud
		 
		
		Job job = new MountListenerJob("Mount listener to Java file",
				JavaCore.create(selectProject));
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
		

		 Delegates all heavy lifting to {@link PathFinder} 
		
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
		
		*/
		return null;
		
		
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
}
