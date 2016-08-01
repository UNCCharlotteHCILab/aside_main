package edu.uncc.aside.codeannotate.listeners;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
import org.eclipse.jdt.core.BufferChangedEvent;
import org.eclipse.jdt.core.IBufferChangedListener;



/**
 * Coordinate with {@link Messenger} to communicate information with {@link CodeAnnotateElementChangeListener}
 * @author Jing Xie (jxie2@uncc.edu)
 *
 */
public class DocumentEditListener implements IBufferChangedListener {
	
	public DocumentEditListener(){
		super();
	}

	@Override
	public void bufferChanged(BufferChangedEvent event) {
	//MM Send event to the Messenger so that other consumers( such as ElementChangeListener) can get it from Messenger
		Messenger.getInstance().setDocumentEvent(event);
	}
	
	
}
