package kr.pe.maun.csdgenerator.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
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
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

public class CSDGeneratorDialog extends Dialog {

	private CSDGeneratorPropertiesItem propertiesItem;
	private CSDGeneratorPropertiesHelper propertiesHelper;

	private String type;

	private String prefix = "";
	private Text prefixField;

	private boolean isParentLocation = false;
	private boolean isCreateFolder = true;

	Combo templateCombo;

	private String targetTable;
	private IConnectionProfile connectionProfile;

	private ISelection selection;

	Button createParentLocation;
	Button createFolder;

	Combo connectionProfileCombo;
	Combo tablesCombo;

	Button templateBoard;

	IConnectionProfile[] connectionProfiles;
	DatabaseResource databaseResource;

	private Tree previewTree;
	private Tree databaseTablesTree;

	private String[] databaseTables;

	IPackageFragment javaPackageFragment;
	IResource resource;

	private Button okButton;

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
		container.setBackgroundMode(SWT.INHERIT_FORCE);

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

		type = propertiesItem.getType();
		String databaseConnectionProfileName = propertiesItem.getDatabaseConnectionProfileName();
		String[] templateGroupNames = propertiesHelper.getGeneralTemplateGroupNames();

		container.setLayout(new GridLayout(4, false));

		Label templateLabel = new Label(container, SWT.NONE);
		templateLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		templateLabel.setText("Template: ");

		templateCombo = new Combo(container, SWT.READ_ONLY);
		templateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		templateCombo.setItems(templateGroupNames);

		templateCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
		connectionProfileCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		for (IConnectionProfile profile : connectionProfiles) {
			connectionProfileCombo.add(profile.getName());
		}
		connectionProfileCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDatabaseTables();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		createParentLocation = new Button(container, SWT.CHECK);
		createParentLocation.setText("Create parent location");
		createParentLocation.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
		createParentLocation.addSelectionListener(new SelectionListener() {
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
		if("B".equals(type)) createParentLocation.setEnabled(false);

		createFolder = new Button(container, SWT.CHECK);
		createFolder.setText("Create folder");
		createFolder.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
		createFolder.addSelectionListener(new SelectionListener() {
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
		createFolder.setSelection(this.isCreateFolder);

		/* E : DB 연결 */

		/*
		 * Group templateGroup = new Group(container, SWT.NONE);
		 * templateGroup.setText("Template"); templateGroup.setLayout(new
		 * RowLayout(SWT.VERTICAL)); templateGroup.setLayoutData(new
		 * GridData(GridData.FILL, GridData.CENTER, true, false));
		 *
		 * templateBoard = new Button(templateGroup, SWT.CHECK);
		 * templateBoard.setText("Board");
		 * templateBoard.addSelectionListener(new SelectionListener() {
		 *
		 * @Override public void widgetSelected(SelectionEvent e) { Button
		 * button = (Button) e.widget; }
		 *
		 * @Override public void widgetDefaultSelected(SelectionEvent e) {
		 * widgetSelected(e); } } );
		 */

		/*
		 * Label prefixLabel = new Label(container, SWT.NONE);
		 * prefixLabel.setLayoutData(new GridData(GridData.BEGINNING,
		 * GridData.CENTER, false, false)); prefixLabel.setText("Prefix: ");
		 */
		prefixField = new Text(container, SWT.BORDER);
		prefixField.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
		prefixField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				prefix = prefixField.getText();
				createTree();
			}
		});

		Label previewLabel = new Label(container, SWT.NONE);
		previewLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 3, 0));
		previewLabel.setText("Preview: ");

		Label tablesLabel = new Label(container, SWT.NONE);
		tablesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		tablesLabel.setText("Tables: ");

		previewTree = new Tree(container, SWT.MULTI | SWT.BORDER);
		previewTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		createTree();

		databaseTablesTree = new Tree(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		databaseTablesTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		databaseTablesTree.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTables = null;
				TreeItem[] treeItems = databaseTablesTree.getItems();
				List<String> databaseTableList = new ArrayList<String>();
				for(TreeItem treeItem : treeItems) {
					if(treeItem.getChecked()) {
						databaseTableList.add(treeItem.getText());
					}
				}
				databaseTables = databaseTableList.toArray(new String[databaseTableList.size()]);
				createTree();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
/*
		tablesCombo = new Combo(container, SWT.READ_ONLY);
		tablesCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		tablesCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				targetTable = tablesCombo.getText();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		tablesCombo.setEnabled(false);

*/
		if(!"".equals(databaseConnectionProfileName)) {
			connectionProfileCombo.select(connectionProfileCombo.indexOf(databaseConnectionProfileName));
			getDatabaseTables();
		}

		return container;
	}

	private void createTree() {

		previewTree.removeAll();

		boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
		boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();

		boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
		boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();

		boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
		boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImplFolder();

		boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
		boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();

		boolean isCreateMapper = propertiesItem.getCreateMapper();
		String mapperPath = propertiesItem.getMapperPath();

		IJavaProject javaProject = javaPackageFragment.getJavaProject();

		String javaBuildPath = "";
		String resourceBuildPath = "";

		try {
			IClasspathEntry[] classpaths = javaProject.getRawClasspath();
			for (IClasspathEntry classpath : classpaths) {
				IPath path = classpath.getPath();
				if (javaPackageFragment.getPath().toString().indexOf(path.toString()) > -1) {
					javaBuildPath = path.removeFirstSegments(1).toString();
				} else if (isCreateMapper && !"".equals(mapperPath) && mapperPath.indexOf(path.toString()) > -1) {
					resourceBuildPath = path.removeFirstSegments(1).toString();
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

		if (isParentLocation) {
			packagePath = packagePath.substring(0, packagePath.lastIndexOf("."));
		}

		/* S : Create Java File */

		TreeItem javaBuildTreeItem = new TreeItem(previewTree, 0);
		javaBuildTreeItem.setText(javaBuildPath);
		javaBuildTreeItem.setImage(packageRootIcon);

		TreeItem javaPackageTreeItem = new TreeItem(javaBuildTreeItem, 1);
		javaPackageTreeItem.setText(packagePath);
		javaPackageTreeItem.setImage(packageIcon);

		TreeItem javaRootTreeItem = null;

		/*if("A".equals(type)) {*/

			if(databaseTables == null || databaseTables.length == 0) databaseTables = new String[]{prefix};

			TreeItem controllerFolderTreeItem = null;
			TreeItem serviceFolderTreeItem = null;
			TreeItem serviceImplFolderTreeItem = null;
			TreeItem daoFolderTreeItem = null;

			for(String prefix : databaseTables) {

				prefix = StringUtils.toCamelCase(prefix);

				String capitalizePrefix = "";

				if (prefix.length() > 1)
					capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
				else if (prefix.length() == 1)
					capitalizePrefix = prefix.substring(0, 1).toUpperCase();

				if ("A".equals(type) && isCreateFolder && !"".equals(prefix)) {
					javaRootTreeItem = new TreeItem(javaPackageTreeItem, 1);
					javaRootTreeItem.setText(prefix);
					javaRootTreeItem.setImage(packageIcon);
				} else {
					javaRootTreeItem = javaPackageTreeItem;
				}

				if ("A".equals(type) && isCreateControllerFolder) {

					String controllerFolder = "";

					if (isAddPrefixControllerFolder) {
						controllerFolder += capitalizePrefix;
					}

					controllerFolder += "Controller";

					controllerFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
					controllerFolderTreeItem.setText(controllerFolder);

					TreeItem controllerJavaTreeItem = new TreeItem(controllerFolderTreeItem, SWT.NONE);
					controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
					controllerJavaTreeItem.setImage(javaIcon);

					controllerFolderTreeItem.setExpanded(true);
					controllerFolderTreeItem.setImage(packageIcon);

				} else if ("B".equals(type)) {

					if(controllerFolderTreeItem == null) {
						controllerFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						controllerFolderTreeItem.setText("controller");
					}

					TreeItem controllerSubFolderTreeItem = new TreeItem(controllerFolderTreeItem, 1);
					controllerSubFolderTreeItem.setText(capitalizePrefix);

					if ("".equals(capitalizePrefix) || !isCreateControllerFolder) {
						controllerSubFolderTreeItem.dispose();
					}

					TreeItem controllerJavaTreeItem = new TreeItem(controllerSubFolderTreeItem.isDisposed() ? controllerFolderTreeItem : controllerSubFolderTreeItem, SWT.NONE);
					controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
					controllerJavaTreeItem.setImage(javaIcon);

					if (!"".equals(capitalizePrefix) && isCreateControllerFolder) {
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

				if ("A".equals(type) && isCreateServiceFolder) {

					String serviceFolder = "";

					if (isAddPrefixServiceFolder) {
						serviceFolder += capitalizePrefix;
					}

					serviceFolder += "Service";

					serviceFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
					serviceFolderTreeItem.setText(serviceFolder);
					serviceFolderTreeItem.setImage(packageIcon);

					if (isCreateServiceImpl) {
						if (isCreateServiceImplFolder) {
							String serviceImplFolder = "Impl";

							serviceImplFolderTreeItem = new TreeItem(serviceFolderTreeItem, 1);
							serviceImplFolderTreeItem.setText(serviceImplFolder);
							serviceImplFolderTreeItem.setImage(packageIcon);
						} else {
							serviceImplFolderTreeItem = serviceFolderTreeItem;
						}
						TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
						serviceImplJavaTreeItem.setText(capitalizePrefix + "ServiceImpl.java");
						serviceImplJavaTreeItem.setImage(javaIcon);
						serviceImplFolderTreeItem.setExpanded(true);
					}

					TreeItem serviceJavaTreeItem = new TreeItem(serviceFolderTreeItem, SWT.NONE);
					serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
					serviceJavaTreeItem.setImage(javaIcon);

					serviceFolderTreeItem.setExpanded(true);
				} else if ("B".equals(type)) {

					if(serviceFolderTreeItem == null) {
						serviceFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						serviceFolderTreeItem.setText("service");
					}

					TreeItem serviceSubFolderTreeItem = new TreeItem(serviceFolderTreeItem, 1);
					serviceSubFolderTreeItem.setText(capitalizePrefix);

					if ("".equals(capitalizePrefix) || !isCreateServiceFolder) {
						serviceSubFolderTreeItem.dispose();
					}

					if (isCreateServiceImpl) {
						if (isCreateServiceImplFolder) {
							if(serviceImplFolderTreeItem == null || isCreateServiceFolder) {
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

					if (!"".equals(capitalizePrefix) && isCreateServiceFolder) {
						serviceSubFolderTreeItem.setExpanded(true);
						serviceSubFolderTreeItem.setImage(packageIcon);
					}

					serviceFolderTreeItem.setExpanded(true);
					serviceFolderTreeItem.setImage(packageIcon);
				} else {
					TreeItem serviceJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
					serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
					serviceJavaTreeItem.setImage(packageIcon);
				}

				if ("A".equals(type) && isCreateDaoFolder) {

					String daoFolder = "";

					if (isAddPrefixDaoFolder) {
						daoFolder += capitalizePrefix;
					}

					daoFolder += "Dao";

					daoFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
					daoFolderTreeItem.setText(daoFolder);
					daoFolderTreeItem.setImage(packageIcon);

					TreeItem daoJavaTreeItem = new TreeItem(daoFolderTreeItem, SWT.NONE);
					daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
					daoJavaTreeItem.setImage(javaIcon);

					daoFolderTreeItem.setExpanded(true);
				} else if ("B".equals(type)) {

					if(daoFolderTreeItem == null) {
						daoFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
						daoFolderTreeItem.setText("dao");
					}

					TreeItem daoSubFolderTreeItem = new TreeItem(daoFolderTreeItem, 1);
					daoSubFolderTreeItem.setText(capitalizePrefix);

					if ("".equals(capitalizePrefix) || !isCreateDaoFolder) {
						daoSubFolderTreeItem.dispose();
					}

					TreeItem daoJavaTreeItem = new TreeItem(daoSubFolderTreeItem.isDisposed() ? daoFolderTreeItem : daoSubFolderTreeItem, SWT.NONE);
					daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
					daoJavaTreeItem.setImage(javaIcon);

					if (!"".equals(capitalizePrefix) && isCreateDaoFolder) {
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

				javaPackageTreeItem.setExpanded(true);
				javaRootTreeItem.setExpanded(true);
			}
/*
		} else {

			String capitalizePrefix = "";

			if (prefix.length() > 1)
				capitalizePrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
			else if (prefix.length() == 1)
				capitalizePrefix = prefix.substring(0, 1).toUpperCase();

			javaRootTreeItem = javaPackageTreeItem;

			if (isCreateControllerFolder) {

				String controllerFolder = "";

				if (isAddPrefixControllerFolder) {
					controllerFolder += capitalizePrefix;
				}

				controllerFolder += "Controller";

				TreeItem controllerFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
				controllerFolderTreeItem.setText(controllerFolder);

				TreeItem controllerJavaTreeItem = new TreeItem(controllerFolderTreeItem, SWT.NONE);
				controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
				controllerJavaTreeItem.setImage(javaIcon);

				controllerFolderTreeItem.setExpanded(true);
				controllerFolderTreeItem.setImage(packageIcon);

			} else {
				TreeItem controllerJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
				controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
				controllerJavaTreeItem.setImage(javaIcon);
			}

			if (isCreateServiceFolder) {

				String serviceFolder = "";

				if (isAddPrefixServiceFolder) {
					serviceFolder += capitalizePrefix;
				}

				serviceFolder += "Service";

				TreeItem serviceFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
				serviceFolderTreeItem.setText(serviceFolder);
				serviceFolderTreeItem.setImage(packageIcon);

				if (isCreateServiceImpl) {
					TreeItem serviceImplFolderTreeItem = null;
					if (isCreateServiceImplFolder) {
						String serviceImplFolder = "Impl";

						serviceImplFolderTreeItem = new TreeItem(serviceFolderTreeItem, 1);
						serviceImplFolderTreeItem.setText(serviceImplFolder);
						serviceImplFolderTreeItem.setImage(packageIcon);
					} else {
						serviceImplFolderTreeItem = serviceFolderTreeItem;
					}
					TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
					serviceImplJavaTreeItem.setText(capitalizePrefix + "ServiceImpl.java");
					serviceImplJavaTreeItem.setImage(javaIcon);
					serviceImplFolderTreeItem.setExpanded(true);
				}

				TreeItem serviceJavaTreeItem = new TreeItem(serviceFolderTreeItem, SWT.NONE);
				serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
				serviceJavaTreeItem.setImage(javaIcon);

				serviceFolderTreeItem.setExpanded(true);

			} else {
				TreeItem serviceJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
				serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
				serviceJavaTreeItem.setImage(packageIcon);
			}

			if (isCreateDaoFolder) {

				String daoFolder = "";

				if (isAddPrefixDaoFolder) {
					daoFolder += capitalizePrefix;
				}

				daoFolder += "Dao";

				TreeItem daoFolderTreeItem = new TreeItem(javaRootTreeItem, 1);
				daoFolderTreeItem.setText(daoFolder);
				daoFolderTreeItem.setImage(packageIcon);

				TreeItem daoJavaTreeItem = new TreeItem(daoFolderTreeItem, SWT.NONE);
				daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
				daoJavaTreeItem.setImage(javaIcon);

				daoFolderTreeItem.setExpanded(true);

			} else {
				TreeItem daoJavaTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
				daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
				daoJavaTreeItem.setImage(javaIcon);
			}
		}
*/
		javaPackageTreeItem.setExpanded(true);
		javaRootTreeItem.setExpanded(true);
		javaBuildTreeItem.setExpanded(true);

		/* E : Create Java File */

		/* S : Create Resource File */

		if (isCreateMapper && !"".equals(resourceBuildPath)) {
			TreeItem resourceBuildTreeItem = new TreeItem(previewTree, 0);
			resourceBuildTreeItem.setText(resourceBuildPath);
			resourceBuildTreeItem.setImage(packageRootIcon);

			String mapperPackage = mapperPath.replace("/", ".")
					.substring(mapperPath.lastIndexOf(resourceBuildPath) + resourceBuildPath.length() + 1);
			String[] mapperPackagePath = mapperPackage.split("\\.");

			TreeItem parentMapperFolderTreeItem = resourceBuildTreeItem;

			for (int i = 0; i < mapperPackagePath.length; i++) {
				TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, 0);
				mapperFolderTreeItem.setText(mapperPackagePath[i]);
				mapperFolderTreeItem.setImage(folderIcon);

				parentMapperFolderTreeItem.setExpanded(true);
				parentMapperFolderTreeItem = mapperFolderTreeItem;
			}

			if (!"".equals(prefix)) {
				TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, 0);
				mapperFolderTreeItem.setText(prefix);
				mapperFolderTreeItem.setImage(folderIcon);

				TreeItem mapperFileTreeItem = new TreeItem(mapperFolderTreeItem, 0);
				mapperFileTreeItem.setText(prefix + "Mapper.xml");
				mapperFileTreeItem.setImage(fileIcon);

				mapperFolderTreeItem.setExpanded(true);
			}

			parentMapperFolderTreeItem.setExpanded(true);
			resourceBuildTreeItem.setExpanded(true);
		}

		/* E : Create Resource File */

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Genernator");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(660, 700);
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

	public String getTargetTable() {
		return targetTable;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}
/*
	private void getDatabaseTables() {
		targetTable = null;
		tablesCombo.removeAll();
		tablesCombo.setEnabled(false);
		connectionProfile = null;
		for (IConnectionProfile profile : connectionProfiles) {
			if (profile.getName().equals(connectionProfileCombo.getText())) {
				connectionProfile = profile;
				break;
			}
		}
		if (connectionProfile != null) {
			List<String> tables = databaseResource.getTables(connectionProfile);
			if (tables != null) {
				for (String table : tables) {
					tablesCombo.add(table);
				}
				tablesCombo.setEnabled(true);
			}
		}
	}
*/
	private void getDatabaseTables() {
		targetTable = null;
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
}
