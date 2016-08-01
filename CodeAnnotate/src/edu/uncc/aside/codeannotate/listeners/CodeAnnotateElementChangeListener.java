package edu.uncc.aside.codeannotate.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.BufferChangedEvent;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import edu.uncc.aside.codeannotate.PathFinder;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.jobs.GarbagePathCollector;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.visitors.MethodInvocationAccessorVisitor;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */
public class CodeAnnotateElementChangeListener implements
		IElementChangedListener {

	private static CodeAnnotateElementChangeListener listener = null;

	public static CodeAnnotateElementChangeListener getListener() {
		if (listener == null)
			listener = new CodeAnnotateElementChangeListener();

		return listener;
	}

	private CodeAnnotateElementChangeListener() {

	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		
		int type = event.getType();
		IJavaElementDelta delta = event.getDelta();
		IJavaElement element = delta.getElement();
		int elementType = element.getElementType();

		
		int flags = delta.getFlags();

		if (type == ElementChangedEvent.POST_RECONCILE) {
			/*
			 * F_CONTENT indicates that the content of the a folder or a file
			 * has changed. This is what I am concerned at this moment. It
			 * covers the creation, deletion and all editing of a file.
			 */
			if ((flags & IJavaElementDelta.F_CONTENT) == 0
					|| (flags & IJavaElementDelta.F_MOVED_FROM) == 0) {

				if (elementType == IJavaElement.COMPILATION_UNIT) {
					
					ICompilationUnit unitOfInterest = (ICompilationUnit) element;
					
					IProject projectOfInterest = unitOfInterest
							.getJavaProject().getProject();
					
				
					BufferChangedEvent evt = Messenger.getInstance()
							.getDocumentEvent();
					
					// Why do we need this event from Messenger: To get start and length of changed characters
					if (evt == null) {
						return;
					} else {
						String modificationType = "UNKNOWN";
						int offset = evt.getOffset();
						int length = evt.getLength();
						String text = evt.getText();

						if (offset == 0 && (text==null || text.equals("")))
							return;
						
						if(offset > 0 && !text.equals("") && length == 0){
							modificationType = "INSERTION";
						}else if(offset > 0 && text == null && length > 0){
							modificationType = "REMOVAL";
						}else if(offset > 0 && text != null && length >= 0){
							modificationType = "REPLACEMENT";
						}

						CompilationUnit astRoot = Utils
								.getCompilationUnit(unitOfInterest);
						
						int changedLine = astRoot.getLineNumber(offset);

						try {
							Document doc = new Document(
									unitOfInterest.getSource());
							
							int changedLineStartOffset = doc.getLineOffset(changedLine-1);
							int nextStartOffset = doc.getLineOffset(changedLine);

							//
							NodeFinder finder = new NodeFinder(
									Utils.getCompilationUnit(unitOfInterest),
									changedLineStartOffset, nextStartOffset - changedLineStartOffset);
							/*
							 * this should be the ASTNode of the line that has
							 * been touched
							 */
							ASTNode nodeFound = finder.getCoveredNode();
							if (nodeFound == null) {
								nodeFound = finder.getCoveringNode();
							}
							if (nodeFound == null) {
								return;
							}

							//MM Removing the Annotation requests and answers
							/*MM
							GarbagePathCollector job = new GarbagePathCollector(
									"Job for Removing Destroyed Paths",
									projectOfInterest, unitOfInterest,
									changedLineStartOffset, nextStartOffset, modificationType, offset, length, text);
							
							job.setPriority(Job.INTERACTIVE);
							job.schedule();
							*/
							
							// Investigating the new method for Access Control Annotations
							MethodInvocationAccessorVisitor visitor = new MethodInvocationAccessorVisitor(
									nodeFound, unitOfInterest, projectOfInterest,
									changedLineStartOffset, nextStartOffset);
							
							nodeFound.accept(visitor);
						//	visitor.process();

						} catch (JavaModelException e) {
							e.printStackTrace();
						} catch (BadLocationException e) {
							e.printStackTrace();
						}

					}//else event == null

				}// if elementType == IJavaElement.COMPILATION_UNIT
			}//if flags & IJavaElementDelta.F_CONTENT
		}//if ElementChangedEvent.POST_RECONCILE

	}
}
