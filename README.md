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
2. Download code or clone repository. If downloaded, extract the zip file.
3. Import the "CodeAnnotate" folder into eclipse as an "existing project." It will contain errors at this step.
4. Import the "edu.uncc.sis.aside.logging" folder into eclipse as an "existing project." Only one error "An Api baseline has not been set for the current workspace" should now be showing. If more errors are showing, please see the troubleshooting section. 
5. In Eclipse, navigate to Windows -> Preferences -> Plug-in Development -> API Baselines
6. Under Options and "find Missing API baseline" -> If Error is selected change it to Warning or Ignore -> Apply. All errors should now be resolved. If some are still showing, please see the troubleshooting section.


5. Install the eclipse pde (plugin development environment)
6. Install the pdt (php development environment). The code uses the dynamic language toolkit contained inside
7. The errors should now be gone. Right click the project and run as "eclipse application"
Another eclipse window will appear. This is a version of eclipse containing the running plugin. In this eclipse window, import the desired project (eg. gold rush, iTrust, etc)
Resolve any errors in the imported project
Right click the project and choose "Code Annotate." This will run aside on the project. Feedback will be displayed in the other (original) eclipse window.
Navigate to the annotation requests and have fun :)


Troubleshooting:

Problem: Eclipse shows error "An Api baseline has not been set for the current workspace"
Solution: This result is expected. Please see the installation instructions above for resolution information

Problem: Eclipse shows about 100 "Java Problems" such as "abstract vm not resolved" and org.eclipse.jdt.launching cannot be resolved
Solution: These errors sometimes occur on Eclipse Luna and always occurs on prior versions. The best course of action is to upgrade to Eclipse Mars or newer (Eclipse Neon recommended). However, if you are using Eclipse Luna and wish to continue doing so, please delete the project from your workspace completely (also delete from disk) and try downloading again. Please also ensure that you are using Windows 7, 8, or 10.


-Tyler W Thomas

