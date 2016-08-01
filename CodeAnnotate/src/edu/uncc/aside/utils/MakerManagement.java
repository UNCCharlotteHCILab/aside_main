package edu.uncc.aside.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;

import edu.uncc.aside.codeannotate.Plugin;
import edu.uncc.aside.codeannotate.PluginConstants;
import edu.uncc.sis.aside.Old_AsidePlugin;
import edu.uncc.sis.aside.auxiliary.core.RunAnalysis;

public class MakerManagement {
	private static final Logger logger = Plugin.getLogManager().getLogger(
			RunAnalysis.class.getName());
}
