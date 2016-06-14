package edu.uncc.aside.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.asideInterface.InterfaceUtil;
import edu.uncc.sis.aside.AsidePlugin;
import edu.uncc.sis.aside.auxiliary.core.TestRunOnAllProjects;
import edu.uncc.sis.aside.constants.PluginConstants;

public class MakerManagement {
	private static final Logger logger = Plugin.getLogManager().getLogger(
			TestRunOnAllProjects.class.getName());
	
	public static void deleteWorkspaceMarkers(String markerType){
		IJavaProject javaProject ;
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		

		for(IProject project : allProjects){
			if(project.isOpen() && !project.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
			{
				javaProject= null;
				javaProject = JavaCore.create(project);  

				if(javaProject == null )				
					continue;

				deleteProjectMarkers(javaProject, markerType);
			}
		}

	}
	
	public static void deleteProjectMarkers(IJavaProject project, String markerType){
		
	    if(project != null){
	    	try{

	    		project.getCorrespondingResource().deleteMarkers( //PluginConstants.ASIDE_MARKER_TYPE
	    				markerType, true,
	    				IResource.DEPTH_INFINITE);

	    	}catch(Exception e){
	    		System.err.println("project.get1");
	    		e.printStackTrace();
	    	}
	    }
	
		try {
			
		
		for (IPackageFragment fragment : project.getPackageFragments()) {
			
			for (ICompilationUnit unit : fragment.getCompilationUnits()) {
				
				deleteCompilationUnitMarkers(unit, markerType);
			}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			System.err.println("project.get2");
			//e.printStackTrace();
			return;
		}
	}
	
	public static void deleteCompilationUnitMarkers(ICompilationUnit unit, String markerType){
		try {
			IResource resource = unit.getUnderlyingResource();
			//markerType="edu.uncc.sis.aside.AsideMarker"
			IMarker[] markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
			for(IMarker marker :markers){
				marker.delete();
			}
			markers = resource.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
			
			System.out.println("Markers in " + unit.getElementName() + " is " + markers.length);
		} catch (JavaModelException e) {
			System.err.println("project.get3");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			System.err.println("project.get4");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}

	public static void saveMarkersToFile(String markerType,String fileName){

	for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
		
			if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
			{
				try {
					IPath location = p.getLocation();
					
					FileWriter writer = new FileWriter(location.append(fileName).toString(), false);

					for( IMarker marker : p.findMarkers(markerType, true, IResource.DEPTH_INFINITE) )
					{
						writer.append(marker.getType());
						writer.append(',');
						writer.append(marker.getResource().getProjectRelativePath().toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.CHAR_START).toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.CHAR_END).toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.LINE_NUMBER).toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.SEVERITY).toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.PRIORITY).toString());
						
						writer.append(',');
						writer.append(marker.getAttribute(IMarker.MESSAGE).toString());	
						
						writer.append(',');
						writer.append( marker.getAttribute("creationTime").toString());	
						
						writer.append(',');
						writer.append( marker.getAttribute("userID").toString());
						
						writer.append('\n');
					}
					writer.flush();
					writer.close();
					

				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}//if   
		}
	}

	public static void restoreMarkersFromFile(String fileName)
	{
		String line;
		String[] fields ;
		IMarker marker;
		IPath location;
		FileReader reader ;
		BufferedReader bufferedReader ;
	
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
			
			if(p.isOpen() && !p.getName().equalsIgnoreCase("RemoteSystemsTempFiles"))
			{
				try {
					location = p.getLocation();
					
					reader = new FileReader(location.append(fileName).toString());
					
					bufferedReader = new BufferedReader(reader);
										
					while ((line = bufferedReader.readLine()) != null) {

						fields = line.trim().split(",");
						if( (fields== null) || (fields.length < 10)) continue;
						//ResourcesPlugin.getWorkspace().getRoot().get
						IJavaProject javaProject = JavaCore.create(p);
						IResource resource;
						String name;
						
						for (IPackageFragment fragment : javaProject.getPackageFragments()) {							
							for (ICompilationUnit unit : fragment.getCompilationUnits()) {
								
								name= unit.getResource().getProjectRelativePath().toString();
								
								if(fields[1].equalsIgnoreCase(name))
								{
									InterfaceUtil.createMarker(unit.getResource(),fields[0] ,
											Integer.parseInt(fields[2]) //Start
											, Integer.parseInt(fields[3]) //End
											,Integer.parseInt(fields[4]) //Line Number
											,Integer.parseInt(fields[5]) //Severity
											,Integer.parseInt(fields[6]) //Priority
											, fields[7] //message
											,fields[8]  //Creation Time
											,fields[9]  //UserId
													); //message
									/*
									resource = unit.getResource();
									marker=resource.createMarker(fields[0]);
									marker.setAttribute(IMarker.CHAR_START, fields[2]);
									marker.setAttribute(IMarker.CHAR_END, fields[3]);
									marker.setAttribute(IMarker.MESSAGE, fields[4]);
									*/
								}


							}
						}
					}//while
					bufferedReader.close();
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			}//if   
		}//for projects

	}//function

}
