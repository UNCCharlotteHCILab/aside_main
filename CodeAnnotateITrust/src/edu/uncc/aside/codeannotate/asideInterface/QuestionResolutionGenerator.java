package edu.uncc.aside.codeannotate.asideInterface;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;

	public class QuestionResolutionGenerator  implements
			IMarkerResolutionGenerator, IMarkerResolutionGenerator2
	{
		private static final String UNKNOWN_TAG = "Unknown tag"; //$NON-NLS-1$
		private static final String MISSING_ATTRIBUTE = "Missing required attribute"; //$NON-NLS-1$
		public IMarkerResolution[] getResolutions(IMarker mk) 
		{
			//handle highlighting
			InterfaceUtil.clearAndSetHighlighting(2, mk);
			
			//fake vulnerabilities
			InterfaceUtil.fakeVulnerabilities();
			
		       try {
		          Object problem = mk.getAttribute("WhatsUp");
		          //making "problem" an empty string. normally it would be from the marker
		          problem = "";
		          
		          return new IMarkerResolution[] 
		        {
		             new QuestionTitleResolution("**********ASIDE Annotation**********"+problem),
		             new QuestionResolution("ASIDE Delete Annotation"+problem),
		             new QuestionResolution("ASIDE Modify Annotation"+problem),
		             new AnnotationRequestReadMore("ASIDE Read More"+problem),
		             new AnnotateNowResolution("ASIDE Annotate Now"+problem, "yellow.question"),
		          };
		       }
		       catch (CoreException e) 
		       {
		          return new IMarkerResolution[0];
		       }
		    }

		@Override
		public boolean hasResolutions(IMarker arg0) 
		{
			// TODO Auto-generated method stub
			return true;
		}
/*
		@Override
		public IJavaCompletionProposal[] getProposals(Annotation annotation,
				Position position) {
			return findProposals(annotation, position); 
		}

		@Override
		public boolean hasProposals(Annotation annotation, Position position) {
			String message = annotation.getText();
			return message.startsWith(UNKNOWN_TAG) || message.startsWith(MISSING_ATTRIBUTE);
		}	
		
		private IJavaCompletionProposal[] findProposals(Annotation annotation, Position position){
			ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
			if(!(annotation instanceof TemporaryAnnotation)){
				return new IJavaCompletionProposal[]{};
			}
			TemporaryAnnotation ta = (TemporaryAnnotation)annotation;
			
			String message = annotation.getText();
			if(ta.getPosition() == null)
				return new IJavaCompletionProposal[]{};
			
			final int start = position.getOffset();
			
			final int end = position.getOffset()+position.getLength();
			
			if(message.startsWith(UNKNOWN_TAG)){
				getAddTLD(proposals, ta, message, start, end);
			}else if(message.startsWith(MISSING_ATTRIBUTE)){
				getAddAttribute(proposals, ta, message, start, end);
			}
			
			return proposals.toArray(new IJavaCompletionProposal[]{});
		}
		*/
}
