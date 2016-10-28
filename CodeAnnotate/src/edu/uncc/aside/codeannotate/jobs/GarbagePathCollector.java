package edu.uncc.aside.codeannotate.jobs;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.utils.InterfaceUtil;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @author Mahmoud Mohammadi ( mmoham12 at uncc dot edu)
 */
public class GarbagePathCollector extends Job {

	private IProject projectOfInterest;
	private ICompilationUnit unitOfInterest;

	private IResource sourceOfInterest;

	private int lineChangedStartOffset, nextStartOffset;

	private int offset, length;
	private String modificationType, text;

	public GarbagePathCollector(String name, IProject project,
			ICompilationUnit targetUnit, int startOffset, int nextStartOffset,
			String modificationType, int offset, int length, String text) {
		super(name);
		projectOfInterest = project;
		unitOfInterest = targetUnit;
		lineChangedStartOffset = startOffset;
		this.nextStartOffset = nextStartOffset;

		this.offset = offset;
		this.length = length;
		this.modificationType = modificationType;
		this.text = text;

		try {
			sourceOfInterest = unitOfInterest.getUnderlyingResource();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
/*
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		if (sourceOfInterest == null ) {
			return Status.OK_STATUS;
		}
			
		try {
			IMarker[] markers = sourceOfInterest.findMarkers(PluginConstants.MARKER_ROOT_TYPE, true, IResource.DEPTH_INFINITE);
			
			
			
			monitor.beginTask(
					"Collecting paths that are destroyed already and removing UI parts that are associated with them",
					markers.length);
			
			synchronized (markers) {
				for (IMarker marker : markers) {
					
					if ( ! ( marker.getType().equals(PluginConstants.MARKER_ANNOTATION_REQUEST)
						|| 
						marker.getType().equals(PluginConstants.MARKER_ANNOTATION_CHECKED)
							) )
						continue;
						
					int start = (Integer) marker.getAttribute(IMarker.CHAR_START);
					int end =(Integer) marker.getAttribute(IMarker.CHAR_END);
					
					if (end < lineChangedStartOffset) {
						// that  marker is intact
					} else if (start >= nextStartOffset) { //MM Update other markers' start and length offset
					
						if (modificationType.equals("INSERTION")) {
							
							marker.setAttribute(PluginConstants.ASIDE_NODE_PROP_START, start
											+ text.length());							
							marker.setAttribute(
									PluginConstants.ASIDE_NODE_PROP_END,
									end + text.length());
							
						} else if (modificationType.equals("REMOVAL")) {
							marker.setAttribute(
									PluginConstants.ASIDE_NODE_PROP_START, start
											- length);
							marker.setAttribute(
									PluginConstants.ASIDE_NODE_PROP_END, end
											- length);
						} else if (modificationType.equals("REPLACEMENT")) {
							marker.setAttribute(
									PluginConstants.ASIDE_NODE_PROP_START, start
											+ text.length() - length);
							marker.setAttribute(
									PluginConstants.ASIDE_NODE_PROP_END,
									end + text.length() - length);
						} else {
							// I don't know what to do for this case
						}
						

					} else {

						
					//	InterfaceUtil.removeAssociatedAnnotations(marker);
				    	
					//	InterfaceUtil.removeMarker(marker);
						
						
						
				    	//then find the corresponding marker and deal with it if no more annotations remain.
				    	if(InterfaceUtil.hasAnnotationsRemaining(markerIndex) == false)
				    	{
				    		InterfaceUtil.changeMarker(PluginConstants.MARKER_ANNOTATION_REQUEST, markerIndex, 0, 0);
				    	}
				    	InterfaceUtil.checkForVulnerabilities();
				    	
				    							
					}
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}	
					
				}//While
				
			}//Sync
			
			}//try
		 catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			monitor.done();
		}
		return Status.OK_STATUS;

	}
	*/
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {

		final ModelCollector modelCollector = ModelRegistry
				.getPathCollectorForProject(projectOfInterest);

		if (modelCollector == null)
			return Status.CANCEL_STATUS;

		List<Path> paths = modelCollector.getAllPaths();

		try {
			monitor.beginTask(
					"Collecting paths that are destroyed already and removing UI parts that are associated with them",
					paths.size());

			synchronized (paths) {

				Iterator<Path> it = paths.iterator();
				while (it.hasNext()) {
					Path path = it.next();
					//
					 // accessor from b4 editing. the challenge here is to find a
					 // way to see which node has been modified and which hasn't
					 
					AccessControlPoint sink = path.getSensitiveOperation();
					IResource resource = sink.getResource();
					ASTNode originalNode = sink.getNode();
					
					IMarker marker ;
					
					
					if (sourceOfInterest == null || resource == null) {
						return Status.OK_STATUS;
						
					} else if (sourceOfInterest.equals(resource)) { //MM Searching in the current Java SOurce Code
						int start = -1, end = -1;
						
						Object obj_start = originalNode
								.getProperty(PluginConstants.ASIDE_NODE_PROP_START);
						
						if (obj_start == null) {
							start = originalNode.getStartPosition();
							originalNode.setProperty(
									PluginConstants.ASIDE_NODE_PROP_START, start);
						} else {
							start = Integer.parseInt(obj_start.toString());
						}
						Object obj_end = originalNode
								.getProperty(PluginConstants.ASIDE_NODE_PROP_END);
						if (obj_end == null) {
							end = start + originalNode.getLength();
							originalNode.setProperty(
									PluginConstants.ASIDE_NODE_PROP_END, end);
						} else {
							end = Integer.parseInt(obj_end.toString());
						}

						if (end < lineChangedStartOffset) {
							// the node is intact
						} else if (start >= nextStartOffset) { //MM Update other markers' start and length offset
							if (modificationType.equals("INSERTION")) {
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_START, start
												+ text.length());
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_END,
										end + text.length());
							} else if (modificationType.equals("REMOVAL")) {
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_START, start
												- length);
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_END, end
												- length);
							} else if (modificationType.equals("REPLACEMENT")) {
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_START, start
												+ text.length() - length);
								originalNode.setProperty(
										PluginConstants.ASIDE_NODE_PROP_END,
										end + text.length() - length);
							} else {
								// I don't know what to do for this case
							}

						} else {
							Utils.removeMarkersOnPath(path);
							it.remove();
						}

					}//
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}//while 
			}//Sync
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
}
