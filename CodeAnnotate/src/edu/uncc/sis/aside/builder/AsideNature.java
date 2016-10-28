package edu.uncc.sis.aside.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import edu.uncc.aside.codeannotate.PluginConstants;

public class AsideNature implements IProjectNature {

	private IProject project;
	
	public AsideNature() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() {

		try {
			IProjectDescription description = project.getDescription();

			ICommand[] commands = description.getBuildSpec();

			for (int i = 0; i < commands.length; ++i) {
				if (commands[i].getBuilderName().equals(PluginConstants.ASIDE_BUILDER_ID)) {
					return;
				}
			}

			ICommand[] newCommands = new ICommand[commands.length + 1];
			System.arraycopy(commands, 0, newCommands, 0, commands.length);
			ICommand command = description.newCommand();
			command.setBuilderName(PluginConstants.ASIDE_BUILDER_ID);
			newCommands[newCommands.length - 1] = command;
			description.setBuildSpec(newCommands);

			project.setDescription(description, null);
		} catch (CoreException e) {
			// Something went wrong
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(PluginConstants.ASIDE_BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
