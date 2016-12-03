package edu.uncc.sis.aside.auxiliary.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.utils.Converter;
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.jobs.ESAPIConfigurationJob;
import edu.uncc.sis.aside.visitors.MethodDeclarationVisitor;

/**
 * Application Security IDE Plugin (ASIDE)
 * 
 * @author Jun Zhu (jzhu16 at uncc dot edu) <a href="http://www.uncc.edu/">UNC
 *         Charlotte</a>
 */
public class AsideScanOneICompilationUnit {

	private static final Logger logger = Plugin.getLogManager().getLogger(
			AsideScanOneICompilationUnit.class.getName());

	private IAction targetAction;
	private IWorkbenchPart targetWorkbench;
    private ICompilationUnit targetICompilationUnit;
	private static Map<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>> projectMarkerMap = null;

	public AsideScanOneICompilationUnit(IProject project, ICompilationUnit fCompilationUnit) {
		if(Plugin.getDefault().isAllowed()){
		try{
			IJavaProject javaProject = JavaCore.create(project);
			ESAPIConfigurationJob job = new ESAPIConfigurationJob(
					"ESAPI Configuration", project, javaProject);
			job.scheduleInteractive();
		    boolean result = false;
		    if(Plugin.getDefault().isAllowed()){
		    	System.out.println("Deleteing market in aside one scan");
		    	inspectOnJavaFile(javaProject, fCompilationUnit);
		    if(result == false){
		    	System.out.println("result = inspectOnJavaFile(javaProject, fCompilationUnit) in AsideScanOneCompilation");
		    	return;
		    }
		    }
		    //System.out.println("AsideScanning-----");
		 // At last, set the CompliationParticipant active
//			if (Plugin.getDefault().getSignal()) {
//				Plugin.getDefault().setSignal(false);
//			}
		}catch(Exception e2){}
		}
	}


	public ICompilationUnit getTargetICompilationUnit() {
		return targetICompilationUnit;
	}


	public void setTargetICompilationUnit(ICompilationUnit targetICompilationUnit) {
		this.targetICompilationUnit = targetICompilationUnit;
	}



	private boolean inspectOnJavaFile(IJavaProject project,
			ICompilationUnit unit) {
		//logger.info("inspecting java file: " + unit.getElementName());
		if (project == null) {
			System.out.println("Project is null");
			return false;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  
	    //get current date time with Date()
		Date date = new Date();
	    logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " . After user applied some rule options, " + Plugin.PLUGIN_NAME + " is re-inspecting java file: <<" + unit.getElementName() + ">>");

		Map<MethodDeclaration, ArrayList<IMarker>> MarkerIndexForICompilationUnit;
		CompilationUnit astRoot = Converter.parse(unit);
	
		MethodDeclarationVisitor declarationVisitor = new MethodDeclarationVisitor(
				astRoot, null, unit, null);
		
		projectMarkerMap = Plugin.getDefault().getMarkerIndex(project);

		if (projectMarkerMap == null) {
			projectMarkerMap = new HashMap<ICompilationUnit, Map<MethodDeclaration, ArrayList<IMarker>>>();
			return false;
		}
	   projectMarkerMap.remove(unit);
	    //System.out.println("projectMarkerMap.get(unit) "+projectMarkerMap.get(unit));
	    try {
	    	System.out.println("I am insde try");
	    	IFile file = (IFile) unit.getUnderlyingResource();
	    	IMarker[] markers = file.findMarkers(
			PluginConstants.MARKER_INPUT_VALIDATION, false,
			IResource.DEPTH_ONE);
	    	
			//IResource resource = unit.getUnderlyingResource();
			//IMarker[] markers = resource.findMarkers("edu.uncc.sis.aside.AsideMarker", false, IResource.DEPTH_ONE);
			for(IMarker marker :markers){
				int mstart = marker.getAttribute(IMarker.CHAR_START, -1);
				//int mend = marker.getAttribute(IMarker.CHAR_END, -1);
				int mlinenumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
				System.out.println("deleting marker in onescan in start is" + mstart+" line number is "+mlinenumber);
				marker.delete();
				
				
			}
			markers = file.findMarkers(
					PluginConstants.MARKER_MULTI_OUTPUT_ENCODING, false,
					IResource.DEPTH_ONE);
			
			for(IMarker marker :markers){
				int mstart = marker.getAttribute(IMarker.CHAR_START, -1);
				//int mend = marker.getAttribute(IMarker.CHAR_END, -1);
				int mlinenumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
				System.out.println("deleting marker in onescan in start is" + mstart+" line number is "+mlinenumber);
				marker.delete();
				
			}
			//System.out.println("Markers in " + unit.getElementName() + " is " + markers.length);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	    
	    MarkerIndexForICompilationUnit = declarationVisitor.process();    
	    
		projectMarkerMap.put(unit, MarkerIndexForICompilationUnit);
		
		date = new Date();
		logger.info(dateFormat.format(date) + " " + Plugin.getUserId() + " . " + Plugin.PLUGIN_NAME + " finished re-inspecting java file: <<" + unit.getElementName() + ">>");

        return true;
	}


}

