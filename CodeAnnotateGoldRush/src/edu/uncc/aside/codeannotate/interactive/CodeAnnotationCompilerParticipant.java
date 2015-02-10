package edu.uncc.aside.codeannotate.interactive;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class CodeAnnotationCompilerParticipant extends CompilationParticipant {

	/*
	 * public 0-argument constructor required by extension point
	 */
	public CodeAnnotationCompilerParticipant() {
	}

	@Override
	public boolean isActive(IJavaProject project) {
		/*
		 * this participate stays active on project that is selected in order to
		 * be consistent with the relationships view
		 */
		return project.isOpen();
	}

	@Override
	public void reconcile(ReconcileContext context) {
	
	}

}
