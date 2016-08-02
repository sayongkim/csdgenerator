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
import kr.pe.maun.csdgenerator.dialogs.CSDGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;
import kr.pe.maun.csdgenerator.model.SerialVersionUIDItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

public class CSDGeneratorAction implements IObjectActionDelegate {

	CSDGeneratorDialog dialog;

	private ISelection selection;
	private Shell shell;

	@Override
	public void run(IAction action) {

		dialog = new CSDGeneratorDialog(shell, selection);

		if(dialog.open() != InputDialog.OK)
			return;


		Job job = new Job("Generator") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				subMonitor.setWorkRemaining(70);

				IFolder javaFolder = null;
				IFolder javaTestFolder = null;
				IPackageFragment javaPackageFragment = null;
				IPackageFragment javaTestPackageFragment = null;
				String prefix  = dialog.getPrefix();
				boolean isCreateFolder = dialog.isCreateFolder();
				boolean isParentLocation = dialog.isParentLocation();

				if(selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection) selection;
					TreePath[] treePaths = treeSelection.getPaths();
					for(TreePath treePath : treePaths) {
						if(treePath.getLastSegment() instanceof IFolder) {
							javaFolder = (IFolder) treePath.getLastSegment();
							/*System.out.println("path 1 : "+ folder. getLocation().toOSString());*/
						} else if(treePath.getLastSegment() instanceof IPackageFragment) {
							javaPackageFragment = (IPackageFragment) treePath.getLastSegment();
						}
					}
				}

				IProject project = javaPackageFragment.getJavaProject().getProject();

				CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

				if(javaFolder != null && prefix != null) {

					IFolder newFolder = javaFolder.getFolder(new Path(prefix));

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
				} else if(javaPackageFragment != null && prefix != null) {

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(true);
					factory.setNamespaceAware(true);

					long startTime = System.currentTimeMillis();
					long lapTime = System.currentTimeMillis();

					IResource resource = (IResource) javaPackageFragment.getJavaProject().getProject().getAdapter(IResource.class);

					CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);
					CSDGeneratorPropertiesHelper propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(javaPackageFragment.getJavaProject().getProject()).getNode(CSDGeneratorPlugin.PLUGIN_ID));

					IConnectionProfile connectionProfile = dialog.getConnectionProfile();
					DatabaseResource databaseResource = connectionProfile == null ? null : new DatabaseResource(connectionProfile);

					String generalTemplateController = propertiesHelper.getGeneralTemplateController(dialog.getTemplateGroupName());
					String generalTemplateService = propertiesHelper.getGeneralTemplateService(dialog.getTemplateGroupName());
					String generalTemplateDao = propertiesHelper.getGeneralTemplateDao(dialog.getTemplateGroupName());
					String generalTemplateMapper = propertiesHelper.getGeneralTemplateMapper(dialog.getTemplateGroupName());
					String generalTemplateJsp = propertiesHelper.getGeneralTemplateJsp(dialog.getTemplateGroupName());

					String testTemplateController = propertiesHelper.getTestTemplateController(dialog.getTemplateGroupName());
					String testTemplateService = propertiesHelper.getTestTemplateService(dialog.getTemplateGroupName());
					String testTemplateDao = propertiesHelper.getTestTemplateDao(dialog.getTemplateGroupName());

					String testPath = propertiesItem.getTestPath();

					boolean isCreateController = dialog.isCreateController();
					boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
					boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();
					boolean isCreateControllerSubFolder = propertiesItem.getCreateControllerSubFolder();
					String controllerTemplateFile = generalTemplateController == null ? null : propertiesHelper.getControllerTemplateFile(generalTemplateController);

					boolean isCreateTestController = dialog.isCreateTestController();
					boolean isCreateTestControllerFolder = propertiesItem.getCreateTestControllerFolder();
					String testControllerTemplateFile = testTemplateController == null ? null : propertiesHelper.getControllerTemplateFile(testTemplateController);

					boolean isCreateService = dialog.isCreateService();
					boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
					boolean isCreateServiceSubFolder = propertiesItem.getCreateServiceSubFolder();
					boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();
					String serviceTemplateFile = generalTemplateService == null ? null : propertiesHelper.getServiceTemplateFile(generalTemplateService);

					boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
					boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImpl();

					boolean isCreateTestService = dialog.isCreateTestService();
					boolean isCreateTestServiceFolder = propertiesItem.getCreateTestServiceFolder();
					String testServiceTemplateFile = testTemplateService == null ? null : propertiesHelper.getServiceTemplateFile(testTemplateService);

					boolean isCreateDao = dialog.isCreateDao();
					boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
					boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();
					boolean isCreateDaoSubFolder = propertiesItem.getCreateDaoSubFolder();
					String daoTemplateFile = generalTemplateDao == null ? null : propertiesHelper.getDaoTemplateFile(generalTemplateDao);
					String myBatisSettingFile = propertiesItem.getMyBatisSettingFile();

					boolean isCreateTestDao = dialog.isCreateTestDao();
					boolean isCreateTestDaoFolder = propertiesItem.getCreateTestDaoFolder();
					String testDaoTemplateFile = testTemplateDao == null ? null : propertiesHelper.getDaoTemplateFile(testTemplateDao);

					boolean isCreateMapper = dialog.isCreateMapper();
					String mapperPath = propertiesItem.getMapperPath();
					String mapperTemplateFile = generalTemplateMapper == null ? null : propertiesHelper.getMapperTemplateFile(generalTemplateMapper);

					boolean isCreateVo = dialog.isCreateVo();
					boolean isCreateSearchVo = propertiesItem.getCreateSearchVo();
					boolean isCreateVoFolder = propertiesItem.getCreateVoFolder();
					String voFolderName = propertiesItem.getVoFolder();
					String voPath = propertiesItem.getVoPath();
					String voSuperclass = dialog.getVoSuperclass();
					boolean isExtendVoSuperclass = dialog.isExtendVoSuperclass() && voSuperclass != null && voSuperclass.indexOf(".") != -1;

					boolean isCreateJsp = dialog.isCreateJsp();
					String jspPath = propertiesItem.getJspPath();
					String jspTemplateListFile =  generalTemplateMapper == null ? null : propertiesHelper.getJspTemplateListFile(generalTemplateJsp);
					String jspTemplatePostFile = generalTemplateMapper == null ? null : propertiesHelper.getJspTemplatePostFile(generalTemplateJsp);
					String jspTemplateViewFile = generalTemplateMapper == null ? null : propertiesHelper.getJspTemplateListFile(generalTemplateJsp);

					String parameterType = dialog.getParameterType();
					String returnType = dialog.getReturnType();

					String[] dataTypes = propertiesHelper.getDataTypes();

					IJavaProject javaProject = javaPackageFragment.getJavaProject();

					String javaVoBuildPath = "";

					try {
						javaTestPackageFragment = javaProject.findPackageFragment(new Path(testPath));
					} catch (JavaModelException e) {
						e.printStackTrace();
						isCreateTestController = false;
						isCreateTestService = false;
						isCreateTestDao = false;
					}

					String packageFullPath = javaPackageFragment.getElementName();
					String testPackageFullPath = javaTestPackageFragment == null ? "" : javaTestPackageFragment.getElementName();

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

					javaFolder = (IFolder) javaPackageFragment.getResource().getAdapter(IFolder.class);
					javaTestFolder = javaTestPackageFragment == null ? null : (IFolder) javaTestPackageFragment.getResource().getAdapter(IFolder.class);

					if(isParentLocation) {
						packageFullPath = packageFullPath.substring(0, packageFullPath.lastIndexOf("."));
						javaFolder = (IFolder) javaFolder.getParent().getAdapter(IFolder.class);
					}

					String[] databaseTables = dialog.getDatabaseTables();

					if(databaseTables == null || databaseTables.length == 0) databaseTables = new String[]{prefix};

					Pattern pattern = null;

					try {
						if(!"".equals(prefix)) {
							pattern =Pattern.compile(prefix);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					org.w3c.dom.Document myBatisSettingDocument = null;

					if(myBatisSettingFile != null) {

						String myBatisSettingContent = getSource(myBatisSettingFile);

						try {
							DocumentBuilder builder = factory.newDocumentBuilder();
							myBatisSettingDocument = builder.parse(new ByteArrayInputStream(myBatisSettingContent.getBytes("UTF-8")));
						} catch (Exception e){
							e.printStackTrace();
						}
					}

					for(String databaseTableName : databaseTables) {

						String rootPackagePath = packageFullPath;
						String rootTestPackagePath = testPackageFullPath;

						prefix = databaseTableName;

						if(pattern != null) {
							Matcher matcher = pattern.matcher(prefix);
							if(matcher.matches() && matcher.groupCount() > 0) {
								prefix = matcher.group(1);
							}
						}

						prefix = StringUtils.toCamelCase(prefix);

						IFolder newJavaFolder = javaFolder.getFolder(new Path(prefix));
						IFolder newJavaTestFolder = javaTestFolder.getFolder(new Path(prefix));

						String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);

						try {

							List<ColumnItem> columns = null;
							List<String> indexColumns = null;

							if(connectionProfile != null) {
								columns = databaseResource.getColumns(databaseTableName);
								indexColumns = databaseResource.getIndexColumns(databaseTableName);
							}

							/* 폴더를 생성한다. */
							if(isCreateFolder
									&& (isCreateController || isCreateService || isCreateDao || isCreateVo)) {
								if(!newJavaFolder.exists()) newJavaFolder.create(true ,true, new NullProgressMonitor());
								rootPackagePath = rootPackagePath + "." + prefix;
							} else {
								newJavaFolder = javaFolder;
							}

							if(isCreateTestController || isCreateTestService || isCreateTestDao) {
								if(!newJavaTestFolder.exists()) newJavaTestFolder.create(true ,true, new NullProgressMonitor());
								rootTestPackagePath = rootTestPackagePath + "." + prefix;
							} else {
								newJavaTestFolder = javaTestFolder;
							}

							String voPackage = voPath == null || voPath.length() < 1 || voPath.lastIndexOf(javaVoBuildPath) == -1 ? "" : voPath.replace("/", ".").substring(voPath.lastIndexOf(javaVoBuildPath) + javaVoBuildPath.length() + 1);

							if(isCreateVoFolder) {
								voPackage = rootPackagePath + "." + voFolderName;
							}

							String importParameterVo = null;
							String importReturnVo = null;

							if(parameterType.toLowerCase().indexOf("hashmap") == -1) {
								importParameterVo = voPackage + "." + parameterType;
							}

							if(returnType.toLowerCase().indexOf("hashmap") == -1) {
								importReturnVo = voPackage + "." + returnType;
							}

							/* S : Vo 생성 */

							if(isCreateVo) {
								if(connectionProfile != null) {

									parameterType = capitalizePrefix + "Vo";
									returnType = capitalizePrefix + "Vo";
									importParameterVo = voPackage + "." + capitalizePrefix + "Vo";
									importReturnVo = voPackage + "." + capitalizePrefix + "Vo";

									IFolder voFolder = null;

									if(isCreateVoFolder) {
										voFolder = newJavaFolder.getFolder(new Path(voFolderName));
									} else {
										voFolder = newJavaFolder.getWorkspace().getRoot().getFolder(new Path(voPath + "/"));
									}

									if(!voFolder.exists()) voFolder.create(true ,true, new NullProgressMonitor());

									String voContent = getSource("platform:/plugin/kr.pe.maun.csdgenerator/resource/template/voTemplate.txt");

									voContent = voContent.replaceAll("\\[packagePath\\]", voPackage);
									voContent = voContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);

									StringBuffer valueBuffer = new StringBuffer();
									StringBuffer gettersAndSetters = new StringBuffer();

									HashMap<String, String> importDeclaration = new HashMap<String, String>();

									int columnsSize = columns.size();

									for(int i = 0; i < columnsSize; i++) {

										ColumnItem column = columns.get(i);
										String columnName = StringUtils.toCamelCase(column.getColumnName());

										String dataType = "String";

										if(dataTypes != null) {
											for(String _dataType : dataTypes) {
												if(column.getDataType().toLowerCase().indexOf(_dataType.toLowerCase()) > -1) {
													dataType = propertiesHelper.getJavaObject(_dataType);
													switch (dataType) {
													case "BigDecimal":
														importDeclaration.put("BigDecimal", "java.math.BigDecimal");
														break;
													case "Date":
														importDeclaration.put("Date", "java.util.Date");
														break;
													case "Timestamp":
														importDeclaration.put("Timestamp", "java.sql.Timestamp");
														break;
													}
												}
											}
										}

										valueBuffer.append("\tprivate ");
										valueBuffer.append(dataType);
										valueBuffer.append(" ");
										valueBuffer.append(columnName);
										valueBuffer.append(";");
										if(!"".equals(column.getComments().trim())) {
											valueBuffer.append(" // ");
											valueBuffer.append(column.getComments());
										}
										valueBuffer.append("\n");

										gettersAndSetters.append("\tpublic ");
										gettersAndSetters.append(dataType);
										gettersAndSetters.append(" ");
										gettersAndSetters.append(StringUtils.toCamelCase("get_" + column.getColumnName()));
										gettersAndSetters.append("() {\n");
										gettersAndSetters.append("\t\treturn this.");
										gettersAndSetters.append(columnName);
										gettersAndSetters.append(";\n");
										gettersAndSetters.append("\t}\n\n");

										gettersAndSetters.append("\tpublic void ");
										gettersAndSetters.append(StringUtils.toCamelCase("set_" + column.getColumnName()));
										gettersAndSetters.append("(");
										gettersAndSetters.append(dataType);
										gettersAndSetters.append(" ");
										gettersAndSetters.append(columnName);
										gettersAndSetters.append(") {\n");
										gettersAndSetters.append("\t\tthis.");
										gettersAndSetters.append(columnName);
										gettersAndSetters.append(" = ");
										gettersAndSetters.append(columnName);
										gettersAndSetters.append(";\n");
										gettersAndSetters.append("\t}\n\n");
									}

									voContent = voContent.replaceAll("\\[value\\]", valueBuffer.toString());
									voContent = voContent.replaceAll("\\[GettersAndSetters\\]", gettersAndSetters.toString());

									ByteArrayInputStream voFileStream = new ByteArrayInputStream(voContent.getBytes("UTF-8"));

									IFile voFile = voFolder.getFile(new Path(capitalizePrefix + "Vo.java"));
									if(!voFile.exists()) voFile.create(voFileStream ,true, new NullProgressMonitor());

									IFile searchVoFile = null;

									if(isCreateSearchVo) {
										String searchVoContent = getSource("platform:/plugin/kr.pe.maun.csdgenerator/resource/template/voTemplate.txt");

										searchVoContent = searchVoContent.replaceAll("\\[packagePath\\]", voPackage);
										searchVoContent = searchVoContent.replaceAll("\\[capitalizePrefix\\]", "Search" + capitalizePrefix);
										searchVoContent = searchVoContent.replaceAll("\\[value\\]", valueBuffer.toString());
										searchVoContent = searchVoContent.replaceAll("\\[GettersAndSetters\\]", "");

										ByteArrayInputStream searchVoFileStream = new ByteArrayInputStream(searchVoContent.getBytes("UTF-8"));

										searchVoFile = voFolder.getFile(new Path("Search" + capitalizePrefix + "Vo.java"));
										if(!searchVoFile.exists()) searchVoFile.create(searchVoFileStream ,true, new NullProgressMonitor());
									}

									if(columnsSize > 0) {

										ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(voFile);

										Set<String> keySet = importDeclaration.keySet();
										for(String key : keySet) {
											compilationUnit.createImport(importDeclaration.get(key), null, new NullProgressMonitor());
										}

										if(isExtendVoSuperclass) {
											compilationUnit.createImport(voSuperclass, null, new NullProgressMonitor());

											ASTParser voParser = ASTParser.newParser(AST.JLS8);
											voParser.setSource(compilationUnit);
										    voParser.setResolveBindings(true);

										    CompilationUnit voParserCompilationUnit = (CompilationUnit) voParser.createAST(null);

											AST voAst = voParserCompilationUnit.getAST();
											TypeDeclaration voTypeDeclaration = (TypeDeclaration) voParserCompilationUnit.types().get(0);
											voTypeDeclaration.setSuperclassType(voAst.newSimpleType(voAst.newSimpleName(voSuperclass.substring(voSuperclass.lastIndexOf(".") + 1))));

											TextEdit voTextEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, voParserCompilationUnit.toString(), 0, voParserCompilationUnit.toString().length(), 0, null);
											Document voDocument = new Document(voParserCompilationUnit.toString());

											voTextEdit.apply(voDocument);

											compilationUnit.getBuffer().setContents(voDocument.get());
										}

										compilationUnit.save(null, true);

										if(isCreateSearchVo && searchVoFile != null) {
											ICompilationUnit compilationSearchUnit = JavaCore.createCompilationUnitFrom(searchVoFile);
											for(String key : keySet) {
												compilationSearchUnit.createImport(importDeclaration.get(key), null, new NullProgressMonitor());
											}

											if(isExtendVoSuperclass) {
												ASTParser searchVoParser = ASTParser.newParser(AST.JLS8);
												searchVoParser.setSource(compilationSearchUnit);
												searchVoParser.setResolveBindings(true);

												CompilationUnit searchVoParserCompilationUnit = (CompilationUnit) searchVoParser.createAST(null);

												AST searchVoAst = searchVoParserCompilationUnit.getAST();
												TypeDeclaration searchVoTypeDeclaration = (TypeDeclaration) searchVoParserCompilationUnit.types().get(0);
												searchVoTypeDeclaration.setSuperclassType(searchVoAst.newSimpleType(searchVoAst.newSimpleName(voSuperclass.substring(voSuperclass.lastIndexOf(".") + 1))));

												TextEdit searchVoTextEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, searchVoParserCompilationUnit.toString(), 0, searchVoParserCompilationUnit.toString().length(), 0, null);
												Document searchVoDocument = new Document(searchVoParserCompilationUnit.toString());

												searchVoTextEdit.apply(searchVoDocument);

												compilationSearchUnit.getBuffer().setContents(searchVoDocument.get());
											}

											compilationSearchUnit.save(null, true);

											addSerialVersionUIDList.add(new SerialVersionUIDItem(compilationSearchUnit, voPackage + ".Search" + capitalizePrefix + "Vo"));
										}

										addSerialVersionUIDList.add(new SerialVersionUIDItem(compilationUnit, voPackage + "." + capitalizePrefix + "Vo"));
									}

									if(myBatisSettingFile != null) {

										try {

											Element documentElement = myBatisSettingDocument.getDocumentElement();

											NodeList nodeList = documentElement.getChildNodes();

											for(int i = 0; i < nodeList.getLength(); i++) {
												Node typeAliasesNode = nodeList.item(i);

												if(typeAliasesNode.getNodeName().equals("typeAliases")) {
													boolean findNode = false;
													boolean findSearchNode = false;
													if(typeAliasesNode.hasChildNodes()) {
														NodeList typeAliasNodes = typeAliasesNode.getChildNodes();
														for(int j = 0; j < typeAliasNodes.getLength(); j++) {
															Node typeAliasNode = typeAliasNodes.item(j);
															if(typeAliasNode.hasAttributes()) {
																NamedNodeMap nodeMap = typeAliasNode.getAttributes();
																Node node = nodeMap.getNamedItem("type");
																if(node.getNodeValue().equals(voPackage + "." + capitalizePrefix + "Vo")) findNode = true;
																if(node.getNodeValue().equals(voPackage + "." + "Search" + capitalizePrefix + "Vo")) findSearchNode = true;
															}
														}
													}

													if(!findNode) {
														Element createElement = myBatisSettingDocument.createElement("typeAlias");
														createElement.setAttribute("type", voPackage + "." + capitalizePrefix + "Vo");
														createElement.setAttribute("alias", prefix + "Vo");
														typeAliasesNode.appendChild(createElement);
													}

													if(isCreateSearchVo && !findSearchNode) {
														Element createElement = myBatisSettingDocument.createElement("typeAlias");
														createElement.setAttribute("type", voPackage + "." + "Search" + capitalizePrefix + "Vo");
														createElement.setAttribute("alias", "Search" + capitalizePrefix + "Vo");
														typeAliasesNode.appendChild(createElement);
													}
												}
											}

											StringWriter writer = new StringWriter();
											Transformer transformer = TransformerFactory.newInstance().newTransformer();
											transformer.setOutputProperty(OutputKeys.INDENT, "yes");
									        DocumentType documentType = myBatisSettingDocument.getDoctype();
									        if(documentType != null) {
									            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
									            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
									        }
											transformer.transform(new DOMSource(myBatisSettingDocument), new StreamResult(writer));

											IFile _myBatisSettingFile = newJavaFolder.getWorkspace().getRoot().getFile(Path.fromOSString(myBatisSettingFile));

											if(_myBatisSettingFile.exists()) {
												_myBatisSettingFile.create(new ByteArrayInputStream(myBatisSettingDocument.toString().getBytes("UTF-8")) ,true, new NullProgressMonitor());
											} else {
												BufferedWriter myBatisSetting = new BufferedWriter(new FileWriter(myBatisSettingFile));
												myBatisSetting.write(writer.toString());
												myBatisSetting.close();
											}

										} catch (IOException | TransformerException | TransformerFactoryConfigurationError e) {
											e.printStackTrace();
										}
									}
								}

								System.out.println("Vo 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}

			/* E : Vo 생성 */

			/* S : Dao 생성 */
							String daoPackage = null;
							if(isCreateDao) {
								/* S : Dao 폴더를 생성한다. */
								daoPackage = rootPackagePath;
								String daoFolderName = "";
								IFolder daoFolder = null;

								if(isCreateDaoFolder) {

									if(isAddPrefixDaoFolder)  {
										daoFolderName += prefix;
										daoFolderName += "Dao";
									} else {
										daoFolderName += "dao";
									}

									daoPackage = daoPackage + "." + daoFolderName;
									daoFolder = newJavaFolder.getFolder(new Path(daoFolderName));
									if(!daoFolder.exists()) daoFolder.create(true ,true, new NullProgressMonitor());
								} else {
									daoFolder = newJavaFolder;
								}
								/* E : Dao 폴더를 생성한다. */

								if(isCreateDaoSubFolder) {
									daoPackage = daoPackage + "." + prefix;
									daoFolder = daoFolder.getFolder(new Path(prefix));
									if(!daoFolder.exists()) daoFolder.create(true ,true, new NullProgressMonitor());
								}

								if(daoTemplateFile == null || "".equals(daoTemplateFile)) {
									daoTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/daoClassTemplate.txt";
								}

								/* Dao 파일내용을 가져온다.. */
								String daoContent = getSource(daoTemplateFile);
								daoContent = daoContent.replaceAll("\\[packagePath\\]", daoPackage);
								daoContent = StringUtils.replaceReservedWord(propertiesItem, prefix, daoContent);
								daoContent = StringUtils.replaceParameter(parameterType, daoContent);
								daoContent = StringUtils.replaceReturn(returnType, daoContent);
								if(connectionProfile != null) daoContent = StringUtils.replaceRepeatWord(daoContent, columns);
								/* S: Dao 파일을 생성한다. */
								ByteArrayInputStream daoFileStream = new ByteArrayInputStream(daoContent.getBytes("UTF-8"));

								IFile daoFile = daoFolder.getFile(new Path(capitalizePrefix + "Dao.java"));
								if(!daoFile.exists()) daoFile.create(daoFileStream ,true, new NullProgressMonitor());

								ICompilationUnit daoCompilationUnit = JavaCore.createCompilationUnitFrom(daoFile);
								if(importParameterVo != null) daoCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
								if(importReturnVo != null) daoCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());
								daoCompilationUnit.save(null, true);

								if(isCreateTestDao) {
									String testDaoPackage = rootTestPackagePath;
									String testDaoFolderName = "";
									IFolder testDaoFolder = null;

									if(isCreateTestDaoFolder && isCreateDaoFolder) {

									  if(isAddPrefixDaoFolder)  {
									    testDaoFolderName += prefix;
									    testDaoFolderName += "Dao";
									  } else {
									    testDaoFolderName += "dao";
									  }

									  testDaoPackage = testDaoPackage + "." + testDaoFolderName;
									  testDaoFolder = newJavaTestFolder.getFolder(new Path(testDaoFolderName));
									  if(!testDaoFolder.exists()) testDaoFolder.create(true ,true, new NullProgressMonitor());
									} else {
									  testDaoFolder = newJavaTestFolder;
									}
									/* E : Dao 폴더를 생성한다. */

									if(isCreateDaoSubFolder) {
									  testDaoPackage = testDaoPackage + "." + prefix;
									  testDaoFolder = testDaoFolder.getFolder(new Path(prefix));
									  if(!testDaoFolder.exists()) testDaoFolder.create(true ,true, new NullProgressMonitor());
									}

									if(testDaoTemplateFile == null || "".equals(testDaoTemplateFile)) {
									  testDaoTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/testDaoClassTemplate.txt";
									}

									/* Dao 파일내용을 가져온다.. */
									String testDaoContent = getSource(testDaoTemplateFile);
									testDaoContent = testDaoContent.replaceAll("\\[packagePath\\]", testDaoPackage);
									testDaoContent = StringUtils.replaceReservedWord(propertiesItem, prefix, testDaoContent);
									testDaoContent = StringUtils.replaceParameter(parameterType, testDaoContent);
									testDaoContent = StringUtils.replaceReturn(returnType, testDaoContent);
									/* S: Dao 파일을 생성한다. */
									ByteArrayInputStream testDaoFileStream = new ByteArrayInputStream(testDaoContent.getBytes("UTF-8"));

									IFile testDaoFile = testDaoFolder.getFile(new Path("Test" + capitalizePrefix + "Dao.java"));
									if(!testDaoFile.exists()) testDaoFile.create(testDaoFileStream ,true, new NullProgressMonitor());

									ICompilationUnit testDaoCompilationUnit = JavaCore.createCompilationUnitFrom(testDaoFile);
									if(importParameterVo != null) testDaoCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
									if(importReturnVo != null) testDaoCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());
									testDaoCompilationUnit.save(null, true);
								}

								System.out.println("Dao 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}
			/* E : Dao 생성 */

			/* S : Service 생성 */
							String servicePackage = null;

							if(isCreateService) {
								/* S : Service 폴더를 생성한다. */
								servicePackage = rootPackagePath;
								String serviceFolderName = "";
								IFolder serviceFolder = null;

								if(isCreateServiceFolder) {

									if(isAddPrefixServiceFolder) {
										serviceFolderName += prefix;
										serviceFolderName += "Service";
									} else {
										serviceFolderName += "service";
									}

									servicePackage = servicePackage + "." + serviceFolderName;
									serviceFolder = newJavaFolder.getFolder(new Path(serviceFolderName));

									if(!serviceFolder.exists()) serviceFolder.create(true ,true, new NullProgressMonitor());
								} else {
									serviceFolder = newJavaFolder;
								}
								/* E : Service 폴더를 생성한다. */

								if(isCreateServiceSubFolder) {
									servicePackage = servicePackage + "." + prefix;
									serviceFolder = serviceFolder.getFolder(new Path(prefix));
									if(!serviceFolder.exists()) serviceFolder.create(true ,true, new NullProgressMonitor());
								}

								/* S : ServiceImpl 폴더를 생성한다. */

								String serviceImplFolderName = "";
								String serviceImplPackage = servicePackage;
								IFolder serviceImplFolder = null;

								if(isCreateServiceImplFolder) {

									serviceImplFolderName += "impl";
									serviceImplPackage = serviceImplPackage + "." + serviceImplFolderName;
									serviceImplFolder = serviceFolder.getFolder(new Path(serviceImplFolderName));

									if(!serviceImplFolder.exists()) serviceImplFolder.create(true ,true, new NullProgressMonitor());
								} else {
									serviceImplFolder = newJavaFolder;
								}

								/* E : ServiceImpl 폴더를 생성한다. */

								if(serviceTemplateFile == null || "".equals(serviceTemplateFile)) {
									serviceTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/serviceClassTemplate.txt";
								}

								/* Service 파일내용을 가져온다.. */
								String serviceContent = getSource(serviceTemplateFile);
								serviceContent = serviceContent.replaceAll("\\[packagePath\\]", servicePackage);
								serviceContent = StringUtils.replaceReservedWord(propertiesItem, prefix, serviceContent);
								serviceContent = StringUtils.replaceParameter(parameterType, serviceContent);
								serviceContent = StringUtils.replaceReturn(returnType, serviceContent);
								if(connectionProfile != null) serviceContent = StringUtils.replaceRepeatWord(serviceContent, columns);
								/* S: Service 파일을 생성한다. */
								ByteArrayInputStream serviceFileStream = new ByteArrayInputStream(serviceContent.getBytes("UTF-8"));

								IFile serviceFile = serviceFolder.getFile(new Path(capitalizePrefix + "Service.java"));
								if(!serviceFile.exists()) serviceFile.create(serviceFileStream ,true, new NullProgressMonitor());

								ICompilationUnit serviceCompilationUnit = JavaCore.createCompilationUnitFrom(serviceFile);
								if(importParameterVo != null) serviceCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
								if(importReturnVo != null) serviceCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());
								if(isCreateDao) {
									serviceCompilationUnit.createImport(daoPackage + "." + capitalizePrefix + "Dao", null, new NullProgressMonitor());
								}

								if(isCreateServiceImpl) {

									ASTParser serviceParser = ASTParser.newParser(AST.JLS8);
									serviceParser.setSource(serviceCompilationUnit);
									serviceParser.setResolveBindings(true);

								    CompilationUnit serviceParserCompilationUnit = (CompilationUnit) serviceParser.createAST(null);
								    serviceParserCompilationUnit.recordModifications();

								    TypeDeclaration serviceTypeDeclaration = (TypeDeclaration) serviceParserCompilationUnit.types().get(0);
								    serviceTypeDeclaration.setInterface(true);

								    List<Object> serviceTypeDeclarationModifiers = serviceTypeDeclaration.modifiers();
								    List<Object> removeServiceTypeDeclarationModifiers = new ArrayList<Object>();
								    for(Object serviceTypeDeclarationModifier : serviceTypeDeclarationModifiers) {
								    	if(serviceTypeDeclarationModifier instanceof MarkerAnnotation
								    			|| serviceTypeDeclarationModifier instanceof NormalAnnotation
								    			|| serviceTypeDeclarationModifier instanceof SingleMemberAnnotation) {
								    		removeServiceTypeDeclarationModifiers.add(serviceTypeDeclarationModifier);
								    	}
								    }
									serviceTypeDeclarationModifiers.removeAll(removeServiceTypeDeclarationModifiers);

								    FieldDeclaration[] serviceFieldDeclarations = serviceTypeDeclaration.getFields();
								    for(FieldDeclaration fieldDeclaration : serviceFieldDeclarations) {
								    	fieldDeclaration.delete();
								    }

									for(MethodDeclaration methodDeclaration : serviceTypeDeclaration.getMethods()) {
									    List<Object> methodDeclarationModifiers = methodDeclaration.modifiers();
									    List<Object> removeMethodDeclarationModifiers = new ArrayList<Object>();
									    for(Object methodDeclarationModifier : methodDeclarationModifiers) {
									    	if(methodDeclarationModifier instanceof MarkerAnnotation
									    			|| methodDeclarationModifier instanceof NormalAnnotation
									    			|| methodDeclarationModifier instanceof SingleMemberAnnotation) {
									    		removeMethodDeclarationModifiers.add(methodDeclarationModifier);
									    	}
									    }
									    methodDeclaration.modifiers().removeAll(removeMethodDeclarationModifiers);
										if(methodDeclaration.getBody() != null) methodDeclaration.getBody().delete();
									}

									TextEdit serviceTextEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, serviceParserCompilationUnit.toString(), 0, serviceParserCompilationUnit.toString().length(), 0, null);
									Document serviceDocument = new Document(serviceParserCompilationUnit.toString());

									serviceTextEdit.apply(serviceDocument);

									serviceCompilationUnit.getBuffer().setContents(serviceDocument.get());
									serviceCompilationUnit.save(null, false);

									 /*Service 파일내용을 가져온다..*/
									String serviceImplContent = getSource(serviceTemplateFile);
									serviceImplContent = serviceImplContent.replaceAll("\\[packagePath\\]", serviceImplPackage);
									serviceImplContent = StringUtils.replaceReservedWord(propertiesItem, prefix, serviceImplContent);
									serviceImplContent = StringUtils.replaceParameter(parameterType, serviceImplContent);
									serviceImplContent = StringUtils.replaceReturn(returnType, serviceImplContent);
									if(connectionProfile != null) serviceImplContent = StringUtils.replaceRepeatWord(serviceImplContent, columns);
									ByteArrayInputStream serviceImplFileStream = new ByteArrayInputStream(serviceImplContent.getBytes("UTF-8"));

									IFile serviceImplFile = serviceImplFolder.getFile(new Path(capitalizePrefix + "ServiceImpl.java"));
									if(!serviceImplFile.exists())  {
										serviceImplFile.create(serviceImplFileStream ,true, new NullProgressMonitor());

										ICompilationUnit serviceImplCompilationUnit = JavaCore.createCompilationUnitFrom(serviceImplFile);
										if(importParameterVo != null) serviceImplCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
										if(importReturnVo != null) serviceImplCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());

										if(isCreateDao) {
											serviceImplCompilationUnit.createImport(daoPackage + "." + capitalizePrefix + "Dao", null, new NullProgressMonitor());
										}

										ASTParser serviceImplParser = ASTParser.newParser(AST.JLS8);
										serviceImplParser.setSource(serviceImplCompilationUnit);
									    serviceImplParser.setResolveBindings(true);

									    CompilationUnit serviceImplParserCompilationUnit = (CompilationUnit) serviceImplParser.createAST(null);

										AST serviceImplAst = serviceImplParserCompilationUnit.getAST();
										TypeDeclaration serviceImplTypeDeclaration = (TypeDeclaration) serviceImplParserCompilationUnit.types().get(0);
										serviceImplTypeDeclaration.setName(serviceImplAst.newSimpleName(serviceImplTypeDeclaration.getName() + "Impl"));

										serviceImplTypeDeclaration.superInterfaceTypes().add(serviceImplAst.newSimpleType(serviceImplAst.newSimpleName(capitalizePrefix + "Service")));

										for(MethodDeclaration methodDeclaration : serviceImplTypeDeclaration.getMethods()) {
										    MarkerAnnotation markerAnnotation = methodDeclaration.getAST().newMarkerAnnotation();
										    markerAnnotation.setTypeName(methodDeclaration.getAST().newName("Override"));
										    methodDeclaration.modifiers().add(0, markerAnnotation);
										}

										TextEdit serviceImplTextEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, serviceImplParserCompilationUnit.toString(), 0, serviceImplParserCompilationUnit.toString().length(), 0, null);
										Document serviceImplDocument = new Document(serviceImplParserCompilationUnit.toString());

										serviceImplTextEdit.apply(serviceImplDocument);

										serviceImplCompilationUnit.getBuffer().setContents(serviceImplDocument.get());
										serviceImplCompilationUnit.createImport(servicePackage + "." + capitalizePrefix + "Service",  null, new NullProgressMonitor());
										serviceImplCompilationUnit.save(null, false);
									}
								}

								if(isCreateTestService) {
									/* S : Service 폴더를 생성한다. */
									String testServicePackage = rootTestPackagePath;
									String testServiceFolderName = "";
									IFolder testServiceFolder = null;

									if(isCreateTestServiceFolder && isCreateServiceFolder) {

									  if(isAddPrefixServiceFolder) {
									    testServiceFolderName += prefix;
									    testServiceFolderName += "Service";
									  } else {
									    testServiceFolderName += "service";
									  }

									  testServicePackage = testServicePackage + "." + testServiceFolderName;
									  testServiceFolder = newJavaTestFolder.getFolder(new Path(testServiceFolderName));

									  if(!testServiceFolder.exists()) testServiceFolder.create(true ,true, new NullProgressMonitor());
									} else {
									  testServiceFolder = newJavaTestFolder;
									}
									/* E : Service 폴더를 생성한다. */

									if(isCreateServiceSubFolder) {
									  testServicePackage = testServicePackage + "." + prefix;
									  testServiceFolder = testServiceFolder.getFolder(new Path(prefix));
									  if(!testServiceFolder.exists()) testServiceFolder.create(true ,true, new NullProgressMonitor());
									}

									if(testServiceTemplateFile == null || "".equals(testServiceTemplateFile)) {
									  testServiceTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/testServiceClassTemplate.txt";
									}

									/* Service 파일내용을 가져온다.. */
									String testServiceContent = getSource(testServiceTemplateFile);
									testServiceContent = testServiceContent.replaceAll("\\[packagePath\\]", testServicePackage);
									testServiceContent = StringUtils.replaceReservedWord(propertiesItem, prefix, testServiceContent);
									testServiceContent = StringUtils.replaceParameter(parameterType, testServiceContent);
									testServiceContent = StringUtils.replaceReturn(returnType, testServiceContent);
									if(connectionProfile != null) testServiceContent = StringUtils.replaceRepeatWord(testServiceContent, columns);
									/* S: Service 파일을 생성한다. */
									ByteArrayInputStream testServiceFileStream = new ByteArrayInputStream(testServiceContent.getBytes("UTF-8"));

									IFile testServiceFile = testServiceFolder.getFile(new Path("Test" + capitalizePrefix + "Service.java"));
									if(!testServiceFile.exists()) testServiceFile.create(testServiceFileStream ,true, new NullProgressMonitor());
								}

								System.out.println("Service 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}
			/* E : Service 생성 */

			/* S : Contoller 생성 */

							if(isCreateController) {
								/* S : Contoller 폴더를 생성한다. */

								String controllerFolderName = "";
								String controllerPackage = rootPackagePath;
								IFolder controllerFolder = null;

								if(isCreateControllerFolder) {

									if(isAddPrefixControllerFolder) {
										controllerFolderName += capitalizePrefix;
										controllerFolderName += "Controller";
									} else {
										controllerFolderName += "controller";
									}

									controllerPackage = controllerPackage + "." + controllerFolderName;
									controllerFolder = newJavaFolder.getFolder(new Path(controllerFolderName));
									if(!controllerFolder.exists()) controllerFolder.create(true ,true, new NullProgressMonitor());
								} else {
									controllerFolder = newJavaFolder;
								}

								/* E : Contoller 폴더를 생성한다. */

								if(isCreateControllerSubFolder) {
									controllerPackage = controllerPackage + "." + prefix;
									controllerFolder = controllerFolder.getFolder(new Path(prefix));
									if(!controllerFolder.exists()) controllerFolder.create(true ,true, new NullProgressMonitor());
								}

								if(controllerTemplateFile == null || "".equals(controllerTemplateFile)) {
									controllerTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/controllerClassTemplate.txt";
								}

								/* Contoller 파일내용을 가져온다.. */
								String controllerContent = getSource(controllerTemplateFile);
								controllerContent = controllerContent.replaceAll("\\[packagePath\\]", controllerPackage);
								controllerContent = StringUtils.replaceReservedWord(propertiesItem, prefix, controllerContent);
								controllerContent = StringUtils.replaceParameter(parameterType, controllerContent);
								controllerContent = StringUtils.replaceReturn(returnType, controllerContent);
								if(connectionProfile != null) controllerContent = StringUtils.replaceRepeatWord(controllerContent, columns);
								/* S: Contoller 파일을 생성한다. */
								ByteArrayInputStream controllerFileStream = new ByteArrayInputStream(controllerContent.getBytes("UTF-8"));

								IFile controllerFile = controllerFolder.getFile(new Path(capitalizePrefix + "Controller.java"));
								if(!controllerFile.exists()) controllerFile.create(controllerFileStream ,true, new NullProgressMonitor());

								/* E: Contoller 파일을 생성한다. */

								ICompilationUnit controllerCompilationUnit = JavaCore.createCompilationUnitFrom(controllerFile);
								if(importParameterVo != null) controllerCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
								if(importReturnVo != null) controllerCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());

								if(isCreateService) {
									controllerCompilationUnit.createImport(servicePackage + "." + capitalizePrefix + "Service", null, new NullProgressMonitor());
								}

								if(isCreateTestController) {
									String testControllerFolderName = "";
									String testControllerPackage = rootTestPackagePath;
									IFolder testControllerFolder = null;

									if(isCreateTestControllerFolder && isCreateControllerFolder) {

									  if(isAddPrefixControllerFolder) {
										  testControllerFolderName += capitalizePrefix;
										  testControllerFolderName += "Controller";
									  } else {
										  testControllerFolderName += "controller";
									  }

									  testControllerPackage = testControllerPackage + "." + testControllerFolderName;
									  testControllerFolder = newJavaTestFolder.getFolder(new Path(testControllerFolderName));
									  if(!testControllerFolder.exists()) testControllerFolder.create(true ,true, new NullProgressMonitor());
									} else {
									  testControllerFolder = newJavaTestFolder;
									}

									/* E : Contoller 폴더를 생성한다. */

									if(isCreateControllerSubFolder) {
									  testControllerPackage = testControllerPackage + "." + prefix;
									  testControllerFolder = testControllerFolder.getFolder(new Path(prefix));
									  if(!testControllerFolder.exists()) testControllerFolder.create(true ,true, new NullProgressMonitor());
									}

									if(testControllerTemplateFile == null || "".equals(testControllerTemplateFile)) {
									  testControllerTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/testControllerClassTemplate.txt";
									}

									/* Contoller 파일내용을 가져온다.. */
									String testControllerContent = getSource(testControllerTemplateFile);
									testControllerContent = testControllerContent.replaceAll("\\[packagePath\\]", testControllerPackage);
									testControllerContent = StringUtils.replaceReservedWord(propertiesItem, prefix, testControllerContent);
									testControllerContent = StringUtils.replaceParameter(parameterType, testControllerContent);
									testControllerContent = StringUtils.replaceReturn(returnType, testControllerContent);
									if(connectionProfile != null) testControllerContent = StringUtils.replaceRepeatWord(testControllerContent, columns);
									/* S: Contoller 파일을 생성한다. */
									ByteArrayInputStream testControllerFileStream = new ByteArrayInputStream(testControllerContent.getBytes("UTF-8"));

									IFile testControllerFile = testControllerFolder.getFile(new Path("Test" + capitalizePrefix + "Controller.java"));
									if(!testControllerFile.exists()) testControllerFile.create(testControllerFileStream ,true, new NullProgressMonitor());
								}

								System.out.println("Controller 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}
			/* E : Contoller 생성 */

			/* S : Mapper 생성 */
							if(isCreateMapper) {

								/* S : Mapper 폴더를 생성한다. */

								IFolder mapperFolder = newJavaFolder.getWorkspace().getRoot().getFolder(new Path(mapperPath + "/" + prefix));
								if(!mapperFolder.exists()) mapperFolder.create(true ,true, new NullProgressMonitor());

								/* E : Mapper 폴더를 생성한다. */

								if(mapperTemplateFile == null || "".equals(mapperTemplateFile)) {
									mapperTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/mapperTemplate.txt";
								}

								/* Mapper 파일내용을 가져온다.. */
								String mapperContent = getSource(mapperTemplateFile);

								mapperContent = mapperContent.replaceAll("\\[namespace\\]", prefix + "Mapper");
								mapperContent = StringUtils.replaceReservedWord(propertiesItem, prefix, mapperContent);

								if(!prefix.equals(databaseTableName)) {
									mapperContent = mapperContent.replaceAll("\\[table\\]", databaseTableName.toUpperCase());
								} else {
									mapperContent = mapperContent.replaceAll("\\[table\\]", capitalizePrefix);
								}

								if(connectionProfile != null) {

									try {

										DocumentBuilder builder = factory.newDocumentBuilder();

										org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(mapperContent.getBytes("UTF-8")));

										Element documentElement = document.getDocumentElement();

										NodeList nodeList = documentElement.getChildNodes();

										for(int i = 0; i < nodeList.getLength(); i++) {
											Node item = nodeList.item(i);
											String content = item.getTextContent();
											if("select".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", StringUtils.replaceMapperSelecColumn(columns));
												content = content.replaceAll("\\[indexColumns\\]", StringUtils.replaceMapperIndexColumn(indexColumns));
											} else if("insert".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", StringUtils.replaceMapperInsertColumn(columns));
												content = content.replaceAll("\\[values\\]", StringUtils.replaceMapperInsertValue(columns));
											} else if("update".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", StringUtils.replaceMapperUpdateColumn(columns));
												content = content.replaceAll("\\[indexColumns\\]", StringUtils.replaceMapperIndexColumn(indexColumns));
											} else {
												content = content.replaceAll("\\[columns\\]", "");
												content = content.replaceAll("\\[indexColumns\\]", "");
											}
											item.setTextContent(content);
										}

										StringWriter writer = new StringWriter();
										Transformer transformer = TransformerFactory.newInstance().newTransformer();
										transformer.setOutputProperty(OutputKeys.INDENT, "yes");
										DocumentType documentType = document.getDoctype();
								        if(documentType != null) {
								            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
								            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
								        }
										transformer.transform(new DOMSource(document), new StreamResult(writer));

										mapperContent = writer.toString();

									} catch (ParserConfigurationException | SAXException | IOException | TransformerException | TransformerFactoryConfigurationError e) {
										e.printStackTrace();
									}

								}

								if(parameterType.toLowerCase().indexOf("hashmap") == -1) {
									mapperContent = mapperContent.replaceAll("\\[paramType\\]", parameterType.toLowerCase().charAt(0) + parameterType.substring(1));
								} else {
									mapperContent = mapperContent.replaceAll("\\[paramType\\]", "hashMap");
								}

								if(returnType.toLowerCase().indexOf("hashmap") == -1) {
									mapperContent = mapperContent.replaceAll("\\[resultType\\]", returnType.toLowerCase().charAt(0) + returnType.substring(1));
								} else {
									mapperContent = mapperContent.replaceAll("\\[resultType\\]", "hashMap");
								}

								mapperContent = mapperContent.replaceAll("&lt;", "<");
								mapperContent = mapperContent.replaceAll("&gt;", ">");

								IFile mapperFile = mapperFolder.getFile(new Path(prefix + "Mapper.xml"));
								if(!mapperFile.exists()) mapperFile.create(new ByteArrayInputStream(mapperContent.getBytes("UTF-8")) ,true, new NullProgressMonitor());

								System.out.println("Mapper 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}
			/* E : Mapper 생성 */

			/* S : Jsp 생성 */
							if(isCreateJsp) {

								/* S : Jsp 폴더를 생성한다. */

								IFolder jspFolder = null;

								jspFolder = newJavaFolder.getWorkspace().getRoot().getFolder(new Path(jspPath + "/" + prefix));
								if(!jspFolder.exists()) jspFolder.create(true ,true, new NullProgressMonitor());

								/* E : Jsp 폴더를 생성한다. */

								/* Jsp 파일내용을 가져온다.. */

								/* S : Jsp List */
								if(jspTemplateListFile == null || "".equals(jspTemplateListFile)) {
									jspTemplateListFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								String jspListContent = getSource(jspTemplateListFile);

								if(connectionProfile != null) jspListContent = StringUtils.replaceRepeatWord(jspListContent, columns);

								ByteArrayInputStream jspListFileStream = new ByteArrayInputStream(jspListContent.getBytes("UTF-8"));

								IFile jspListFile = jspFolder.getFile(new Path(prefix + "List.jsp"));
								if(!jspListFile.exists()) jspListFile.create(jspListFileStream ,true, new NullProgressMonitor());
								/* E : Jsp List */

								/* S : Jsp Post */
								if(jspTemplatePostFile == null || "".equals(jspTemplatePostFile)) {
									jspTemplatePostFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								String jspPostContent = getSource(jspTemplatePostFile);

								if(connectionProfile != null) jspPostContent = StringUtils.replaceRepeatWord(jspPostContent, columns);

								ByteArrayInputStream jspPostFileStream = new ByteArrayInputStream(jspPostContent.getBytes("UTF-8"));

								IFile jspPostFile = jspFolder.getFile(new Path(prefix + "Post.jsp"));
								if(!jspPostFile.exists()) jspPostFile.create(jspPostFileStream ,true, new NullProgressMonitor());
								/* E : Jsp Post */

								/* S : Jsp View */
								if(jspTemplateViewFile == null || "".equals(jspTemplateViewFile)) {
									jspTemplateViewFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								String jspViewContent = getSource(jspTemplateViewFile);

								if(connectionProfile != null) jspViewContent = StringUtils.replaceRepeatWord(jspViewContent, columns);

								ByteArrayInputStream jspViewFileStream = new ByteArrayInputStream(jspViewContent.getBytes("UTF-8"));

								IFile jspViewFile = jspFolder.getFile(new Path(prefix + "View.jsp"));
								if(!jspViewFile.exists()) jspViewFile.create(jspViewFileStream ,true, new NullProgressMonitor());
								/* E : Jsp View */

								System.out.println("Jsp 생성 : " + (System.currentTimeMillis() - lapTime) + " milliseconds");
								lapTime = System.currentTimeMillis();
							}
			/* E : Jsp 생성 */

						} catch (CoreException | UnsupportedEncodingException | MalformedTreeException | BadLocationException e) {
							e.printStackTrace();
						}
					}

					if(addSerialVersionUIDList.size() > 0) {
				        try {

							project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

							String[] classPaths = JavaRuntime.computeDefaultRuntimeClassPath(javaPackageFragment.getJavaProject());
					        URL[] urls = new URL[classPaths.length];

							for (int i = 0; i < classPaths.length; i++) {
								urls[i] = new URL("file:///" + classPaths[i] + File.separator);
							}

							URLClassLoader loader = new URLClassLoader(urls);

							for(SerialVersionUIDItem serialVersionUIDItem : addSerialVersionUIDList) {
								Class<?> clazz = loader.loadClass(serialVersionUIDItem.getClassName());
								ObjectStreamClass streamClass = ObjectStreamClass.lookup(clazz);

								ICompilationUnit compilationUnit = serialVersionUIDItem.getCompilationUnit();

								IType[] types = compilationUnit.getTypes();
								IType type = types[0];
								type.createField("\tstatic final long serialVersionUID = " + streamClass.getSerialVersionUID() + "L;\n\n", type.getFields()[0], false, new NullProgressMonitor());

								compilationUnit.save(null, true);
							}

							loader.close();

						} catch (ClassNotFoundException | IOException | CoreException e) {
							e.printStackTrace();
						}
					}
					System.out.println("총 생성 시간 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
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
				in = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile), "UTF8"));
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
