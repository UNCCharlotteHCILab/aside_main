package edu.uncc.aside.codeannotate.visitors;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.models.InputValidationPoint;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.OutputEncodingPoint;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.ModelCollector;
import edu.uncc.aside.codeannotate.models.AccessControlPoint;
import edu.uncc.aside.codeannotate.models.SQLInjectionPoint;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class AddModelVisitor implements IModelVisitor {

	private static AddModelVisitor instance;

	private AddModelVisitor() {

	}

	public static AddModelVisitor getInstance() {
		if (instance == null)
			instance = new AddModelVisitor();
		return instance;
	}

	@Override
	public void visit(ModelCollector modelCollector) {
		
		modelCollector.addPropertyChangeListener(ModelRegistry.getInstance());
		
		modelCollector.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		
	}

	@Override
	public void visit(Path path) {
		path.addPropertyChangeListener(ModelRegistry.getInstance());

		path.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		
	}

	@Override
	public void visit(AccessControlPoint accessControlPoint) {
		
		accessControlPoint.addPropertyChangeListener(ModelRegistry.getInstance());
		
		accessControlPoint.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		

	}
	
	@Override
	public void visit(InputValidationPoint point) {
		
	//	point.addPropertyChangeListener(ModelRegistry.getInstance());
		
		point.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		

	}

	@Override
	public void visit(OutputEncodingPoint point) {
		point.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		
	}

	@Override
	public void visit(SQLInjectionPoint point) {
		point.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		
	}


}
