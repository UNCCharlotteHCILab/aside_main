package edu.uncc.aside.codeannotate.presentations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.OpenAndLinkWithEditorHelper;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.aside.codeannotate.models.InputValidationPoint;
//import edu.uncc.aside.codeannotate.models.InputValidationsCollector;
import edu.uncc.aside.codeannotate.models.Model;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.models.Point;

/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 * @authod Mahmoud Mohammadi ( mmoham12 at uncc dot edu)
 * 
 */

//This class manages and shows the ASIDE view in Eclipse 
public class AnnotationView extends ViewPart implements PropertyChangeListener {

	private TreeViewer viewer;
	private StyledText styledText;
	private ITreeContentProvider contentProvider;
	private ILabelProvider labelProvider;

	private ISelectionListener listener;

	private OpenAction fOpenAction;
	private OpenAndLinkWithEditorHelper fOpenAndLinkWithEditorHelper;

	public AnnotationView() {
		super();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		fOpenAction = new OpenAction(site);
	}

	@Override
	public void createPartControl(Composite parent) {

		SashForm form = new SashForm(parent, SWT.HORIZONTAL);
		form.setLayout(new FillLayout());

		Composite leftTree = new Composite(form, SWT.BORDER_DOT);
		leftTree.setLayout(new FillLayout());

		GridData layoutData = new GridData(GridData.FILL_BOTH);

		viewer = new TreeViewer(leftTree);

		//
		contentProvider = new AnnotationContentProvider();
		
		viewer.setUseHashlookup(true);
		
		//MM very important
		viewer.setContentProvider(contentProvider);
		
		//
		labelProvider = new AnnotationTreeLabelProvider();
		
		viewer.setLabelProvider(labelProvider);
		
		//
		getSite().setSelectionProvider(viewer);
		
		listener = new ProjectSelectionListener(viewer);
		
		getSite().getPage().addSelectionListener(listener);

		// the invocation of setInput() method will trigger the execution of the
		// getElements() method
		viewer.setInput(getInitialInput());
		viewer.expandAll();

		layoutData.grabExcessVerticalSpace = true;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		
		viewer.getControl().setLayoutData(layoutData);

		//MM When the user selects an item in the Tree view selection changes event gets called  to show the proper text
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				
				if (selection == null || selection.isEmpty()) {
					styledText.setText("");
				} else {
					IStructuredSelection sSelection = (IStructuredSelection) selection;
					
					Model model = (Model) sSelection.getFirstElement();
					
					styledText.setText(model.toString());
				}
			}

		});
//MM Text Shown in the right panel
		Composite rightText = new Composite(form, SWT.BORDER_DOT);
		rightText.setLayout(new FillLayout());

		styledText = new StyledText(rightText, SWT.BORDER | SWT.WRAP);
		styledText.setText("");
		styledText.setEditable(false);
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		styledText.setLayoutData(layoutData);
		
		styledText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				styledText.setTopIndex(styledText.getLineCount() - 1);
			}
		});

		fOpenAndLinkWithEditorHelper = new OpenAndLinkWithEditorHelper(viewer) {
			protected void activate(ISelection selection) {

			}

			protected void linkToEditor(ISelection selection) {
				try {
					if (selection instanceof ITreeSelection) {
						ITreeSelection tSelection = (ITreeSelection) selection;
						TreePath[] paths = tSelection.getPaths();
						Object elementOfInterest = paths[0].getFirstSegment();
						
						if (elementOfInterest instanceof Path) {
							Path pathOfInterest = (Path) elementOfInterest;
							AccessControlPoint sink = pathOfInterest.getSensitiveOperation();
							
							linkNodeToEditor(sink);
							

						} else if (elementOfInterest instanceof Point) {
							
							linkNodeToEditor(elementOfInterest);

							
						} else {
							// do nothing
							System.err.println(elementOfInterest.getClass()
									.toString());
						}
						
					}

				} catch (PartInitException ex) {
					ex.printStackTrace();
				}

			}
/**
 * @param elementOfInterest
 * @throws PartInitException
 * @throws NumberFormatException
 */
private void linkNodeToEditor(Object elementOfInterest)
		throws PartInitException, NumberFormatException {
	// TODO this does not work as expected
	Point point = (Point) elementOfInterest;
	
	IFile file = (IFile) point.getResource();
	IEditorPart part = EditorUtility.openInEditor(file);
	
	
	if (part == null)
		return;
	int startOffset = point.getStartOffset();
	
	EditorUtility.revealInEditor(part, startOffset,
			point.getNode().getLength());
}

			protected void open(ISelection selection, boolean activate) {
				if (selection instanceof IStructuredSelection) {
					fOpenAction.run((IStructuredSelection) selection);
				} else {
					System.err.println("The selection is like "
							+ selection.getClass());
				}
			}
		};

		fOpenAndLinkWithEditorHelper.setLinkWithEditor(true);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private Model getInitialInput() {
		return null;
	}

	@Override
	public void dispose() {
		getSite().getPage().removeSelectionListener(listener);
		super.dispose();
	}

	class ProjectSelectionListener implements ISelectionListener {

		public ProjectSelectionListener(TreeViewer viewer) {

		}

		@Override
		public void selectionChanged(IWorkbenchPart sourcePart,
				ISelection selection) {
			//MM When something other than View selected and View looses focus
			if (sourcePart != AnnotationView.this) {
				showSelection(sourcePart, selection);
			}
		}

		public void showSelection(IWorkbenchPart sourcePart,
				ISelection selection) {
			
			setContentDescription(sourcePart.getTitle() + " ("
					+ selection.getClass().getName() + ")");
			
			if (selection instanceof IStructuredSelection) {
				
				IStructuredSelection sSelection = (IStructuredSelection) selection;
				
				if (sSelection.isEmpty())
					return;
				
				Object fElement = sSelection.getFirstElement();
				
				if (fElement != null && fElement instanceof IResource) {
					
					IResource element = (IResource) fElement;
					
					IProject selectProject = element.getProject();
					
					setContentDescription(sourcePart.getTitle() + " ("
							+ selection.getClass().getName() + "): "
							+ selectProject.getName());
					
					ModelCollector root = ModelRegistry
							.getPathCollectorForProject(selectProject);
					
					if (root == null) {
						root = new ModelCollector(selectProject);
					}
					//MM triggers getElement of ContenetProvider					
					viewer.setInput(root);
					
				//	InputValidationsCollector root2 = ModelRegistry
					//		.getInputValidationsCollectorForProject(selectProject);
					
				//	viewer.setInput(root2);
					
				}
			} else if (selection instanceof ITextSelection) {
				ITextSelection ts = (ITextSelection) selection;
			}
		}

	}

	//MM It is Called when the one of the subclasses of the Model classes implementing the PropertyListern
	// are called such as adding markers or annotations. It is needed to be modified to handle different vulnerabilities not only path and pathcollector classes of the annotations.
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();

		
		if (source instanceof ModelCollector) {

			ModelCollector collector = (ModelCollector) source;
			
			final ModelCollector _collector = ModelRegistry
					.getPathCollectorForProject(collector.getProject());
			
			Plugin.getDefault().getWorkbench().getDisplay()
					.asyncExec(new Runnable() {

						@Override
						public void run() {
							//MM to refresh the part of the tree start at _collector.
							viewer.refresh(_collector);
						}
					});

		}

		if (source instanceof Path) {
			
			final Path path = (Path) source;
			ModelCollector parent = (ModelCollector) path.getParent();
			
			final ModelCollector collector = ModelRegistry
					.getPathCollectorForProject(parent.getProject());
			
			Plugin.getDefault().getWorkbench().getDisplay()
					.asyncExec(new Runnable() {

						@Override
						public void run() {
							Path _parent = path;
							for (Path kid : collector.getAllPaths()) {
								if (kid.equalsTo(path)) {
									_parent = kid;
									break;
								}
							}
//MM to refresh the part of the tree start at _parent.
							viewer.refresh(_parent);
						}
					});

		}
		if (source instanceof Point) {

			final Point point = (Point) source;
			ModelCollector parent = (ModelCollector) point.getParent();

			final ModelCollector collector = ModelRegistry
					.getPathCollectorForProject(parent.getProject());

			Plugin.getDefault().getWorkbench().getDisplay()
			.asyncExec(new Runnable() {

				@Override
				public void run() {
					Point _parent = point;
					for (InputValidationPoint kid : collector.getAllInputValidationPoints()) {
						if (kid.equalsTo(point)) {
							_parent = kid;
							break;
						}
					}
					//MM to refresh the part of the tree start at _parent.
					viewer.refresh(_parent);
				}
			});

		}

	}
}
