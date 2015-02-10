package edu.uncc.aside.codeannotate.jobs;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.Utils;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
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

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		final PathCollector pathCollector = ModelRegistry
				.getPathCollectorForProject(projectOfInterest);

		if (pathCollector == null)
			return Status.CANCEL_STATUS;

		List<Path> paths = pathCollector.getAllPaths();

		try {
			monitor.beginTask(
					"Collecting paths that are destroyed already and removing UI parts that are associated with them",
					paths.size());

			synchronized (paths) {

				Iterator<Path> it = paths.iterator();
				while (it.hasNext()) {
					Path path = it.next();
					/*
					 * accessor from b4 editing. the challenge here is to find a
					 * way to see which node has been modified and which hasn't
					 */
					Point accessor = path.getAccessor();
					IResource resource = accessor.getResource();
					ASTNode originalNode = accessor.getNode();
					if (sourceOfInterest == null || resource == null) {
						return Status.OK_STATUS;
					} else if (sourceOfInterest.equals(resource)) {
						int start = -1, end = -1;
						Object obj_start = originalNode
								.getProperty(Plugin.ASIDE_NODE_PROP_START);
						if (obj_start == null) {
							start = originalNode.getStartPosition();
							originalNode.setProperty(
									Plugin.ASIDE_NODE_PROP_START, start);
						} else {
							start = Integer.parseInt(obj_start.toString());
						}
						Object obj_end = originalNode
								.getProperty(Plugin.ASIDE_NODE_PROP_END);
						if (obj_end == null) {
							end = start + originalNode.getLength();
							originalNode.setProperty(
									Plugin.ASIDE_NODE_PROP_END, end);
						} else {
							end = Integer.parseInt(obj_end.toString());
						}

						if (end < lineChangedStartOffset) {
							// the node is intact
						} else if (start >= nextStartOffset) {
							if (modificationType.equals("INSERTION")) {
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_START, start
												+ text.length());
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_END,
										end + text.length());
							} else if (modificationType.equals("REMOVAL")) {
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_START, start
												- length);
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_END, end
												- length);
							} else if (modificationType.equals("REPLACEMENT")) {
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_START, start
												+ text.length() - length);
								originalNode.setProperty(
										Plugin.ASIDE_NODE_PROP_END,
										end + text.length() - length);
							} else {
								// I don't know what to do for this case
							}

						} else {
							Utils.removeMarkersOnPath(path);
							it.remove();
						}

					}
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}
