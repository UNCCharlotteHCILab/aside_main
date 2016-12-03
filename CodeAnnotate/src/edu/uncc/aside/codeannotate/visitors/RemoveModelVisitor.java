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
 * @author Mahmoud Mohammadi ( mmoham12 at uncc dot edu)
 */
public class RemoveModelVisitor implements IModelVisitor {
	
	private static RemoveModelVisitor instance;
	private RemoveModelVisitor(){
		
	}
	
public static RemoveModelVisitor getInstance(){
	if(instance == null)
		instance = new RemoveModelVisitor();
	return instance;
}
	
	@Override
	public void visit(ModelCollector modelCollector) {
		
		modelCollector.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		modelCollector.removePropertyChangeListener(ModelRegistry.getInstance());

	}

	@Override
	public void visit(Path path) {
		path.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		path.removePropertyChangeListener(ModelRegistry.getInstance());

	}

	@Override
	public void visit(AccessControlPoint point) {
		point.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		point.removePropertyChangeListener(ModelRegistry.getInstance());
	}

	@Override
	//MM
	public void visit(InputValidationPoint point) {
		
		point.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		point.removePropertyChangeListener(ModelRegistry.getInstance());
		
	}

	@Override
	public void visit(OutputEncodingPoint point) {
		
		point.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		point.removePropertyChangeListener(ModelRegistry.getInstance());
		
	}

	@Override
	public void visit(SQLInjectionPoint point) {
		
		point.removePropertyChangeListener(Plugin.getDefault().getAnnotationView());
		point.removePropertyChangeListener(ModelRegistry.getInstance());
		
	}

}
