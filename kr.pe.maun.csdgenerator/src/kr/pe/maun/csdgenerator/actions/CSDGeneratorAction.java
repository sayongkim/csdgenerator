package kr.pe.maun.csdgenerator.actions;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectStreamClass;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.CSDGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.ColumnItem;

public class CSDGeneratorAction implements IObjectActionDelegate {

	private ISelection selection;
	private Shell shell;

	public CSDGeneratorAction() {
		super();
	}

	@Override
	public void run(IAction action) {

		CSDGeneratorDialog dialog = new CSDGeneratorDialog(shell, selection);

		if(dialog.open() != InputDialog.OK)
			return;

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

			IResource resource = (IResource) packageFragment.getJavaProject().getProject().getAdapter(IResource.class);
			CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);

			String company = propertiesItem.getCompany();
			String author = !"".equals(propertiesItem.getAuthor()) ? propertiesItem.getAuthor() : System.getProperty("user.name");

			boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
			boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();
			String controllerTemplateFile = propertiesItem.getControllerTemplateFile();

			boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
			boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();
			String serviceTemplateFile = propertiesItem.getServiceTemplateFile();

			boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
			boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImpl();
			String serviceImplTemplateFile = propertiesItem.getServiceImplTemplateFile();

			boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
			boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();
			String daoTemplateFile = propertiesItem.getDaoTemplateFile();

			boolean isCreateMapper = propertiesItem.getCreateMapper();
			String mapperPath = propertiesItem.getMapperPath();
			String mapperTemplateFile = propertiesItem.getMapperTemplateFile();

			boolean isCreateJsp = propertiesItem.getCreateJsp();
			String jspPath = propertiesItem.getJspPath();
			String jspTemplateFile = propertiesItem.getJspTemplateFile();

			String rootPackagePath = packageFragment.getElementName();
			folder = (IFolder) packageFragment.getResource().getAdapter(IFolder.class);

			if(isParentLocation) {
				rootPackagePath = rootPackagePath.substring(0, rootPackagePath.lastIndexOf("."));
				folder = (IFolder) folder.getParent().getAdapter(IFolder.class);
			}

			IFolder newFolder = folder.getFolder(new Path(prefix));

			String capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
			String upperPrefix = prefix.toUpperCase();
			String lowerPrefix = prefix.toLowerCase();

			SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY. MM. DD");
			String date = dateFormat.format(new Date());

			try {

				/* 폴더를 생성한다. */
				if(isCreateFolder) {
					if(!newFolder.exists()) newFolder.create(true ,true, new NullProgressMonitor());
				} else {
					newFolder = folder;
				}

				/* S : Contoller 폴더를 생성한다. */

				String controllerFolderName = "";
				String controllerPackage = rootPackagePath;
				if(isCreateFolder) controllerPackage = controllerPackage + "." + prefix;
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

				/* S : Service 폴더를 생성한다. */

				String serviceFolderName = "";
				String servicePackage = rootPackagePath;
				if(isCreateFolder) servicePackage = servicePackage + "." + prefix;
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

				/* S : Dao 폴더를 생성한다. */

				String daoFolderName = "";
				String daoPackage = rootPackagePath;
				if(isCreateFolder) daoPackage = daoPackage + "." + prefix;
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

				/* S : Mapper 폴더를 생성한다. */

				IFolder mapperFolder = null;

				if(isCreateMapper) {
					mapperFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(mapperPath + "/" + prefix));
					if(!mapperFolder.exists()) mapperFolder.create(true ,true, new NullProgressMonitor());
				}

				/* E : Mapper 폴더를 생성한다. */

				/* S : Jsp 폴더를 생성한다. */

				IFolder jspFolder = null;

				if(isCreateJsp) {
					jspFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(jspPath + "/" + prefix));
					if(!jspFolder.exists()) jspFolder.create(true ,true, new NullProgressMonitor());
				}

				/* E : Jsp 폴더를 생성한다. */

/* S : Contoller 생성 */

				if(controllerTemplateFile == null || "".equals(controllerTemplateFile)) {
					controllerTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/controllerClassTemplate.txt";
				}

				/* Contoller 파일내용을 가져온다.. */
				String controllerContent = getSource(controllerTemplateFile);
				controllerContent = controllerContent.replaceAll("\\[packagePath\\]", controllerPackage);
				controllerContent = controllerContent.replaceAll("\\[serviceFullPath\\]", servicePackage + "." + capitalizePrefix + "Service");
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
/* E : Contoller 생성 */

/* S : Service 생성 */

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
				serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + capitalizePrefix + "Dao");
				serviceContent = serviceContent.replaceAll("\\[prefix\\]", prefix);
				serviceContent = serviceContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
				serviceContent = serviceContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
				serviceContent = serviceContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
				serviceContent = serviceContent.replaceAll("\\[company\\]", company);
				serviceContent = serviceContent.replaceAll("\\[author\\]", author);
				serviceContent = serviceContent.replaceAll("\\[date\\]", date);
				/* S: Service 파일을 생성한다. */
				ByteArrayInputStream serviceFileStream = new ByteArrayInputStream(serviceContent.getBytes("UTF-8"));

				IFile serviceFile = serviceFolder.getFile(new Path(capitalizePrefix + "Service.java"));
				if(!serviceFile.exists()) serviceFile.create(serviceFileStream ,true, new NullProgressMonitor());

				if(isCreateServiceImpl) {

					if(serviceImplTemplateFile == null || "".equals(serviceImplTemplateFile)) {
						serviceImplTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/serviceImplClassTemplate.txt";
					}

					/* Service 파일내용을 가져온다.. */
					String serviceImplContent = getSource(serviceImplTemplateFile);
					serviceImplContent = serviceImplContent.replaceAll("\\[packagePath\\]", serviceImplPackage);
					serviceImplContent = serviceImplContent.replaceAll("\\[serviceFullPath\\]", servicePackage + "." + capitalizePrefix + "Service");
					serviceImplContent = serviceImplContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + capitalizePrefix + "Dao");
					serviceImplContent = serviceImplContent.replaceAll("\\[prefix\\]", prefix);
					serviceImplContent = serviceImplContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
					serviceImplContent = serviceImplContent.replaceAll("\\[lowerPrefix\\]", lowerPrefix);
					serviceImplContent = serviceImplContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);
					serviceImplContent = serviceImplContent.replaceAll("\\[company\\]", company);
					serviceImplContent = serviceImplContent.replaceAll("\\[author\\]", author);
					serviceImplContent = serviceImplContent.replaceAll("\\[date\\]", date);
					/* S: Service 파일을 생성한다. */
					ByteArrayInputStream serviceImplFileStream = new ByteArrayInputStream(serviceImplContent.getBytes("UTF-8"));

					IFile serviceImplFile = serviceImplFolder.getFile(new Path(capitalizePrefix + "ServiceImpl.java"));
					if(!serviceImplFile.exists()) serviceImplFile.create(serviceImplFileStream ,true, new NullProgressMonitor());
				}
/* E : Service 생성 */

/* S : Dao 생성 */

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
/* E : Dao 생성 */

/* S : Mapper 생성 */
				if(isCreateMapper) {
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
					if(dialog.getTargetTable() != null) {
						mapperContent = mapperContent.replaceAll("\\[table\\]", dialog.getTargetTable().toUpperCase());
					} else {
						mapperContent = mapperContent.replaceAll("\\[table\\]", capitalizePrefix);
					}

					if(dialog.getTargetTable() != null & dialog.getConnectionProfile() != null) {

						DatabaseResource databaseResource = new DatabaseResource();

						List<ColumnItem> columns = databaseResource.getColumn(dialog.getConnectionProfile(), dialog.getTargetTable());

						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						try {
							DocumentBuilder builder = factory.newDocumentBuilder();
							Document document = builder.parse(new ByteArrayInputStream(mapperContent.getBytes()));
							Element documentElement = document.getDocumentElement();

							NodeList nodeList = documentElement.getChildNodes();

							for(int i = 0; i < nodeList.getLength(); i++) {
								Node item = nodeList.item(i);
								String content = item.getTextContent();
								if("select".equals(item.getNodeName())) {
									content = content.replaceAll("\\[columns\\]", selecColumn(columns));
								} else if("insert".equals(item.getNodeName())) {
									content = content.replaceAll("\\[columns\\]", insertColumn(columns));
									content = content.replaceAll("\\[values\\]", insertValue(columns));
								} else if("update".equals(item.getNodeName())) {
									content = content.replaceAll("\\[columns\\]", updateColumn(columns));
								}
								item.setTextContent(content);
							}

							StringWriter writer = new StringWriter();
							Transformer transformer = TransformerFactory.newInstance().newTransformer();
							transformer.setOutputProperty(OutputKeys.INDENT, "yes");
							transformer.transform(new DOMSource(document), new StreamResult(writer));

							mapperContent = writer.toString();

						} catch (ParserConfigurationException | SAXException | IOException | TransformerException | TransformerFactoryConfigurationError e) {
							e.printStackTrace();
						}
					}

					mapperContent = mapperContent.replaceAll("&lt;", "<");
					mapperContent = mapperContent.replaceAll("&gt;", ">");

					ByteArrayInputStream mapperImplFileStream = new ByteArrayInputStream(mapperContent.getBytes("UTF-8"));

					IFile mapperFile = mapperFolder.getFile(new Path(prefix + "Mapper.xml"));
					if(!mapperFile.exists()) mapperFile.create(new ByteArrayInputStream(mapperContent.getBytes()) ,true, new NullProgressMonitor());

				}
/* E : Mapper 생성 */

/* S : Jsp 생성 */
				if(isCreateJsp) {
					if(jspTemplateFile == null || "".equals(jspTemplateFile)) {
						jspTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
					}

					/* Jsp 파일내용을 가져온다.. */
					String jspContent = getSource(jspTemplateFile);

					ByteArrayInputStream jspImplFileStream = new ByteArrayInputStream(jspContent.getBytes("UTF-8"));

					IFile jspFile = jspFolder.getFile(new Path(prefix + ".jsp"));
					if(!jspFile.exists()) jspFile.create(jspImplFileStream ,true, new NullProgressMonitor());
				}
/* E : Jsp 생성 */

/* S : VO 생성 */

				if(dialog.getTargetTable() != null & dialog.getConnectionProfile() != null) {

					DatabaseResource databaseResource = new DatabaseResource();

					List<ColumnItem> columns = databaseResource.getColumn(dialog.getConnectionProfile(), dialog.getTargetTable());

					String voContent = getSource("platform:/plugin/kr.pe.maun.csdgenerator/resource/template/voTemplate.txt");

					voContent = voContent.replaceAll("\\[packagePath\\]", "vo");
					voContent = voContent.replaceAll("\\[capitalizePrefix\\]", capitalizePrefix);

					StringBuffer valueBuffer = new StringBuffer();
					StringBuffer gettersAndSetters = new StringBuffer();

					for(int i = 0; i < columns.size(); i++) {

						ColumnItem column = columns.get(i);
						String columnName = toCamelCase(column.getColumnName());

						valueBuffer.append("\tprivate String ");
						valueBuffer.append(columnName);
						valueBuffer.append("; // ");
						valueBuffer.append(column.getComments());
						valueBuffer.append("\n");

						gettersAndSetters.append("\tpublic String ");
						gettersAndSetters.append(toCamelCase("get_" + column.getColumnName()));
						gettersAndSetters.append("() {\n");
						gettersAndSetters.append("\t\treturn this.");
						gettersAndSetters.append(columnName);
						gettersAndSetters.append(";\n");
						gettersAndSetters.append("\t}\n\n");

						gettersAndSetters.append("\tpublic String ");
						gettersAndSetters.append(toCamelCase("set_" + column.getColumnName()));
						gettersAndSetters.append("(String ");
						gettersAndSetters.append(columnName);
						gettersAndSetters.append(") {\n");
						gettersAndSetters.append("\t\treturn this.");
						gettersAndSetters.append(columnName);
						gettersAndSetters.append(" = ");
						gettersAndSetters.append(columnName);
						gettersAndSetters.append(";\n");
						gettersAndSetters.append("\t}\n\n");
					}

					voContent = voContent.replaceAll("\\[value\\]", valueBuffer.toString());
					voContent = voContent.replaceAll("\\[GettersAndSetters\\]", gettersAndSetters.toString());

					ByteArrayInputStream voFileStream = new ByteArrayInputStream(voContent.getBytes());

					IFolder voFolder = newFolder.getWorkspace().getRoot().getFolder(new Path("/Test/src/main/java/vo"));
					IFile voFile = voFolder.getFile(new Path(capitalizePrefix + "Vo.java"));
					if(!voFile.exists()) voFile.create(voFileStream ,true, new NullProgressMonitor());

					IJavaProject javaProject = packageFragment.getJavaProject();
					IProject project = packageFragment.getJavaProject().getProject();

					project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

					String[] classPaths = JavaRuntime.computeDefaultRuntimeClassPath(packageFragment.getJavaProject());
			        URL[] urls = new URL[classPaths.length];
			        try {

						for (int i = 0; i < classPaths.length; i++) {
							urls[i] = new URL("file:///" + classPaths[i] + File.separator);
						}

						URLClassLoader loader = new URLClassLoader(urls);
						Class<?> clazz = loader.loadClass("vo." + capitalizePrefix + "Vo");
						ObjectStreamClass streamClass = ObjectStreamClass.lookup(clazz);
						loader.close();

						ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(voFile);

						IType[] types = compilationUnit.getTypes();
						IType type = types[0];
						type.createField("\tstatic final long serialVersionUID = " + streamClass.getSerialVersionUID() + "L;\n\n", type.getFields()[0], false, null);

						compilationUnit.save(null, true);

					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

/* E : VO 생성 */
			} catch (CoreException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
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

	private String toCamelCase(String source) {
	    StringBuffer result = new StringBuffer();
	    String[] sourceArray = source.split("_");
	    result.append(sourceArray[0].toLowerCase());
	    if(sourceArray.length > 1) {
		    for (int i = 1; i < sourceArray.length; i++) {
		    	String s = sourceArray[i];
		        result.append(Character.toUpperCase(s.charAt(0)));
		        if (s.length() > 1) {
		            result.append(s.substring(1, s.length()).toLowerCase());
		        }
		    }
	    }
	    return result.toString();
	}

	private String selecColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName());
				result.append(" AS ");
				result.append(toCamelCase(columnItem.getColumnName()));
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
				result.append(toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}

	private String updateColumn(List<ColumnItem> columnItems) {
		StringBuffer result = new StringBuffer();
		if(columnItems.size() > 0) {
			for (int i = 0; i < columnItems.size(); i++) {
				ColumnItem columnItem = columnItems.get(i);
				if(i > 0) result.append("\t\t\t,");
				result.append(columnItem.getColumnName());
				result.append(" = #{");
				result.append(toCamelCase(columnItem.getColumnName()));
				result.append("}");
				if(i < (columnItems.size() - 1)) result.append("\n");
			}
		}
		return result.toString();
	}
}
