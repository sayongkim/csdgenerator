package kr.pe.maun.csdgenerator.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamClass;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.CSDTemplateGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

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
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
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
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
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

public class CSDTemplateGeneratorAction implements IObjectActionDelegate {

	CSDTemplateGeneratorDialog dialog;

	ICompilationUnit controllerCompilationUnit;
	ICompilationUnit serviceCompilationUnit;
	ICompilationUnit daoCompilationUnit;

	private ISelection selection;
	private Shell shell;

	@Override
	public void run(IAction action) {

		dialog = new CSDTemplateGeneratorDialog(shell, selection);

		if(selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for(TreePath treePath : treePaths) {
				if(treePath.getLastSegment() instanceof ICompilationUnit) {
					controllerCompilationUnit = (ICompilationUnit) treePath.getLastSegment();
				}
			}
		}

		if(dialog.open() != InputDialog.OK)
			return;

		Job job = new Job("Generator") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				String name  = dialog.getName();

				IJavaProject javaProject = controllerCompilationUnit.getJavaProject();
				IProject project = javaProject.getProject();

				if(name != null) {
					try {
						String capitalizePrefix = controllerCompilationUnit.getElementName().replaceAll("Controller.java", "");
						String prefix = capitalizePrefix.substring(0, 1).toLowerCase() + capitalizePrefix.substring(1);

						long startTime = System.currentTimeMillis();

						IResource resource = (IResource) project.getAdapter(IResource.class);

						CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);
						CSDGeneratorPropertiesHelper propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

						IFolder templateRootFolder = project.getWorkspace().getRoot().getFolder(new Path("/" + project.getName() + "/template"));
						if(!templateRootFolder.exists()) templateRootFolder.create(true ,true, new NullProgressMonitor());

						IFolder templateFolder = templateRootFolder.getFolder(name);
						templateFolder.create(true ,true, new NullProgressMonitor());

						/* S : Create Controller Template File */
						IImportDeclaration[] controllerImports = controllerCompilationUnit.getImports();

						String controllerSource = controllerCompilationUnit.getSource();

						for(IImportDeclaration importDeclaration : controllerImports) {
							String elementName = importDeclaration.getElementName();
							if(elementName.indexOf(capitalizePrefix + "Service") > -1) {
								controllerSource = controllerSource.replaceAll("import " + elementName + ";[\t ]*[\r\n|\n|\r]", "");
							}
						}
						controllerSource = controllerSource.replaceAll(controllerCompilationUnit.getPackageDeclarations()[0].getElementName(), "\\[packagePath\\]");
						controllerSource = controllerSource.replaceAll(prefix, "\\[prefix\\]");
						controllerSource = controllerSource.replaceAll(capitalizePrefix, "\\[capitalizePrefix\\]");

						IFile controllerTemplateFile = templateFolder.getFile(new Path("_controller.txt"));
						if(!controllerTemplateFile.exists()) controllerTemplateFile.create(new ByteArrayInputStream(controllerSource.getBytes()) ,true, new NullProgressMonitor());
						/* E : Create Controller Template File */

						/* S : Create Service Template File */
						String serviceSource = "";
						for(IImportDeclaration importDeclaration : controllerImports) {
							if(importDeclaration.getElementName().indexOf(capitalizePrefix + "Service") > -1) {
								SearchEngine searchEngine = new SearchEngine();
								searchEngine.search(SearchPattern.createPattern(importDeclaration.getElementName(), IJavaSearchConstants.TYPE, IJavaSearchConstants.CONSTRUCTOR, SearchPattern.R_FULL_MATCH), new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, SearchEngine.createWorkspaceScope(), new SearchRequestor() {
									@SuppressWarnings("restriction")
									@Override
									public void acceptSearchMatch(SearchMatch searchMatch) throws CoreException {
										if(searchMatch.getElement() instanceof ResolvedSourceType) {
											ResolvedSourceType resolvedSourceType = (ResolvedSourceType) searchMatch.getElement();
											if(resolvedSourceType.getParent().getElementType() == IJavaElement.COMPILATION_UNIT) {
												serviceCompilationUnit = (ICompilationUnit) ((ResolvedSourceType) searchMatch.getElement()).getParent();
											}
										}
									}
								}, null);
								break;
							}
						}

						IImportDeclaration[] serviceImports = serviceCompilationUnit.getImports();
						serviceSource = serviceCompilationUnit.getSource();
						for(IImportDeclaration importDeclaration : serviceImports) {
							String elementName = importDeclaration.getElementName();
							if(elementName.indexOf(capitalizePrefix + "Service") > -1) {
								serviceSource = serviceSource.replaceAll("import " + elementName + ";[\t ]*[\r\n|\n|\r]", "");
							} else if(elementName.indexOf(capitalizePrefix + "Dao") > -1) {
								serviceSource = serviceSource.replaceAll("import " + elementName + ";[\t ]*[\r\n|\n|\r]", "");
							}
						}
						serviceSource = serviceSource.replaceAll(serviceCompilationUnit.getPackageDeclarations()[0].getElementName(), "\\[packagePath\\]");
						serviceSource = serviceSource.replaceAll("[\t ]*@Override[\t ]*[\r\n|\n|\r]", "");
						serviceSource = serviceSource.replaceAll("Impl", "");
						serviceSource = serviceSource.replaceAll("implements[ \t]+" + capitalizePrefix + "Service[ \t]+", "");
						serviceSource = serviceSource.replaceAll(prefix, "\\[prefix\\]");
						serviceSource = serviceSource.replaceAll(capitalizePrefix, "\\[capitalizePrefix\\]");

						IFile serviceTemplateFile = templateFolder.getFile(new Path("_service.txt"));
						if(!serviceTemplateFile.exists()) serviceTemplateFile.create(new ByteArrayInputStream(serviceSource.getBytes()) ,true, new NullProgressMonitor());
						/* E : Create Service Template File */

						/* S : Create Dao Template File */
						String daoSource = "";
						for(IImportDeclaration importDeclaration : serviceImports) {
							if(importDeclaration.getElementName().indexOf(capitalizePrefix + "Dao") > -1) {
								SearchEngine searchEngine = new SearchEngine();
								searchEngine.search(SearchPattern.createPattern(importDeclaration.getElementName(), IJavaSearchConstants.TYPE, IJavaSearchConstants.CONSTRUCTOR, SearchPattern.R_FULL_MATCH), new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, SearchEngine.createWorkspaceScope(), new SearchRequestor() {
									@SuppressWarnings("restriction")
									@Override
									public void acceptSearchMatch(SearchMatch searchMatch) throws CoreException {
										if(searchMatch.getElement() instanceof ResolvedSourceType) {
											ResolvedSourceType resolvedSourceType = (ResolvedSourceType) searchMatch.getElement();
											if(resolvedSourceType.getParent().getElementType() == IJavaElement.COMPILATION_UNIT) {
												daoCompilationUnit = (ICompilationUnit) ((ResolvedSourceType) searchMatch.getElement()).getParent();
											}
										}
									}
								}, null);
								break;
							}
						}

						daoSource = daoCompilationUnit.getSource();
						daoSource = daoSource.replaceAll(daoCompilationUnit.getPackageDeclarations()[0].getElementName(), "\\[packagePath\\]");
						daoSource = daoSource.replaceAll(prefix, "\\[prefix\\]");
						daoSource = daoSource.replaceAll(capitalizePrefix, "\\[capitalizePrefix\\]");

						IFile daoTemplateFile = templateFolder.getFile(new Path("_dao.txt"));
						if(!daoTemplateFile.exists()) daoTemplateFile.create(new ByteArrayInputStream(daoSource.getBytes()) ,true, new NullProgressMonitor());
						/* E : Create Dao Template File */

						/* S : Create Mapper Template File */
						String mapperPath = propertiesItem.getMapperPath();
						IFolder mapperFolder = project.getWorkspace().getRoot().getFolder(new Path(mapperPath + "/" + prefix));
						if(mapperFolder.exists()) {
							IFile mapperFile = mapperFolder.getFile(new Path(prefix + "Mapper.xml"));
							if(mapperFile.exists()) {
								String mapperSource = getSource(mapperFile.getLocation().toOSString());
								IFile mapperTemplateFile = templateFolder.getFile(new Path("_mapper.txt"));
								if(!mapperTemplateFile.exists()) mapperTemplateFile.create(new ByteArrayInputStream(mapperSource.getBytes()) ,true, new NullProgressMonitor());
							}
						}
						/* E : Create Mapper Template File */

						/* S : Create List Template File */
						String jspPath = propertiesItem.getJspPath();
						IFolder jspFolder = project.getWorkspace().getRoot().getFolder(new Path(jspPath + "/" + prefix));
						if(jspFolder.exists()) {
							IFile jspListFile = jspFolder.getFile(new Path(prefix + "List.jsp"));
							if(jspListFile.exists()) {
								String jspListSource = getSource(jspListFile.getLocation().toOSString());
								IFile jspListTemplateFile = templateFolder.getFile(new Path("_list.txt"));
								if(!jspListTemplateFile.exists()) jspListTemplateFile.create(new ByteArrayInputStream(jspListSource.getBytes()) ,true, new NullProgressMonitor());
							}
							IFile jspPostFile = jspFolder.getFile(new Path(prefix + "Post.jsp"));
							if(jspPostFile.exists()) {
								String jspPostSource = getSource(jspPostFile.getLocation().toOSString());
								IFile jspPostTemplateFile = templateFolder.getFile(new Path("_post.txt"));
								if(!jspPostTemplateFile.exists()) jspPostTemplateFile.create(new ByteArrayInputStream(jspPostSource.getBytes()) ,true, new NullProgressMonitor());
							}
							IFile jspViewFile = jspFolder.getFile(new Path(prefix + "View.jsp"));
							if(jspViewFile.exists()) {
								String jspViewSource = getSource(jspViewFile.getLocation().toOSString());
								IFile jspViewTemplateFile = templateFolder.getFile(new Path("_view.txt"));
								if(!jspViewTemplateFile.exists()) jspViewTemplateFile.create(new ByteArrayInputStream(jspViewSource.getBytes()) ,true, new NullProgressMonitor());
							}
						}
						/* E : Create Mapper Template File */

					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(shell, "CSD Tempate Generator", "CSD Tempate Generator has finished.");
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
				in = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile),"UTF8"));
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
}
