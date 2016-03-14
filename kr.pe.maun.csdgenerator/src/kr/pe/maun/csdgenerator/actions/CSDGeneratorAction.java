package kr.pe.maun.csdgenerator.actions;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import kr.pe.maun.csdgenerator.dialogs.CSDGeneratorDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

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

			IFolder newFolder = folder.getFolder(new Path(prefix.toString()));

			String controllerPath = prefix.toString() + "Controller";	
			String servicePath = prefix.toString() + "Service";
			String daoPath = prefix.toString() + "Dao";

			try {

				newFolder.create(true ,true,new NullProgressMonitor());

				IFolder controllerFolder = newFolder.getFolder(new Path(controllerPath));
				controllerFolder.create(true ,true,new NullProgressMonitor());

				String controllerContent = new String();
				controllerContent = "package " + controllerFolder.getFullPath().toString().replace("/", ".");
				controllerContent += "\r\n";

				ByteArrayInputStream controllerFileStream = new ByteArrayInputStream(controllerContent.getBytes());

				IFile controllerFile = controllerFolder.getFile(new Path(controllerPath + ".java"));
				controllerFile.create(controllerFileStream ,true,new NullProgressMonitor());

				IFolder serviceFolder = newFolder.getFolder(new Path(servicePath));
				serviceFolder.create(true ,true,new NullProgressMonitor());

				IFolder daoFolder = newFolder.getFolder(new Path(daoPath));
				daoFolder.create(true ,true,new NullProgressMonitor());

			} catch (CoreException e) {
				e.printStackTrace();
			}
		} else if(packageFragment != null && prefix != null) {

			IResource resource = (IResource) packageFragment.getJavaProject().getProject().getAdapter(IResource.class);
			CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);

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

			IFolder newFolder = folder.getFolder(new Path(prefix.toString()));

			String upperPrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());

			try {

				/* 폴더를 생성한다. */
				if(isCreateFolder) {
					if(!newFolder.exists()) newFolder.create(true ,true,new NullProgressMonitor());
				} else {
					newFolder = folder;
				}

				/* S : Contoller 폴더를 생성한다. */

				String controllerFolderName = "";
				String controllerPackage = rootPackagePath;
				if(isCreateFolder) controllerPackage = controllerPackage + "." + prefix;
				IFolder controllerFolder = null;

				if(isCreateControllerFolder) {

					if(isAddPrefixControllerFolder) controllerFolderName += upperPrefix;

					controllerFolderName += "Controller";
					controllerPackage = controllerPackage + "." + controllerFolderName;
					controllerFolder = newFolder.getFolder(new Path(controllerFolderName));
					if(!controllerFolder.exists()) controllerFolder.create(true ,true,new NullProgressMonitor());
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

					if(isAddPrefixServiceFolder) serviceFolderName += upperPrefix;

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

					if(isAddPrefixDaoFolder) daoFolderName += upperPrefix;

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
					if(!mapperFolder.exists()) mapperFolder.create(true ,true,new NullProgressMonitor());
				}

				/* E : Mapper 폴더를 생성한다. */

				/* S : Jsp 폴더를 생성한다. */

				IFolder jspFolder = null;

				if(isCreateJsp) {
					jspFolder = newFolder.getWorkspace().getRoot().getFolder(new Path(jspPath + "/" + prefix));
					if(!jspFolder.exists()) jspFolder.create(true ,true,new NullProgressMonitor());
				}

				/* E : Jsp 폴더를 생성한다. */

/* S : Contoller 생성 */

				if(controllerTemplateFile == null || "".equals(controllerTemplateFile)) {
					controllerTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/controllerClassTemplate.txt";
				}

				/* Contoller 파일내용을 가져온다.. */
				String controllerContent = getSource(controllerTemplateFile);
				controllerContent = controllerContent.replaceAll("\\[packagePath\\]", controllerPackage);
				controllerContent = controllerContent.replaceAll("\\[serviceFullPath\\]", servicePackage + "." + upperPrefix + "Service");
				controllerContent = controllerContent.replaceAll("\\[prefix\\]", prefix);
				controllerContent = controllerContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
				/* S: Contoller 파일을 생성한다. */
				ByteArrayInputStream controllerFileStream = new ByteArrayInputStream(controllerContent.getBytes());

				IFile controllerFile = controllerFolder.getFile(new Path(upperPrefix + "Controller.java"));
				if(!controllerFile.exists()) controllerFile.create(controllerFileStream ,true,new NullProgressMonitor());
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
				serviceContent = serviceContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + upperPrefix + "Dao");
				serviceContent = serviceContent.replaceAll("\\[prefix\\]", prefix);
				serviceContent = serviceContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
				/* S: Service 파일을 생성한다. */
				ByteArrayInputStream serviceFileStream = new ByteArrayInputStream(serviceContent.getBytes());

				IFile serviceFile = serviceFolder.getFile(new Path(upperPrefix + "Service.java"));
				if(!serviceFile.exists()) serviceFile.create(serviceFileStream ,true,new NullProgressMonitor());

				if(isCreateServiceImpl) {

					if(serviceImplTemplateFile == null || "".equals(serviceImplTemplateFile)) {
						serviceImplTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/serviceImplClassTemplate.txt";
					}

					/* Service 파일내용을 가져온다.. */
					String serviceImplContent = getSource(serviceImplTemplateFile);
					serviceImplContent = serviceImplContent.replaceAll("\\[packagePath\\]", serviceImplPackage);
					serviceImplContent = serviceImplContent.replaceAll("\\[serviceFullPath\\]", servicePackage + "." + upperPrefix + "Service");
					serviceImplContent = serviceImplContent.replaceAll("\\[daoFullPath\\]", daoPackage + "." + upperPrefix + "Dao");
					serviceImplContent = serviceImplContent.replaceAll("\\[prefix\\]", prefix);
					serviceImplContent = serviceImplContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
					/* S: Service 파일을 생성한다. */
					ByteArrayInputStream serviceImplFileStream = new ByteArrayInputStream(serviceImplContent.getBytes());

					IFile serviceImplFile = serviceImplFolder.getFile(new Path(upperPrefix + "ServiceImpl.java"));
					if(!serviceImplFile.exists()) serviceImplFile.create(serviceImplFileStream ,true,new NullProgressMonitor());
				}
/* E : Service 생성 */

/* S : Dao 생성 */

				if(daoTemplateFile == null || "".equals(daoTemplateFile)) {
					daoTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/daoClassTemplate.txt";
				}

				/* Dao 파일내용을 가져온다.. */
				String daoContent = getSource(daoTemplateFile);
				daoContent = daoContent.replaceAll("\\[packagePath\\]", daoPackage);
				daoContent = daoContent.replaceAll("\\[upperPrefix\\]", upperPrefix);
				/* S: Dao 파일을 생성한다. */
				ByteArrayInputStream daoFileStream = new ByteArrayInputStream(daoContent.getBytes());

				IFile daoFile = daoFolder.getFile(new Path(upperPrefix + "Dao.java"));
				if(!daoFile.exists()) daoFile.create(daoFileStream ,true,new NullProgressMonitor());
/* E : Dao 생성 */

/* S : Mapper 생성 */
				if(isCreateMapper) {
					if(mapperTemplateFile == null || "".equals(mapperTemplateFile)) {
						mapperTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/mapperTemplate.txt";
					}

					/* Mapper 파일내용을 가져온다.. */
					String mapperContent = getSource(mapperTemplateFile);
					mapperContent = mapperContent.replaceAll("\\[namespace\\]", prefix + "Mapper");

					ByteArrayInputStream mapperImplFileStream = new ByteArrayInputStream(mapperContent.getBytes());

					IFile mapperFile = mapperFolder.getFile(new Path(prefix + "Mapper.xml"));
					if(!mapperFile.exists()) mapperFile.create(mapperImplFileStream ,true,new NullProgressMonitor());
				}
/* E : Mapper 생성 */

/* S : Jsp 생성 */
				if(isCreateJsp) {
					if(jspTemplateFile == null || "".equals(jspTemplateFile)) {
						jspTemplateFile = "platform:/plugin/kr.pe.maun.csdgenerator/resource/template/jspTemplate.txt";
					}

					/* Jsp 파일내용을 가져온다.. */
					String jspContent = getSource(jspTemplateFile);

					ByteArrayInputStream jspImplFileStream = new ByteArrayInputStream(jspContent.getBytes());

					IFile jspFile = jspFolder.getFile(new Path(prefix + ".jsp"));
					if(!jspFile.exists()) jspFile.create(jspImplFileStream ,true,new NullProgressMonitor());
				}
/* E : Jsp 생성 */

			} catch (CoreException e) {
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
				in = new BufferedReader(new InputStreamReader(inputStream));
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
}
