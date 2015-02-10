package edu.uncc.aside.codeannotate.listeners;

import org.eclipse.jdt.core.BufferChangedEvent;
/**
 * 
 * @author Jing Xie (jxie2 at uncc dot edu)
 *
 */
public class Messenger {

	private BufferChangedEvent event;
	
	private static Messenger instance = null;
	
	private Messenger(){
		
	}
	
	public static Messenger getInstance(){
		if(instance == null)
			instance = new Messenger();
		
		return instance;
	}
	
	public void setDocumentEvent(BufferChangedEvent _event){
		event = _event;
	}
	
	public BufferChangedEvent getDocumentEvent(){
		return event;
	}
}
