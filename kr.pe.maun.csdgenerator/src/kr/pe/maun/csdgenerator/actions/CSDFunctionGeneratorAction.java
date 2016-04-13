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
import kr.pe.maun.csdgenerator.dialogs.CSDFunctionGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;
import kr.pe.maun.csdgenerator.model.SerialVersionUIDItem;
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

public class CSDFunctionGeneratorAction implements IObjectActionDelegate {

	CSDFunctionGeneratorDialog dialog;

	ICompilationUnit serviceImplCompilationUnit;
	ICompilationUnit daoCompilationUnit;

	private ISelection selection;
	private Shell shell;

	ICompilationUnit serviceCompilationUnit;
	IImportDeclaration[] importDeclarations;

	@Override
	public void run(IAction action) {

		dialog = new CSDFunctionGeneratorDialog(shell, selection);

		if(selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for(TreePath treePath : treePaths) {
				if(treePath.getLastSegment() instanceof ICompilationUnit) {
					serviceCompilationUnit = (ICompilationUnit) treePath.getLastSegment();
				}
			}
		}

		List<String> importDaos = new ArrayList<String>();

		try {
			String namePrefix = serviceCompilationUnit.getElementName().replaceAll("Service.java", "");
			IType serviceType = serviceCompilationUnit.getType(namePrefix + "Service");
			boolean isServiceInterface = serviceType.isInterface();

			if(isServiceInterface) {
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
				searchEngine.search(createPattern, searchParticipants, SearchEngine.createWorkspaceScope(), requestor, null);

				importDeclarations = serviceImplCompilationUnit.getImports();
			} else {
				importDeclarations = serviceCompilationUnit.getImports();
			}

			for(IImportDeclaration importDeclaration : importDeclarations) {
				if(importDeclaration.getElementName().indexOf("Dao") > -1) {
					importDaos.add(importDeclaration.getElementName());
				}
			}

		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		dialog.setImportDaos(importDaos);

		if(dialog.open() != InputDialog.OK)
			return;

		Job job = new Job("Generator") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {

				SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

				subMonitor.setWorkRemaining(70);

				String prefix  = dialog.getPrefix();
				String parameterType = dialog.getParameterType();
				String returnType = dialog.getReturnType();

				IJavaProject javaProject = serviceCompilationUnit.getJavaProject();
				IProject project = javaProject.getProject();

				if(prefix != null) {

					prefix = StringUtils.toCamelCase(prefix);
					String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(true);
					factory.setNamespaceAware(true);

					long startTime = System.currentTimeMillis();

					IResource resource = (IResource) project.getAdapter(IResource.class);

					CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);
					CSDGeneratorPropertiesHelper propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

					IConnectionProfile connectionProfile = dialog.getConnectionProfile();
					DatabaseResource databaseResource = connectionProfile == null ? null : new DatabaseResource(connectionProfile);
					String databaseTableName = dialog.getDatabaseTableName();

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
					boolean isCreateSelectCount = dialog.isCreateSelectCount();
					boolean isCreateSelectList = dialog.isCreateSelectList();
					boolean isCreateSelectOne = dialog.isCreateSelectOne();
					boolean isCreateInsert = dialog.isCreateInsert();
					boolean isCreateUpdate = dialog.isCreateUpdate();
					boolean isCreateDelete = dialog.isCreateDelete();

					String serviceTemplate = "";
					String serviceSelectCountTemplate = propertiesHelper.getServiceFunctionSelectCountTemplate();
					String serviceSelectListTemplate = propertiesHelper.getServiceFunctionSelectListTemplate();
					String serviceSelectOneTemplate = propertiesHelper.getServiceFunctionSelectOneTemplate();
					String serviceInsertTemplate = propertiesHelper.getServiceFunctionInsertTemplate();
					String serviceUpdateTemplate = propertiesHelper.getServiceFunctionUpdateTemplate();
					String serviceDeleteTemplate = propertiesHelper.getServiceFunctionDeleteTemplate();

					String daoTemplate = "";
					String daoSelectCountTemplate = propertiesHelper.getDaoFunctionSelectCountTemplate();
					String daoSelectListTemplate = propertiesHelper.getDaoFunctionSelectListTemplate();
					String daoSelectOneTemplate = propertiesHelper.getDaoFunctionSelectOneTemplate();
					String daoInsertTemplate = propertiesHelper.getDaoFunctionInsertTemplate();
					String daoUpdateTemplate = propertiesHelper.getDaoFunctionUpdateTemplate();
					String daoDeleteTemplate = propertiesHelper.getDaoFunctionDeleteTemplate();

					String mapperTemplate = "";
					String mapperSelectCountTemplate = propertiesHelper.getMapperFunctionSelectCountTemplate();
					String mapperSelectListTemplate = propertiesHelper.getMapperFunctionSelectListTemplate();
					String mapperSelectOneTemplate = propertiesHelper.getMapperFunctionSelectOneTemplate();
					String mapperInsertTemplate = propertiesHelper.getMapperFunctionInsertTemplate();
					String mapperUpdateTemplate = propertiesHelper.getMapperFunctionUpdateTemplate();
					String mapperDeleteTemplate = propertiesHelper.getMapperFunctionDeleteTemplate();

					String selectImportDao = dialog.getSelectImportDao();
					String prefixDao = selectImportDao != null ? selectImportDao.substring(selectImportDao.lastIndexOf(".") + 1).replace("Dao", "") : null;

					String namePrefix = serviceCompilationUnit.getElementName().replaceAll("Service.java", "");

					String javaVoBuildPath = "";

					try {
						IClasspathEntry[] classpaths = javaProject.getRawClasspath();
						for (IClasspathEntry classpath : classpaths) {
							IPath path = classpath.getPath();
							if (voPath != null && voPath.indexOf(path.toString()) > -1) {
								javaVoBuildPath = path.removeFirstSegments(1).toString();
							}
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}

					String voPackage = voPath.replace("/", ".").substring(voPath.lastIndexOf(javaVoBuildPath) + javaVoBuildPath.length() + 1);

					String importParameterVo = null;
					String importReturnVo = null;

					if(parameterType.toLowerCase().indexOf("hashmap") == -1) {
						importParameterVo = voPackage + "." + parameterType;
					}

					if(returnType.toLowerCase().indexOf("hashmap") == -1) {
						importReturnVo = voPackage + "." + returnType;
					}

					if(isCreateVo && databaseTableName != null && connectionProfile != null) {
						try {

							parameterType = capitalizePrefix + "Vo";
							returnType = capitalizePrefix + "Vo";
							importParameterVo = voPackage + "." + capitalizePrefix + "Vo";
							importReturnVo = voPackage + "." + capitalizePrefix + "Vo";

							List<ColumnItem> columns = databaseResource.getColumns(databaseTableName);

							IFolder voFolder = project.getWorkspace().getRoot().getFolder(new Path(voPath + "/"));
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
								gettersAndSetters.append("( ");
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

							ByteArrayInputStream voFileStream = new ByteArrayInputStream(voContent.getBytes());

							IFile voFile = voFolder.getFile(new Path(capitalizePrefix + "Vo.java"));
							if(!voFile.exists()) voFile.create(voFileStream ,true, new NullProgressMonitor());

							IFile searchVoFile = null;

							if(isCreateSearchVo) {
								String searchVoContent = getSource("platform:/plugin/kr.pe.maun.csdgenerator/resource/template/voTemplate.txt");

								searchVoContent = searchVoContent.replaceAll("\\[packagePath\\]", voPackage);
								searchVoContent = searchVoContent.replaceAll("\\[capitalizePrefix\\]", "Search" + capitalizePrefix);
								searchVoContent = searchVoContent.replaceAll("\\[value\\]", valueBuffer.toString());
								searchVoContent = searchVoContent.replaceAll("\\[GettersAndSetters\\]", "");

								ByteArrayInputStream searchVoFileStream = new ByteArrayInputStream(searchVoContent.getBytes());

								searchVoFile = voFolder.getFile(new Path("Search" + capitalizePrefix + "Vo.java"));
								if(!searchVoFile.exists()) searchVoFile.create(searchVoFileStream ,true, new NullProgressMonitor());
							}

							if(columnsSize > 0) {

								ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(voFile);

								Set<String> keySet = importDeclaration.keySet();
								for(String key : keySet) {
									compilationUnit.createImport(importDeclaration.get(key), null, new NullProgressMonitor());
								}

								compilationUnit.save(null, true);

								if(isCreateSearchVo && searchVoFile != null) {
									ICompilationUnit compilationSearchUnit = JavaCore.createCompilationUnitFrom(searchVoFile);
									for(String key : keySet) {
										compilationSearchUnit.createImport(importDeclaration.get(key), null, new NullProgressMonitor());
									}

									compilationSearchUnit.save(null, true);
								}

						        try {

									project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

									String[] classPaths = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);
							        URL[] urls = new URL[classPaths.length];

									for (int i = 0; i < classPaths.length; i++) {
										urls[i] = new URL("file:///" + classPaths[i] + File.separator);
									}

									URLClassLoader loader = new URLClassLoader(urls);

									Class<?> clazz = loader.loadClass(voPackage + "." + capitalizePrefix + "Vo");
									ObjectStreamClass streamClass = ObjectStreamClass.lookup(clazz);

									IType[] types = compilationUnit.getTypes();
									IType type = types[0];
									type.createField("\tstatic final long serialVersionUID = " + streamClass.getSerialVersionUID() + "L;\n\n", type.getFields()[0], false, new NullProgressMonitor());

									compilationUnit.save(null, true);

									loader.close();

								} catch (ClassNotFoundException | IOException | CoreException e) {
									e.printStackTrace();
								}
							}

							if(myBatisSettingFile != null) {

								try {

									org.w3c.dom.Document myBatisSettingDocument = null;

									String myBatisSettingContent = getSource(myBatisSettingFile);
									DocumentBuilder builder = factory.newDocumentBuilder();
									myBatisSettingDocument = builder.parse(new ByteArrayInputStream(myBatisSettingContent.getBytes()));

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

									IFile _myBatisSettingFile = project.getWorkspace().getRoot().getFile(Path.fromOSString(myBatisSettingFile));

									if(_myBatisSettingFile.exists()) {
										_myBatisSettingFile.create(new ByteArrayInputStream(myBatisSettingDocument.toString().getBytes()) ,true, new NullProgressMonitor());
									} else {
										BufferedWriter myBatisSetting = new BufferedWriter(new FileWriter(myBatisSettingFile));
										myBatisSetting.write(writer.toString());
										myBatisSetting.close();
									}

								} catch (IOException | TransformerException | TransformerFactoryConfigurationError | CoreException | SAXException | ParserConfigurationException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if(isCreateService) {
						try {

							IType serviceType = serviceCompilationUnit.getType(namePrefix + "Service");
							boolean isServiceInterface = serviceType.isInterface();

							if(isCreateSelectCount) {
								serviceTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, serviceSelectCountTemplate));
							}

							if(isCreateSelectList) {
								serviceTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, serviceSelectListTemplate));
							}

							if(isCreateSelectOne) {
								serviceTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, serviceSelectOneTemplate));
							}

							if(isCreateInsert) {
								serviceTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, serviceInsertTemplate));
							}

							if(isCreateUpdate) {
								serviceTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(serviceUpdateTemplate));
							}

							if(isCreateDelete) {
								serviceTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(serviceDeleteTemplate));
							}

							serviceTemplate = StringUtils.replaceParameter(parameterType, serviceTemplate);
							serviceTemplate = StringUtils.replaceReturn(returnType, serviceTemplate);
							if(prefixDao != null) {
								serviceTemplate = serviceTemplate.replaceAll(prefix.toLowerCase() + "Dao", prefixDao.substring(0, 1).toLowerCase() + prefixDao.substring(1) + "Dao");
							}

							if(importParameterVo != null) serviceCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
							if(importReturnVo != null) serviceCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());

							String serviceContent = serviceCompilationUnit.getSource();
							serviceContent += serviceTemplate;
							serviceContent += "\n}";

							if(isServiceInterface) {
/*
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
*/

								if(importParameterVo != null) serviceImplCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
								if(importReturnVo != null) serviceImplCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());

								String serviceImplContent = serviceImplCompilationUnit.getSource();
								serviceImplContent = serviceImplContent.substring(0, serviceImplContent.lastIndexOf("}"));
								serviceImplContent += serviceTemplate;
								serviceImplContent += "\n}";

								IFile serviceImplFile = (IFile) serviceImplCompilationUnit.getResource();
								serviceImplFile.setContents(new ByteArrayInputStream(serviceImplContent.getBytes("UTF-8")), true ,true, new NullProgressMonitor());
/*
								ASTParser serviceImplParser = ASTParser.newParser(AST.JLS8);
								serviceImplParser.setSource(serviceImplContent.toCharArray());
							    serviceImplParser.setResolveBindings(true);

							    CompilationUnit serviceImplParserCompilationUnit = (CompilationUnit) serviceImplParser.createAST(null);

								AST serviceImplAst = serviceImplParserCompilationUnit.getAST();
								TypeDeclaration serviceImplTypeDeclaration = (TypeDeclaration) serviceImplParserCompilationUnit.types().get(0);
								serviceImplTypeDeclaration.setName(serviceImplAst.newSimpleName(serviceImplTypeDeclaration.getName() + "Impl"));

								for(MethodDeclaration methodDeclaration : serviceImplTypeDeclaration.getMethods()) {
									boolean addOverrideAnnotation = true;
								    List<Object> methodDeclarationModifiers = methodDeclaration.modifiers();
								    for(Object methodDeclarationModifier : methodDeclarationModifiers) {
								    	if(methodDeclarationModifier instanceof MarkerAnnotation
								    			&& "Override".equals(((MarkerAnnotation) methodDeclarationModifier).getTypeName())) {
								    		addOverrideAnnotation = false;
								    	}
								    }

								    if(addOverrideAnnotation) {
								    	MarkerAnnotation markerAnnotation = methodDeclaration.getAST().newMarkerAnnotation();
									    markerAnnotation.setTypeName(methodDeclaration.getAST().newName("Override"));
									    methodDeclaration.modifiers().add(0, markerAnnotation);
								    }
								}

								serviceImplFile.setContents(new ByteArrayInputStream(serviceImplParserCompilationUnit.toString().getBytes("UTF-8")), true ,true, new NullProgressMonitor());
*/
								ASTParser serviceParser = ASTParser.newParser(AST.JLS8);
								serviceParser.setSource(serviceContent.toCharArray());
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

								CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
								TextEdit serviceTextEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, serviceParserCompilationUnit.toString(), 0, serviceParserCompilationUnit.toString().length(), 0, null);
								Document serviceDocument = new Document(serviceParserCompilationUnit.toString());

								serviceTextEdit.apply(serviceDocument);

								IFile serviceFile = (IFile) serviceCompilationUnit.getResource();
								serviceFile.setContents(new ByteArrayInputStream(serviceDocument.get().getBytes("UTF-8")), true ,true, new NullProgressMonitor());
							} else {
								IFile serviceFile = (IFile) serviceCompilationUnit.getResource();
								serviceFile.setContents(new ByteArrayInputStream(serviceContent.getBytes("UTF-8")), true ,true, new NullProgressMonitor());
							}

						} catch (CoreException | UnsupportedEncodingException | MalformedTreeException | BadLocationException e) {
							e.printStackTrace();
						}
					}

					if(isCreateDao) {
						try {
							if(isCreateSelectCount) {
								daoTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, daoSelectCountTemplate));
							}

							if(isCreateSelectList) {
								daoTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, daoSelectListTemplate));
							}

							if(isCreateSelectOne) {
								daoTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, daoSelectOneTemplate));
							}

							if(isCreateInsert) {
								daoTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, daoInsertTemplate));
							}

							if(isCreateUpdate) {
								daoTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(daoUpdateTemplate));
							}

							if(isCreateDelete) {
								daoTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(daoDeleteTemplate));
							}
							daoTemplate = StringUtils.replaceParameter(parameterType, daoTemplate);
							daoTemplate = StringUtils.replaceReturn(returnType, daoTemplate);
							daoTemplate = daoTemplate.replaceAll("\\[namespace\\]", prefixDao.substring(0, 1).toLowerCase() + prefixDao.substring(1) + "Mapper");

							SearchRequestor requestor = new SearchRequestor() {
								@Override
								public void acceptSearchMatch(SearchMatch searchMatch) throws CoreException {
									if(searchMatch.getElement() instanceof ResolvedSourceType) {
										ResolvedSourceType resolvedSourceType = (ResolvedSourceType) searchMatch.getElement();
										if(resolvedSourceType.getParent().getElementType() == IJavaElement.COMPILATION_UNIT) {
											daoCompilationUnit = (ICompilationUnit) ((ResolvedSourceType) searchMatch.getElement()).getParent();
										}
									}
								}
							};

							SearchEngine searchEngine = new SearchEngine();
							SearchParticipant[] searchParticipants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
							SearchPattern createPattern = SearchPattern.createPattern(selectImportDao, IJavaSearchConstants.TYPE, IJavaSearchConstants.CONSTRUCTOR, SearchPattern.R_FULL_MATCH);
							searchEngine.search(createPattern, searchParticipants, SearchEngine.createWorkspaceScope(), requestor, null);

							if(importParameterVo != null) daoCompilationUnit.createImport(importParameterVo, null, new NullProgressMonitor());
							if(importReturnVo != null) daoCompilationUnit.createImport(importReturnVo, null, new NullProgressMonitor());

							String daoContent = daoCompilationUnit.getSource();
							daoContent = daoContent.substring(0, daoContent.lastIndexOf("}"));
							daoContent += daoTemplate;
							daoContent += "\n}";

							IFile daoFile = (IFile) daoCompilationUnit.getResource();
							daoFile.setContents(new ByteArrayInputStream(daoContent.getBytes("UTF-8")), true ,true, new NullProgressMonitor());

						} catch (CoreException | UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

					if(isCreateMapper && databaseTableName != null && connectionProfile != null) {
						try {
							if(isCreateSelectCount) {
								mapperTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, mapperSelectCountTemplate));
							}

							if(isCreateSelectList) {
								mapperTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, mapperSelectListTemplate));
							}

							if(isCreateSelectOne) {
								mapperTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, mapperSelectOneTemplate));
							}

							if(isCreateInsert) {
								mapperTemplate += StringUtils.appedFirstAndEndNewLine(StringUtils.replaceReservedWord(propertiesItem, prefix, mapperInsertTemplate));
							}

							if(isCreateUpdate) {
								mapperTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(mapperUpdateTemplate));
							}

							if(isCreateDelete) {
								mapperTemplate += StringUtils.replaceReservedWord(propertiesItem, prefix, StringUtils.appedFirstAndEndNewLine(mapperDeleteTemplate));
							}

							List<ColumnItem> columns = databaseResource.getColumns(databaseTableName);
							List<String> indexColumns = databaseResource.getIndexColumns(databaseTableName);

							mapperTemplate = mapperTemplate.replaceAll("\\[table\\]", databaseTableName);
							mapperTemplate = mapperTemplate.replaceAll("\\[columns\\]", updateColumn(columns));
							mapperTemplate = mapperTemplate.replaceAll("\\[values\\]", insertValue(columns));
							mapperTemplate = mapperTemplate.replaceAll("\\[indexColumns\\]", indexColumn(indexColumns));

							if(parameterType.toLowerCase().indexOf("hashmap") == -1) {
								mapperTemplate = mapperTemplate.replaceAll("\\[paramType\\]", parameterType.toLowerCase().charAt(0) + parameterType.substring(1));
							} else {
								mapperTemplate = mapperTemplate.replaceAll("\\[paramType\\]", "hashMap");
							}

							if(returnType.toLowerCase().indexOf("hashmap") == -1) {
								mapperTemplate = mapperTemplate.replaceAll("\\[resultType\\]", returnType.toLowerCase().charAt(0) + returnType.substring(1));
							} else {
								mapperTemplate = mapperTemplate.replaceAll("\\[resultType\\]", "hashMap");
							}

							mapperTemplate = mapperTemplate.replaceAll("&lt;", "<");
							mapperTemplate = mapperTemplate.replaceAll("&gt;", ">");

							String mapperFilePath = mapperPath + "/" + prefixDao.substring(0, 1).toLowerCase() + prefixDao.substring(1) + "/" + prefixDao.substring(0, 1).toLowerCase() + prefixDao.substring(1) + "Mapper.xml";

							String mapperContent = getSource(project.getWorkspace().getRoot().getLocation().toOSString() + mapperFilePath);
							mapperContent = mapperContent.substring(0, mapperContent.lastIndexOf("</mapper>"));
							mapperContent += mapperTemplate;
							mapperContent += "\n\n</mapper>";

							IFile mapperFile = project.getWorkspace().getRoot().getFile(new Path(mapperFilePath));
							mapperFile.setContents(new ByteArrayInputStream(mapperContent.getBytes()), true, true, new NullProgressMonitor());
						} catch (CoreException e) {
							e.printStackTrace();
						}
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
