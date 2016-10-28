package edu.uncc.sis.aside.auxiliary.popup.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.sis.aside.auxiliary.properties.ESAPIPropertiesReader;
import edu.uncc.sis.aside.views.ExplanationView;

public class DynamicMenuContributionItem extends CompoundContributionItem {

	public DynamicMenuContributionItem() {
		super();
		System.out.println("DynamicMenuContributionItem.java");
	}

	public DynamicMenuContributionItem(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {

		IProject fProject = null;
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		IEditorSite currentEditorSite = editorPart.getEditorSite();

		IEditorInput editorInput = editorPart.getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IFile ifile = fileEditorInput.getFile();

			fProject = ifile.getProject();
		}

		ArrayList<String> definedInputTypeList = ESAPIPropertiesReader
				.getInstance(fProject).retrieveESAPIDefinedInputTypes();
		String[] inputTypes = definedInputTypeList
				.toArray(new String[definedInputTypeList.size()]);
		IContributionItem[] items = new IContributionItem[inputTypes.length];

		if (inputTypes.length == 0) {
			presentComplimentaryExplanationView();
			return items;
		}

		final CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
				currentEditorSite, PluginConstants.CONTRIBUTION_ITEM_ID,
				PluginConstants.CONTRIBUTION_COMMAND_ID, CommandContributionItem.STYLE_PUSH);

		for (int i = 0; i < inputTypes.length; i++) {
			contributionParameter.label = "Validate input using type: "
					+ inputTypes[i];
			items[i] = new CommandContributionItem(contributionParameter);
		}

		return items;
	}

	private void presentComplimentaryExplanationView() {

		try {
			IViewPart view = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.showView(PluginConstants.EXPLANATION_VIEW_ID);
			if (view != null && view instanceof ExplanationView) {
				ExplanationView guidanceView = (ExplanationView) view;
				StyledText text = guidanceView.getWidget();
				String guidance = Plugin.PLUGIN_NAME + " failed to locate ESAPI configuration file. Please follow OWASP ESAPI installation instructions and place ESAPI.properties file under HOME_DIRECTORY/.esapi";
				text.setText(guidance);
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
