package kr.pe.maun.csdgenerator.properties;

import java.util.List;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.PropertiesControllerTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesDaoTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesGeneralTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesMapperTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesServiceTemplateDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;

public class CSDGeneratorResourcePerpertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private CSDGeneratorPropertiesHelper propertiesHelper;

	String[] generalTemplateGroupNames;
	String[] controllerTemplateNames;
	String[] serviceTemplateNames;
	String[] daoTemplateNames;
	String[] mapperTemplateNames;

	Tree generalTemplateTree;
	Tree controllerTemplateTree;
	Tree serviceTemplateTree;
	Tree daoTemplateTree;
	Tree mapperTemplateTree;
	Tree jspTemplateTree;

	private Text companyText;
	private Text authorText;

	/*private Text controllerTemplateFileName;*/
	private Button controllerTemplateFileButton;

	private Text serviceTemplateFileName;
	private Button serviceTemplateFileButton;

	private Text serviceImplTemplateFileName;
	private Button serviceImplTemplateFileButton;

	private Text daoTemplateFileName;
	private Button daoTemplateFileButton;

	private Button specificSettings;

	private Button createControllerFolderButton;
	private Button addPrefixControllerFolderButton;

	private Button createServiceFolder;
	private Button addPrefixServiceFolder;
	private Button createServiceImpl;
	private Button createServiceImplFolder;

	private Button createDaoFolder;
	private Button addPrefixDaoFolder;

	private Button createMapper;

	private Text mapperPathName;
	private Button mapperPathButton;

	private Text mapperTemplateFileName;
	private Button mapperTemplateFileButton;

	private Button createJsp;

	private Text jspPathName;
	private Button jspPathButton;

	private Text jspTemplateFileName;
	private Button jspTemplateFileButton;

	Combo connectionCombo;

	IConnectionProfile[] connectionProfiles;
	DatabaseResource databaseResource;

	@Override
	protected Control createContents(Composite parent) {

		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope((IProject) getElement().getAdapter(IProject.class)).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		boolean isSpecificSettings = isSpecificSettings();

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout defalutLayout = new GridLayout(3, false);
		defalutLayout.marginHeight = 0;
		defalutLayout.marginWidth = 0;
		panel.setLayout(defalutLayout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		specificSettings = new Button(panel, SWT.CHECK);
		specificSettings.setText("Enable project specific settings");
		specificSettings.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				if(button.getSelection()) {
					setEnabledSettings();
				} else {
					setDisabledSettings();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		specificSettings.setSelection(isSpecificSettings);

		Link link = new Link(panel, SWT.NONE);
		link.setText("<a>Configure Workspace Settings..</a>");
		link.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 0));
		link.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferenceDialog createPreferenceDialogOn = PreferencesUtil.createPreferenceDialogOn(null, "kr.pe.maun.csdgenerator.preferences.CSDGeneratorPreferencePage", new String[]{""}, null);
				createPreferenceDialogOn.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label separator = new Label(panel, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));

		org.eclipse.jdt.ui.ISharedImages sharedImages = JavaUI.getSharedImages();
		org.eclipse.ui.ISharedImages workbenchSharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image packageRootIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
		Image packageIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		Image javaIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		Image fileIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);

	    TabFolder tabFolder = new TabFolder(panel, SWT.NONE);
	    tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 0));

/* S : General Tab Folder */

	    TabItem generalTab = new TabItem(tabFolder, SWT.NONE);
	    generalTab.setText("General");

	    Composite generalComposite = new Composite(tabFolder, SWT.NULL);
	    generalComposite.setLayout(new GridLayout(3, false));

	    Composite typeComposite = new Composite(generalComposite, SWT.NULL);
	    typeComposite.setLayout(new GridLayout(4, false));
	    typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0));

	    /* S : Type A */

	    Button typeAButton = new Button(typeComposite, SWT.RADIO);
	    typeAButton.setText("Type A:");
	    typeAButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    Tree typeATree = new Tree(typeComposite, SWT.MULTI | SWT.BORDER);
	    typeATree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    TreeItem typeASourceTreeItem = new TreeItem(typeATree, SWT.NULL);
	    typeASourceTreeItem.setText("src/main/java");
	    typeASourceTreeItem.setImage(packageRootIcon);

	    TreeItem typeAPackageTreeItem = new TreeItem(typeASourceTreeItem, SWT.NULL);
	    typeAPackageTreeItem.setText("org.example");
	    typeAPackageTreeItem.setImage(packageIcon);

	    TreeItem typeARootTreeItem = new TreeItem(typeAPackageTreeItem, SWT.NULL);
	    typeARootTreeItem.setText("test");
	    typeARootTreeItem.setImage(packageIcon);

	    TreeItem typeAControllerRootTreeItem = new TreeItem(typeARootTreeItem, SWT.NULL);
	    typeAControllerRootTreeItem.setText("controller");
	    typeAControllerRootTreeItem.setImage(packageIcon);

	    TreeItem typeAControllerTreeItem = new TreeItem(typeAControllerRootTreeItem, SWT.NULL);
	    typeAControllerTreeItem.setText("Controller.java");
	    typeAControllerTreeItem.setImage(javaIcon);

	    typeAControllerRootTreeItem.setExpanded(true);

	    TreeItem typeAServiceRootTreeItem = new TreeItem(typeARootTreeItem, SWT.NULL);
	    typeAServiceRootTreeItem.setText("service");
	    typeAServiceRootTreeItem.setImage(packageIcon);

	    TreeItem typeAServiceTreeItem = new TreeItem(typeAServiceRootTreeItem, SWT.NULL);
	    typeAServiceTreeItem.setText("Service.java");
	    typeAServiceTreeItem.setImage(javaIcon);

	    typeAServiceRootTreeItem.setExpanded(true);

	    TreeItem typeADaoRootTreeItem = new TreeItem(typeARootTreeItem, SWT.NULL);
	    typeADaoRootTreeItem.setText("dao");
	    typeADaoRootTreeItem.setImage(packageIcon);

	    TreeItem typeADaoTreeItem = new TreeItem(typeADaoRootTreeItem, SWT.NULL);
	    typeADaoTreeItem.setText("Dao.java");
	    typeADaoTreeItem.setImage(javaIcon);

	    typeADaoRootTreeItem.setExpanded(true);

	    typeARootTreeItem.setExpanded(true);
	    typeAPackageTreeItem.setExpanded(true);
	    typeASourceTreeItem.setExpanded(true);

		/* E : Type A */
		/* S : Type B */

	    Button typeBButton = new Button(typeComposite, SWT.RADIO);
		typeBButton.setText("Type B:");
		typeBButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    Tree typeBTree = new Tree(typeComposite, SWT.MULTI | SWT.BORDER);
	    typeBTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    TreeItem typeBSourceTreeItem = new TreeItem(typeBTree, 1);
	    typeBSourceTreeItem.setText("src/main/java");
	    typeBSourceTreeItem.setImage(packageRootIcon);

	    TreeItem typeBPackageTreeItem = new TreeItem(typeBSourceTreeItem, SWT.NULL);
	    typeBPackageTreeItem.setText("org.example");
	    typeBPackageTreeItem.setImage(packageIcon);


	    TreeItem typeBControllerRootTreeItem = new TreeItem(typeBPackageTreeItem, SWT.NULL);
	    typeBControllerRootTreeItem.setText("controller");
	    typeBControllerRootTreeItem.setImage(packageIcon);

	    TreeItem typeBControllerFolderTreeItem = new TreeItem(typeBControllerRootTreeItem, SWT.NULL);
	    typeBControllerFolderTreeItem.setText("test");
	    typeBControllerFolderTreeItem.setImage(packageIcon);

	    TreeItem typeBControllerTreeItem = new TreeItem(typeBControllerFolderTreeItem, SWT.NULL);
	    typeBControllerTreeItem.setText("Controller.java");
	    typeBControllerTreeItem.setImage(javaIcon);

	    typeBControllerFolderTreeItem.setExpanded(true);
	    typeBControllerRootTreeItem.setExpanded(true);

	    TreeItem typeBServiceRootTreeItem = new TreeItem(typeBPackageTreeItem, SWT.NULL);
	    typeBServiceRootTreeItem.setText("service");
	    typeBServiceRootTreeItem.setImage(packageIcon);

	    TreeItem typeBServiceFolderTreeItem = new TreeItem(typeBServiceRootTreeItem, SWT.NULL);
	    typeBServiceFolderTreeItem.setText("test");
	    typeBServiceFolderTreeItem.setImage(packageIcon);

	    TreeItem typeBServiceTreeItem = new TreeItem(typeBServiceFolderTreeItem, SWT.NULL);
	    typeBServiceTreeItem.setText("Service.java");
	    typeBServiceTreeItem.setImage(javaIcon);

	    typeBServiceFolderTreeItem.setExpanded(true);
	    typeBServiceRootTreeItem.setExpanded(true);

	    TreeItem typeBDaoRootTreeItem = new TreeItem(typeBPackageTreeItem, SWT.NULL);
	    typeBDaoRootTreeItem.setText("dao");
	    typeBDaoRootTreeItem.setImage(packageIcon);

	    TreeItem typeBDaoFolderTreeItem = new TreeItem(typeBDaoRootTreeItem, SWT.NULL);
	    typeBDaoFolderTreeItem.setText("test");
	    typeBDaoFolderTreeItem.setImage(packageIcon);

	    TreeItem typeBDaoTreeItem = new TreeItem(typeBDaoFolderTreeItem, SWT.NULL);
	    typeBDaoTreeItem.setText("Dao.java");
	    typeBDaoTreeItem.setImage(javaIcon);

	    typeBDaoFolderTreeItem.setExpanded(true);
	    typeBDaoRootTreeItem.setExpanded(true);

	    typeBPackageTreeItem.setExpanded(true);
	    typeBSourceTreeItem.setExpanded(true);

		/* E : Type B */

		Label companyLabel = new Label(generalComposite, SWT.NONE);
		companyLabel.setText("Company:");
		companyLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		companyText = new Text(generalComposite, SWT.FILL | SWT.BORDER);
		companyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 0));
		if(!isSpecificSettings) companyText.setEnabled(false);

		Label authorLabel = new Label(generalComposite, SWT.NONE);
		authorLabel.setText("Author:");
		authorLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		authorText = new Text(generalComposite, SWT.FILL | SWT.BORDER);
		authorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 0));
		if(!isSpecificSettings) authorText.setEnabled(false);

		Label databaseConnectionLabel = new Label(generalComposite, SWT.NONE);
		databaseConnectionLabel.setText("Database Connection:");
		databaseConnectionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		databaseResource = new DatabaseResource();

		connectionProfiles = ProfileManager.getInstance().getProfiles();
		connectionCombo = new Combo(generalComposite, SWT.NONE);
		connectionCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 0));
		for (IConnectionProfile profile : connectionProfiles) {
			connectionCombo.add(profile.getName());
		}
		connectionCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label generalTemplateLabel = new Label(generalComposite, SWT.NONE);
		generalTemplateLabel.setText("Template Group:");
		generalTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    generalTemplateTree = new Tree(generalComposite, SWT.SINGLE | SWT.BORDER);
	    generalTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite generalButtonComposite = new Composite(generalComposite, SWT.NONE);
		GridLayout generalButtonLayout = new GridLayout(1, false);
		generalButtonLayout.marginHeight = 0;
		generalButtonLayout.marginWidth = 0;
		generalButtonComposite.setLayout(generalButtonLayout);
		generalButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		generalTemplateGroupNames = propertiesHelper.getGeneralTemplateGroupNames();
		for(String key : generalTemplateGroupNames) {
			TreeItem generalTemplateNameTreeItem = new TreeItem(generalTemplateTree, SWT.NULL);
			generalTemplateNameTreeItem.setText(key);
			TreeItem generalTemplateControllerTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
			generalTemplateControllerTreeItem.setText("Controller: " + propertiesHelper.getGeneralTemplateController(key));
			generalTemplateControllerTreeItem.setData(propertiesHelper.getGeneralTemplateController(key));
			TreeItem generalTemplateServiceTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
			generalTemplateServiceTreeItem.setText("Service: " + propertiesHelper.getGeneralTemplateService(key));
			generalTemplateServiceTreeItem.setData(propertiesHelper.getGeneralTemplateService(key));
			TreeItem generalTemplateDaoTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
			generalTemplateDaoTreeItem.setText("Dao: " + propertiesHelper.getGeneralTemplateDao(key));
			generalTemplateDaoTreeItem.setData(propertiesHelper.getGeneralTemplateDao(key));
			TreeItem generalTemplateMapperTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
			generalTemplateMapperTreeItem.setText("Mapper: " + propertiesHelper.getGeneralTemplateMapper(key));
			generalTemplateMapperTreeItem.setData(propertiesHelper.getGeneralTemplateMapper(key));
			TreeItem generalTemplateJSPTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
			generalTemplateJSPTreeItem.setText("JSP: " + propertiesHelper.getGeneralTemplateJSP(key));
			generalTemplateJSPTreeItem.setData(propertiesHelper.getGeneralTemplateJSP(key));
			generalTemplateNameTreeItem.setExpanded(true);
		}

		Button addGeneralTemplateButton = new Button(generalButtonComposite, SWT.NONE);
		addGeneralTemplateButton.setText("Add Template...");
		addGeneralTemplateButton.setLayoutData(new GridData(100, 24));
		addGeneralTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesGeneralTemplateDialog propertiesGeneralTemplateDialog = new PropertiesGeneralTemplateDialog(getShell());
				propertiesGeneralTemplateDialog.setTemplateGroupNames(generalTemplateGroupNames);

				propertiesGeneralTemplateDialog.setControllerTemplateNames(controllerTemplateNames);
				propertiesGeneralTemplateDialog.setServiceTemplateNames(serviceTemplateNames);
				propertiesGeneralTemplateDialog.setDaoTemplateNames(daoTemplateNames);
				propertiesGeneralTemplateDialog.setMapperTemplateNames(mapperTemplateNames);

				if(propertiesGeneralTemplateDialog.open() == Window.OK) {
					TreeItem generalTemplateNameTreeItem = new TreeItem(generalTemplateTree, SWT.NULL);
					generalTemplateNameTreeItem.setText(propertiesGeneralTemplateDialog.getTemplateGroupName());
					TreeItem generalTemplateControllerTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
					generalTemplateControllerTreeItem.setText("Controller: " + propertiesGeneralTemplateDialog.getControllerTemplateName());
					generalTemplateControllerTreeItem.setData(propertiesGeneralTemplateDialog.getControllerTemplateName());
					TreeItem generalTemplateServiceTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
					generalTemplateServiceTreeItem.setText("Service: " + propertiesGeneralTemplateDialog.getServiceTemplateName());
					generalTemplateServiceTreeItem.setData(propertiesGeneralTemplateDialog.getServiceTemplateName());
					TreeItem generalTemplateDaoTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
					generalTemplateDaoTreeItem.setText("Dao: " + propertiesGeneralTemplateDialog.getDaoTemplateName());
					generalTemplateDaoTreeItem.setData(propertiesGeneralTemplateDialog.getDaoTemplateName());
					TreeItem generalTemplateMapperTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
					generalTemplateMapperTreeItem.setText("Mapper: " + propertiesGeneralTemplateDialog.getMapperTemplateName());
					generalTemplateMapperTreeItem.setData(propertiesGeneralTemplateDialog.getMapperTemplateName());
					TreeItem generalTemplateJSPTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
					generalTemplateJSPTreeItem.setText("JSP: " + propertiesGeneralTemplateDialog.getJspTemplateName());
					generalTemplateJSPTreeItem.setData(propertiesGeneralTemplateDialog.getJspTemplateName());
					generalTemplateNameTreeItem.setExpanded(true);
					propertiesHelper.generalPropertiesFlush(generalTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editGeneralTemplateButton = new Button(generalButtonComposite, SWT.NONE);
		editGeneralTemplateButton.setText("Edit...");
		editGeneralTemplateButton.setLayoutData(new GridData(100, 24));
		editGeneralTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(generalTemplateTree.getSelection().length > 0) {
					PropertiesGeneralTemplateDialog propertiesGeneralTemplateDialog = new PropertiesGeneralTemplateDialog(getShell());
					propertiesGeneralTemplateDialog.setTemplateGroupNames(generalTemplateGroupNames);

					TreeItem selectItem = generalTemplateTree.getSelection()[0];
					TreeItem templateGroupName;
					TreeItem controllerTemplateName;
					TreeItem serviceTemplateName;
					TreeItem daoTemplateName;
					TreeItem mapperTemplateName;
					TreeItem jspTemplateName;
					if(selectItem.getParentItem() == null) {
						templateGroupName = selectItem;
					} else {
						templateGroupName = selectItem.getParentItem();
					}

					controllerTemplateName = templateGroupName.getItems()[0];
					serviceTemplateName = templateGroupName.getItems()[1];
					daoTemplateName = templateGroupName.getItems()[2];
					mapperTemplateName = templateGroupName.getItems()[3];
					jspTemplateName = templateGroupName.getItems()[4];

					propertiesGeneralTemplateDialog.setControllerTemplateNames(controllerTemplateNames);
					propertiesGeneralTemplateDialog.setServiceTemplateNames(serviceTemplateNames);
					propertiesGeneralTemplateDialog.setDaoTemplateNames(daoTemplateNames);
					propertiesGeneralTemplateDialog.setMapperTemplateNames(mapperTemplateNames);
					/*propertiesGeneralTemplateDialog.setJspTemplateNames(jspTemplateNames);*/

					propertiesGeneralTemplateDialog.setTemplateGroupName((String) templateGroupName.getData());
					propertiesGeneralTemplateDialog.setControllerTemplateName((String) controllerTemplateName.getData());
					propertiesGeneralTemplateDialog.setServiceTemplateName((String) serviceTemplateName.getData());
					propertiesGeneralTemplateDialog.setDaoTemplateName((String) daoTemplateName.getData());
					propertiesGeneralTemplateDialog.setMapperTemplateName((String) mapperTemplateName.getData());
					propertiesGeneralTemplateDialog.setJspTemplateName((String) jspTemplateName.getData());

					if(propertiesGeneralTemplateDialog.open() == Window.OK) {
						propertiesHelper.generalPropertiesFlush(generalTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeGeneralTemplateButton = new Button(generalButtonComposite, SWT.NONE);
		removeGeneralTemplateButton.setText("Remove");
		removeGeneralTemplateButton.setLayoutData(new GridData(100, 24));
		removeGeneralTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(generalTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = generalTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.generalPropertiesFlush(generalTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    generalTab.setControl(generalComposite);

		generalTab.setControl(generalComposite);

/* E : General Tab Folder */

/* S : Controller Tab Folder */
	    TabItem controllerTab = new TabItem(tabFolder, SWT.NONE);
	    controllerTab.setText("Controller");

	    Composite controllerComposite = new Composite(tabFolder, SWT.NONE);
	    controllerComposite.setLayout(new GridLayout(3, false));
	    controllerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createControllerFolderButton = new Button(controllerComposite, SWT.CHECK);
		createControllerFolderButton.setText("Create controller folder");
		createControllerFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createControllerFolderButton.setEnabled(false);
		createControllerFolderButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledAddPrefixControllerFolder();
					} else {
						setDisabledAddPrefixControllerFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createControllerFolderButton.setSelection(isCreateControllerFolder());

		addPrefixControllerFolderButton = new Button(controllerComposite, SWT.CHECK);
		addPrefixControllerFolderButton.setText("Add prefix controller folder name");
		addPrefixControllerFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateControllerFolder()) addPrefixControllerFolderButton.setEnabled(false);
		addPrefixControllerFolderButton.setSelection(isAddPrefixControllerFolder());

		/* S : select controller template file
		Label controllerTemplateFileNameLabel = new Label(controllerComposite, SWT.NONE);
		controllerTemplateFileNameLabel.setText("Controller template file:");
		controllerTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		controllerTemplateFileName = new Text(controllerComposite, SWT.SINGLE | SWT.BORDER);
		controllerTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) controllerTemplateFileName.setEnabled(false);
		controllerTemplateFileName.setText(getControllerTemplateFile());

		controllerTemplateFileButton = new Button(controllerComposite, SWT.PUSH);
		controllerTemplateFileButton.setText("Browse...");
		controllerTemplateFileButton.setLayoutData(new GridData(100, 24));
		controllerTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) controllerTemplateFileName.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings) controllerTemplateFileButton.setEnabled(false);
		/* E : select controller template file */

		Label controllerTemplateLabel = new Label(controllerComposite, SWT.NONE);
		controllerTemplateLabel.setText("Controller Template:");
		controllerTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    controllerTemplateTree = new Tree(controllerComposite, SWT.SINGLE | SWT.BORDER);
	    controllerTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite controllerButtonComposite = new Composite(controllerComposite, SWT.NONE);
		GridLayout controllerButtonLayout = new GridLayout(1, false);
		controllerButtonLayout.marginHeight = 0;
		controllerButtonLayout.marginWidth = 0;
		controllerButtonComposite.setLayout(controllerButtonLayout);
		controllerButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		controllerTemplateNames = propertiesHelper.getControllerTemplateNames();
		for(String key : controllerTemplateNames){
			TreeItem controllerTemplateNameTreeItem = new TreeItem(controllerTemplateTree, SWT.NULL);
			controllerTemplateNameTreeItem.setText(key);
			TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
			controllerTemplatePathTreeItem.setText(propertiesHelper.getControllerTemplatePath(key));
			controllerTemplateNameTreeItem.setExpanded(true);
		}

		Button addControllerTemplateButton = new Button(controllerButtonComposite, SWT.NONE);
		addControllerTemplateButton.setText("Add Template...");
		addControllerTemplateButton.setLayoutData(new GridData(100, 24));
		addControllerTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesControllerTemplateDialog propertiesControllerTemplateDialog = new PropertiesControllerTemplateDialog(getShell());
				propertiesControllerTemplateDialog.setTemplateNames(controllerTemplateNames);
				if(propertiesControllerTemplateDialog.open() == Window.OK) {
					TreeItem controllerTemplateNameTreeItem = new TreeItem(controllerTemplateTree, SWT.NULL);
					controllerTemplateNameTreeItem.setText(propertiesControllerTemplateDialog.getTemplateName());
					controllerTemplateNameTreeItem.setData("name", propertiesControllerTemplateDialog.getTemplateFile());
					TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
					controllerTemplatePathTreeItem.setText(propertiesControllerTemplateDialog.getTemplateFile());
					controllerTemplateNameTreeItem.setExpanded(true);
					propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editControllerTemplateButton = new Button(controllerButtonComposite, SWT.NONE);
		editControllerTemplateButton.setText("Edit...");
		editControllerTemplateButton.setLayoutData(new GridData(100, 24));
		editControllerTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(controllerTemplateTree.getSelection().length > 0) {
					PropertiesControllerTemplateDialog propertiesControllerTemplateDialog = new PropertiesControllerTemplateDialog(getShell());
					propertiesControllerTemplateDialog.setTemplateNames(controllerTemplateNames);

					TreeItem selectItem = controllerTemplateTree.getSelection()[0];
					TreeItem templateNameName;
					TreeItem templateNamePath;
					if(selectItem.getParentItem() == null) {
						templateNameName = selectItem;
					} else {
						templateNameName = selectItem.getParentItem();
					}

					templateNamePath = selectItem.getItems()[0];

					propertiesControllerTemplateDialog.setTemplateName(templateNameName.getText());
					propertiesControllerTemplateDialog.setTemplateFile(templateNamePath.getText());

					if(propertiesControllerTemplateDialog.open() == Window.OK) {
						templateNamePath.setText(propertiesControllerTemplateDialog.getTemplateFile());
						propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeControllerTemplateButton = new Button(controllerButtonComposite, SWT.NONE);
		removeControllerTemplateButton.setText("Remove");
		removeControllerTemplateButton.setLayoutData(new GridData(100, 24));
		removeControllerTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(controllerTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = controllerTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    controllerTab.setControl(controllerComposite);

/* E : Controller Tab Folder */

/* S : Service Tab Folder */
	    TabItem serviceTab = new TabItem(tabFolder, SWT.NONE);
	    serviceTab.setText("Service");

	    Composite serviceComposite = new Composite(tabFolder, SWT.NULL);
	    serviceComposite.setLayout(new GridLayout(3, false));


		createServiceFolder = new Button(serviceComposite, SWT.CHECK);
		createServiceFolder.setText("Create service folder");
		createServiceFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceFolder.setEnabled(false);
		createServiceFolder.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledAddPrefixServiceFolder();
					} else {
						setDisabledAddPrefixServiceFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceFolder.setSelection(isCreateServiceFolder());

		addPrefixServiceFolder = new Button(serviceComposite, SWT.CHECK);
		addPrefixServiceFolder.setText("Add prefix service folder name");
		addPrefixServiceFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateServiceFolder()) addPrefixServiceFolder.setEnabled(false);
		addPrefixServiceFolder.setSelection(isAddPrefixServiceFolder());

		/* S : select service template file */
		Label serviceTemplateFileNameLabel = new Label(serviceComposite, SWT.NONE);
		serviceTemplateFileNameLabel.setText("Service template file:");
		serviceTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		serviceTemplateFileName=new Text(serviceComposite, SWT.SINGLE | SWT.BORDER);
		serviceTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) serviceTemplateFileName.setEnabled(false);
		serviceTemplateFileName.setText(getServiceImplTemplateFile());

		serviceTemplateFileButton = new Button(serviceComposite, SWT.PUSH);
		serviceTemplateFileButton.setText("Browse...");
		serviceTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) serviceTemplateFileName.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings) serviceTemplateFileButton.setEnabled(false);
		/* E : select service template file */

		createServiceImpl = new Button(serviceComposite, SWT.CHECK);
		createServiceImpl.setText("Create ServiceImpl");
		createServiceImpl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceImpl.setEnabled(false);
		createServiceImpl.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledServiceImplFolder();
						setEnabledServiceImplTemplateFile();
					} else {
						setDisabledServiceImplFolder();
						setDisabledServiceImplTemplateFile();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceImpl.setSelection(isCreateServiceImpl());

		createServiceImplFolder = new Button(serviceComposite, SWT.CHECK);
		createServiceImplFolder.setText("Create ServiceImpl folder");
		createServiceImplFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceImplFolder.setEnabled(false);
		createServiceImplFolder.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceImplFolder.setSelection(isCreateServiceImplFolder());
		if(!isSpecificSettings || !isCreateServiceImpl()) createServiceImplFolder.setEnabled(false);

		/* S : select serviceImpl template file */
		Label serviceImplTemplateFileNameLabel = new Label(serviceComposite, SWT.NONE);
		serviceImplTemplateFileNameLabel.setText("ServiceImpl template file:");
		serviceImplTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		serviceImplTemplateFileName=new Text(serviceComposite, SWT.SINGLE | SWT.BORDER);
		serviceImplTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings || !isCreateServiceImpl()) serviceImplTemplateFileName.setEnabled(false);
		serviceImplTemplateFileName.setText(getServiceImplTemplateFile());

		serviceImplTemplateFileButton = new Button(serviceComposite, SWT.PUSH);
		serviceImplTemplateFileButton.setText("Browse...");
		serviceImplTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) serviceImplTemplateFileName.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateServiceImpl()) serviceImplTemplateFileButton.setEnabled(false);
		/* E : select serviceImpl template file */

		Label serviceTemplateLabel = new Label(serviceComposite, SWT.NONE);
		serviceTemplateLabel.setText("Service Template:");
		serviceTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    serviceTemplateTree = new Tree(serviceComposite, SWT.SINGLE | SWT.BORDER);
	    serviceTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite serviceButtonComposite = new Composite(serviceComposite, SWT.NONE);
		GridLayout serviceButtonLayout = new GridLayout(1, false);
		serviceButtonLayout.marginHeight = 0;
		serviceButtonLayout.marginWidth = 0;
		serviceButtonComposite.setLayout(serviceButtonLayout);
		serviceButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		serviceTemplateNames = propertiesHelper.getServiceTemplateNames();
		for(String key : serviceTemplateNames){
			TreeItem serviceTemplateNameTreeItem = new TreeItem(serviceTemplateTree, SWT.NULL);
			serviceTemplateNameTreeItem.setText(key);
			TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
			serviceTemplatePathTreeItem.setText(propertiesHelper.getServiceTemplatePath(key));
			serviceTemplateNameTreeItem.setExpanded(true);
		}

		Button addServiceTemplateButton = new Button(serviceButtonComposite, SWT.NONE);
		addServiceTemplateButton.setText("Add Template...");
		addServiceTemplateButton.setLayoutData(new GridData(100, 24));
		addServiceTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesServiceTemplateDialog propertiesServiceTemplateDialog = new PropertiesServiceTemplateDialog(getShell());
				propertiesServiceTemplateDialog.setTemplateNames(serviceTemplateNames);
				if(propertiesServiceTemplateDialog.open() == Window.OK) {
					TreeItem serviceTemplateNameTreeItem = new TreeItem(serviceTemplateTree, SWT.NULL);
					serviceTemplateNameTreeItem.setText(propertiesServiceTemplateDialog.getTemplateName());
					serviceTemplateNameTreeItem.setData("name", propertiesServiceTemplateDialog.getTemplateFile());
					TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
					serviceTemplatePathTreeItem.setText(propertiesServiceTemplateDialog.getTemplateFile());
					serviceTemplateNameTreeItem.setExpanded(true);
					propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editServiceTemplateButton = new Button(serviceButtonComposite, SWT.NONE);
		editServiceTemplateButton.setText("Edit...");
		editServiceTemplateButton.setLayoutData(new GridData(100, 24));
		editServiceTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(serviceTemplateTree.getSelection().length > 0) {
					PropertiesServiceTemplateDialog propertiesServiceTemplateDialog = new PropertiesServiceTemplateDialog(getShell());
					propertiesServiceTemplateDialog.setTemplateNames(serviceTemplateNames);

					TreeItem selectItem = serviceTemplateTree.getSelection()[0];
					TreeItem templateNameName;
					TreeItem templateNamePath;
					if(selectItem.getParentItem() == null) {
						templateNameName = selectItem;
					} else {
						templateNameName = selectItem.getParentItem();
					}

					templateNamePath = selectItem.getItems()[0];

					propertiesServiceTemplateDialog.setTemplateName(templateNameName.getText());
					propertiesServiceTemplateDialog.setTemplateFile(templateNamePath.getText());

					if(propertiesServiceTemplateDialog.open() == Window.OK) {
						templateNamePath.setText(propertiesServiceTemplateDialog.getTemplateFile());
						propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeServiceTemplateButton = new Button(serviceButtonComposite, SWT.NONE);
		removeServiceTemplateButton.setText("Remove");
		removeServiceTemplateButton.setLayoutData(new GridData(100, 24));
		removeServiceTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(serviceTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = serviceTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    serviceTab.setControl(serviceComposite);

/* E : Service Tab Folder */

/* S : Dao Tab Folder */
	    TabItem daoTab = new TabItem(tabFolder, SWT.NONE);
	    daoTab.setText("Dao");

	    Composite daoComposite = new Composite(tabFolder, SWT.NULL);
	    daoComposite.setLayout(new GridLayout(3, false));

		createDaoFolder = new Button(daoComposite, SWT.CHECK);
		createDaoFolder.setText("Create dao folder");
		createDaoFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createDaoFolder.setEnabled(false);
		createDaoFolder.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledAddPrefixDaoFolder();
					} else {
						setDisabledAddPrefixDaoFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createDaoFolder.setSelection(isCreateDaoFolder());

		addPrefixDaoFolder = new Button(daoComposite, SWT.CHECK);
		addPrefixDaoFolder.setText("Add prefix dao folder name");
		addPrefixDaoFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateDaoFolder()) addPrefixDaoFolder.setEnabled(false);
		addPrefixDaoFolder.setSelection(isAddPrefixDaoFolder());

		/* S : select dao template file */
		Label daoTemplateFileNameLabel = new Label(daoComposite, SWT.NONE);
		daoTemplateFileNameLabel.setText("Dao template file:");
		daoTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		daoTemplateFileName=new Text(daoComposite, SWT.SINGLE | SWT.BORDER);
		daoTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) daoTemplateFileName.setEnabled(false);
		daoTemplateFileName.setText(getDaoTemplateFile());

		daoTemplateFileButton = new Button(daoComposite, SWT.PUSH);
		daoTemplateFileButton.setText("Browse...");
		daoTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) daoTemplateFileName.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings) daoTemplateFileButton.setEnabled(false);
		/* E : select dao template file */

		Label daoTemplateLabel = new Label(daoComposite, SWT.NONE);
		daoTemplateLabel.setText("Dao Template:");
		daoTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    daoTemplateTree = new Tree(daoComposite, SWT.SINGLE | SWT.BORDER);
	    daoTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite daoButtonComposite = new Composite(daoComposite, SWT.NONE);
		GridLayout daoButtonLayout = new GridLayout(1, false);
		daoButtonLayout.marginHeight = 0;
		daoButtonLayout.marginWidth = 0;
		daoButtonComposite.setLayout(daoButtonLayout);
		daoButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		daoTemplateNames = propertiesHelper.getDaoTemplateNames();
		for(String key : daoTemplateNames){
			TreeItem daoTemplateNameTreeItem = new TreeItem(daoTemplateTree, SWT.NULL);
			daoTemplateNameTreeItem.setText(key);
			TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
			daoTemplatePathTreeItem.setText(propertiesHelper.getDaoTemplatePath(key));
			daoTemplateNameTreeItem.setExpanded(true);
		}

		Button addDaoTemplateButton = new Button(daoButtonComposite, SWT.NONE);
		addDaoTemplateButton.setText("Add Template...");
		addDaoTemplateButton.setLayoutData(new GridData(100, 24));
		addDaoTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesDaoTemplateDialog propertiesDaoTemplateDialog = new PropertiesDaoTemplateDialog(getShell());
				propertiesDaoTemplateDialog.setTemplateNames(daoTemplateNames);
				if(propertiesDaoTemplateDialog.open() == Window.OK) {
					TreeItem daoTemplateNameTreeItem = new TreeItem(daoTemplateTree, SWT.NULL);
					daoTemplateNameTreeItem.setText(propertiesDaoTemplateDialog.getTemplateName());
					daoTemplateNameTreeItem.setData("name", propertiesDaoTemplateDialog.getTemplateFile());
					TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
					daoTemplatePathTreeItem.setText(propertiesDaoTemplateDialog.getTemplateFile());
					daoTemplateNameTreeItem.setExpanded(true);
					propertiesHelper.daoPropertiesFlush(daoTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editDaoTemplateButton = new Button(daoButtonComposite, SWT.NONE);
		editDaoTemplateButton.setText("Edit...");
		editDaoTemplateButton.setLayoutData(new GridData(100, 24));
		editDaoTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(daoTemplateTree.getSelection().length > 0) {
					PropertiesDaoTemplateDialog propertiesDaoTemplateDialog = new PropertiesDaoTemplateDialog(getShell());
					propertiesDaoTemplateDialog.setTemplateNames(daoTemplateNames);

					TreeItem selectItem = daoTemplateTree.getSelection()[0];
					TreeItem templateNameName;
					TreeItem templateNamePath;
					if(selectItem.getParentItem() == null) {
						templateNameName = selectItem;
					} else {
						templateNameName = selectItem.getParentItem();
					}

					templateNamePath = selectItem.getItems()[0];

					propertiesDaoTemplateDialog.setTemplateName(templateNameName.getText());
					propertiesDaoTemplateDialog.setTemplateFile(templateNamePath.getText());

					if(propertiesDaoTemplateDialog.open() == Window.OK) {
						templateNamePath.setText(propertiesDaoTemplateDialog.getTemplateFile());
						propertiesHelper.daoPropertiesFlush(daoTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeDaoTemplateButton = new Button(daoButtonComposite, SWT.NONE);
		removeDaoTemplateButton.setText("Remove");
		removeDaoTemplateButton.setLayoutData(new GridData(100, 24));
		removeDaoTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(daoTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = daoTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.daoPropertiesFlush(daoTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    daoTab.setControl(daoComposite);

/* E : Dao Tab Folder */

/* S : Mapper Tab Folder */
	    TabItem mapperTab = new TabItem(tabFolder, SWT.NONE);
	    mapperTab.setText("Mapper");

	    Composite mapperComposite = new Composite(tabFolder, SWT.NULL);
	    mapperComposite.setLayout(new GridLayout(3, false));

		createMapper = new Button(mapperComposite, SWT.CHECK);
		createMapper.setText("Create mapper");
		createMapper.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createMapper.setEnabled(false);
		createMapper.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledMapperPath();
						setEnabledMapperTemplateFile();
					} else {
						setDisabledMapperPath();
						setDisabledMapperTemplateFile();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createMapper.setSelection(isCreateMapper());

		/* S : select mapper path */
		Label mapperPathLabel = new Label(mapperComposite, SWT.NONE);
		mapperPathLabel.setText("Mapper Path:");
		mapperPathLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		mapperPathName=new Text(mapperComposite, SWT.SINGLE | SWT.BORDER);
		mapperPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) mapperPathName.setEnabled(false);
		mapperPathName.setText(getMapperPath());

		mapperPathButton = new Button(mapperComposite, SWT.PUSH);
		mapperPathButton.setText("Browse...");
		mapperPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), null, false, "");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) mapperPathName.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateMapper()) mapperPathButton.setEnabled(false);
		/* E : select mapper path */

		/* S : select mapper template file */
		Label mapperTemplateFileLabel = new Label(mapperComposite, SWT.NONE);
		mapperTemplateFileLabel.setText("Mapper template file:");
		mapperTemplateFileLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		mapperTemplateFileName=new Text(mapperComposite, SWT.SINGLE | SWT.BORDER);
		mapperTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) mapperTemplateFileName.setEnabled(false);
		mapperTemplateFileName.setText(getMapperTemplateFile());

		mapperTemplateFileButton = new Button(mapperComposite, SWT.PUSH);
		mapperTemplateFileButton.setText("Browse...");
		mapperTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) mapperTemplateFileButton.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateMapper()) mapperTemplateFileButton.setEnabled(false);
		/* E : select mapper template file */

		Label mapperTemplateLabel = new Label(mapperComposite, SWT.NONE);
		mapperTemplateLabel.setText("Mapper Template:");
		mapperTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    mapperTemplateTree = new Tree(mapperComposite, SWT.SINGLE | SWT.BORDER);
	    mapperTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite mapperButtonComposite = new Composite(mapperComposite, SWT.NONE);
		GridLayout mapperButtonLayout = new GridLayout(1, false);
		mapperButtonLayout.marginHeight = 0;
		mapperButtonLayout.marginWidth = 0;
		mapperButtonComposite.setLayout(mapperButtonLayout);
		mapperButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		mapperTemplateNames = propertiesHelper.getMapperTemplateNames();
		for(String key : mapperTemplateNames){
			TreeItem mapperTemplateNameTreeItem = new TreeItem(mapperTemplateTree, SWT.NULL);
			mapperTemplateNameTreeItem.setText(key);
			TreeItem mapperTemplatePathTreeItem = new TreeItem(mapperTemplateNameTreeItem, SWT.NULL);
			mapperTemplatePathTreeItem.setText(propertiesHelper.getMapperTemplatePath(key));
			mapperTemplateNameTreeItem.setExpanded(true);
		}

		Button addMapperTemplateButton = new Button(mapperButtonComposite, SWT.NONE);
		addMapperTemplateButton.setText("Add Template...");
		addMapperTemplateButton.setLayoutData(new GridData(100, 24));
		addMapperTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesMapperTemplateDialog propertiesMapperTemplateDialog = new PropertiesMapperTemplateDialog(getShell());
				propertiesMapperTemplateDialog.setTemplateNames(mapperTemplateNames);
				if(propertiesMapperTemplateDialog.open() == Window.OK) {
					TreeItem mapperTemplateNameTreeItem = new TreeItem(mapperTemplateTree, SWT.NULL);
					mapperTemplateNameTreeItem.setText(propertiesMapperTemplateDialog.getTemplateName());
					mapperTemplateNameTreeItem.setData("name", propertiesMapperTemplateDialog.getTemplateFile());
					TreeItem mapperTemplatePathTreeItem = new TreeItem(mapperTemplateNameTreeItem, SWT.NULL);
					mapperTemplatePathTreeItem.setText(propertiesMapperTemplateDialog.getTemplateFile());
					mapperTemplateNameTreeItem.setExpanded(true);
					propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editMapperTemplateButton = new Button(mapperButtonComposite, SWT.NONE);
		editMapperTemplateButton.setText("Edit...");
		editMapperTemplateButton.setLayoutData(new GridData(100, 24));
		editMapperTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mapperTemplateTree.getSelection().length > 0) {
					PropertiesMapperTemplateDialog propertiesMapperTemplateDialog = new PropertiesMapperTemplateDialog(getShell());
					propertiesMapperTemplateDialog.setTemplateNames(mapperTemplateNames);

					TreeItem selectItem = mapperTemplateTree.getSelection()[0];
					TreeItem templateNameName;
					TreeItem templateNamePath;
					if(selectItem.getParentItem() == null) {
						templateNameName = selectItem;
					} else {
						templateNameName = selectItem.getParentItem();
					}

					templateNamePath = selectItem.getItems()[0];

					propertiesMapperTemplateDialog.setTemplateName(templateNameName.getText());
					propertiesMapperTemplateDialog.setTemplateFile(templateNamePath.getText());

					if(propertiesMapperTemplateDialog.open() == Window.OK) {
						templateNamePath.setText(propertiesMapperTemplateDialog.getTemplateFile());
						propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeMapperTemplateButton = new Button(mapperButtonComposite, SWT.NONE);
		removeMapperTemplateButton.setText("Remove");
		removeMapperTemplateButton.setLayoutData(new GridData(100, 24));
		removeMapperTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(mapperTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = mapperTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    mapperTab.setControl(mapperComposite);

/* E : Mapper Tab Folder */


/* S : VO Tab Folder */
	    TabItem voTab = new TabItem(tabFolder, SWT.NONE);
	    voTab.setText("VO");

	    Composite voComposite = new Composite(tabFolder, SWT.NULL);
	    voComposite.setLayout(new GridLayout(3, false));

	    voTab.setControl(voComposite);

/* E : VO Tab Folder */

/* S : JSP Tab Folder */
	    TabItem jspTab = new TabItem(tabFolder, SWT.NONE);
	    jspTab.setText("JSP");

	    Composite jspComposite = new Composite(tabFolder, SWT.NULL);
	    jspComposite.setLayout(new GridLayout(3, false));

		createJsp = new Button(jspComposite, SWT.CHECK);
		createJsp.setText("Create jsp");
		createJsp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createJsp.setEnabled(false);
		createJsp.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledJspPath();
						setEnabledJspTemplateFile();
					} else {
						setDisabledJspPath();
						setDisabledJspTemplateFile();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createJsp.setSelection(isCreateJsp());

		/* S : select jsp path */
		Label jspPathLabel = new Label(jspComposite, SWT.NONE);
		jspPathLabel.setText("Jsp Path:");
		jspPathLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		jspPathName=new Text(jspComposite, SWT.SINGLE | SWT.BORDER);
		jspPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) jspPathName.setEnabled(false);
		jspPathName.setText(getJspPath());

		jspPathButton = new Button(jspComposite, SWT.PUSH);
		jspPathButton.setText("Browse...");
		jspPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), null, false, "");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) jspPathName.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateJsp()) jspPathButton.setEnabled(false);
		/* E : select jsp path */

		/* S : select jsp template file */
		Label jspTemplateFileLabel = new Label(jspComposite, SWT.NONE);
		jspTemplateFileLabel.setText("Jsp template file:");
		jspTemplateFileLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		jspTemplateFileName=new Text(jspComposite, SWT.SINGLE | SWT.BORDER);
		jspTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) jspTemplateFileName.setEnabled(false);
		jspTemplateFileName.setText(getJspTemplateFile());

		jspTemplateFileButton = new Button(jspComposite, SWT.PUSH);
		jspTemplateFileButton.setText("Browse...");
		jspTemplateFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					String fileName = dialog.open();
					if(fileName != null) jspTemplateFileButton.setText(fileName);
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateJsp()) jspTemplateFileButton.setEnabled(false);
		/* E : select jsp template file */

	    jspTab.setControl(jspComposite);

/* E : JSP Tab Folder */

		return panel;
	}


	public boolean performOk() {

		setSpecificSettings(specificSettings.getSelection());

		setCompany(companyText.getText());
		setAuthor(authorText.getText());

		setCreateControllerFolder(createControllerFolderButton.getSelection());
		setAddPrefixControllerFolder(addPrefixControllerFolderButton.getSelection());
		/*setControllerTemplateFile(controllerTemplateFileName.getText());*/

		setCreateServiceFolder(createServiceFolder.getSelection());
		setAddPrefixServiceFolder(addPrefixServiceFolder.getSelection());
		setServiceTemplateFile(serviceTemplateFileName.getText());

		setCreateServiceImpl(createServiceImpl.getSelection());
		setCreateServiceImplFolder(createServiceImplFolder.getSelection());
		setServiceImplTemplateFile(serviceImplTemplateFileName.getText());

		setCreateDaoFolder(createDaoFolder.getSelection());
		setAddPrefixDaoFolder(addPrefixDaoFolder.getSelection());
		setDaoTemplateFile(daoTemplateFileName.getText());

		setCreateMapper(createMapper.getSelection());
		setMapperPath(mapperPathName.getText());
		setMapperTemplateFile(mapperTemplateFileName.getText());

		setCreateJsp(createJsp.getSelection());
		setJspPath(jspPathName.getText());
		setJspTemplateFile(jspTemplateFileName.getText());

		return super.performOk();
	}

	public boolean isSpecificSettings() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.PROJECT_SPECIFIC_SETTINGS);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setSpecificSettings(boolean specificSettings) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(specificSettings) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.PROJECT_SPECIFIC_SETTINGS, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.PROJECT_SPECIFIC_SETTINGS, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}


	public String getCompany() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.COMPANY);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setCompany(String company) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(company)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.COMPANY, company);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public String getAuthor() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.AUTHOR);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setAuthor(String company) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(company)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.AUTHOR, company);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public boolean isCreateControllerFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateControllerFolder(boolean createControllerFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createControllerFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_FOLDER, "false");
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isAddPrefixControllerFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_CONTROLLER_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setAddPrefixControllerFolder(boolean addPrefixControllerFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(addPrefixControllerFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_CONTROLLER_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_CONTROLLER_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getControllerTemplateFile() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CONTROLLER_TEMPLATE_FILE);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setControllerTemplateFile(String controllerTemplateFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(controllerTemplateFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CONTROLLER_TEMPLATE_FILE, controllerTemplateFile);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateServiceFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateServiceFolder(boolean createServiceFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createServiceFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isAddPrefixServiceFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_SERVICE_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setAddPrefixServiceFolder(boolean addPrefixServiceFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(addPrefixServiceFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_SERVICE_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_SERVICE_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getServiceTemplateFile() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.SERVICE_TEMPLATE_FILE);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setServiceTemplateFile(String serviceTemplateFile) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(serviceTemplateFile)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.SERVICE_TEMPLATE_FILE, serviceTemplateFile);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public boolean isCreateServiceImpl() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateServiceImpl(boolean createServiceImpl) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createServiceImpl) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL, "false");
	    	  }
	      } catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateServiceImplFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateServiceImplFolder(boolean createServiceImplFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createServiceImplFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICEIMPL_FOLDER, "false");
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String getServiceImplTemplateFile() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.SERVICEIMPL_TEMPLATE_FILE);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setServiceImplTemplateFile(String serviceImplTemplateFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(serviceImplTemplateFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.SERVICEIMPL_TEMPLATE_FILE, serviceImplTemplateFile);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateDaoFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateDaoFolder(boolean createDaoFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createDaoFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isAddPrefixDaoFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setAddPrefixDaoFolder(boolean addPrefixDaoFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(addPrefixDaoFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getDaoTemplateFile() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.SERVICEIMPL_TEMPLATE_FILE);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setDaoTemplateFile(String daoTemplateFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(daoTemplateFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.DAO_TEMPLATE_FILE, daoTemplateFile);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateMapper() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_MAPPER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateMapper(boolean createMapper) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createMapper) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_MAPPER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_MAPPER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getMapperPath() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_PATH);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setMapperPath(String mapperPath) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(mapperPath)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_PATH, mapperPath);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getMapperTemplateFile() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_TEMPLATE_FILE);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setMapperTemplateFile(String mapperTemplateFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(mapperTemplateFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.MAPPER_TEMPLATE_FILE, mapperTemplateFile);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateJsp() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_JSP);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateJsp(boolean createJsp) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createJsp) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_JSP, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_JSP, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getJspPath() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.JSP_PATH);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setJspPath(String jspPath) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(jspPath)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.JSP_PATH, jspPath);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getJspTemplateFile() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.JSP_TEMPLATE_FILE);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setJspTemplateFile(String jspTemplateFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(jspTemplateFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.JSP_TEMPLATE_FILE, jspTemplateFile);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	private void setEnabledAddPrefixControllerFolder() {
		addPrefixControllerFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixControllerFolder() {
		addPrefixControllerFolderButton.setEnabled(false);
	}

	private void setEnabledAddPrefixServiceFolder() {
		addPrefixServiceFolder.setEnabled(true);
	}

	private void setDisabledAddPrefixServiceFolder() {
		addPrefixServiceFolder.setEnabled(false);
	}

	private void setEnabledAddPrefixDaoFolder() {
		addPrefixDaoFolder.setEnabled(true);
	}

	private void setDisabledAddPrefixDaoFolder() {
		addPrefixDaoFolder.setEnabled(false);
	}

	private void setEnabledServiceImplFolder() {
		createServiceImplFolder.setEnabled(true);
	}

	private void setDisabledServiceImplFolder() {
		createServiceImplFolder.setEnabled(false);
	}

	private void setEnabledServiceImplTemplateFile() {
		serviceImplTemplateFileName.setEnabled(true);
		serviceImplTemplateFileButton.setEnabled(true);
	}

	private void setDisabledServiceImplTemplateFile() {
		serviceImplTemplateFileName.setEnabled(false);
		serviceImplTemplateFileButton.setEnabled(false);
	}

	private void setEnabledMapperPath() {
		mapperPathName.setEnabled(true);
		mapperPathButton.setEnabled(true);
	}

	private void setDisabledMapperPath() {
		mapperPathName.setEnabled(false);
		mapperPathButton.setEnabled(false);
	}

	private void setEnabledMapperTemplateFile() {
		mapperTemplateFileName.setEnabled(true);
		mapperTemplateFileButton.setEnabled(true);
	}

	private void setDisabledMapperTemplateFile() {
		mapperTemplateFileName.setEnabled(false);
		mapperTemplateFileButton.setEnabled(false);
	}

	private void setEnabledJspPath() {
		jspPathName.setEnabled(true);
		jspPathButton.setEnabled(true);
	}

	private void setDisabledJspPath() {
		jspPathName.setEnabled(false);
		jspPathButton.setEnabled(false);
	}

	private void setEnabledJspTemplateFile() {
		jspTemplateFileName.setEnabled(true);
		jspTemplateFileButton.setEnabled(true);
	}

	private void setDisabledJspTemplateFile() {
		jspTemplateFileName.setEnabled(false);
		jspTemplateFileButton.setEnabled(false);
	}

	private void setEnabledSettings() {

		companyText.setEnabled(true);
		authorText.setEnabled(true);

		createControllerFolderButton.setEnabled(true);
		if(createControllerFolderButton.getSelection()) addPrefixControllerFolderButton.setEnabled(true);
		/*controllerTemplateFileName.setEnabled(true);*/
		controllerTemplateFileButton.setEnabled(true);

		createServiceFolder.setEnabled(true);
		if(createServiceFolder.getSelection()) addPrefixServiceFolder.setEnabled(true);
		serviceTemplateFileName.setEnabled(true);
		serviceTemplateFileButton.setEnabled(true);

		createServiceImpl.setEnabled(true);
		if(createServiceImpl.getSelection()) {
			createServiceImplFolder.setEnabled(true);
			serviceImplTemplateFileName.setEnabled(true);
			serviceImplTemplateFileButton.setEnabled(true);
		}

		createDaoFolder.setEnabled(true);
		if(createDaoFolder.getSelection()) addPrefixDaoFolder.setEnabled(true);
		daoTemplateFileName.setEnabled(true);
		daoTemplateFileButton.setEnabled(true);

		createMapper.setEnabled(true);
		if(createMapper.getSelection()) {
			mapperPathButton.setEnabled(true);
			mapperPathName.setEnabled(true);
			mapperTemplateFileButton.setEnabled(true);
			mapperTemplateFileName.setEnabled(true);
		}

		createJsp.setEnabled(true);
		if(createJsp.getSelection()) {
			jspPathButton.setEnabled(true);
			jspPathName.setEnabled(true);
			jspTemplateFileButton.setEnabled(true);
			jspTemplateFileName.setEnabled(true);
		}
	}

	private void setDisabledSettings() {

		companyText.setEnabled(false);
		authorText.setEnabled(false);

		addPrefixControllerFolderButton.setEnabled(false);
		/*controllerTemplateFileName.setEnabled(false);*/
		controllerTemplateFileButton.setEnabled(false);

		addPrefixServiceFolder.setEnabled(false);
		serviceTemplateFileName.setEnabled(false);
		serviceTemplateFileButton.setEnabled(false);

		serviceImplTemplateFileName.setEnabled(false);
		serviceImplTemplateFileButton.setEnabled(false);

		addPrefixDaoFolder.setEnabled(false);
		daoTemplateFileName.setEnabled(false);
		daoTemplateFileButton.setEnabled(false);

		createControllerFolderButton.setEnabled(false);

		createServiceFolder.setEnabled(false);
		createServiceImpl.setEnabled(false);
		createServiceImplFolder.setEnabled(false);

		createDaoFolder.setEnabled(false);

		createMapper.setEnabled(false);
		mapperPathButton.setEnabled(false);
		mapperPathName.setEnabled(false);
		mapperTemplateFileButton.setEnabled(false);
		mapperTemplateFileName.setEnabled(false);

		createJsp.setEnabled(false);
		jspPathButton.setEnabled(false);
		jspPathName.setEnabled(false);
		jspTemplateFileButton.setEnabled(false);
		jspTemplateFileName.setEnabled(false);
	}

}
