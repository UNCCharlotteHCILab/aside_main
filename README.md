ESIDE
=============

Description:

This Branch is ESIDE. This is the educational version of ASIDE.

Before Installing: 

Please download a compatible version of Eclipse. ESIDE has been tested and works on Windows 7, 8, and 10 in the below versions of Eclipse

Eclipse Neon for Java EE Developers
Eclipse Mars for Java EE Developers
Eclipse Luna for Java EE Developers

Eclipse Neon for Java EE Developers is recommended. At the time of this writing, it can be downloaded at the below url

http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon1rc3

Installation Instructions:

1. Ensure you have followed the "Before Installing" section to make sure you have a compatible version of Eclipse
2. Download and extract the contents of ESIDE zip file into the "plugins" directory of eclipse
3. Restart Eclipse. Congratulations, ESIDE is installed.




Installation Instructions (for PhD students and researchers):

1. Ensure you have followed the "Before Installing" section to make sure you have a compatible version of Eclipse
2. Download code or clone repository. If downloaded, extract the zip file.
3. Import the "CodeAnnotate" folder into eclipse as an "existing project." It will contain errors at this step.
4. Import the "edu.uncc.sis.aside.logging" folder into eclipse as an "existing project." Only one error "An Api baseline has not been set for the current workspace" should now be showing. If more errors are showing, please see the troubleshooting section. 
5. In Eclipse, navigate to Windows -> Preferences -> Plug-in Development -> API Baselines
6. Under Options and "find Missing API baseline" -> If Error is selected change it to Warning or Ignore -> Apply. All errors should now be resolved. If some are still showing, please see the troubleshooting section.
7. Right click the project and run as "eclipse application. "Another eclipse window will appear. This is a version of eclipse containing the running plugin. 
8. Import the desired project (eg. gold rush, iTrust, etc)
9. In this eclipse window, on the left side (the project explorer window) right click your imported project. This is the code you  wish to run ESIDE on. Once this is done, a menu should appear.
10. Left click the option "Turn ESIDE On/Off." It should be about three fourths of the way down the menu.
11. After a few seconds, "errors" should appear in the project. When the files containing errors are opened, they will be marked with "red devil" icons, instead of Eclipse's usual icon. These are vulnerability warnings produced by ESIDE. Note that they do not stop compilation.
12. Congratulations, ESIDE is now up and running. Please resolve each vulnerability as you see fit.


Troubleshooting:

Problem: Eclipse shows error "An Api baseline has not been set for the current workspace"
Solution: This result is expected. Please see the installation instructions above for resolution information

Problem: Eclipse shows about 100 "Java Problems" such as "abstract vm not resolved" and org.eclipse.jdt.launching cannot be resolved
Solution: First, ensure that you are using a supported version of eclipse for "Java EE Developers." These include Neon, Mars, and Luna for Java EE Developers. If you are using a version of eclipse for only "Java Developers," you will not have the necessary eclipse addons. Also note that these errors sometimes occur on Eclipse Luna for Jave EE Developers and always occurs on prior versions. The best course of action is to upgrade to Eclipse Mars or newer (Eclipse Neon recommended). However, if you are using Eclipse Luna and wish to continue doing so, please delete the project from your workspace completely (also delete from disk) and try downloading again. Please also ensure that you are using Windows 7, 8, or 10.



Optional: Installing on Eclipse for Java Developers (Not EE)

If you already have eclipse Neon, Mars, or Luna for Java Developers (with no EE) installed, we recommend that you download a supported version of eclipse for "Java EE Developers." Multiple Eclipse installations can exist on one device without any difficulties, provided that they have different workspaces. However, if you wish to use your existing version, you may be able to do so, but will need to install the Eclipse plugin development environment (pde). To do this, please click "Help" -> "Install New Software" -> choose "All Available Sites" search for "PDE" and install it. It may take several minutes for the entry to appear. If any errors occur after installing and restarting Eclipse, please install an alternative version of Eclipse with EE


-Tyler W Thomas

