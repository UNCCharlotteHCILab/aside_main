package edu.uncc.aside.codeannotate.interactive;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import edu.uncc.aside.codeannotate.Plugin;

/**
 * This is currently under experimentation. Most of the code is copied from
 * http:
 * //help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv
 * %2Fguide%2FresAdv_saving.htm
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * 
 */

public class CodeAnnotateSaveParticipant implements ISaveParticipant {

	@Override
	public void doneSaving(ISaveContext context) {
		Plugin myPluginInstance = Plugin.getDefault();

		// delete the old saved state since it is not necessary anymore
		int previousSaveNumber = context.getPreviousSaveNumber();
		String oldFileName = "CodeAnnotate-"
				+ Integer.toString(previousSaveNumber);
		File f = myPluginInstance.getStateLocation().append(oldFileName)
				.toFile();
		f.delete();

	}

	@Override
	public void prepareToSave(ISaveContext context) throws CoreException {
		System.out
				.println("CodeAnnotate is notified that the current workspace is about to be closed.");

	}

	@Override
	public void rollback(ISaveContext context) {
		Plugin myPluginInstance = Plugin.getDefault();

		// since the save operation has failed, delete the saved state we have
		// just written
		int saveNumber = context.getSaveNumber();
		String saveFileName = "CodeAnnotate-" + Integer.toString(saveNumber);
		File f = myPluginInstance.getStateLocation().append(saveFileName)
				.toFile();
		f.delete();

	}

	@Override
	public void saving(ISaveContext context) throws CoreException {
		switch (context.getKind()) {
		case ISaveContext.FULL_SAVE:
			Plugin myPluginInstance = Plugin.getDefault();
			// save the plug-in state
			int saveNumber = context.getSaveNumber();
			String saveFileName = "CodeAnnotate-"
					+ Integer.toString(saveNumber);
			File f = myPluginInstance.getStateLocation().append(saveFileName)
					.toFile();
			// if we fail to write, an exception is thrown and we do not update
			// the path
			myPluginInstance.writeImportantState(f);
			context.map(new Path("CodeAnnotate"), new Path(saveFileName));
			context.needSaveNumber();
			break;
		case ISaveContext.PROJECT_SAVE:
			// get the project related to this save operation
			IProject project = context.getProject();
			// save its information, if necessary
			break;
		case ISaveContext.SNAPSHOT:
			// This operation needs to be really fast because
			// snapshots can be requested frequently by the
			// workspace.
			break;
		}

	}

}
