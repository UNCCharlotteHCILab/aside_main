package edu.uncc.aside.codeannotate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.uncc.aside.codeannotate.listeners.CodeAnnotateElementChangeListener;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.presentations.AnnotationView;

import java.util.ArrayDeque;
import java.util.Deque;
/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 */
public class Plugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "CodeAnnotate"; //$NON-NLS-1$

	public static final String JAVA_WEB_APP_NATURE = "org.eclipse.jdt.core.javanature";

	public static final String ANNOTATION_RELATIONSHIP_VIEW_ID = "relationships";

	public static final String ANNOTATION_ANSWER = "CodeAnnotate.annotationAnswer";
	public static final String ANNOTATION_QUESTION = "CodeAnnotate.annotationQuestion";
	public static final String ANNOTATION_QUESTION_CHECKED = "CodeAnnotate.annotationQuestionChecked";
	public static final String SENSITIVE_ACCESSORS_CONFIG = "SensitiveInfoAccessors.xml";
	
	public static final String ASIDE_NODE_PROP_START = "aside_start";
	public static final String ASIDE_NODE_PROP_END = "aside_end";
	
	// The shared instance
	private static Plugin plugin;

	public static Map<ImageDescriptor, Image> imageCache;

	public static Path annotationPath = null;
	
	public static  List<Object> callGraph ;
	
	public static IInformationControl currentInformationControl = null;

	/**
	 * The constructor
	 */
	public Plugin() {
		imageCache = new HashMap<ImageDescriptor, Image>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		 JavaCore.addElementChangedListener(CodeAnnotateElementChangeListener
		 .getListener());
//		 ISaveParticipant saveParticipant = new CodeAnnotateSaveParticipant();
//		   ISavedState lastState =
//		      ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID, saveParticipant);
//
//		   if (lastState != null) {
//		      String saveFileName = lastState.lookup(new org.eclipse.core.runtime.Path("CodeAnnotate")).toString();
//		      File f = plugin.getStateLocation().append(saveFileName).toFile();
//		
//		   }

//		ResourcesPlugin.getWorkspace().addResourceChangeListener(
//				CodeAnnotateMarkerChangeListener.getListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		JavaCore.removeElementChangedListener(CodeAnnotateElementChangeListener
				.getListener());
//		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
//				CodeAnnotateMarkerChangeListener.getListener());
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Plugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */

	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/";
		try {
			URL installURL = getDefault().getBundle().getEntry("/");
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	public AnnotationView getAnnotationView() {

		IWorkbench workbench = this.getWorkbench();

		if (workbench == null) {
			return null;
		}
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}

		IWorkbenchPage page = window.getActivePage();

		if (page == null) {
			return null;
		}

		IViewReference reference = page
				.findViewReference(Plugin.ANNOTATION_RELATIONSHIP_VIEW_ID);

		if (reference != null)
			return (AnnotationView) reference.getView(true);

		return null;
	}

	public void writeImportantState(File f) {
		System.err.println("writing in to " + f.getName());	
	}
}
