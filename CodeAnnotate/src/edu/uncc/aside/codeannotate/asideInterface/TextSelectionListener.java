package edu.uncc.aside.codeannotate.asideInterface;

import org.eclipse.jface.text.IBlockTextSelection;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

public class TextSelectionListener implements ISelectionListener

{

	@Override
	public void selectionChanged(IWorkbenchPart theWorkbenchPart, ISelection theSelection) 
	{
		
		if (VariablesAndConstants.isAnnotatingNow == true && theSelection instanceof ITextSelection) 
		{
			ITextSelection theTextSelection = (ITextSelection)theSelection;
			
			//Create New Marker using Highlighted (selected) text by user
	        InterfaceUtil.processAnnotationChanges(theWorkbenchPart, theTextSelection);
	    }
	    
		
	}

}