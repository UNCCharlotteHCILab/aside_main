package edu.uncc.aside.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.views.UserIDDialog;

public class ConsentForm {
	public static void process(){
		Display display = Display.getCurrent(); //new Display (); ///
		if(display == null)
			System.out.println("display == null in ConsentForm.java");
		final Shell shell = new Shell(display);
		UserIDDialog userIDDialog = new UserIDDialog(shell, 1);
		 userIDDialog.showConsentForm();
	     
	}

}
