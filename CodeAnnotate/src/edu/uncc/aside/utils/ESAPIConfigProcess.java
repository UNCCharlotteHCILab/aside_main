package edu.uncc.aside.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;

import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.sis.aside.AsidePlugin;

public class ESAPIConfigProcess{

	private IProject fProject;
	private IJavaProject javaProject;

	public ESAPIConfigProcess(String name, IProject project,
			IJavaProject javaProject) {
		this.fProject = project;
		this.javaProject = javaProject;
	}
	

	public void run() {

		/**
		 * Apparently, here's the assumption that the target project follows the
		 * dynamic web project structure defined by Eclipse
		 */
	
		final IFolder lib = fProject.getFolder(PluginConstants.PROJECT_LIB_PATH);
		if (!lib.exists()) {
			System.out.println("Cannot find: " + lib);
			return ;
		}

		Bundle bundle = Platform.getBundle(PluginConstants.PLUGIN_ID);
		Path internalPath = new Path(IPath.SEPARATOR + PluginConstants.ESAPI_CONFIG_DIR_NAME);
		URL fileURL = FileLocator.find(bundle, internalPath, null);
		if (fileURL == null) {
			System.out.println("cannot locate ESAPI directory");
			return;
		}
        //System.out.println("FileLocator.find(bundle, path, null)--fileURL=="+fileURL);
		InputStream is = null;
		try {

		//	URL localFileURL = FileLocator.toFileURL(fileURL);

		//	String sourcePath = localFileURL.getFile();
			File localURL = new File(FileLocator.toFileURL(fileURL).getFile());
			if (localURL.exists() && localURL.isDirectory()) {
				//Get list of ESAPI jar files
				File[] sourceFiles = localURL.listFiles();
				for (File file : sourceFiles) {
									
			//	for (int i = 0; i < sourceFiles.length; i++) {

				//	File file = sourceFiles[i];
					String fileName = file.getName();

					if (file.isFile()) {
						if (fileName.endsWith(".jar")) {
							is = new BufferedInputStream(new FileInputStream(
									file.getAbsolutePath()));
							IFile destination = fProject
									.getFile(IPath.SEPARATOR + PluginConstants.PROJECT_LIB_PATH
											+ IPath.SEPARATOR + fileName);
							
							if (!destination.exists()) {
								try {
									destination.create(is, false, null);
								} catch (CoreException e) {
									continue;
								}
							}
						}else if(fileName.equals("log4j.properties")){ //copy the log4j.properties file
							is = new BufferedInputStream(new FileInputStream(
									file.getAbsolutePath()));
							IFile destination = fProject
									.getFile(IPath.SEPARATOR + PluginConstants.PROJECT_WEBINF_PATH
											+ IPath.SEPARATOR + fileName);
							
							if (!destination.exists()) {
								try {
									destination.create(is, false, null);
								} catch (CoreException e) {
									continue;
								}
							}
						}
					} else if (file.isDirectory()) {
						if (fileName.equalsIgnoreCase("esapi")
								|| fileName.equalsIgnoreCase(".esapi")) {
							//IFolder destination = fProject.getFolder(fileName); // this one may need modification
							//added Mar. 2
							//IFolder tmp = fProject.getFolder(fileName);
							IFolder destination = fProject.getFolder(IPath.SEPARATOR + PluginConstants.PROJECT_WEBINF_PATH + IPath.SEPARATOR + fileName);
							String tmp = IPath.SEPARATOR + PluginConstants.PROJECT_WEBINF_PATH + IPath.SEPARATOR + fileName;
							//System.out.println("line 126 destination = " + destination + " tmp" + tmp);
							/////
							
							if (!destination.exists()) {
								try {
									destination.create(true, true, null);
								} catch (CoreException e) {
									continue;
								}
							}
							copyDirectory(file, tmp, is);
						}
					}
				}
			} else {
				System.out.println("file does not exist");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// setESAPIClasspathContainer(lib);
		setESAPIResourceLocation();
		return;
	}

	private void copyDirectory(File sourceDir, String basePath, InputStream is) throws CoreException,
			FileNotFoundException {
       // System.out.println("basePath " + basePath);
		File[] subFiles = sourceDir.listFiles();
		for (File subFile : subFiles) {

			String sub_basePath = basePath + IPath.SEPARATOR
					+ subFile.getName();
			if (subFile.isDirectory()) {

				IFolder destination = fProject.getFolder(sub_basePath);
				if (!destination.exists()) {
					try {
						destination.create(true, true, null);
					} catch (CoreException e) {
						continue;
					}
				}
				copyDirectory(subFile, sub_basePath, is);

			} else if (subFile.isFile()) {
				is = new BufferedInputStream(new FileInputStream(
						subFile.getAbsolutePath()));
				IFile destination = fProject.getFile(sub_basePath);
				if (!destination.exists()) {
					try {
						destination.create(is, false, null);
					} catch (CoreException e) {
						continue;
					}
				}
			}
		}
	}

	private void setESAPIClasspathContainer(final IFolder lib) {
		final IPath containerPath = new Path(PluginConstants.ASIDE_ESAPI_CONTAINER)
				.append(fProject.getFullPath());
		IClasspathContainer esapiContainer = new IClasspathContainer() {

			@Override
			public IClasspathEntry[] getClasspathEntries() {
				ArrayList<IClasspathEntry> entryList = new ArrayList<IClasspathEntry>();
				try {
					IResource[] members = lib.members();
					for (IResource resource : members) {
						if (IFile.class.isAssignableFrom(resource.getClass())) {
							if (resource.getName().endsWith(".jar")) {
								entryList.add(JavaCore.newLibraryEntry(
										new Path(resource.getFullPath()
												.toOSString()), null, new Path(
												"/")));
							}
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				// convert the list to an array and return it
				IClasspathEntry[] entryArray = new IClasspathEntry[entryList
						.size()];
				return entryList.toArray(entryArray);
			}

			@Override
			public String getDescription() {
				return "ESAPI Libraries";
			}

			@Override
			public int getKind() {
				return IClasspathEntry.CPE_CONTAINER;
			}

			@Override
			public IPath getPath() {
				return containerPath;
			}

			@Override
			public String toString() {
				return getDescription();
			}
		};

		try {
			JavaCore.setClasspathContainer(containerPath,
					new IJavaProject[] { javaProject },
					new IClasspathContainer[] { esapiContainer }, null);

			IClasspathEntry[] entries = javaProject.getRawClasspath();

			// check if the container is already on the path
			boolean hasEsapiContainer = false;

			for (int i = 0; i < entries.length; i++) {
				if (entries[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER
						&& entries[i].getPath().equals(containerPath)) {
					hasEsapiContainer = true;
				}
			}
			if (!hasEsapiContainer) {
				IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];

				System.arraycopy(entries, 0, newEntries, 0, entries.length);

				// add a new entry using the path to the container
				newEntries[entries.length] = JavaCore
						.newContainerEntry(esapiContainer.getPath());

				javaProject.setRawClasspath(newEntries, null);

			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

	}

	private void setESAPIResourceLocation() {
		URI locationUri = null;
		IFolder folder = fProject.getFolder(PluginConstants.PROJECT_WEBINF_PATH + IPath.SEPARATOR + "esapi"); //here may need changes

		if (folder.exists()) {
			//System.out.println("setESAPIResourceLocation class--folder.exists()="+locationUri);
			locationUri = folder.getRawLocationURI();
			//System.out.println("setESAPIResourceLocation class--older.getRawLocationURI="+locationUri);
		} else {
			folder = fProject.getFolder(".esapi");
			System.out.println("folder not exist!");
			
			if (folder.exists()) {
				locationUri = folder.getRawLocationURI();
				//System.out.println("setESAPIResourceLocation class-- fProject.getFolder .esapi exist, locationUri="+locationUri);
				
			}
		}

		if (locationUri == null){
			System.out.println("setESAPIResourceLocation class--locationUri= null");
			return;
		}
		//String path = ESAPI_VM_ARG + "=\"" + locationUri.getPath().substring(1) + "\""; just added for test Feb. 28
		
		String path = PluginConstants.ESAPI_VM_ARG + "=\"" + "/" + locationUri.getPath().substring(1) + "\"";
		
        //System.out.println("Line 307 Path = " + path);
		try {
			AbstractVMInstall vminstall = (AbstractVMInstall) JavaRuntime
					.getVMInstall(javaProject);
			
			if (vminstall != null) {
				String[] vmargs = vminstall.getVMArguments();
				
				if (vmargs == null) {
					vminstall.setVMArguments(new String[] { path });
				    System.out.println("vmargs == null, and it is reseted, Line 315 Path = " + path);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}