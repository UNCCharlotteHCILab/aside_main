package edu.uncc.aside.codeannotate.visitors;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.models.ModelRegistry;
import edu.uncc.aside.codeannotate.models.Path;
import edu.uncc.aside.codeannotate.models.PathCollector;
import edu.uncc.aside.codeannotate.models.Point;
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
	public void visit(PathCollector pathCollector) {
		pathCollector.addPropertyChangeListener(ModelRegistry.getInstance());
		pathCollector.addPropertyChangeListener(Plugin.getDefault()
				.getAnnotationView());
		
	}

	@Override
	public void visit(Path path) {
		path.addPropertyChangeListener(ModelRegistry.getInstance());

		path.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		
	}

	@Override
	public void visit(Point point) {
		point.addPropertyChangeListener(ModelRegistry.getInstance());
		point.addPropertyChangeListener(Plugin.getDefault().getAnnotationView());
		

	}

}
