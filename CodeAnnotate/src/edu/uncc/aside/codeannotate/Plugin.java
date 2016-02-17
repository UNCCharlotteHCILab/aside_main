package edu.uncc.aside.codeannotate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.uncc.sis.aside.logging.AsideLoggingManager;
import edu.uncc.sis.aside.preferences.IPreferenceConstants;
import edu.uncc.sis.aside.auxiliary.core.TestRunOnAllProjects;
import edu.uncc.sis.aside.constants.PluginConstants;
import edu.uncc.sis.aside.utils.AuthenCenter;
import edu.uncc.sis.aside.utils.ConsentForm;
import edu.uncc.sis.aside.utils.MakerManagement;
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
	public static final String ANNOTATION_QUESTION = "yellow.question.box";
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
	
	// codes for input/output encoding
	
	public static Date END_DAY_SEND_LOGS;
	
	private static String userId;

	private static final String LOG_PROPERTIES_FILE = "logger.properties";
	
	private static final String ASIDE_USERID_FILE = "userID.txt";
	
	// The shared AST matcher instance
	private static ASTMatcher astMatcher;
		
		// AsideCompilationParticipant is off when signal is false
	private boolean signal = false;

	private static Map<IJavaProject, Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>>> markerIndex = null;

	private AsideLoggingManager loggingManager;
		
	private IProject project;
		
	private static boolean isAllowed;
	/**
	 * The constructor
	 */
	public Plugin() {
		imageCache = new HashMap<ImageDescriptor, Image>();
		if (markerIndex == null) {
			markerIndex = new HashMap<IJavaProject, Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>>>();
		}
	}

	public static boolean isAllowed() {
		return isAllowed;
	}

	public static void setAllowed(boolean isAllowed) {
		Plugin.isAllowed = isAllowed;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		
		 //io code starts here
		setAllowed(false);
		String userIDFromSystem = System.getProperty("user.name");
		setUserId(userIDFromSystem);
		if(AuthenCenter.hasPermission(userIDFromSystem)){
			setAllowed(true);
			
		}
		
		 //io code ends here
		
		super.start(context);
		plugin = this;
		 JavaCore.addElementChangedListener(CodeAnnotateElementChangeListener
		 .getListener());
		 
		 //io code starts here
		 //MakerManagement.removeAllASIDEMarkersInWorkspace();
		 if (astMatcher == null) {
				astMatcher = new ASTMatcher();
			}

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
	        
			IWorkspaceDescription description = workspace.getDescription();
			if (!description.isAutoBuilding()) {
				description.setAutoBuilding(true);
				workspace.setDescription(description);
			}

			configure();
			System.out.println("Aside Plugin start print test");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// get current date time with Date()
			Date date = new Date();
		 
			IPath stateLocation = Plugin.getDefault().getStateLocation();
			String fileName = stateLocation + "/"
					+ Plugin.getAsideUseridFile(); // might have to be updated
														// about "/"
			File userIdFile = new File(fileName);
			
			System.out.println("Plugin.getDefault().isAllowed() = "
					+ Plugin.getDefault().isAllowed());
//			if (Plugin.isAllowed()) {
//				if (userIdFile.exists()) {
//					try {
//						FileReader fr = new FileReader(userIdFile);
//						BufferedReader br = new BufferedReader(fr);
//						String userIdRead = br.readLine();
//						System.out.println("userId read from the file = "
//								+ userIdRead);
//						Plugin.getDefault().setUserId(userIdRead);
//						br.close();
//						fr.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					
//				} else {
//					// /////////////
//					// pop up consent form asking for id and consent
//					Display.getDefault().syncExec(new Runnable() {
//						public void run() {
//							ConsentForm.process();
//						}
//					});
//					
//					// System.out.println("userIdRead in Maunual =" + userIdRead);
//
//					boolean created = false;
//					try {
//						created = userIdFile.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if (created) {
//						FileWriter fw = null;
//						try {
//							fw = new FileWriter(userIdFile);
//							BufferedWriter bw = new BufferedWriter(fw);
//							bw.write(userIDFromSystem);
//							bw.close();
//							fw.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						Plugin.getDefault().setUserId(userIDFromSystem);
//					} else {
//						System.err.println("UserId file is not created properly!");
//					}
//				}
				TestRunOnAllProjects testRunOnAllProjects = new TestRunOnAllProjects();
				testRunOnAllProjects.runOnAllProjects();
//
//			}else{
//				System.out.println("this user is not allowed");
//			}
//			
			//io code ends here
		 
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
		Date currentDate = new Date();
		
	    if(!currentDate.after(this.END_DAY_SEND_LOGS)){
	    if(this.isAllowed){
	    	//this used to be used in the spring 2013 version
			// SendMailTLS.sendLogFile(project, this.getUserId());
	    }}////
		plugin = null;
		JavaCore.removeElementChangedListener(CodeAnnotateElementChangeListener
				.getListener());
//		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
//				CodeAnnotateMarkerChangeListener.getListener());
		if (this.loggingManager != null) {
			this.loggingManager.shutdown();
			this.loggingManager = null;
		}
		super.stop(context);
	}
	private void configure() {

		try {
			this.END_DAY_SEND_LOGS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(PluginConstants.LOG_SEND_END_DATE);   //the date needs to be verified
			
			URL url = getBundle().getEntry("/" + LOG_PROPERTIES_FILE);
			InputStream propertiesInputStream = url.openStream();
			
			if (propertiesInputStream != null) {
				//System.out.println("configure log run.......................");
				Properties props = new Properties();
				props.load(propertiesInputStream);
				propertiesInputStream.close();
				this.loggingManager = new AsideLoggingManager(this, props);
				this.loggingManager.hookPlugin(Plugin.getDefault()
						.getBundle().getSymbolicName(), Plugin
						.getDefault().getLog());
			}
			
		} catch (Exception e) {
			String message = "Error while initializing log properties.\n\n\n\n"
					+ e.getMessage();
			IStatus status = new Status(IStatus.ERROR, getDefault().getBundle()
					.getSymbolicName(), IStatus.ERROR, message, e);
			getLog().log(status);
			throw new RuntimeException(
					"Error while initializing log properties.\n", e);
		}
	}
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Plugin getDefault() {
		return plugin;
	}
	public static AsideLoggingManager getLogManager() {
		return getDefault().loggingManager;
	}
	
	public IWorkbenchWindow getActiveWorkbenchWindow() {
		if (Display.getCurrent() != null) {
			return getDefault().getWorkbench().getActiveWorkbenchWindow();
		}
		// need to call from UI thread
		final IWorkbenchWindow[] windows = new IWorkbenchWindow[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				windows[0] = getDefault().getWorkbench()
						.getActiveWorkbenchWindow();
			}
		});
		return windows[0];
	}
	public Shell getShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}
	public Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>> getMarkerIndex(
			IJavaProject project) {

		if (markerIndex == null) {
			return null;
		}

		if (project == null) {
			return null;
		}

		return markerIndex.get(project);
	}
	
	public Set<IJavaProject> getIndexedJavaProjects() {
		return markerIndex.keySet();
	}

	public void setMarkerIndex(
			IJavaProject project,
			Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>> markerIndex) {
		Plugin.markerIndex.put(project, markerIndex);
	}
	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
// differently define in AsidePlugin. Has to investigate later
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
	public void setProject(IProject project) {
		this.project = project;
	}
	public ASTMatcher getASTMatcher() {
		if (astMatcher == null)
			astMatcher = new ASTMatcher();
		return astMatcher;
	}	
	
	/*
	 * ASIDE PREFERENCES
	 */
	public String[] getDefaultTBPathsPreference() {
		return convert(getPreferenceStore().getDefaultString(
				IPreferenceConstants.EXTERNAL_TB_PATH_PREFERENCE));
	}

	public String[] getTBPathsPreference() {
		return convert(getPreferenceStore().getString(
				IPreferenceConstants.EXTERNAL_TB_PATH_PREFERENCE));
	}

	public void setTBPathPreference(String[] elements) {

		if (elements == null) {
			return;
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(IPreferenceConstants.PATH_DELIMITER);
		}
		getPreferenceStore().setValue(
				IPreferenceConstants.EXTERNAL_TB_PATH_PREFERENCE,
				buffer.toString());
	}

	public boolean getAsideTBCheckPreference() {
		return getPreferenceStore().getBoolean(
				IPreferenceConstants.ASIDE_TB_PREFERENCE);
	}

	public void setAsideTBCheckPreference(boolean check) {
		getPreferenceStore().setValue(IPreferenceConstants.ASIDE_TB_PREFERENCE,
				check);
	}

	public boolean getProjectTBCheckPreference() {
		return getPreferenceStore().getBoolean(
				IPreferenceConstants.PROJECT_TB_PREFERENCE);
	}

	public void setProjectTBCheckPreference(boolean check) {
		getPreferenceStore().setValue(
				IPreferenceConstants.PROJECT_TB_PREFERENCE, check);
	}

	public boolean getExternalTBCheckPreference() {
		return getPreferenceStore().getBoolean(
				IPreferenceConstants.EXTERNAL_TB_PREFERENCE);
	}

	public void setExternalTBCheckPreference(boolean check) {
		getPreferenceStore().setValue(
				IPreferenceConstants.EXTERNAL_TB_PREFERENCE, check);
	}
	
	public void setAsideVRCheckPreference(boolean check) {
		getPreferenceStore().setValue(IPreferenceConstants.ASIDE_VR_PREFERENCE,
				check);
	}
	
	public boolean getExternalVRCheckPreference() {
		return getPreferenceStore().getBoolean(
				IPreferenceConstants.EXTERNAL_VR_PREFERENCE);
	}

	public void setExternalVRCheckPreference(boolean check) {
		getPreferenceStore().setValue(
				IPreferenceConstants.EXTERNAL_VR_PREFERENCE, check);
	}
	public void setProjectVRCheckPreference(boolean check) {
		getPreferenceStore().setValue(
				IPreferenceConstants.PROJECT_VR_PREFERENCE, check);
	}
	public void setVRPathPreference(String[] elements) {

		if (elements == null) {
			return;
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			buffer.append(IPreferenceConstants.PATH_DELIMITER);
		}
		getPreferenceStore().setValue(
				IPreferenceConstants.EXTERNAL_VR_PATH_PREFERENCE,
				buffer.toString());
	}

	private String[] convert(String preferenceValue) {
		StringTokenizer tokenizer = new StringTokenizer(preferenceValue,
				IPreferenceConstants.PATH_DELIMITER);
		int tokenCount = tokenizer.countTokens();
		String[] elements = new String[tokenCount];

		for (int i = 0; i < tokenCount; i++) {
			elements[i] = tokenizer.nextToken();
		}

		return elements;
	}
	
	public String[] getDefaultVRPathsPreference() {
		return convert(getPreferenceStore().getDefaultString(
				IPreferenceConstants.EXTERNAL_VR_PATH_PREFERENCE));
	}

	public String[] getVRPathsPreference() {
		return convert(getPreferenceStore().getString(
				IPreferenceConstants.EXTERNAL_VR_PATH_PREFERENCE));
	}
	
	public void setSignal(boolean signal) {
		this.signal = signal;
	}

	public boolean getSignal() {
		return signal;
	}

	public static String getUserId() {
		return userId;
	}

	public static void setUserId(String userId) {
		Plugin.userId = userId;
	}

	public static String getAsideUseridFile() {
		return ASIDE_USERID_FILE;
	}
}
