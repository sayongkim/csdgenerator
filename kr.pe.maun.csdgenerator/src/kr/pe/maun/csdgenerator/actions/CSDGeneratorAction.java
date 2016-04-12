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

				IFolder folder = null;
				IPackageFragment packageFragment = null;
				String prefix  = dialog.getPrefix();
				boolean isCreateFolder = dialog.isCreateFolder();
				boolean isParentLocation = dialog.isParentLocation();

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

					String company = propertiesItem.getCompany() != null ? propertiesItem.getCompany() : "";
					String author = propertiesItem.getAuthor() != null ? propertiesItem.getAuthor() : System.getProperty("user.name");

					String generalTemplateController = propertiesHelper.getGeneralTemplateController(dialog.getTemplateGroupName());
					String generalTemplateService = propertiesHelper.getGeneralTemplateService(dialog.getTemplateGroupName());
					String generalTemplateDao = propertiesHelper.getGeneralTemplateDao(dialog.getTemplateGroupName());
					String generalTemplateMapper = propertiesHelper.getGeneralTemplateMapper(dialog.getTemplateGroupName());
					String generalTemplateJsp = propertiesHelper.getGeneralTemplateJsp(dialog.getTemplateGroupName());

					boolean isCreateController = dialog.isCreateController();
					boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
					boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();
					boolean isCreateControllerSubFolder = propertiesItem.getCreateControllerSubFolder();
					String controllerTemplateFile = generalTemplateController == null ? null : propertiesHelper.getControllerTemplateFile(generalTemplateController);

					boolean isCreateService = dialog.isCreateService();
					boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
					boolean isCreateServiceSubFolder = propertiesItem.getCreateServiceSubFolder();
					boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();
					String serviceTemplateFile = generalTemplateService == null ? null : propertiesHelper.getServiceTemplateFile(generalTemplateService);

					boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
					boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImpl();

					boolean isCreateDao = dialog.isCreateDao();
					boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
					boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();
					boolean isCreateDaoSubFolder = propertiesItem.getCreateDaoSubFolder();
					String daoTemplateFile = generalTemplateDao == null ? null : propertiesHelper.getDaoTemplateFile(generalTemplateDao);
					String myBatisSettingFile = propertiesItem.getMyBatisSettingFile();

					boolean isCreateMapper = dialog.isCreateMapper();
					String mapperPath = propertiesItem.getMapperPath();
					String mapperTemplateFile = generalTemplateMapper == null ? null : propertiesHelper.getMapperTemplateFile(generalTemplateMapper);

					boolean isCreateVo = dialog.isCreateVo();
					boolean isCreateSearchVo = propertiesItem.getCreateSearchVo();
					String voPath = propertiesItem.getVoPath();

					boolean isCreateJsp = dialog.isCreateJsp();
					String jspPath = propertiesItem.getJspPath();
					String jspTemplateListFile =  generalTemplateMapper == null ? null : propertiesHelper.getJspTemplateListFile(generalTemplateJsp);
					String jspTemplatePostFile = generalTemplateMapper == null ? null : propertiesHelper.getJspTemplatePostFile(generalTemplateJsp);
					String jspTemplateViewFile = generalTemplateMapper == null ? null : propertiesHelper.getJspTemplateListFile(generalTemplateJsp);

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

					if(isParentLocation) {
						packageFullPath = packageFullPath.substring(0, packageFullPath.lastIndexOf("."));
						folder = (IFolder) folder.getParent().getAdapter(IFolder.class);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd");
					String date = dateFormat.format(new Date());

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
							myBatisSettingDocument = builder.parse(new ByteArrayInputStream(myBatisSettingContent.getBytes()));
						} catch (Exception e){
							e.printStackTrace();
						}
					}

					for(String databaseTableName : databaseTables) {

						String rootPackagePath = packageFullPath;

						prefix = databaseTableName;

						if(pattern != null) {
							Matcher matcher = pattern.matcher(prefix);
							if(matcher.matches() && matcher.groupCount() > 0) {
								prefix = matcher.group(1);
							}
						}

						prefix = StringUtils.toCamelCase(prefix);

						IFolder newFolder = folder.getFolder(new Path(prefix));

						String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
						String upperPrefix = prefix.toUpperCase();
						String lowerPrefix = prefix.toLowerCase();

						try {

							/* 폴더를 생성한다. */
							if(isCreateFolder
									&& (isCreateController || isCreateService || isCreateDao)) {
								if(!newFolder.exists()) newFolder.create(true ,true, new NullProgressMonitor());
								rootPackagePath = rootPackagePath + "." + prefix;
							} else {
								newFolder = folder;
							}

			/* S : Dao 생성 */
							String daoPackage = null;
							if(isCreateDao) {
								/* S : Dao 폴더를 생성한다. */
								daoPackage = rootPackagePath;
								String daoFolderName = "";
								IFolder daoFolder = null;

								if(isCreateDaoFolder) {

									if(isAddPrefixDaoFolder) daoFolderName += capitalizePrefix;

									daoFolderName += "Dao";
									daoPackage = daoPackage + "." + daoFolderName;
									daoFolder = newFolder.getFolder(new Path(daoFolderName));
									if(!daoFolder.exists()) daoFolder.create(true ,true, new NullProgressMonitor());
								} else {
									daoFolder = newFolder;
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
								daoContent = daoContent.replaceAll("\\[prefix\\]", prefix);
								daoContent = daoContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
								daoContent = daoContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								daoContent = daoContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								daoContent = daoContent.replaceAll("\\[company\\]", company);
								daoContent = daoContent.replaceAll("\\[author\\]", author);
								daoContent = daoContent.replaceAll("\\[date\\]", date);
								/* S: Dao 파일을 생성한다. */
								ByteArrayInputStream daoFileStream = new ByteArrayInputStream(daoContent.getBytes("UTF-8"));

								IFile daoFile = daoFolder.getFile(new Path(capitalizePrefix + "Dao.java"));
								if(!daoFile.exists()) daoFile.create(daoFileStream ,true, new NullProgressMonitor());

								System.out.println("Dao 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
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

									if(isAddPrefixServiceFolder) serviceFolderName += capitalizePrefix;

									serviceFolderName += "Service";
									servicePackage = servicePackage + "." + serviceFolderName;
									serviceFolder = newFolder.getFolder(new Path(serviceFolderName));

									if(!serviceFolder.exists()) serviceFolder.create(true ,true, new NullProgressMonitor());
								} else {
									serviceFolder = newFolder;
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

									serviceImplFolderName += "Impl";
									serviceImplPackage = serviceImplPackage + "." + serviceImplFolderName;
									serviceImplFolder = serviceFolder.getFolder(new Path(serviceImplFolderName));

									if(!serviceImplFolder.exists()) serviceImplFolder.create(true ,true, new NullProgressMonitor());
								} else {
									serviceImplFolder = newFolder;
								}

								/* E : ServiceImpl 폴더를 생성한다. */

								if(serviceTemplateFile == null || "".equals(serviceTemplateFile)) {
									if(isCreateServiceImpl) {
										serviceTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/serviceInterfaceTemplate.txt";
									} else {
										serviceTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/serviceClassTemplate.txt";
									}
								}

								/* Service 파일내용을 가져온다.. */
								String serviceContent = getSource(serviceTemplateFile);
								serviceContent = serviceContent.replaceAll("\\[packagePath\\]", servicePackage);
								if(isCreateDao) {
									serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + capitalizePrefix + "Dao");
								} else {
									serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", "");
								}
								serviceContent = serviceContent.replaceAll("\\[prefix\\]", prefix);
								serviceContent = serviceContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
								serviceContent = serviceContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								serviceContent = serviceContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								serviceContent = serviceContent.replaceAll("\\[company\\]", company);
								serviceContent = serviceContent.replaceAll("\\[author\\]", author);
								serviceContent = serviceContent.replaceAll("\\[date\\]", date);

								/* S: Service 파일을 생성한다. */
								ByteArrayInputStream serviceFileStream = new ByteArrayInputStream(serviceContent.getBytes("UTF-8"));

								if(isCreateServiceImpl) {

									CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

									IFile serviceFile = serviceFolder.getFile(new Path(capitalizePrefix + "Service.java"));
									if(!serviceFile.exists()) serviceFile.create(serviceFileStream ,true, new NullProgressMonitor());

									ICompilationUnit serviceCompilationUnit = JavaCore.createCompilationUnitFrom(serviceFile);

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
									if(isCreateDao) {
										serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + capitalizePrefix + "Dao");
									} else {
										serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", "");
									}
									serviceImplContent = serviceImplContent.replaceAll("\\[prefix\\]", prefix);
									serviceImplContent = serviceImplContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
									serviceImplContent = serviceImplContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
									serviceImplContent = serviceImplContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
									serviceImplContent = serviceImplContent.replaceAll("\\[company\\]", company);
									serviceImplContent = serviceImplContent.replaceAll("\\[author\\]", author);
									serviceImplContent = serviceImplContent.replaceAll("\\[date\\]", date);

									ByteArrayInputStream serviceImplFileStream = new ByteArrayInputStream(serviceImplContent.getBytes("UTF-8"));

									IFile serviceImplFile = serviceImplFolder.getFile(new Path(capitalizePrefix + "ServiceImpl.java"));
									if(!serviceImplFile.exists())  {
										serviceImplFile.create(serviceImplFileStream ,true, new NullProgressMonitor());

										ICompilationUnit serviceImplCompilationUnit = JavaCore.createCompilationUnitFrom(serviceImplFile);

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

								} else {
									IFile serviceFile = serviceFolder.getFile(new Path(capitalizePrefix + "Service.java"));
									if(!serviceFile.exists()) serviceFile.create(serviceFileStream ,true, new NullProgressMonitor());
								}

								System.out.println("Service 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
							}
			/* E : Service 생성 */

			/* S : Contoller 생성 */

							if(isCreateController) {
								/* S : Contoller 폴더를 생성한다. */

								String controllerFolderName = "";
								String controllerPackage = rootPackagePath;
								IFolder controllerFolder = null;

								if(isCreateControllerFolder) {

									if(isAddPrefixControllerFolder) controllerFolderName += capitalizePrefix;

									controllerFolderName += "Controller";
									controllerPackage = controllerPackage + "." + controllerFolderName;
									controllerFolder = newFolder.getFolder(new Path(controllerFolderName));
									if(!controllerFolder.exists()) controllerFolder.create(true ,true, new NullProgressMonitor());
								} else {
									controllerFolder = newFolder;
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
								if(servicePackage == null) {
									controllerContent = controllerContent.replaceAll("\\[serviceFullPath\\]", "");
								} else {
									controllerContent = controllerContent.replaceAll("\\[serviceFullPath\\]", servicePackage + "." + capitalizePrefix + "Service");
								}
								controllerContent = controllerContent.replaceAll("\\[prefix\\]", prefix);
								controllerContent = controllerContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
								controllerContent = controllerContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								controllerContent = controllerContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								controllerContent = controllerContent.replaceAll("\\[company\\]", company);
								controllerContent = controllerContent.replaceAll("\\[author\\]", author);
								controllerContent = controllerContent.replaceAll("\\[date\\]", date);
								/* S: Contoller 파일을 생성한다. */
								ByteArrayInputStream controllerFileStream = new ByteArrayInputStream(controllerContent.getBytes("UTF-8"));

								IFile controllerFile = controllerFolder.getFile(new Path(capitalizePrefix + "Controller.java"));
								if(!controllerFile.exists()) controllerFile.create(controllerFileStream ,true, new NullProgressMonitor());

								/* E: Contoller 파일을 생성한다. */

								System.out.println("Controller 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
							}
			/* E : Contoller 생성 */

			/* S : Mapper 생성 */
							if(isCreateMapper) {

								/* S : Mapper 폴더를 생성한다. */

								IFolder mapperFolder = null;

								mapperFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(mapperPath + "/" + prefix));
								if(!mapperFolder.exists()) mapperFolder.create(true ,true, new NullProgressMonitor());

								/* E : Mapper 폴더를 생성한다. */

								if(mapperTemplateFile == null || "".equals(mapperTemplateFile)) {
									mapperTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/mapperTemplate.txt";
								}

								/* Mapper 파일내용을 가져온다.. */
								String mapperContent = getSource(mapperTemplateFile);

								mapperContent = mapperContent.replaceAll("\\[namespace\\]", prefix + "Mapper");
								mapperContent = mapperContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
								mapperContent = mapperContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
								mapperContent = mapperContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
								mapperContent = mapperContent.replaceAll("\\[company\\]", company);
								mapperContent = mapperContent.replaceAll("\\[author\\]", author);
								mapperContent = mapperContent.replaceAll("\\[date\\]", date);

								if(!prefix.equals(databaseTableName)) {
									mapperContent = mapperContent.replaceAll("\\[table\\]", databaseTableName);
								} else {
									mapperContent = mapperContent.replaceAll("\\[table\\]", capitalizePrefix);
								}

								if(connectionProfile != null) {

									List<ColumnItem> columns = databaseResource.getColumns(databaseTableName);
									List<String> indexColumns = databaseResource.getIndexColumns(databaseTableName);

									try {
										DocumentBuilder builder = factory.newDocumentBuilder();

										org.w3c.dom.Document document = builder.parse(new ByteArrayInputStream(mapperContent.getBytes()));

										Element documentElement = document.getDocumentElement();

										NodeList nodeList = documentElement.getChildNodes();

										for(int i = 0; i < nodeList.getLength(); i++) {
											Node item = nodeList.item(i);
											String content = item.getTextContent();
											if("select".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", selecColumn(columns));
												content = content.replaceAll("\\[indexColumns\\]", indexColumn(indexColumns));
											} else if("insert".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", insertColumn(columns));
												content = content.replaceAll("\\[values\\]", insertValue(columns));
											} else if("update".equals(item.getNodeName())) {
												content = content.replaceAll("\\[columns\\]", updateColumn(columns));
												content = content.replaceAll("\\[indexColumns\\]", indexColumn(indexColumns));
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

								mapperContent = mapperContent.replaceAll("&lt;", "<");
								mapperContent = mapperContent.replaceAll("&gt;", ">");

								IFile mapperFile = mapperFolder.getFile(new Path(prefix + "Mapper.xml"));
								if(!mapperFile.exists()) mapperFile.create(new ByteArrayInputStream(mapperContent.getBytes()) ,true, new NullProgressMonitor());

								System.out.println("Mapper 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
							}
			/* E : Mapper 생성 */

			/* S : Jsp 생성 */
							if(isCreateJsp) {

								/* S : Jsp 폴더를 생성한다. */

								IFolder jspFolder = null;

								jspFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(jspPath + "/" + prefix));
								if(!jspFolder.exists()) jspFolder.create(true ,true, new NullProgressMonitor());

								/* E : Jsp 폴더를 생성한다. */

								if(jspTemplateListFile == null || "".equals(jspTemplateListFile)) {
									jspTemplateListFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								/* Jsp 파일내용을 가져온다.. */
								String jspListContent = getSource(jspTemplateListFile);

								ByteArrayInputStream jspListFileStream = new ByteArrayInputStream(jspListContent.getBytes("UTF-8"));

								IFile jspListFile = jspFolder.getFile(new Path(prefix + "List.jsp"));
								if(!jspListFile.exists()) jspListFile.create(jspListFileStream ,true, new NullProgressMonitor());

								if(jspTemplatePostFile == null || "".equals(jspTemplatePostFile)) {
									jspTemplatePostFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								String jspPostContent = getSource(jspTemplatePostFile);

								ByteArrayInputStream jspPostFileStream = new ByteArrayInputStream(jspPostContent.getBytes("UTF-8"));

								IFile jspPostFile = jspFolder.getFile(new Path(prefix + "Post.jsp"));
								if(!jspPostFile.exists()) jspPostFile.create(jspPostFileStream ,true, new NullProgressMonitor());

								if(jspTemplateViewFile == null || "".equals(jspTemplateViewFile)) {
									jspTemplateViewFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
								}

								String jspViewContent = getSource(jspTemplateViewFile);

								ByteArrayInputStream jspViewFileStream = new ByteArrayInputStream(jspViewContent.getBytes("UTF-8"));

								IFile jspViewFile = jspFolder.getFile(new Path(prefix + "View.jsp"));
								if(!jspViewFile.exists()) jspViewFile.create(jspViewFileStream ,true, new NullProgressMonitor());

								System.out.println("Jsp 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
							}
			/* E : Jsp 생성 */

			/* S : Vo 생성 */

							if(isCreateVo) {
								if(connectionProfile != null) {

									String voPackage = voPath.replace("/", ".").substring(voPath.lastIndexOf(javaVoBuildPath) + javaVoBuildPath.length() + 1);

									List<ColumnItem> columns = databaseResource.getColumns(databaseTableName);

									IFolder voFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(voPath + "/"));
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

											IFile _myBatisSettingFile = newFolder.getWorkspace().getRoot().getFile(Path.fromOSString(myBatisSettingFile));

											if(_myBatisSettingFile.exists()) {
												_myBatisSettingFile.create(new ByteArrayInputStream(myBatisSettingDocument.toString().getBytes()) ,true, new NullProgressMonitor());
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

								System.out.println("Vo 생성 : " + (System.currentTimeMillis() - startTime) + " milliseconds");
								startTime = System.currentTimeMillis();
							}

			/* E : Vo 생성 */
						} catch (CoreException | UnsupportedEncodingException | MalformedTreeException | BadLocationException e) {
							e.printStackTrace();
						}
					}

					if(addSerialVersionUIDList.size() > 0) {
				        try {

							project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

							String[] classPaths = JavaRuntime.computeDefaultRuntimeClassPath(packageFragment.getJavaProject());
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
