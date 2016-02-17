package edu.uncc.sis.aside.views;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * This class demonstrates how to create your own dialog classes. It allows users
 * to input a String
 */
public class UserIDDialog extends Dialog {
  private String message;
  private String input;
  private Shell shell;
  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   */
  public UserIDDialog(Shell parent) {
    // Pass the default styles here
    this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
  }

  /**
   * InputDialog constructor
   * 
   * @param parent the parent
   * @param style the style
   */
  public UserIDDialog(Shell parent, int style) {
    // Let users override the default styles
	
    super(parent, style);
    this.shell = parent;
    setText("ASIDE Usage Logs Submission");
  }

  /**
   * Opens the dialog and returns the input
   * 
   * @return String
   */
  public void showConsentForm() {
    // Create the dialog window
 //   Shell shell = new Shell(getParent(), getStyle());
    shell.setText(getText());
    createContents(shell);
  //  shell.pack();
    shell.open();
    Display display = getParent().getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    
  }

  /**
   * Creates the dialog's contents
   * 
   * @param shell the dialog window
   */
  private void createContents(final Shell shell) {
	FillLayout fillLayout = new FillLayout();
	fillLayout.type = SWT.VERTICAL;
    shell.setLayout(new GridLayout(2, true));
    
   	
   // shell.setSize(6, 6);
    // Show the message
    final String asideLink = "http://hci.uncc.edu/tomcat/ASIDE/ASIDEoverview.jsp";
    String msg = "Welcome to ASIDE! \n\nASIDE is a research tool for detecting security vulnerabilities in Java Code, while you program. For more information on ASIDE or this research, please see [http://hci.uncc.edu/tomcat/ASIDE/ASIDEoverview.jsp] or contact Michael Whitney at ASIDEplugin@gmail.com. \n\nThis tool is currently being used as part of a research study. If you are a part of that study, the tool will automatically submit logs of your interaction back to the research team. These logs will only contain information about how you used ASIDE, if you choose to use it but not about your code. \n\nIf you do not want your data to be part of this research, please contact Michael Whitney at ASIDEplugin@gmail.com and let him know to remove your id from data collection.\n";
    final StyledText styledText = new StyledText(shell, SWT.WRAP | SWT.BORDER);
	styledText.setText(msg);
	//styledText.setLineIndent(0, 1, 50);
	//styledText.setLineAlignment(6, 1, SWT.LEFT);
	//styledText.setLineJustify(6, 1, true);
	styledText.setEditable(false);
	GridData gridLayoutData = new GridData(GridData.FILL_BOTH);
	gridLayoutData.horizontalSpan = 2;
	styledText.setLayoutData(gridLayoutData);
	
	StyleRange style = new StyleRange();
	style.underline = true;
	style.underlineStyle = SWT.UNDERLINE_LINK;
	
	int[] ranges = {msg.indexOf(asideLink), asideLink.length()}; 
	StyleRange[] styles = {style};
	styledText.setStyleRanges(ranges, styles);
	
	styledText.addListener(SWT.MouseDown, new Listener() {
		public void handleEvent(Event event) {
				try {
					int offset = styledText.getOffsetAtLocation(new Point(event.x, event.y));
					StyleRange style = styledText.getStyleRangeAtOffset(offset);
					if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
						System.out.println("Click on a Link");
						String url = asideLink;
						Runtime rt = Runtime.getRuntime();
						try {
							String os = System.getProperty("os.name").toLowerCase();
							 if (os.indexOf( "win" ) >= 0) {
								 
							        // this doesn't support showing urls in the form of "page.html#nameLink" 
							        rt.exec( "rundll32 url.dll,FileProtocolHandler " + url);
						 
							    } else if (os.indexOf( "mac" ) >= 0) {
						 
							        rt.exec( "open " + url);
						 
						            } else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {
						 
							        // Do a best guess on unix until we get a platform independent way
							        // Build a list of browsers to try, in this order.
							        String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
							       			             "netscape","opera","links","lynx"};
						 
							        // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
							        StringBuffer cmd = new StringBuffer();
							        for (int i=0; i<browsers.length; i++)
							            cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + url + "\" ");
						 
							        rt.exec(new String[] { "sh", "-c", cmd.toString() });
						 
						           } else {
						                return;
						           }
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (IllegalArgumentException e) {
					// no character under event.x, event.y
				}
				
			
		}
	});
	//styledText.setForegroundColor(Color.darkGray);
	
    /*String msg0 = "                             Welcome to ASIDE!               ";
    Label label0 = new Label(shell, SWT.NONE);
    label0.setText(msg0);
    GridData data0 = new GridData();
    data0.horizontalSpan = 2;
    label0.setLayoutData(data0);
    
    String msg1 = "ASIDE is a research tool for detecting security vulnerabilities in Java Code, while you program. For more information on ASIDE or this research, please see [URL] or contact Michael Whitney at mwhitne6@uncc.edu. This tool is currently being used as part of a research study. If you are a part of that study, the tool will automatically submit logs of your interaction back to the research team. These logs will only contain information about how you used ASIDE, if you choose to use it. If you agree to submit your ASIDE usage logs, please enter your 49er id below and click I Agree. If you do not wish to submit your usage logs, click Cancel. You will still be able to use ASIDE, but your logs will not be submitted.";
    Label label1 = new Label(shell, SWT.NONE);
    label1.setText(msg1);
    GridData data1 =  new GridData(GridData.FILL, GridData.BEGINNING, true,
            false, 2, 1);
    data1.horizontalSpan = 2;
    label1.setLayoutData(data1);*/
    
    /*String msg111 = "while you program. For more information on ASIDE or this research,";
    Label label111 = new Label(shell, SWT.NONE);
    label111.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);
    
    String msg112 = "This tool is currently being used as part of a research study. If ";
    Label label112 = new Label(shell, SWT.NONE);
    label112.setText(msg112);
    GridData data112 = new GridData();
    data112.horizontalSpan = 2;
    label112.setLayoutData(data112);
    
    String msg113 = "you are a part of that study, the tool will automatically submit logs";
    Label label113 = new Label(shell, SWT.NONE);
    label113.setText(msg113);
    GridData data113 = new GridData();
    data113.horizontalSpan = 2;
    label113.setLayoutData(data113);
    
    String msg114 = "of your interaction back to the research team. These logs will only ";
    Label label114 = new Label(shell, SWT.NONE);
    label114.setText(msg114);
    GridData data114 = new GridData();
    data114.horizontalSpan = 2;
    label114.setLayoutData(data114);*/
    
  /*  "contain information about how you used ASIDE, if you choose to use it.";
    Label label111 = new Label(shell, SWT.NONE);
    label1.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);
    
    "If you agree to submit your ASIDE usage logs, please enter your 49er id";
    Label label111 = new Label(shell, SWT.NONE);
    label1.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);
    
    "below and click I Agree. If you do not wish to submit your usage logs, ";
    Label label111 = new Label(shell, SWT.NONE);
    label1.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);
    "click Cancel. You will still be able to use ASIDE, but your logs will ";
    Label label111 = new Label(shell, SWT.NONE);
    label1.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);
    "not be submitted.";
    Label label111 = new Label(shell, SWT.NONE);
    label1.setText(msg111);
    GridData data111 = new GridData();
    data111.horizontalSpan = 2;
    label111.setLayoutData(data111);*/
    
   
    
    
   /* 
    final Label label = new Label(shell, SWT.NONE);
    label.setText(message);
    GridData data = new GridData(GridData.BEGINNING);
    data.horizontalSpan = 2;
    label.setLayoutData(data);
    
    // Display the input box
    final Text text = new Text(shell, SWT.BORDER);
    data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalSpan = 2;
    text.setLayoutData(data);

   
 
    final Label label4 = new Label(shell, SWT.NONE);
   // label4.setText("");
    GridData data4 = new GridData(GridData.FILL_HORIZONTAL);
    data4.horizontalSpan = 2;
    label4.setLayoutData(data4);*/
    
    // Create the OK button and add a handler
    // so that pressing it will set input
    // to the entered value
   
    // Create the cancel button and add a handler
    // so that pressing it will set input to null
    Button close = new Button(shell, SWT.PUSH);
    close.setText("Close");
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    close.setLayoutData(data);
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });

    shell.setDefaultButton(close);
   // shell.pack();
   // shell.open();
  }

}
     
