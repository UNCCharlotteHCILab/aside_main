package edu.uncc.sis.aside;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import edu.uncc.aside.utils.ConsentForm;
import edu.uncc.aside.utils.MakerManagement;
import edu.uncc.sis.aside.auxiliary.core.RunAnalysis;

/**
 * 
 * @author Jun Zhu (jzhu16 at uncc dot edu) <a href="http://www.uncc.edu/">UNC
 *         Charlotte</a>
 * 
 */
public class Old_AsideStartup implements IStartup {
	

	@Override
	public void earlyStartup() {
		
		/*// newly added
		String userIDFromSystem = System.getProperty("user.name");
				IPath stateLocation = Old_AsidePlugin.getDefault().getStateLocation();
				String fileName = stateLocation + "/"
						+ Old_AsidePlugin.getAsideUseridFile(); // might have to be updated
															// about "/"
				File userIdFile = new File(fileName);
				
				System.out.println("Old_AsidePlugin.getDefault().isAllowed() = "
						+ Old_AsidePlugin.getDefault().isAllowed());
				if (Old_AsidePlugin.isAllowed()) {
					if (userIdFile.exists()) {
						try {
							FileReader fr = new FileReader(userIdFile);
							BufferedReader br = new BufferedReader(fr);
							String userIdRead = br.readLine();
							System.out.println("userId read from the file = "
									+ userIdRead);
							Old_AsidePlugin.getDefault().setUserId(userIdRead);
							br.close();
							fr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						
					} else {
						// /////////////
						// pop up consent form asking for id and consent
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								ConsentForm.process();
							}
						});
						
						// System.out.println("userIdRead in Maunual =" + userIdRead);

						boolean created = false;
						try {
							created = userIdFile.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (created) {
							FileWriter fw = null;
							try {
								fw = new FileWriter(userIdFile);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(userIDFromSystem);
								bw.close();
								fw.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Old_AsidePlugin.getDefault().setUserId(userIDFromSystem);
						} else {
							System.err.println("UserId file is not created properly!");
						}
					}
					TestRunOnAllProjects testRunOnAllProjects = new TestRunOnAllProjects();
					testRunOnAllProjects.runOnAllProjects();

				}else{
					System.out.println("this user is not allowed");
				}
				*/
	}

}
