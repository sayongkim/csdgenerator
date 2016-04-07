package kr.pe.maun.csdgenerator.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.GeneratorItemConstants;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.model.GeneratorItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

public class CSDGeneratorDialog extends Dialog {

	private CSDGeneratorPropertiesItem propertiesItem;
	private CSDGeneratorPropertiesHelper propertiesHelper;

	private String templateGroupName = "";

	private String prefix = "";
	private Text prefixField;
/*
	private String regExGroup = "";
	private Text regExGroupField;
*/
	private boolean isParentLocation = false;
	private boolean isCreateFolder = true;

	private boolean isCreateController = true;
	private boolean isCreateService = true;
	private boolean isCreateDao = true;
	private boolean isCreateMapper = true;
	private boolean isCreateJsp = true;
	private boolean isCreateVo = true;
	private boolean isCreateSearchVo = true;

	private boolean existsJspTemplateListFile = true;
	private boolean existsJspTemplatePostFile = true;
	private boolean existsJspTemplateViewFile = true;

	String mapperPath;
	String voPath;
	String jspPath;

	private IConnectionProfile connectionProfile;

	private ISelection selection;

	private Combo templateCombo;
	private Combo connectionProfileCombo;
	private Combo parameterCombo;
	private Combo returnCombo;

	private Button createParentLocationButton;
	private Button createFolderButton;
	private Button createControllerButton;
	private Button createServiceButton;
	private Button createDaoButton;
	private Button createMapperButton;
	private Button createJspButton;
	private Button createVoButton;
	private Button regExButton;

	private IConnectionProfile[] connectionProfiles;
	private DatabaseResource databaseResource;

	private Tree previewTree;
	private Tree databaseTablesTree;

	private String[] databaseTables;

	private IPackageFragment javaPackageFragment;

	private Button okButton;

	private GeneratorItem javaGeneratorItem;
	private GeneratorItem mapperGeneratorItem;
	private GeneratorItem jspGeneratorItem;

	public CSDGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	public CSDGeneratorDialog(Shell parentShell, ISelection selection) {
		super(parentShell);
		this.selection = selection;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);

		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for (TreePath treePath : treePaths) {
				if (treePath.getLastSegment() instanceof IPackageFragmentRoot) {
				} else {
					javaPackageFragment = (IPackageFragment) treePath.getLastSegment();
				}
			}
		}

		IProject project = javaPackageFragment.getJavaProject().getProject();

		propertiesItem = new CSDGeneratorPropertiesItem((IResource) project.getAdapter(IResource.class));
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		mapperPath = propertiesItem.getMapperPath();
		voPath = propertiesItem.getVoPath();
		jspPath = propertiesItem.getJspPath();

		isCreateMapper = propertiesItem.getCreateMapper() && mapperPath != null && !"".equals(mapperPath);
		isCreateJsp = propertiesItem.getCreateJsp() && jspPath != null && !"".equals(jspPath);
		isCreateVo = propertiesItem.getCreateVo() && voPath != null && !"".equals(voPath);
		isCreateSearchVo = propertiesItem.getCreateSearchVo();

		String databaseConnectionProfileName = propertiesItem.getDatabaseConnectionProfileName();

		String[] templateGroupNames = propertiesHelper.getGeneralTemplateGroupNames();

		container.setLayout(new GridLayout(4, false));

		Label templateLabel = new Label(container, SWT.NONE);
		templateLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		templateLabel.setText("Template: ");

		GridData comboGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		comboGridData.widthHint = 150;

		templateCombo = new Combo(container, SWT.READ_ONLY);
		templateCombo.setLayoutData(comboGridData);
		templateCombo.setItems(templateGroupNames);

		templateCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				templateGroupName = templateCombo.getText();

				String generalTemplateController = propertiesHelper.getGeneralTemplateController(templateCombo.getText());
				String generalTemplateService = propertiesHelper.getGeneralTemplateService(templateCombo.getText());
				String generalTemplateDao = propertiesHelper.getGeneralTemplateDao(templateCombo.getText());
				String generalTemplateMapper = propertiesHelper.getGeneralTemplateMapper(templateCombo.getText());
				String generalTemplateJsp = propertiesHelper.getGeneralTemplateJsp(templateCombo.getText());

				if(generalTemplateController != null
						&& !"".equals(propertiesHelper.getControllerTemplateFile(generalTemplateController))) {
					createControllerButton.setEnabled(true);
				} else {
					createControllerButton.setEnabled(false);
				}

				if(generalTemplateService != null
						&& !"".equals(propertiesHelper.getServiceTemplateFile(generalTemplateService))) {
					createServiceButton.setEnabled(true);
				} else {
					createServiceButton.setEnabled(false);
				}

				if(generalTemplateDao != null
						&& !"".equals(propertiesHelper.getDaoTemplateFile(generalTemplateDao))) {
					createDaoButton.setEnabled(true);
				} else {
					createDaoButton.setEnabled(false);
				}

				if(isCreateMapper) {
					if(generalTemplateMapper != null
							&& !"".equals(propertiesHelper.getMapperTemplateFile(generalTemplateMapper))) {
						createMapperButton.setEnabled(true);
					} else {
						createMapperButton.setEnabled(false);
					}
				}

				if(isCreateJsp) {
					if(generalTemplateJsp != null
							&& (!"".equals(propertiesHelper.getJspTemplateListFile(generalTemplateJsp))
							|| !"".equals(propertiesHelper.getJspTemplatePostFile(generalTemplateJsp))
							|| !"".equals(propertiesHelper.getJspTemplateViewFile(generalTemplateJsp)))) {
						createJspButton.setEnabled(true);
					} else {
						createJspButton.setEnabled(false);
					}
				}

				if(generalTemplateJsp != null
						&& !"".equals(propertiesHelper.getJspTemplateListFile(generalTemplateJsp))) {
					existsJspTemplateListFile = true;
				} else {
					existsJspTemplateListFile = false;
				}

				if(generalTemplateJsp != null
						&& !"".equals(propertiesHelper.getJspTemplatePostFile(generalTemplateJsp))) {
					existsJspTemplatePostFile = true;
				} else {
					existsJspTemplatePostFile = false;
				}

				if(generalTemplateJsp != null
						&& !"".equals(propertiesHelper.getJspTemplateViewFile(generalTemplateJsp))) {
					existsJspTemplateViewFile = true;
				} else {
					existsJspTemplateViewFile = false;
				}

				createTree();

				okButtonEnabled();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		/* S : DB 연결 */
		databaseResource = new DatabaseResource();

		Label connectionProfileLabel = new Label(container, SWT.NONE);
		connectionProfileLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		connectionProfileLabel.setText("Database Connection: ");

		connectionProfiles = ProfileManager.getInstance().getProfiles();
		connectionProfileCombo = new Combo(container, SWT.READ_ONLY);
		connectionProfileCombo.setLayoutData(comboGridData);
		for (IConnectionProfile profile : connectionProfiles) {
			connectionProfileCombo.add(profile.getName());
		}
		connectionProfileCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDatabaseTrees();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		new Label(container, SWT.NONE);
		createParentLocationButton = new Button(container, SWT.CHECK);
		createParentLocationButton.setText("Create parent location");
		createParentLocationButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 0));
		createParentLocationButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setParentLocation(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createParentLocationButton.setEnabled(false);

		new Label(container, SWT.NONE);
		createFolderButton = new Button(container, SWT.CHECK);
		createFolderButton.setText("Create folder");
		createFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 0));
		createFolderButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateFolder(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createFolderButton.setSelection(isCreateFolder);

		GridLayout createButtonCompositeLayout = new GridLayout(6, false);
		createButtonCompositeLayout.marginWidth = 0;
		createButtonCompositeLayout.marginHeight = 0;

		new Label(container, SWT.NONE);
		Composite craeteButtonComposite = new Composite(container, SWT.NONE);
		craeteButtonComposite.setLayout(createButtonCompositeLayout);
		craeteButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0));

		createControllerButton = new Button(craeteButtonComposite, SWT.CHECK);
		createControllerButton.setText("Controller");
		createControllerButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateController(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createControllerButton.setSelection(isCreateController);

		createServiceButton = new Button(craeteButtonComposite, SWT.CHECK);
		createServiceButton.setText("Service");
		createServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateService(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createServiceButton.setSelection(isCreateService);

		createDaoButton = new Button(craeteButtonComposite, SWT.CHECK);
		createDaoButton.setText("Dao");
		createDaoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateDao(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createDaoButton.setSelection(isCreateDao);

		createMapperButton = new Button(craeteButtonComposite, SWT.CHECK);
		createMapperButton.setText("Mapper");
		createMapperButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateMapper(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createMapperButton.setSelection(isCreateMapper);
		if(!isCreateMapper) createMapperButton.setEnabled(false);

		createVoButton = new Button(craeteButtonComposite, SWT.CHECK);
		createVoButton.setText("Vo");
		createVoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateVo(button.getSelection());
				createTree();
				if(button.getSelection()) {
					parameterCombo.setEnabled(false);
					returnCombo.setEnabled(false);
				} else {
					parameterCombo.setEnabled(true);
					returnCombo.setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createVoButton.setSelection(isCreateVo);
		if(!isCreateVo) createVoButton.setEnabled(false);

		createJspButton = new Button(craeteButtonComposite, SWT.CHECK);
		createJspButton.setText("Jsp");
		createJspButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateJsp(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createJspButton.setSelection(isCreateJsp);
		if(!isCreateJsp) createJspButton.setEnabled(false);

		/* E : DB 연결 */

		GridData layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, false, 3, 0);
		layoutData.widthHint = 400;

		Label parameterLabel = new Label(container, SWT.NONE);
		parameterLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		parameterLabel.setText("    Parameter: ");

		List<String> objectList = new ArrayList<String>();
		objectList.add("HashMap<String, Object>");
		objectList.add("HashMap<String, String>");

		if(voPath != null && !"".equals(voPath)) {
			IFolder voFolder = project.getWorkspace().getRoot().getFolder(new Path(voPath));
			try {
				IResource[] members = voFolder.members();
				for(IResource member : members) {
					objectList.add(member.getName().replaceAll(".java", ""));
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}

		parameterCombo = new Combo(container, SWT.READ_ONLY);
		parameterCombo.setLayoutData(layoutData);
		parameterCombo.setItems(objectList.toArray(new String[objectList.size()]));
		if(isCreateVo) {
			parameterCombo.select(0);
			parameterCombo.setEnabled(false);
		}

		Label returnLabel = new Label(container, SWT.NONE);
		returnLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		returnLabel.setText("Return: ");

		returnCombo = new Combo(container, SWT.READ_ONLY);
		returnCombo.setLayoutData(layoutData);
		returnCombo.setItems(objectList.toArray(new String[objectList.size()]));
		if(isCreateVo) {
			returnCombo.select(0);
			returnCombo.setEnabled(false);
		}

		Label prefixLabel = new Label(container, SWT.NONE);
		prefixLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		prefixLabel.setText("Prefix: ");

		prefixField = new Text(container, SWT.BORDER);
		prefixField.setLayoutData(layoutData);
		prefixField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				prefix = prefixField.getText();
				createTree();
			}
		});

		new Label(container, SWT.NONE);
		regExButton = new Button(container, SWT.CHECK);
		regExButton.setText("Regular expressions");
		regExButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 0));
		regExButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				if(button.getSelection()) {
					prefixField.setText("[A-Za-z0-9]+_([A-Za-z0-9_]+)");
				} else {
					prefixField.setText("");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		regExButton.setEnabled(false);

		Label previewLabel = new Label(container, SWT.NONE);
		previewLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 0));
		previewLabel.setText("Preview: ");

		Label tablesLabel = new Label(container, SWT.NONE);
		tablesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		tablesLabel.setText("Tables: ");

		previewTree = new Tree(container, SWT.MULTI | SWT.BORDER);
		previewTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 0));
		createTree();

		databaseTablesTree = new Tree(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		databaseTablesTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		databaseTablesTree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTables = null;
				TreeItem[] treeItems = databaseTablesTree.getItems();
				List<String> databaseTableList = new ArrayList<String>();

				TreeItem[] selectTreeItems = databaseTablesTree.getSelection();
				for(TreeItem treeItem : selectTreeItems) {
					if(databaseTablesTree.indexOf(treeItem) > 0) {
						if(treeItem.getChecked()) {
							treeItem.setChecked(false);
						} else {
							treeItem.setChecked(true);
						}
					}
				}

				for(TreeItem treeItem : treeItems) {
					if(treeItem.getChecked()) {
						databaseTableList.add(treeItem.getText());
					}
				}
				databaseTables = databaseTableList.toArray(new String[databaseTableList.size()]);

				if(databaseTables.length > 1) {
					regExButton.setEnabled(true);
				} else {
					regExButton.setEnabled(false);
					if(regExButton.getSelection()) prefixField.setText("");
				}

				databaseTablesTree.deselectAll();

				createTree();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if(databaseConnectionProfileName != null
				&&!"".equals(databaseConnectionProfileName)
				&& connectionProfiles.length > 0) {
			for (IConnectionProfile profile : connectionProfiles) {
				if (profile.getName().equals(databaseConnectionProfileName)) {
					connectionProfile = profile;
					break;
				}
			}
			connectionProfileCombo.select(connectionProfileCombo.indexOf(databaseConnectionProfileName));
			getDatabaseTrees();
		}

		return container;
	}

	private void createTree() {

		previewTree.removeAll();

		boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
		boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();
		boolean isCreateControllerSubFolder = propertiesItem.getCreateControllerSubFolder();

		boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
		boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();
		boolean isCreateServiceSubFolder = propertiesItem.getCreateServiceSubFolder();

		boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
		boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImplFolder();

		boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
		boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();
		boolean isCreateDaoSubFolder = propertiesItem.getCreateDaoSubFolder();

		IJavaProject javaProject = javaPackageFragment.getJavaProject();

		String javaBuildPath = "";
		String javaVoBuildPath = "";
		String resourceBuildPath = "";

		try {
			IClasspathEntry[] classpaths = javaProject.getRawClasspath();
			for (IClasspathEntry classpath : classpaths) {
				IPath path = classpath.getPath();
				if (javaPackageFragment.getPath().toString().indexOf(path.toString()) > -1) {
					javaBuildPath = path.removeFirstSegments(1).toString();
				} else if (isCreateMapper && mapperPath != null && mapperPath.indexOf(path.toString()) > -1) {
					resourceBuildPath = path.removeFirstSegments(1).toString();
				}

				if (isCreateVo && voPath != null && voPath.indexOf(path.toString()) > -1) {
					javaVoBuildPath = path.removeFirstSegments(1).toString();
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		String packagePath = javaPackageFragment.getElementName();

		org.eclipse.jdt.ui.ISharedImages sharedImages = JavaUI.getSharedImages();
		org.eclipse.ui.ISharedImages workbenchSharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image packageRootIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
		Image packageIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		Image javaIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		Image folderIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
		Image fileIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);

		javaGeneratorItem = new GeneratorItem(GeneratorItemConstants.BUILD_PATH, javaVoBuildPath, null, packageRootIcon);

		if (isParentLocation) {
			packagePath = packagePath.substring(0, packagePath.lastIndexOf("."));
		}

		/* S : Create Java File */

		TreeItem javaBuildTreeItem = new TreeItem(previewTree, 0);
		javaBuildTreeItem.setText(javaBuildPath);
		javaBuildTreeItem.setImage(packageRootIcon);

		String[] packagePaths = packagePath.split("\\.");

		TreeItem javaPackageTreeItem = javaBuildTreeItem;

		javaGeneratorItem.addChildItem(new GeneratorItem(GeneratorItemConstants.PACKAGE, packagePath, null, packageIcon));

		for(String packagePathName : packagePaths) {

			TreeItem javaTreeItem = new TreeItem(javaPackageTreeItem, 1);
			javaTreeItem.setText(packagePathName);
			javaTreeItem.setImage(packageIcon);

			javaPackageTreeItem.setExpanded(true);
			javaPackageTreeItem = javaTreeItem;
		}

		TreeItem javaRootTreeItem = null;

		if(databaseTables == null || databaseTables.length < 2) databaseTables = new String[]{prefix};

		TreeItem controllerFolderTreeItem = null;
		TreeItem serviceFolderTreeItem = null;
		TreeItem serviceImplFolderTreeItem = null;
		TreeItem daoFolderTreeItem = null;

		Pattern pattern = null;

		try {
			if(!"".equals(prefix) && databaseTables.length > 1) {
				pattern =Pattern.compile(prefix);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String name : databaseTables) {

			if(pattern != null) {
				Matcher matcher = pattern.matcher(name);
				if(matcher.matches() && matcher.groupCount() > 0) {
					name = matcher.group(1);
				}
			}

			name = StringUtils.toCamelCase(name);

			String capitalizePrefix = "";

			if (name.length() > 1)
				capitalizePrefix = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			else if (name.length() == 1)
				capitalizePrefix = name.substring(0, 1).toUpperCase();

			if (isCreateFolder && !"".equals(name)
					&& (isCreateController || isCreateService || isCreateDao)) {
				javaRootTreeItem = new TreeItem(javaPackageTreeItem, 1);
				javaRootTreeItem.setText(name);
				javaRootTreeItem.setImage(packageIcon);

				javaGeneratorItem.addChildItem(new GeneratorItem(GeneratorItemConstants.PACKAGE, name, null, packageIcon));

			} else {
				javaRootTreeItem = javaPackageTreeItem;
			}

			if(isCreateController && createControllerButton.isEnabled()) {
				if (isCreateControllerFolder) {

					String controllerFolder = "";

					if (isAddPrefixControllerFolder) {
						controllerFolder = name + "Controller";
					} else {
						controllerFolder = "controller";
					}

					if(isCreateFolder || controllerFolderTreeItem == null) {
						controllerFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						controllerFolderTreeItem.setText(controllerFolder);

						javaGeneratorItem.addChildItem(new GeneratorItem(GeneratorItemConstants.PACKAGE, controllerFolder, null, packageIcon));
					}

					TreeItem controllerSubFolderTreeItem = new TreeItem(controllerFolderTreeItem, 1);
					controllerSubFolderTreeItem.setText(name);

					if ("".equals(name) || !isCreateControllerSubFolder) {
						controllerSubFolderTreeItem.dispose();
					}

					javaGeneratorItem.addChildItem(new GeneratorItem(GeneratorItemConstants.JAVA, capitalizePrefix + "Controller.java", null, javaIcon));

					TreeItem controllerJavaTreeItem = new TreeItem(controllerSubFolderTreeItem.isDisposed() ? controllerFolderTreeItem : controllerSubFolderTreeItem, SWT.NONE);
					controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
					controllerJavaTreeItem.setImage(javaIcon);

					if (!"".equals(name) && isCreateControllerSubFolder) {
						controllerSubFolderTreeItem.setExpanded(true);
						controllerSubFolderTreeItem.setImage(packageIcon);
					}

					controllerFolderTreeItem.setExpanded(true);
					controllerFolderTreeItem.setImage(packageIcon);
				} else {
					TreeItem controllerJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
					controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
					controllerJavaTreeItem.setImage(javaIcon);
				}
			}

			if(isCreateService && createServiceButton.isEnabled()) {
				if (isCreateServiceFolder) {

					String serviceFolder = "";

					if (isAddPrefixServiceFolder) {
						serviceFolder = name + "Service";
					} else {
						serviceFolder = "service";
					}

					if(isCreateFolder || serviceFolderTreeItem == null) {
						serviceFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						serviceFolderTreeItem.setText(serviceFolder);
					}

					TreeItem serviceSubFolderTreeItem = new TreeItem(serviceFolderTreeItem, 1);
					serviceSubFolderTreeItem.setText(name);

					if ("".equals(name) || !isCreateServiceSubFolder) {
						serviceSubFolderTreeItem.dispose();
					}

					if (isCreateServiceImpl) {
						if (isCreateServiceImplFolder) {
							if(serviceImplFolderTreeItem == null || isCreateServiceSubFolder) {
								String serviceImplFolder = "Impl";

								serviceImplFolderTreeItem = new TreeItem(serviceSubFolderTreeItem.isDisposed() ? serviceFolderTreeItem : serviceSubFolderTreeItem, 1);
								serviceImplFolderTreeItem.setText(serviceImplFolder);
								serviceImplFolderTreeItem.setImage(packageIcon);
							}
						} else {
							serviceImplFolderTreeItem = serviceSubFolderTreeItem.isDisposed() ? serviceFolderTreeItem : serviceSubFolderTreeItem;
						}
						TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
						serviceImplJavaTreeItem.setText(capitalizePrefix + "ServiceImpl.java");
						serviceImplJavaTreeItem.setImage(javaIcon);
						serviceImplFolderTreeItem.setExpanded(true);
					}

					TreeItem serviceJavaTreeItem = new TreeItem(serviceSubFolderTreeItem.isDisposed() ? serviceFolderTreeItem : serviceSubFolderTreeItem, SWT.NONE);
					serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
					serviceJavaTreeItem.setImage(javaIcon);

					if (!"".equals(name) && isCreateServiceSubFolder) {
						serviceSubFolderTreeItem.setExpanded(true);
						serviceSubFolderTreeItem.setImage(packageIcon);
					}

					serviceFolderTreeItem.setExpanded(true);
					serviceFolderTreeItem.setImage(packageIcon);
				} else {

					if (isCreateServiceImpl) {
						if (isCreateServiceImplFolder) {
							if(serviceImplFolderTreeItem == null || isCreateServiceSubFolder) {
								String serviceImplFolder = "Impl";

								serviceImplFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
								serviceImplFolderTreeItem.setText(serviceImplFolder);
								serviceImplFolderTreeItem.setImage(packageIcon);
							}
						} else {
							serviceImplFolderTreeItem = javaRootTreeItem;
						}
						TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
						serviceImplJavaTreeItem.setText(capitalizePrefix + "ServiceImpl.java");
						serviceImplJavaTreeItem.setImage(javaIcon);
						serviceImplFolderTreeItem.setExpanded(true);
					}

					TreeItem serviceJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
					serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
					serviceJavaTreeItem.setImage(javaIcon);
				}
			}

			if(isCreateDao && createDaoButton.isEnabled()) {
				if (isCreateDaoFolder) {

					String daoFolder = "";

					if (isAddPrefixDaoFolder) {
						daoFolder = capitalizePrefix + "Dao";
					} else {
						daoFolder = "dao";
					}

					if(isCreateFolder || daoFolderTreeItem == null) {
						daoFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						daoFolderTreeItem.setText(daoFolder);
					}

					TreeItem daoSubFolderTreeItem = new TreeItem(daoFolderTreeItem, 1);
					daoSubFolderTreeItem.setText(name);

					if ("".equals(name) || !isCreateDaoSubFolder) {
						daoSubFolderTreeItem.dispose();
					}

					TreeItem daoJavaTreeItem = new TreeItem(daoSubFolderTreeItem.isDisposed() ? daoFolderTreeItem : daoSubFolderTreeItem, SWT.NONE);
					daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
					daoJavaTreeItem.setImage(javaIcon);

					if (!"".equals(name) && isCreateDaoSubFolder) {
						daoSubFolderTreeItem.setExpanded(true);
						daoSubFolderTreeItem.setImage(packageIcon);
					}

					daoFolderTreeItem.setExpanded(true);
					daoFolderTreeItem.setImage(packageIcon);
				} else {
					TreeItem daoJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
					daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
					daoJavaTreeItem.setImage(javaIcon);
				}
			}

			javaPackageTreeItem.setExpanded(true);
			javaRootTreeItem.setExpanded(true);
		}

		javaPackageTreeItem.setExpanded(true);
		javaRootTreeItem.setExpanded(true);
		javaBuildTreeItem.setExpanded(true);

		/* E : Create Java File */
		if(isCreateVo && !"".equals(voPath)) {
			for(String name : databaseTables) {

				if(pattern != null) {
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches() && matcher.groupCount() > 0) {
						name = matcher.group(1);
					}
				}

				name = StringUtils.toCamelCase(name);

				String capitalizePrefix = "";

				if (name.length() > 1)
					capitalizePrefix = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
				else if (name.length() == 1)
					capitalizePrefix = name.substring(0, 1).toUpperCase();

				String voPackage = voPath.replace("/", ".").substring(voPath.lastIndexOf(javaBuildPath) + javaBuildPath.length() + 1);
				String[] voPackagePath = voPackage.split("\\.");

				TreeItem parentVoFolderTreeItem = null;

				int i = 0;

				if(javaBuildPath.indexOf(javaVoBuildPath) > -1) {
					for(; i < voPackagePath.length; i++) {
						TreeItem parentJavaTreeItem = findChildJavaTreeItem(javaBuildTreeItem, voPackagePath[i]);
						if(parentJavaTreeItem != null) {
							parentVoFolderTreeItem = parentJavaTreeItem;
						} else {
							break;
						}
					}
				} else {
					TreeItem javaVoBuildTreeItem = new TreeItem(previewTree, 0);
					javaVoBuildTreeItem.setText(javaVoBuildPath);
					javaVoBuildTreeItem.setImage(packageRootIcon);

					parentVoFolderTreeItem = javaVoBuildTreeItem;
				}

				for (; i < voPackagePath.length; i++) {
					TreeItem voFolderTreeItem = new TreeItem(parentVoFolderTreeItem, 0);
					voFolderTreeItem.setText(voPackagePath[i]);
					voFolderTreeItem.setImage(packageIcon);

					parentVoFolderTreeItem.setExpanded(true);
					parentVoFolderTreeItem = voFolderTreeItem;
				}

				if(isCreateSearchVo) {
					TreeItem daoJavaTreeItem = new TreeItem(parentVoFolderTreeItem, SWT.NONE);
					daoJavaTreeItem.setText("Search" + capitalizePrefix + "Vo.java");
					daoJavaTreeItem.setImage(javaIcon);
				}

				TreeItem voJavaTreeItem = new TreeItem(parentVoFolderTreeItem, SWT.NONE);
				voJavaTreeItem.setText(capitalizePrefix + "Vo.java");
				voJavaTreeItem.setImage(javaIcon);

				parentVoFolderTreeItem.setExpanded(true);
			}
		}

		/* S : Create Resource File */

		if (isCreateMapper &&  createMapperButton.isEnabled() && !"".equals(resourceBuildPath)) {
			TreeItem resourceBuildTreeItem = new TreeItem(previewTree, 0);
			resourceBuildTreeItem.setText(resourceBuildPath);
			resourceBuildTreeItem.setImage(packageRootIcon);

			String mapperPackage = mapperPath.replace("/", ".").substring(mapperPath.lastIndexOf(resourceBuildPath) + resourceBuildPath.length() + 1);
			String[] mapperPackagePath = mapperPackage.split("\\.");

			TreeItem parentMapperFolderTreeItem = resourceBuildTreeItem;

			for (int i = 0; i < mapperPackagePath.length; i++) {
				TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, 0);
				mapperFolderTreeItem.setText(mapperPackagePath[i]);
				mapperFolderTreeItem.setImage(folderIcon);

				parentMapperFolderTreeItem.setExpanded(true);
				parentMapperFolderTreeItem = mapperFolderTreeItem;
			}

			for(String name : databaseTables) {

				if(pattern != null) {
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches() && matcher.groupCount() > 0) {
						name = matcher.group(1);
					}
				}

				name = StringUtils.toCamelCase(name);

				if (!"".equals(name)) {
					TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, 0);
					mapperFolderTreeItem.setText(name);
					mapperFolderTreeItem.setImage(folderIcon);

					TreeItem mapperFileTreeItem = new TreeItem(mapperFolderTreeItem, 0);
					mapperFileTreeItem.setText(name + "Mapper.xml");
					mapperFileTreeItem.setImage(fileIcon);

					mapperFolderTreeItem.setExpanded(true);
				} else {
					TreeItem mapperFileTreeItem = new TreeItem(parentMapperFolderTreeItem, 0);
					mapperFileTreeItem.setText(name + "Mapper.xml");
					mapperFileTreeItem.setImage(fileIcon);
				}
			}

			parentMapperFolderTreeItem.setExpanded(true);
			resourceBuildTreeItem.setExpanded(true);
		}

		/* E : Create Resource File */

		/* S : Create JSP File */

		if (isCreateJsp && createJspButton.isEnabled()) {
			TreeItem jspBuildTreeItem = new TreeItem(previewTree, 0);
			jspBuildTreeItem.setText(jspPath.substring(javaProject.getProject().getName().length() + 2));
			jspBuildTreeItem.setImage(folderIcon);

			for(String name : databaseTables) {

				if(pattern != null) {
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches() && matcher.groupCount() > 0) {
						name = matcher.group(1);
					}
				}

				name = StringUtils.toCamelCase(name);

				TreeItem jspFolderTreeItem = null;

				if (!"".equals(name)) {
					jspFolderTreeItem = new TreeItem(jspBuildTreeItem, 0);
					jspFolderTreeItem.setText(name);
					jspFolderTreeItem.setImage(folderIcon);
				}

				if(existsJspTemplateListFile) {
					TreeItem jspListFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, 0);
					jspListFileTreeItem.setText(name + "List.jsp");
					jspListFileTreeItem.setImage(fileIcon);
				}

				if(existsJspTemplatePostFile) {
					TreeItem jspPostFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, 0);
					jspPostFileTreeItem.setText(name + "Post.jsp");
					jspPostFileTreeItem.setImage(fileIcon);
				}

				if(existsJspTemplateViewFile) {
					TreeItem jspViewFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, 0);
					jspViewFileTreeItem.setText(name + "View.jsp");
					jspViewFileTreeItem.setImage(fileIcon);
				}

				if(jspFolderTreeItem != null) jspFolderTreeItem.setExpanded(true);

			}

			jspBuildTreeItem.setExpanded(true);
		}

		/* E : Create JSP File */

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Genernator");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(660, 670);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (id == IDialogConstants.OK_ID) {
			okButton = super.createButton(parent, id, label, defaultButton);
			okButtonEnabled();
			return okButton;
		} else {
			return super.createButton(parent, id, label, defaultButton);
		}
	}

	protected void okButtonEnabled() {
		if (okButton == null)
			return;

		if (!"".equals(templateCombo.getText())) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	public String getTemplateGroupName() {
		return templateGroupName;
	}

	public void setTemplateGroupName(String templateGroupName) {
		this.templateGroupName = templateGroupName;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isCreateFolder() {
		return isCreateFolder;
	}

	public boolean isParentLocation() {
		return isParentLocation;
	}

	public void setCreateFolder(boolean createFolder) {
		this.isCreateFolder = createFolder;
	}

	public void setParentLocation(boolean parentLocation) {
		this.isParentLocation = parentLocation;
	}

	public boolean isCreateService() {
		return isCreateService;
	}

	public void setCreateService(boolean isCreateService) {
		this.isCreateService = isCreateService;
	}

	public boolean isCreateDao() {
		return isCreateDao;
	}

	public void setCreateDao(boolean isCreateDao) {
		this.isCreateDao = isCreateDao;
	}

	public boolean isCreateMapper() {
		return isCreateMapper;
	}

	public void setCreateMapper(boolean isCreateMapper) {
		this.isCreateMapper = isCreateMapper;
	}

	public boolean isCreateJsp() {
		return isCreateJsp;
	}

	public void setCreateJsp(boolean isCreateJsp) {
		this.isCreateJsp = isCreateJsp;
	}

	public boolean isCreateVo() {
		return isCreateVo;
	}

	public void setCreateVo(boolean isCreateVo) {
		this.isCreateVo = isCreateVo;
	}

	public boolean isCreateController() {
		return isCreateController;
	}

	public void setCreateController(boolean isCreateController) {
		this.isCreateController = isCreateController;
	}

	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}

	public String[] getDatabaseTables() {
		return databaseTables;
	}

	public void setDatabaseTables(String[] databaseTables) {
		this.databaseTables = databaseTables;
	}

	private void getDatabaseTrees() {
		databaseTablesTree.removeAll();
		connectionProfile = null;
		for (IConnectionProfile profile : connectionProfiles) {
			if (profile.getName().equals(connectionProfileCombo.getText())) {
				connectionProfile = profile;
				break;
			}
		}
		if (connectionProfile != null) {
			List<String> databaseTables = databaseResource.getDatabaseTables(connectionProfile);
			for(String databaseTable : databaseTables) {
				TreeItem treeItem = new TreeItem(databaseTablesTree, SWT.NONE);
				treeItem.setText(databaseTable);
			}
		}
	}

	private TreeItem findChildJavaTreeItem(TreeItem treeItem, String text) {
		TreeItem findItem = null;
		for(TreeItem item : treeItem.getItems()) {
			if(text.equals(item.getText())) {
				findItem = item;
			} else {
				TreeItem findChildrenItem = findChildJavaTreeItem(item, text);
				findItem = findChildrenItem == null ? findItem : findChildrenItem;
			}
		}
		return findItem;
	}

	@Override
	protected void okPressed() {
		isCreateController = isCreateController && createControllerButton.isEnabled();
		isCreateService = isCreateService && createServiceButton.isEnabled();
		isCreateDao = isCreateDao && createDaoButton.isEnabled();
		isCreateMapper = isCreateMapper && createMapperButton.isEnabled();
		isCreateJsp = isCreateJsp && createJspButton.isEnabled();
		super.okPressed();
	}

}
