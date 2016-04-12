package kr.pe.maun.csdgenerator.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.CSDFunctionGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

public class CSDFunctionGeneratorAction implements IObjectActionDelegate {

	CSDFunctionGeneratorDialog dialog;

	ICompilationUnit serviceImplCompilationUnit;

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

				ICompilationUnit compilationUnit = null;

				String prefix  = dialog.getPrefix();
				String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
				String upperPrefix = prefix.toUpperCase();
				String lowerPrefix = prefix.toLowerCase();

				if(selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection) selection;
					TreePath[] treePaths = treeSelection.getPaths();
					for(TreePath treePath : treePaths) {
						if(treePath.getLastSegment() instanceof ICompilationUnit) {
							compilationUnit = (ICompilationUnit) treePath.getLastSegment();
						}
					}
				}

				IProject project = compilationUnit.getJavaProject().getProject();

				if(prefix != null) {

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(true);
					factory.setNamespaceAware(true);

					long startTime = System.currentTimeMillis();

					IResource resource = (IResource) project.getAdapter(IResource.class);

					CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);
					CSDGeneratorPropertiesHelper propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

					IConnectionProfile connectionProfile = dialog.getConnectionProfile();
					DatabaseResource databaseResource = connectionProfile == null ? null : new DatabaseResource(connectionProfile);
					String databaseTable = dialog.getDatabaseTable();

					String company = propertiesItem.getCompany() != null ? propertiesItem.getCompany() : "";
					String author = propertiesItem.getAuthor() != null ? propertiesItem.getAuthor() : System.getProperty("user.name");

					boolean isCreateService = dialog.isCreateService();

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
/*
					String packageFullPath = packageFragment.getElementName();
*/
					IJavaProject javaProject = compilationUnit.getJavaProject();

					String javaVoBuildPath = "";

					boolean isCreateSelectCount = dialog.isCreateSelectCount();
					boolean isCreateSelectList = dialog.isCreateSelectList();
					boolean isCreateSelectOne = dialog.isCreateSelectOne();
					boolean isCreateInsert = dialog.isCreateInsert();
					boolean isCreateUpdate = dialog.isCreateUpdate();
					boolean isCreateDelete = dialog.isCreateDelete();

					String serviceTemplate = "";
					String serviceInterfaceTemplate = "";
					String serviceSelectCountTemplate = propertiesHelper.getServiceFunctionSelectCountTemplate();
					String serviceSelectListTemplate = propertiesHelper.getServiceFunctionSelectListTemplate();
					String serviceSelectOneTemplate = propertiesHelper.getServiceFunctionSelectOneTemplate();
					String serviceInsertTemplate = propertiesHelper.getServiceFunctionInsertTemplate();
					String serviceUpdateTemplate = propertiesHelper.getServiceFunctionUpdateTemplate();
					String serviceDeleteTemplate = propertiesHelper.getServiceFunctionDeleteTemplate();

					String daoSelectCountTemplate = propertiesHelper.getDaoFunctionSelectCountTemplate();
					String daoSelectListTemplate = propertiesHelper.getDaoFunctionSelectListTemplate();
					String daoSelectOneTemplate = propertiesHelper.getDaoFunctionSelectOneTemplate();
					String daoInsertTemplate = propertiesHelper.getDaoFunctionInsertTemplate();
					String daoUpdateTemplate = propertiesHelper.getDaoFunctionUpdateTemplate();
					String daoDeleteTemplate = propertiesHelper.getDaoFunctionDeleteTemplate();

					String mapperSelectCountTemplate = propertiesHelper.getMapperFunctionSelectCountTemplate();
					String mapperSelectListTemplate = propertiesHelper.getMapperFunctionSelectListTemplate();
					String mapperSelectOneTemplate = propertiesHelper.getMapperFunctionSelectOneTemplate();
					String mapperInsertTemplate = propertiesHelper.getMapperFunctionInsertTemplate();
					String mapperUpdateTemplate = propertiesHelper.getMapperFunctionUpdateTemplate();
					String mapperDeleteTemplate = propertiesHelper.getMapperFunctionDeleteTemplate();

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

					String namePrefix = compilationUnit.getElementName().replaceAll("Service.java", "");

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd");
					String date = dateFormat.format(new Date());

					if(isCreateService) {
						try {

							IType serviceType = compilationUnit.getType(namePrefix + "Service");
							boolean isServiceInterface = serviceType.isInterface();

							if(isCreateSelectCount) {
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[company\\]", company);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[author\\]", author);
								serviceSelectCountTemplate = serviceSelectCountTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceSelectCountTemplate;
								int endIndex = 0;
								if(serviceSelectCountTemplate.indexOf(") {") > -1) {
									endIndex = serviceSelectCountTemplate.indexOf(") {");
								} else {
									endIndex = serviceSelectCountTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectCountTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							if(isCreateSelectList) {
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[company\\]", company);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[author\\]", author);
								serviceSelectListTemplate = serviceSelectListTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceSelectListTemplate;
								int endIndex = 0;
								if(serviceSelectListTemplate.indexOf(") {") > -1) {
									endIndex = serviceSelectListTemplate.indexOf(") {");
								} else {
									endIndex = serviceSelectListTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectListTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							if(isCreateSelectOne) {
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[company\\]", company);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[author\\]", author);
								serviceSelectOneTemplate = serviceSelectOneTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceSelectOneTemplate;
								int endIndex = 0;
								if(serviceSelectOneTemplate.indexOf(") {") > -1) {
									endIndex = serviceSelectOneTemplate.indexOf(") {");
								} else {
									endIndex = serviceSelectOneTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectOneTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							if(isCreateInsert) {
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[company\\]", company);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[author\\]", author);
								serviceInsertTemplate = serviceInsertTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceInsertTemplate;
								int endIndex = 0;
								if(serviceSelectCountTemplate.indexOf(") {") > -1) {
									endIndex = serviceSelectCountTemplate.indexOf(") {");
								} else {
									endIndex = serviceSelectCountTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectCountTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							if(isCreateUpdate) {
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[company\\]", company);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[author\\]", author);
								serviceUpdateTemplate = serviceUpdateTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceUpdateTemplate;
								int endIndex = 0;
								if(serviceUpdateTemplate.indexOf(") {") > -1) {
									endIndex = serviceUpdateTemplate.indexOf(") {");
								} else {
									endIndex = serviceUpdateTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectCountTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							if(isCreateDelete) {
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[prefix\\]", prefix);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[company\\]", company);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[author\\]", author);
								serviceDeleteTemplate = serviceDeleteTemplate.replaceAll("\\[date\\]", date);
								serviceTemplate += serviceDeleteTemplate;
								int endIndex = 0;
								if(serviceDeleteTemplate.indexOf(") {") > -1) {
									endIndex = serviceDeleteTemplate.indexOf(") {");
								} else {
									endIndex = serviceDeleteTemplate.indexOf("){");
								}
								serviceInterfaceTemplate += serviceSelectCountTemplate.substring(0, endIndex + 1);
								serviceInterfaceTemplate += ";";
							}

							String serviceContent = compilationUnit.getSource();
							serviceContent = serviceContent.substring(0, serviceContent.lastIndexOf("}"));

							if(isServiceInterface) {
								String serviceImplContent = serviceImplCompilationUnit.getSource();
								serviceImplContent = serviceImplContent.substring(0, serviceImplContent.lastIndexOf("}"));
								serviceImplContent += serviceTemplate;
								serviceImplContent += "\n}";

								SearchRequestor requestor = new SearchRequestor() {
									@Override
									public void acceptSearchMatch(SearchMatch searchMatch) throws CoreException {
										if(searchMatch.getElement() instanceof ResolvedSourceType) {
											ResolvedSourceType resolvedSourceType = (ResolvedSourceType) searchMatch.getElement();
											if(resolvedSourceType.getParent().getElementType() == IJavaElement.COMPILATION_UNIT) {
												serviceImplCompilationUnit = (ICompilationUnit) ((ResolvedSourceType) searchMatch.getElement()).getParent();
											}
										}
									}
								};

								SearchEngine searchEngine = new SearchEngine();
								SearchParticipant[] searchParticipants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
								SearchPattern createPattern = SearchPattern.createPattern(serviceType, IJavaSearchConstants.IMPLEMENTORS);
								searchEngine.search(createPattern, searchParticipants, SearchEngine.createWorkspaceScope(), requestor, subMonitor);

								IFile serviceImplFile = (IFile) serviceImplCompilationUnit.getResource();
								serviceImplFile.setContents(new ByteArrayInputStream(serviceImplContent.getBytes("UTF-8")), true ,true, new NullProgressMonitor());

								serviceContent += serviceInterfaceTemplate;

							} else {
								serviceContent += serviceTemplate;
							}

							serviceContent += "\n}";

							IFile serviceFile = (IFile) compilationUnit.getResource();
							serviceFile.setContents(new ByteArrayInputStream(serviceContent.getBytes("UTF-8")), true ,true, new NullProgressMonitor());

						} catch (CoreException | UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

					if(isCreateDao) {
					}

					if(isCreateMapper) {
					}

					if(isCreateVo && databaseTable != null) {
					}

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
