package kr.pe.maun.csdgenerator.actions;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamClass;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.CSDFunctionGeneratorDialog;
import kr.pe.maun.csdgenerator.dialogs.CSDGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;
import kr.pe.maun.csdgenerator.model.SerialVersionUIDItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

public class CSDFunctionGeneratorAction implements IObjectActionDelegate {

	CSDFunctionGeneratorDialog dialog;

	private ISelection selection;
	private Shell shell;

	@Override
	public void run(IAction action) {

		dialog = new CSDFunctionGeneratorDialog(shell, selection);

		if(dialog.open() != InputDialog.OK)
			return;

		Job job = new Job("Generator") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				subMonitor.setWorkRemaining(70);

				IFolder folder = null;
				IPackageFragment packageFragment = null;
				String prefix  = dialog.getPrefix();

				if(selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection) selection;
					TreePath[] treePaths = treeSelection.getPaths();
					for(TreePath treePath : treePaths) {
						if(treePath.getLastSegment() instanceof IFolder) {
							folder = (IFolder) treePath.getLastSegment();
							/*System.out.println("path 1 : "+ folder. getLocation().toOSString());*/
						} else if(treePath.getLastSegment() instanceof IPackageFragment) {
							packageFragment = (IPackageFragment) treePath.getLastSegment();
						}
					}
				}

				IProject project = packageFragment.getJavaProject().getProject();

				if(folder != null && prefix != null) {

					IFolder newFolder = folder.getFolder(new Path(prefix));

					String controllerPath = prefix + "Controller";
					String servicePath = prefix + "Service";
					String daoPath = prefix + "Dao";

					try {

						newFolder.create(true ,true, new NullProgressMonitor());

						IFolder controllerFolder = newFolder.getFolder(new Path(controllerPath));
						controllerFolder.create(true ,true, new NullProgressMonitor());

						String controllerContent = new String();
						controllerContent = "package " + controllerFolder.getFullPath().toString().replace("/", ".");
						controllerContent += "\n";

						ByteArrayInputStream controllerFileStream = new ByteArrayInputStream(controllerContent.getBytes("UTF-8"));

						IFile controllerFile = controllerFolder.getFile(new Path(controllerPath + ".java"));
						controllerFile.create(controllerFileStream ,true, new NullProgressMonitor());

						IFolder serviceFolder = newFolder.getFolder(new Path(servicePath));
						serviceFolder.create(true ,true, new NullProgressMonitor());

						IFolder daoFolder = newFolder.getFolder(new Path(daoPath));
						daoFolder.create(true ,true, new NullProgressMonitor());

					} catch (CoreException | UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				} else if(packageFragment != null && prefix != null) {

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(true);
					factory.setNamespaceAware(true);

					long startTime = System.currentTimeMillis();

					IResource resource = (IResource) packageFragment.getJavaProject().getProject().getAdapter(IResource.class);

					CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);
					CSDGeneratorPropertiesHelper propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(packageFragment.getJavaProject().getProject()).getNode(CSDGeneratorPlugin.PLUGIN_ID));

					IConnectionProfile connectionProfile = dialog.getConnectionProfile();
					DatabaseResource databaseResource = connectionProfile == null ? null : new DatabaseResource(connectionProfile);
					String databaseTable = dialog.getDatabaseTable();

					String company = propertiesItem.getCompany() != null ? propertiesItem.getCompany() : "";
					String author = propertiesItem.getAuthor() != null ? propertiesItem.getAuthor() : System.getProperty("user.name");

					boolean isCreateService = dialog.isCreateService();
					boolean isCreateServiceSubFolder = propertiesItem.getCreateServiceSubFolder();
					boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();

					boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
					boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImpl();

					boolean isCreateDao = dialog.isCreateDao();
					boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
					boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();
					boolean isCreateDaoSubFolder = propertiesItem.getCreateDaoSubFolder();
					String myBatisSettingFile = propertiesItem.getMyBatisSettingFile();

					boolean isCreateMapper = dialog.isCreateMapper();
					String mapperPath = propertiesItem.getMapperPath();

					boolean isCreateVo = dialog.isCreateVo();
					boolean isCreateSearchVo = propertiesItem.getCreateSearchVo();
					String voPath = propertiesItem.getVoPath();

					String[] dataTypes = propertiesHelper.getDataTypes();

					String packageFullPath = packageFragment.getElementName();

					IJavaProject javaProject = packageFragment.getJavaProject();

					String javaVoBuildPath = "";


					List<SerialVersionUIDItem> addSerialVersionUIDList = new ArrayList<SerialVersionUIDItem>();

					try {
						IClasspathEntry[] classpaths = javaProject.getRawClasspath();
						for (IClasspathEntry classpath : classpaths) {
							IPath path = classpath.getPath();
							if (isCreateVo && voPath != null && voPath.indexOf(path.toString()) > -1) {
								javaVoBuildPath = path.removeFirstSegments(1).toString();
							}
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}

					folder = (IFolder) packageFragment.getResource().getAdapter(IFolder.class);

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd");
					String date = dateFormat.format(new Date());

				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(shell, "CSD Generator", "CSD Generator has finished.");
					}
				});

				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
		this.selection = targetPart.getSite().getSelectionProvider().getSelection();
	}

	private String getSource(String templateFile) {

		String source = "";

		BufferedReader in = null;
		InputStream inputStream = null;

		try {

			if(templateFile.indexOf("platform") > -1) {
				URL url = new URL(templateFile);
				inputStream = url.openConnection().getInputStream();
				in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			} else {
				in = new BufferedReader(new FileReader(templateFile));
			}

		    String inputLine;

		    while ((inputLine = in.readLine()) != null) {
		    	source += inputLine;
		    	source += "\n";
		    }

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) in.close();
				if(inputStream != null) inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return source;
	};

	private String selecColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName());
				result.append(" AS ");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	private String insertColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName());
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	private String insertValue(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append("#{");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	private String updateColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		int size = columnItems.size();
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName());
				result.append(" = #{");
				result.append(StringUtils.toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (size - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	private String indexColumn(List<String> indexColumns) {
		StringBuffer result = new StringBuffer();
		int size = indexColumns.size();
		if(size > 0) {
			for (int i = 0; i < size; i++) {
				String column = indexColumns.get(i);
				if(i > 0) result.append("\t\t\t");
				result.append("AND ");
				result.append(column);
				result.append(" = #{");
				result.append(StringUtils.toCamelCase(column));
				result.append("}");
				if(i < (size - 1)) result.append("\n");
			}
		}
		return result.toString();
	}
}
