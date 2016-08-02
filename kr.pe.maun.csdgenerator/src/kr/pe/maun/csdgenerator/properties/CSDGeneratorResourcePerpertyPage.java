package kr.pe.maun.csdgenerator.properties;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.dialogs.SelectionDialog;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.PropertiesControllerTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesDaoTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesDataTypeMappingDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesGeneralTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesJspTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesMapperTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesServiceTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesTestTemplateDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

public class CSDGeneratorResourcePerpertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private CSDGeneratorPropertiesHelper propertiesHelper;

	IProject project;

	String[] generalTemplateGroupNames;
	String[] testTemplateGroupNames;
	String[] controllerTemplateNames;
	String[] serviceTemplateNames;
	String[] daoTemplateNames;
	String[] mapperTemplateNames;
	String[] jspTemplateNames;
	String[] dataTypes;

	Tree generalTemplateTree;
	Tree testTemplateTree;
	Tree controllerTemplateTree;
	Tree serviceTemplateTree;
	Tree daoTemplateTree;
	Tree mapperTemplateTree;
	Tree jspTemplateTree;

	Button typeAButton;
	Button typeBButton;

	private Text companyText;
	private Text authorText;

	/*private Text controllerTemplateFileName;*/
	private Button controllerTemplateFileButton;

	private Text serviceTemplateFileText;
	private Button serviceTemplateFileButton;

	private Text daoTemplateFileText;
	private Button daoTemplateFileButton;

	private Button specificSettings;

	private Button createTestControllerFolderButton;
	private Button createTestServiceFolderButton;
	private Button createTestDaoFolderButton;

	private Button createControllerFolderButton;
	private Button addPrefixControllerFolderButton;
	private Button createControllerSubFolderButton;

	private Button createServiceFolderButton;
	private Button addPrefixServiceFolderButton;
	private Button createServiceSubFolderButton;
	private Button createServiceImplButton;
	private Button createServiceImplFolderButton;

	private Button createDaoFolderButton;
	private Button addPrefixDaoFolderButton;
	private Button createDaoSubFolderButton;

	private Button createMapper;

	private Text mapperPathText;
	private Button mapperPathButton;

	private Button createVoButton;
	private Button createSearchVoButton;

	private Text voFolderText;
	private Button createVoFolderButton;

	private Text voPathText;
	private Button voPathButton;

	private Text voSuperclassText;
	private Button voSuperclassButton;

	private Text myBatisSettingsFileText;
	private Button myBatisSettingsFileButton;

	Table dataTypeMappingTable;

	private Button createJspButton;

	private Text jspPathName;
	private Button jspPathButton;

	private Button createTestButton;
	private Text testPathText;
	private Button testPathButton;

	Combo connectionCombo;

	IConnectionProfile[] connectionProfiles;
	DatabaseResource databaseResource;

	@Override
	protected Control createContents(Composite parent) {

		Device device = Display.getCurrent();

		project = (IProject) getElement().getAdapter(IProject.class);
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		boolean isSpecificSettings = true;

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout defalutLayout = new GridLayout(3, false);
		defalutLayout.marginHeight = 0;
		defalutLayout.marginWidth = 0;
		panel.setLayout(defalutLayout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
/*
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
	    separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));

		org.eclipse.jdt.ui.ISharedImages sharedImages = JavaUI.getSharedImages();
		org.eclipse.ui.ISharedImages workbenchSharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image packageRootIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
		Image packageIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		Image javaIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		Image fileIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
*/
	    TabFolder tabFolder = new TabFolder(panel, SWT.NONE);
	    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0));

/* S : General Tab Folder */

	    TabItem generalTab = new TabItem(tabFolder, SWT.NONE);
	    generalTab.setText("General");

	    Composite generalComposite = new Composite(tabFolder, SWT.NULL);
	    generalComposite.setLayout(new GridLayout(3, false));
	    generalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    GridData defaultLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false);
	    defaultLayoutData.widthHint = 100;

	    /*

	    Composite typeComposite = new Composite(generalComposite, SWT.NULL);
	    typeComposite.setLayout(new GridLayout(4, false));
	    typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0));

	     /* S : Type A

	    typeAButton = new Button(typeComposite, SWT.RADIO);
	    typeAButton.setText("Type A:");
	    typeAButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
	    if("A".equals(getType())) typeAButton.setSelection(true);

	    Tree typeATree = new Tree(typeComposite, SWT.MULTI | SWT.BORDER);
	    typeATree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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
		/* S : Type B

	    typeBButton = new Button(typeComposite, SWT.RADIO);
		typeBButton.setText("Type B:");
		typeBButton.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
		if("B".equals(getType())) typeBButton.setSelection(true);

	    Tree typeBTree = new Tree(typeComposite, SWT.MULTI | SWT.BORDER);
	    typeBTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

	    TreeItem typeBSourceTreeItem = new TreeItem(typeBTree, SWT.NULL);
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
		companyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if(!isSpecificSettings) companyText.setEnabled(false);
		companyText.setText(getCompany());
		new Label(generalComposite, SWT.NONE);

		Label authorLabel = new Label(generalComposite, SWT.NONE);
		authorLabel.setText("Author:");
		authorLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		authorText = new Text(generalComposite, SWT.FILL | SWT.BORDER);
		authorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if(!isSpecificSettings) authorText.setEnabled(false);
		authorText.setText(getAuthor());
		new Label(generalComposite, SWT.NONE);

		Label databaseConnectionLabel = new Label(generalComposite, SWT.NONE);
		databaseConnectionLabel.setText("Database Connection:");
		databaseConnectionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		databaseResource = new DatabaseResource();

		connectionProfiles = ProfileManager.getInstance().getProfiles();
		connectionCombo = new Combo(generalComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		connectionCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		for (IConnectionProfile profile : connectionProfiles) {
			connectionCombo.add(profile.getName());
		}
		if(!"".equals(getDatabaseConnectionProfileName())) connectionCombo.select(connectionCombo.indexOf(getDatabaseConnectionProfileName()));
		connectionCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDatabaseConnectionProfileName(connectionCombo.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		new Label(generalComposite, SWT.NONE);

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
			generalTemplateJSPTreeItem.setText("JSP: " + propertiesHelper.getGeneralTemplateJsp(key));
			generalTemplateJSPTreeItem.setData(propertiesHelper.getGeneralTemplateJsp(key));
			/*generalTemplateNameTreeItem.setExpanded(true);*/
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
				propertiesGeneralTemplateDialog.setJspTemplateNames(jspTemplateNames);

				if(propertiesGeneralTemplateDialog.open() == Window.OK) {

					IPath templatePath = propertiesGeneralTemplateDialog.getTemplatePath();

					if(templatePath != null) {

						String controllerTemplateName = "";
						String serviceTemplateName = "";
						String daoTemplateName = "";
						String mapperTemplateName = "";
						String jspTemplateName = "";
						String jspTemplateListFile = "";
						String jspTemplatePostFile = "";
						String jspTemplateViewFile = "";

						try {
							IFolder templateFolder = project.getWorkspace().getRoot().getFolder(templatePath);
							IResource[] members = templateFolder.members();

							for(IResource member : members) {
								File templateFile = member.getLocation().toFile();
								if(templateFile.exists()) {
									String name = member.getName();
									if(name.indexOf("controller") > -1) {
										controllerTemplateName = templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_controller", "");
										TreeItem controllerTemplateNameTreeItem = new TreeItem(controllerTemplateTree, SWT.NULL);
										controllerTemplateNameTreeItem.setText(controllerTemplateName);
										TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
										controllerTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										controllerTemplateNameTreeItem.setExpanded(true);
										controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
									} else if(name.indexOf("service") > -1) {
										serviceTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_service", "");
										TreeItem serviceTemplateNameTreeItem = new TreeItem(serviceTemplateTree, SWT.NULL);
										serviceTemplateNameTreeItem.setText(serviceTemplateName);
										TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
										serviceTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										serviceTemplateNameTreeItem.setExpanded(true);
										serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
									} else if(name.indexOf("dao") > -1) {
										daoTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_dao", "");
										TreeItem daoTemplateNameTreeItem = new TreeItem(daoTemplateTree, SWT.NULL);
										daoTemplateNameTreeItem.setText(daoTemplateName);
										TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
										daoTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										daoTemplateNameTreeItem.setExpanded(true);
										daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
									} else if(name.indexOf("mapper") > -1) {
										mapperTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_mapper", "");
										TreeItem mapperTemplateNameTreeItem = new TreeItem(mapperTemplateTree, SWT.NULL);
										mapperTemplateNameTreeItem.setText(mapperTemplateName);
										TreeItem mapperTemplatePathTreeItem = new TreeItem(mapperTemplateNameTreeItem, SWT.NULL);
										mapperTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										mapperTemplateNameTreeItem.setExpanded(true);
										mapperTemplateNames = propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
									} else if(name.indexOf("list") > -1) {
										jspTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_list", "");
										jspTemplateListFile = templateFile.getAbsolutePath();
									} else if(name.indexOf("post") > -1) {
										if("".equals(jspTemplateName)) jspTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_post", "");
										jspTemplatePostFile = templateFile.getAbsolutePath();
									} else if(name.indexOf("view") > -1) {
										if("".equals(jspTemplateName)) jspTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_view", "");
										jspTemplateViewFile = templateFile.getAbsolutePath();
									}
								}
							}

							if(!"".equals(jspTemplateName)) {
								TreeItem jspTemplateNameTreeItem = new TreeItem(jspTemplateTree, SWT.NULL);
								jspTemplateNameTreeItem.setText(jspTemplateName);
								TreeItem jspTemplateListTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
								jspTemplateListTreeItem.setText("List: " + jspTemplateListFile);
								jspTemplateListTreeItem.setData(jspTemplateListFile);
								TreeItem jspTemplatePostTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
								jspTemplatePostTreeItem.setText("Post: " + jspTemplatePostFile);
								jspTemplatePostTreeItem.setData(jspTemplatePostFile);
								TreeItem jspTemplateViewTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
								jspTemplateViewTreeItem.setText("View: " + jspTemplateViewFile);
								jspTemplateViewTreeItem.setData(jspTemplateViewFile);
								jspTemplateNameTreeItem.setExpanded(true);
								jspTemplateNames = propertiesHelper.jspPropertiesFlush(jspTemplateTree);
							}
						} catch (CoreException e1) {
							e1.printStackTrace();
						}

						if(!"".equals(controllerTemplateName) || !"".equals(serviceTemplateName) || !"".equals(daoTemplateName) || !"".equals(mapperTemplateName) || !"".equals(jspTemplateName)) {
							TreeItem generalTemplateNameTreeItem = new TreeItem(generalTemplateTree, SWT.NULL);
							generalTemplateNameTreeItem.setText(propertiesGeneralTemplateDialog.getTemplateGroupName());
							TreeItem generalTemplateControllerTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
							generalTemplateControllerTreeItem.setText("Controller: " + controllerTemplateName);
							generalTemplateControllerTreeItem.setData(controllerTemplateName);
							TreeItem generalTemplateServiceTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
							generalTemplateServiceTreeItem.setText("Service: " + serviceTemplateName);
							generalTemplateServiceTreeItem.setData(serviceTemplateName);
							TreeItem generalTemplateDaoTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
							generalTemplateDaoTreeItem.setText("Dao: " + daoTemplateName);
							generalTemplateDaoTreeItem.setData(daoTemplateName);
							TreeItem generalTemplateMapperTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
							generalTemplateMapperTreeItem.setText("Mapper: " + mapperTemplateName);
							generalTemplateMapperTreeItem.setData(mapperTemplateName);
							TreeItem generalTemplateJSPTreeItem = new TreeItem(generalTemplateNameTreeItem, SWT.NULL);
							generalTemplateJSPTreeItem.setText("JSP: " + jspTemplateName);
							generalTemplateJSPTreeItem.setData(jspTemplateName);
							generalTemplateNameTreeItem.setExpanded(true);
							generalTemplateGroupNames = propertiesHelper.generalPropertiesFlush(generalTemplateTree);
						}
					} else {
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
						generalTemplateGroupNames = propertiesHelper.generalPropertiesFlush(generalTemplateTree);
					}
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

					propertiesGeneralTemplateDialog.setTemplateGroupNames(generalTemplateGroupNames);
					propertiesGeneralTemplateDialog.setControllerTemplateNames(controllerTemplateNames);
					propertiesGeneralTemplateDialog.setServiceTemplateNames(serviceTemplateNames);
					propertiesGeneralTemplateDialog.setDaoTemplateNames(daoTemplateNames);
					propertiesGeneralTemplateDialog.setMapperTemplateNames(mapperTemplateNames);
					propertiesGeneralTemplateDialog.setJspTemplateNames(jspTemplateNames);

					propertiesGeneralTemplateDialog.setTemplateGroupName((String) templateGroupName.getText());
					propertiesGeneralTemplateDialog.setControllerTemplateName((String) controllerTemplateName.getData());
					propertiesGeneralTemplateDialog.setServiceTemplateName((String) serviceTemplateName.getData());
					propertiesGeneralTemplateDialog.setDaoTemplateName((String) daoTemplateName.getData());
					propertiesGeneralTemplateDialog.setMapperTemplateName((String) mapperTemplateName.getData());
					propertiesGeneralTemplateDialog.setJspTemplateName((String) jspTemplateName.getData());

					if(propertiesGeneralTemplateDialog.open() == Window.OK) {
						templateGroupName.getItems()[0].setText("Controller: " + propertiesGeneralTemplateDialog.getControllerTemplateName());
						templateGroupName.getItems()[0].setData(propertiesGeneralTemplateDialog.getControllerTemplateName());
						templateGroupName.getItems()[1].setText("Service: " + propertiesGeneralTemplateDialog.getServiceTemplateName());
						templateGroupName.getItems()[1].setData(propertiesGeneralTemplateDialog.getServiceTemplateName());
						templateGroupName.getItems()[2].setText("Dao: " + propertiesGeneralTemplateDialog.getDaoTemplateName());
						templateGroupName.getItems()[2].setData(propertiesGeneralTemplateDialog.getDaoTemplateName());
						templateGroupName.getItems()[3].setText("Mapper: " + propertiesGeneralTemplateDialog.getMapperTemplateName());
						templateGroupName.getItems()[3].setData(propertiesGeneralTemplateDialog.getMapperTemplateName());
						templateGroupName.getItems()[4].setText("JSP: " + propertiesGeneralTemplateDialog.getJspTemplateName());
						templateGroupName.getItems()[4].setData(propertiesGeneralTemplateDialog.getJspTemplateName());
						generalTemplateGroupNames = propertiesHelper.generalPropertiesFlush(generalTemplateTree);
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

/* E : General Tab Folder */

/* S : Test Tab Folder */

	    TabItem testTab = new TabItem(tabFolder, SWT.NONE);
	    testTab.setText("Test");

	    Composite testComposite = new Composite(tabFolder, SWT.NULL);
	    testComposite.setLayout(new GridLayout(3, false));
	    testComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTestButton = new Button(testComposite, SWT.CHECK);
		createTestButton.setText("Create test");
		createTestButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createTestButton.setEnabled(false);
		createTestButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledCreateTestControllerFolder();
						setEnabledCreateTestServiceFolder();
						setEnabledCreateTestDaoFolder();
						setEnabledTestPath();
					} else {
						setDisabledCreateTestControllerFolder();
						setDisabledCreateTestServiceFolder();
						setDisabledCreateTestDaoFolder();
						setDisabledTestPath();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createTestButton.setSelection(isCreateTest());

		createTestControllerFolderButton = new Button(testComposite, SWT.CHECK);
		createTestControllerFolderButton.setText("Create test controller folder");
		createTestControllerFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createTestControllerFolderButton.setEnabled(false);
		createTestControllerFolderButton.setSelection(isCreateTestControllerFolder());

		createTestServiceFolderButton = new Button(testComposite, SWT.CHECK);
		createTestServiceFolderButton.setText("Create test service folder");
		createTestServiceFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createTestServiceFolderButton.setEnabled(false);
		createTestServiceFolderButton.setSelection(isCreateTestServiceFolder());

		createTestDaoFolderButton = new Button(testComposite, SWT.CHECK);
		createTestDaoFolderButton.setText("Create test dao folder");
		createTestDaoFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createTestDaoFolderButton.setEnabled(false);
		createTestDaoFolderButton.setSelection(isCreateTestDaoFolder());

		/* S : select test path */
		Label testPathLabel = new Label(testComposite, SWT.NONE);
		testPathLabel.setText("Test Path:");
		testPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		testPathText = new Text(testComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		testPathText.setLayoutData(defaultLayoutData);
		testPathText.setText(getTestPath());
		if(!isSpecificSettings || !isCreateTest()) testPathText.setEnabled(false);
		testPathText.setBackground(new Color(device, 255, 255, 255));

		testPathButton = new Button(testComposite, SWT.PUSH);
		testPathButton.setText("Browse...");
		testPathButton.setLayoutData(new GridData(100, 24));

		testPathButton.addSelectionListener(new SelectionListener() {
		    @Override public void widgetSelected(SelectionEvent e) {
		      ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), project.getParent(), false, "Test Path Selection:");
		      dialog.open();
		      Object[] result=dialog.getResult();
		      if (result != null && result.length == 1) testPathText.setText(((IPath) result[0]).toString());
		    }
		    @Override public void widgetDefaultSelected(SelectionEvent e) {
		      widgetSelected(e);
		    }
		  }
		);
		if(!isSpecificSettings || !isCreateTest()) testPathButton.setEnabled(false);
		/* E : select test path */

		Label testTemplateLabel = new Label(testComposite, SWT.NONE);
		testTemplateLabel.setText("Template Group:");
		testTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    testTemplateTree = new Tree(testComposite, SWT.SINGLE | SWT.BORDER);
	    testTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite testButtonComposite = new Composite(testComposite, SWT.NONE);
		GridLayout testButtonLayout = new GridLayout(1, false);
		testButtonLayout.marginHeight = 0;
		testButtonLayout.marginWidth = 0;
		testButtonComposite.setLayout(testButtonLayout);
		testButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		testTemplateGroupNames = propertiesHelper.getTestTemplateGroupNames();
		for(String key : testTemplateGroupNames) {
			TreeItem testTemplateNameTreeItem = new TreeItem(testTemplateTree, SWT.NULL);
			testTemplateNameTreeItem.setText(key);
			TreeItem testTemplateControllerTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
			testTemplateControllerTreeItem.setText("Controller: " + propertiesHelper.getTestTemplateController(key));
			testTemplateControllerTreeItem.setData(propertiesHelper.getTestTemplateController(key));
			TreeItem testTemplateServiceTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
			testTemplateServiceTreeItem.setText("Service: " + propertiesHelper.getTestTemplateService(key));
			testTemplateServiceTreeItem.setData(propertiesHelper.getTestTemplateService(key));
			TreeItem testTemplateDaoTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
			testTemplateDaoTreeItem.setText("Dao: " + propertiesHelper.getTestTemplateDao(key));
			testTemplateDaoTreeItem.setData(propertiesHelper.getTestTemplateDao(key));
		}

		Button addTestTemplateButton = new Button(testButtonComposite, SWT.NONE);
		addTestTemplateButton.setText("Add Template...");
		addTestTemplateButton.setLayoutData(new GridData(100, 24));
		addTestTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesTestTemplateDialog propertiesTestTemplateDialog = new PropertiesTestTemplateDialog(getShell());
				propertiesTestTemplateDialog.setTemplateGroupNames(testTemplateGroupNames);

				propertiesTestTemplateDialog.setControllerTemplateNames(controllerTemplateNames);
				propertiesTestTemplateDialog.setServiceTemplateNames(serviceTemplateNames);
				propertiesTestTemplateDialog.setDaoTemplateNames(daoTemplateNames);

				if(propertiesTestTemplateDialog.open() == Window.OK) {

					IPath templatePath = propertiesTestTemplateDialog.getTemplatePath();

					if(templatePath != null) {

						String controllerTemplateName = "";
						String serviceTemplateName = "";
						String daoTemplateName = "";
						String mapperTemplateName = "";
						String jspTemplateName = "";

						try {
							IFolder templateFolder = project.getWorkspace().getRoot().getFolder(templatePath);
							IResource[] members = templateFolder.members();

							for(IResource member : members) {
								File templateFile = member.getLocation().toFile();
								if(templateFile.exists()) {
									String name = member.getName();
									if(name.indexOf("controller") > -1) {
										controllerTemplateName = templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_controller", "");
										TreeItem controllerTemplateNameTreeItem = new TreeItem(controllerTemplateTree, SWT.NULL);
										controllerTemplateNameTreeItem.setText(controllerTemplateName);
										TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
										controllerTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										controllerTemplateNameTreeItem.setExpanded(true);
										controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
									} else if(name.indexOf("service") > -1) {
										serviceTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_service", "");
										TreeItem serviceTemplateNameTreeItem = new TreeItem(serviceTemplateTree, SWT.NULL);
										serviceTemplateNameTreeItem.setText(serviceTemplateName);
										TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
										serviceTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										serviceTemplateNameTreeItem.setExpanded(true);
										serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
									} else if(name.indexOf("dao") > -1) {
										daoTemplateName =  templateFolder.getName() + name.substring(0, name.indexOf(".")).replace("_dao", "");
										TreeItem daoTemplateNameTreeItem = new TreeItem(daoTemplateTree, SWT.NULL);
										daoTemplateNameTreeItem.setText(daoTemplateName);
										TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
										daoTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										daoTemplateNameTreeItem.setExpanded(true);
										daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
									}
								}
							}
						} catch (CoreException e1) {
							e1.printStackTrace();
						}

						if(!"".equals(controllerTemplateName) || !"".equals(serviceTemplateName) || !"".equals(daoTemplateName) || !"".equals(mapperTemplateName) || !"".equals(jspTemplateName)) {
							TreeItem testTemplateNameTreeItem = new TreeItem(testTemplateTree, SWT.NULL);
							testTemplateNameTreeItem.setText(propertiesTestTemplateDialog.getTemplateGroupName());
							TreeItem testTemplateControllerTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
							testTemplateControllerTreeItem.setText("Controller: " + controllerTemplateName);
							testTemplateControllerTreeItem.setData(controllerTemplateName);
							TreeItem testTemplateServiceTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
							testTemplateServiceTreeItem.setText("Service: " + serviceTemplateName);
							testTemplateServiceTreeItem.setData(serviceTemplateName);
							TreeItem testTemplateDaoTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
							testTemplateDaoTreeItem.setText("Dao: " + daoTemplateName);
							testTemplateDaoTreeItem.setData(daoTemplateName);
							testTemplateNameTreeItem.setExpanded(true);
							testTemplateGroupNames = propertiesHelper.testPropertiesFlush(testTemplateTree);
						}
					} else {
						TreeItem testTemplateNameTreeItem = new TreeItem(testTemplateTree, SWT.NULL);
						testTemplateNameTreeItem.setText(propertiesTestTemplateDialog.getTemplateGroupName());
						TreeItem testTemplateControllerTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
						testTemplateControllerTreeItem.setText("Controller: " + propertiesTestTemplateDialog.getControllerTemplateName());
						testTemplateControllerTreeItem.setData(propertiesTestTemplateDialog.getControllerTemplateName());
						TreeItem testTemplateServiceTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
						testTemplateServiceTreeItem.setText("Service: " + propertiesTestTemplateDialog.getServiceTemplateName());
						testTemplateServiceTreeItem.setData(propertiesTestTemplateDialog.getServiceTemplateName());
						TreeItem testTemplateDaoTreeItem = new TreeItem(testTemplateNameTreeItem, SWT.NULL);
						testTemplateDaoTreeItem.setText("Dao: " + propertiesTestTemplateDialog.getDaoTemplateName());
						testTemplateDaoTreeItem.setData(propertiesTestTemplateDialog.getDaoTemplateName());
						testTemplateNameTreeItem.setExpanded(true);
						testTemplateGroupNames = propertiesHelper.testPropertiesFlush(testTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editTestTemplateButton = new Button(testButtonComposite, SWT.NONE);
		editTestTemplateButton.setText("Edit...");
		editTestTemplateButton.setLayoutData(new GridData(100, 24));
		editTestTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(testTemplateTree.getSelection().length > 0) {
					PropertiesTestTemplateDialog propertiesTestTemplateDialog = new PropertiesTestTemplateDialog(getShell());
					propertiesTestTemplateDialog.setTemplateGroupNames(testTemplateGroupNames);

					TreeItem selectItem = testTemplateTree.getSelection()[0];
					TreeItem templateGroupName;
					TreeItem controllerTemplateName;
					TreeItem serviceTemplateName;
					TreeItem daoTemplateName;
					if(selectItem.getParentItem() == null) {
						templateGroupName = selectItem;
					} else {
						templateGroupName = selectItem.getParentItem();
					}

					controllerTemplateName = templateGroupName.getItems()[0];
					serviceTemplateName = templateGroupName.getItems()[1];
					daoTemplateName = templateGroupName.getItems()[2];

					propertiesTestTemplateDialog.setTemplateGroupNames(testTemplateGroupNames);
					propertiesTestTemplateDialog.setControllerTemplateNames(controllerTemplateNames);
					propertiesTestTemplateDialog.setServiceTemplateNames(serviceTemplateNames);
					propertiesTestTemplateDialog.setDaoTemplateNames(daoTemplateNames);

					propertiesTestTemplateDialog.setTemplateGroupName((String) templateGroupName.getText());
					propertiesTestTemplateDialog.setControllerTemplateName((String) controllerTemplateName.getData());
					propertiesTestTemplateDialog.setServiceTemplateName((String) serviceTemplateName.getData());
					propertiesTestTemplateDialog.setDaoTemplateName((String) daoTemplateName.getData());

					if(propertiesTestTemplateDialog.open() == Window.OK) {
						templateGroupName.getItems()[0].setText("Controller: " + propertiesTestTemplateDialog.getControllerTemplateName());
						templateGroupName.getItems()[0].setData(propertiesTestTemplateDialog.getControllerTemplateName());
						templateGroupName.getItems()[1].setText("Service: " + propertiesTestTemplateDialog.getServiceTemplateName());
						templateGroupName.getItems()[1].setData(propertiesTestTemplateDialog.getServiceTemplateName());
						templateGroupName.getItems()[2].setText("Dao: " + propertiesTestTemplateDialog.getDaoTemplateName());
						templateGroupName.getItems()[2].setData(propertiesTestTemplateDialog.getDaoTemplateName());
						testTemplateGroupNames = propertiesHelper.testPropertiesFlush(testTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeTestTemplateButton = new Button(testButtonComposite, SWT.NONE);
		removeTestTemplateButton.setText("Remove");
		removeTestTemplateButton.setLayoutData(new GridData(100, 24));
		removeTestTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(testTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = testTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					propertiesHelper.testPropertiesFlush(testTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		testTab.setControl(testComposite);

/* E : Test Tab Folder */

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
						setEnabledCreateControllerSubFolder();
					} else {
						setDisabledAddPrefixControllerFolder();
						setDisabledCreateControllerSubFolder();
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

		createControllerSubFolderButton = new Button(controllerComposite, SWT.CHECK);
		createControllerSubFolderButton.setText("Create controller sub folder");
		createControllerSubFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateControllerFolder()) createControllerSubFolderButton.setEnabled(false);
		createControllerSubFolderButton.setSelection(isCreateControllerSubFolder());

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
			controllerTemplatePathTreeItem.setText(propertiesHelper.getControllerTemplateFile(key));
			/*controllerTemplateNameTreeItem.setExpanded(true);*/
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
					TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
					controllerTemplatePathTreeItem.setText(propertiesControllerTemplateDialog.getTemplateFile());
					controllerTemplateNameTreeItem.setExpanded(true);
					controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
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
					TreeItem templateName;
					TreeItem templateFile;
					if(selectItem.getParentItem() == null) {
						templateName = selectItem;
					} else {
						templateName = selectItem.getParentItem();
					}

					templateFile = selectItem.getItems()[0];

					propertiesControllerTemplateDialog.setTemplateName(templateName.getText());
					propertiesControllerTemplateDialog.setTemplateFile(templateFile.getText());

					if(propertiesControllerTemplateDialog.open() == Window.OK) {
						templateFile.setText(propertiesControllerTemplateDialog.getTemplateFile());
						controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
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
					controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
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

		createServiceFolderButton = new Button(serviceComposite, SWT.CHECK);
		createServiceFolderButton.setText("Create service folder");
		createServiceFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceFolderButton.setEnabled(false);
		createServiceFolderButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledAddPrefixServiceFolder();
						setEnabledCreateServiceSubFolder();
					} else {
						setDisabledAddPrefixServiceFolder();
						setDisabledCreateServiceSubFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceFolderButton.setSelection(isCreateServiceFolder());

		addPrefixServiceFolderButton = new Button(serviceComposite, SWT.CHECK);
		addPrefixServiceFolderButton.setText("Add prefix service folder name");
		addPrefixServiceFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateServiceFolder()) addPrefixServiceFolderButton.setEnabled(false);
		addPrefixServiceFolderButton.setSelection(isAddPrefixServiceFolder());

		createServiceSubFolderButton = new Button(serviceComposite, SWT.CHECK);
		createServiceSubFolderButton.setText("Create service sub folder");
		createServiceSubFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateServiceFolder()) createServiceSubFolderButton.setEnabled(false);
		createServiceSubFolderButton.setSelection(isCreateServiceSubFolder());

		createServiceImplButton = new Button(serviceComposite, SWT.CHECK);
		createServiceImplButton.setText("Create ServiceImpl");
		createServiceImplButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceImplButton.setEnabled(false);
		createServiceImplButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledServiceImplFolder();
					} else {
						setDisabledServiceImplFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceImplButton.setSelection(isCreateServiceImpl());

		createServiceImplFolderButton = new Button(serviceComposite, SWT.CHECK);
		createServiceImplFolderButton.setText("Create ServiceImpl folder");
		createServiceImplFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createServiceImplFolderButton.setEnabled(false);
		createServiceImplFolderButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createServiceImplFolderButton.setSelection(isCreateServiceImplFolder());
		if(!isSpecificSettings || !isCreateServiceImpl()) createServiceImplFolderButton.setEnabled(false);

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
			serviceTemplatePathTreeItem.setText(propertiesHelper.getServiceTemplateFile(key));
			/*serviceTemplateNameTreeItem.setExpanded(true);*/
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
					TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
					serviceTemplatePathTreeItem.setText(propertiesServiceTemplateDialog.getTemplateFile());
					serviceTemplateNameTreeItem.setExpanded(true);
					serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
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
					TreeItem templateName;
					TreeItem templateFile;
					if(selectItem.getParentItem() == null) {
						templateName = selectItem;
					} else {
						templateName = selectItem.getParentItem();
					}

					templateFile = selectItem.getItems()[0];

					propertiesServiceTemplateDialog.setTemplateName(templateName.getText());
					propertiesServiceTemplateDialog.setTemplateFile(templateFile.getText());

					if(propertiesServiceTemplateDialog.open() == Window.OK) {
						templateFile.setText(propertiesServiceTemplateDialog.getTemplateFile());
						serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
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
					serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
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

		createDaoFolderButton = new Button(daoComposite, SWT.CHECK);
		createDaoFolderButton.setText("Create dao folder");
		createDaoFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createDaoFolderButton.setEnabled(false);
		createDaoFolderButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledAddPrefixDaoFolder();
						setEnabledCreateDaoSubFolder();
					} else {
						setDisabledAddPrefixDaoFolder();
						setDisabledCreateDaoSubFolder();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createDaoFolderButton.setSelection(isCreateDaoFolder());

		addPrefixDaoFolderButton = new Button(daoComposite, SWT.CHECK);
		addPrefixDaoFolderButton.setText("Add prefix dao folder name");
		addPrefixDaoFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateDaoFolder()) addPrefixDaoFolderButton.setEnabled(false);
		addPrefixDaoFolderButton.setSelection(isAddPrefixDaoFolder());

		createDaoSubFolderButton = new Button(daoComposite, SWT.CHECK);
		createDaoSubFolderButton.setText("Create dao sub folder");
		createDaoSubFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateDaoFolder()) createDaoSubFolderButton.setEnabled(false);
		createDaoSubFolderButton.setSelection(isCreateDaoSubFolder());

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
			daoTemplatePathTreeItem.setText(propertiesHelper.getDaoTemplateFile(key));
			/*daoTemplateNameTreeItem.setExpanded(true);*/
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
					TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
					daoTemplatePathTreeItem.setText(propertiesDaoTemplateDialog.getTemplateFile());
					daoTemplateNameTreeItem.setExpanded(true);
					daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
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
					TreeItem templateName;
					TreeItem templateFile;
					if(selectItem.getParentItem() == null) {
						templateName = selectItem;
					} else {
						templateName = selectItem.getParentItem();
					}

					templateFile = selectItem.getItems()[0];

					propertiesDaoTemplateDialog.setTemplateName(templateName.getText());
					propertiesDaoTemplateDialog.setTemplateFile(templateFile.getText());

					if(propertiesDaoTemplateDialog.open() == Window.OK) {
						templateFile.setText(propertiesDaoTemplateDialog.getTemplateFile());
						daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
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
					daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
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
		createMapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createMapper.setEnabled(false);
		createMapper.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledMapperPath();
					} else {
						setDisabledMapperPath();
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
		mapperPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		mapperPathText=new Text(mapperComposite, SWT.SINGLE | SWT.BORDER);
		mapperPathText.setLayoutData(defaultLayoutData);
		if(!isSpecificSettings || !isCreateMapper()) mapperPathText.setEnabled(false);
		mapperPathText.setText(getMapperPath());

		mapperPathButton = new Button(mapperComposite, SWT.PUSH);
		mapperPathButton.setText("Browse...");
		mapperPathButton.setLayoutData(new GridData(100, 24));
		mapperPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), project.getParent(), false, "Mapper Path Selection:");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) mapperPathText.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateMapper()) mapperPathButton.setEnabled(false);
		/* E : select mapper path */

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
			mapperTemplatePathTreeItem.setText(propertiesHelper.getMapperTemplateFile(key));
			/*mapperTemplateNameTreeItem.setExpanded(true);*/
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
					TreeItem mapperTemplatePathTreeItem = new TreeItem(mapperTemplateNameTreeItem, SWT.NULL);
					mapperTemplatePathTreeItem.setText(propertiesMapperTemplateDialog.getTemplateFile());
					mapperTemplateNameTreeItem.setExpanded(true);
					mapperTemplateNames = propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
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
					TreeItem templateName;
					TreeItem templateFile;
					if(selectItem.getParentItem() == null) {
						templateName = selectItem;
					} else {
						templateName = selectItem.getParentItem();
					}

					templateFile = selectItem.getItems()[0];

					propertiesMapperTemplateDialog.setTemplateName(templateName.getText());
					propertiesMapperTemplateDialog.setTemplateFile(templateFile.getText());

					if(propertiesMapperTemplateDialog.open() == Window.OK) {
						templateFile.setText(propertiesMapperTemplateDialog.getTemplateFile());
						mapperTemplateNames = propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
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
					mapperTemplateNames = propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
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
	    voTab.setText("Vo");

	    Composite voComposite = new Composite(tabFolder, SWT.NULL);
	    voComposite.setLayout(new GridLayout(3, false));

		createVoButton = new Button(voComposite, SWT.CHECK);
		createVoButton.setText("Create Vo");
		createVoButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		createVoButton.setSelection(isCreateVo());
		if(!isSpecificSettings) createVoButton.setEnabled(false);
		createVoButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledCreateSearchVo();
						setEnabledCreateVoFolder();
						if(isCreateVoFolder()) {
							setEnabledVoFolder();
						} else {
							setEnabledVoPath();
						}
						setEnabledVoSuperclass();
						setEnabledMyBatisSettingsFile();
					} else {
						setDisabledCreateSearchVo();
						setDisabledCreateVoFolder();
						setDisabledVoFolder();
						setDisabledVoPath();
						setDisabledVoSuperclass();
						setDisabledMyBatisSettingsFile();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);

		createSearchVoButton = new Button(voComposite, SWT.CHECK);
		createSearchVoButton.setText("Create Search Vo");
		createSearchVoButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		createSearchVoButton.setSelection(isCreateSearchVo());
		if(!isSpecificSettings || !isCreateVo()) createSearchVoButton.setEnabled(false);

		createVoFolderButton = new Button(voComposite, SWT.CHECK);
		createVoFolderButton.setText("Create vo folder");
		createVoFolderButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		createVoFolderButton.setSelection(isCreateVoFolder());
		if(!isSpecificSettings || !isCreateVo()) createVoFolderButton.setEnabled(false);
		createVoFolderButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledVoFolder();
						setDisabledVoPath();
					} else {
						setDisabledVoFolder();
						setEnabledVoPath();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);

		Label voFolderLabel = new Label(voComposite, SWT.NONE);
		voFolderLabel.setText("Vo Folder Name:");
		voFolderLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		voFolderText = new Text(voComposite, SWT.SINGLE | SWT.BORDER);
		voFolderText.setLayoutData(defaultLayoutData);
		voFolderText.setText(getVoFolder());
		if(!isSpecificSettings || !isCreateVo() || !isCreateVoFolder()) voFolderText.setEnabled(false);
		voFolderText.setBackground(new Color(device, 255, 255, 255));

		new Label(voComposite, SWT.NONE);

		/* S : select vo path */
		Label voPathLabel = new Label(voComposite, SWT.NONE);
		voPathLabel.setText("Vo Path:");
		voPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		voPathText = new Text(voComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		voPathText.setLayoutData(defaultLayoutData);
		voPathText.setText(getVoPath());
		if(!isSpecificSettings || !isCreateVo() || isCreateVoFolder()) voPathText.setEnabled(false);
		voPathText.setBackground(new Color(device, 255, 255, 255));

		voPathButton = new Button(voComposite, SWT.PUSH);
		voPathButton.setText("Browse...");
		voPathButton.setLayoutData(new GridData(100, 24));

		voPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), project.getParent(), false, "Vo Path Selection:");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) voPathText.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateVo() || isCreateVoFolder()) voPathButton.setEnabled(false);
		/* E : select vo path */

		Label voSuperclassLabel = new Label(voComposite, SWT.NONE);
		voSuperclassLabel.setText("Superclass:");
		voSuperclassLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		voSuperclassText = new Text(voComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		voSuperclassText.setLayoutData(defaultLayoutData);
		voSuperclassText.setText(getVoSuperclass());
		if(!isSpecificSettings || !isCreateVo()) voSuperclassText.setEnabled(false);
		voSuperclassText.setBackground(new Color(device, 255, 255, 255));

		voSuperclassButton = new Button(voComposite, SWT.PUSH);
		voSuperclassButton.setText("Browse...");
		voSuperclassButton.setLayoutData(new GridData(100, 24));

		voSuperclassButton.addSelectionListener(new SelectionListener() {
		    @SuppressWarnings("restriction")
			@Override public void widgetSelected(SelectionEvent e) {
				try {
					SelectionDialog dialog = JavaUI.createTypeDialog(getShell(),
					          new ProgressMonitorDialog(getShell()),
					          project,
					          IJavaElementSearchConstants.CONSIDER_CLASSES,
					          false);
					  dialog.setTitle("Superclass Selection");
				      dialog.open();
				      Object[] result = dialog.getResult();
				      if (result != null && result.length == 1) voSuperclassText.setText(((SourceType) result[0]).getFullyQualifiedName());
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}
		    }
		    @Override public void widgetDefaultSelected(SelectionEvent e) {
		      widgetSelected(e);
		    }
		  }
		);
		if(!isSpecificSettings || !isCreateVo()) voSuperclassButton.setEnabled(false);

		/* S : select vo path */
		Label myBatisSettingsFileLabel = new Label(voComposite, SWT.NONE);
		myBatisSettingsFileLabel.setText("MyBatis Setting File:");
		myBatisSettingsFileLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		myBatisSettingsFileText=new Text(voComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		myBatisSettingsFileText.setLayoutData(defaultLayoutData);
		myBatisSettingsFileText.setText(getMyBatisSettingFile());
		if(!isSpecificSettings || !isCreateVo()) myBatisSettingsFileText.setEnabled(false);
		myBatisSettingsFileText.setBackground(new Color(device, 255, 255, 255));

		myBatisSettingsFileButton = new Button(voComposite, SWT.PUSH);
		myBatisSettingsFileButton.setText("Browse...");
		myBatisSettingsFileButton.setLayoutData(new GridData(100, 24));
		myBatisSettingsFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					dialog.setFilterExtensions(new String[]{"*.xml"});
					String fileName = dialog.open();
					if (fileName != null) {
						myBatisSettingsFileText.setText(fileName);
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateVo()) myBatisSettingsFileButton.setEnabled(false);
		/* E : select vo path */

		dataTypeMappingTable = new Table(voComposite, SWT.BORDER);
		dataTypeMappingTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 0));
		TableColumn dataTypeColumn = new TableColumn(dataTypeMappingTable, SWT.NONE);
		dataTypeColumn.setText("Data Type");
		dataTypeColumn.setWidth(250);
		TableColumn javaObjectColumn = new TableColumn(dataTypeMappingTable, SWT.NONE);
		javaObjectColumn.setText("Java Object");
		javaObjectColumn.setWidth(250);
		dataTypeMappingTable.setHeaderVisible(true);

	    Composite dataTypeButtonComposite = new Composite(voComposite, SWT.NONE);
		GridLayout dataTypeButtonLayout = new GridLayout(1, false);
		dataTypeButtonLayout.marginHeight = 0;
		dataTypeButtonLayout.marginWidth = 0;
		dataTypeButtonComposite.setLayout(dataTypeButtonLayout);
		dataTypeButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		dataTypes = propertiesHelper.getDataTypes();
		for(String key : dataTypes) {
			TableItem tableItem = new TableItem(dataTypeMappingTable, SWT.NULL);
			tableItem.setText(0, key);
			tableItem.setText(1, propertiesHelper.getJavaObject(key));
		}

		Button addDataTypeTemplateButton = new Button(dataTypeButtonComposite, SWT.NONE);
		addDataTypeTemplateButton.setText("Add...");
		addDataTypeTemplateButton.setLayoutData(new GridData(100, 24));
		addDataTypeTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesDataTypeMappingDialog propertiesDataTypeMappingDialog = new PropertiesDataTypeMappingDialog(getShell());
				propertiesDataTypeMappingDialog.setDataTypes(dataTypes);
				if(propertiesDataTypeMappingDialog.open() == Window.OK) {
					TableItem tableItem = new TableItem(dataTypeMappingTable, SWT.NULL);
					tableItem.setText(0, propertiesDataTypeMappingDialog.getDataType());
					tableItem.setText(1, propertiesDataTypeMappingDialog.getJavaObject());
					dataTypes = propertiesHelper.dataTypeFlush(dataTypeMappingTable);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editDataTypeTemplateButton = new Button(dataTypeButtonComposite, SWT.NONE);
		editDataTypeTemplateButton.setText("Edit...");
		editDataTypeTemplateButton.setLayoutData(new GridData(100, 24));
		editDataTypeTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dataTypeMappingTable.getSelection().length > 0) {
					PropertiesDataTypeMappingDialog propertiesDataTypeMappingDialog = new PropertiesDataTypeMappingDialog(getShell());
					propertiesDataTypeMappingDialog.setDataTypes(dataTypes);

					TableItem tableItem = dataTypeMappingTable.getItem(dataTypeMappingTable.getSelectionIndex());
					propertiesDataTypeMappingDialog.setDataType(tableItem.getText(0));
					propertiesDataTypeMappingDialog.setJavaObject(tableItem.getText(1));

					if(propertiesDataTypeMappingDialog.open() == Window.OK) {
						tableItem.setText(1, propertiesDataTypeMappingDialog.getJavaObject());
						dataTypes = propertiesHelper.dataTypeFlush(dataTypeMappingTable);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeDataTypeTemplateButton = new Button(dataTypeButtonComposite, SWT.NONE);
		removeDataTypeTemplateButton.setText("Remove");
		removeDataTypeTemplateButton.setLayoutData(new GridData(100, 24));
		removeDataTypeTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(dataTypeMappingTable.getSelection().length > 0) {
					TableItem tableItem = dataTypeMappingTable.getItem(dataTypeMappingTable.getSelectionIndex());
					tableItem.dispose();
					dataTypes = propertiesHelper.dataTypeFlush(dataTypeMappingTable);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    voTab.setControl(voComposite);

/* E : VO Tab Folder */

/* S : JSP Tab Folder */
	    TabItem jspTab = new TabItem(tabFolder, SWT.NONE);
	    jspTab.setText("JSP");

	    Composite jspComposite = new Composite(tabFolder, SWT.NULL);
	    jspComposite.setLayout(new GridLayout(3, false));

		createJspButton = new Button(jspComposite, SWT.CHECK);
		createJspButton.setText("Create Jsp");
		createJspButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createJspButton.setEnabled(false);
		createJspButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledJspPath();
					} else {
						setDisabledJspPath();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createJspButton.setSelection(isCreateJsp());

		/* S : select jsp path */
		Label jspPathLabel = new Label(jspComposite, SWT.NONE);
		jspPathLabel.setText("Jsp Path:");
		jspPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		jspPathName=new Text(jspComposite, SWT.SINGLE | SWT.BORDER);
		jspPathName.setLayoutData(defaultLayoutData);
		if(!isSpecificSettings || !isCreateJsp()) jspPathName.setEnabled(false);
		jspPathName.setText(getJspPath());

		jspPathButton = new Button(jspComposite, SWT.PUSH);
		jspPathButton.setText("Browse...");
		jspPathButton.setLayoutData(new GridData(100, 24));
		jspPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), project.getParent(), false, "Jsp Path Selection:");
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


		Label jspTemplateLabel = new Label(jspComposite, SWT.NONE);
		jspTemplateLabel.setText("Jsp Template:");
		jspTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));

	    jspTemplateTree = new Tree(jspComposite, SWT.SINGLE | SWT.BORDER);
	    jspTemplateTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite jspButtonComposite = new Composite(jspComposite, SWT.NONE);
		GridLayout jspButtonLayout = new GridLayout(1, false);
		jspButtonLayout.marginHeight = 0;
		jspButtonLayout.marginWidth = 0;
		jspButtonComposite.setLayout(jspButtonLayout);
		jspButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		jspTemplateNames = propertiesHelper.getJspTemplateNames();
		for(String key : jspTemplateNames) {
			TreeItem jspTemplateNameTreeItem = new TreeItem(jspTemplateTree, SWT.NULL);
			jspTemplateNameTreeItem.setText(key);
			TreeItem jspTemplateListTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
			jspTemplateListTreeItem.setText("List: " + propertiesHelper.getJspTemplateListFile(key));
			jspTemplateListTreeItem.setData(propertiesHelper.getJspTemplateListFile(key));
			TreeItem jspTemplatePostTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
			jspTemplatePostTreeItem.setText("Post: " + propertiesHelper.getJspTemplatePostFile(key));
			jspTemplatePostTreeItem.setData(propertiesHelper.getJspTemplatePostFile(key));
			TreeItem jspTemplateViewTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
			jspTemplateViewTreeItem.setText("View: " + propertiesHelper.getJspTemplateViewFile(key));
			jspTemplateViewTreeItem.setData(propertiesHelper.getJspTemplateViewFile(key));
		}

		Button addJspTemplateButton = new Button(jspButtonComposite, SWT.NONE);
		addJspTemplateButton.setText("Add Template...");
		addJspTemplateButton.setLayoutData(new GridData(100, 24));
		addJspTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesJspTemplateDialog propertiesJspTemplateDialog = new PropertiesJspTemplateDialog(getShell());
				propertiesJspTemplateDialog.setTemplateNames(jspTemplateNames);

				if(propertiesJspTemplateDialog.open() == Window.OK) {
					TreeItem jspTemplateNameTreeItem = new TreeItem(jspTemplateTree, SWT.NULL);
					jspTemplateNameTreeItem.setText(propertiesJspTemplateDialog.getTemplateName());
					TreeItem jspTemplateListTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
					jspTemplateListTreeItem.setText("List: " + propertiesJspTemplateDialog.getTemplateListFile());
					jspTemplateListTreeItem.setData(propertiesJspTemplateDialog.getTemplateListFile());
					TreeItem jspTemplatePostTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
					jspTemplatePostTreeItem.setText("Post: " + propertiesJspTemplateDialog.getTemplatePostFile());
					jspTemplatePostTreeItem.setData(propertiesJspTemplateDialog.getTemplatePostFile());
					TreeItem jspTemplateViewTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
					jspTemplateViewTreeItem.setText("View: " + propertiesJspTemplateDialog.getTemplateViewFile());
					jspTemplateViewTreeItem.setData(propertiesJspTemplateDialog.getTemplateViewFile());
					jspTemplateNames = propertiesHelper.jspPropertiesFlush(jspTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button editJspTemplateButton = new Button(jspButtonComposite, SWT.NONE);
		editJspTemplateButton.setText("Edit...");
		editJspTemplateButton.setLayoutData(new GridData(100, 24));
		editJspTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(jspTemplateTree.getSelection().length > 0) {
					PropertiesJspTemplateDialog propertiesJspTemplateDialog = new PropertiesJspTemplateDialog(getShell());
					propertiesJspTemplateDialog.setTemplateNames(jspTemplateNames);

					TreeItem selectItem = jspTemplateTree.getSelection()[0];
					TreeItem templateName;
					TreeItem listTemplateName;
					TreeItem regTemplateName;
					TreeItem viewTemplateName;
					if(selectItem.getParentItem() == null) {
						templateName = selectItem;
					} else {
						templateName = selectItem.getParentItem();
					}

					listTemplateName = templateName.getItems()[0];
					regTemplateName = templateName.getItems()[1];
					viewTemplateName = templateName.getItems()[2];

					propertiesJspTemplateDialog.setTemplateNames(jspTemplateNames);

					propertiesJspTemplateDialog.setTemplateName((String) templateName.getText());
					propertiesJspTemplateDialog.setTemplateListFile((String) listTemplateName.getData());
					propertiesJspTemplateDialog.setTemplatePostFile((String) regTemplateName.getData());
					propertiesJspTemplateDialog.setTemplateViewFile((String) viewTemplateName.getData());

					if(propertiesJspTemplateDialog.open() == Window.OK) {
						templateName.getItems()[0].setText("List: " + propertiesJspTemplateDialog.getTemplateListFile());
						templateName.getItems()[0].setData(propertiesJspTemplateDialog.getTemplateListFile());
						templateName.getItems()[1].setText("Post: " + propertiesJspTemplateDialog.getTemplatePostFile());
						templateName.getItems()[1].setData(propertiesJspTemplateDialog.getTemplatePostFile());
						templateName.getItems()[2].setText("View: " + propertiesJspTemplateDialog.getTemplateViewFile());
						templateName.getItems()[2].setData(propertiesJspTemplateDialog.getTemplateViewFile());
						jspTemplateNames = propertiesHelper.jspPropertiesFlush(jspTemplateTree);
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button removeJspTemplateButton = new Button(jspButtonComposite, SWT.NONE);
		removeJspTemplateButton.setText("Remove");
		removeJspTemplateButton.setLayoutData(new GridData(100, 24));
		removeJspTemplateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(jspTemplateTree.getSelection().length > 0) {
					TreeItem treeItem = jspTemplateTree.getSelection()[0];
					TreeItem parentItem = treeItem.getParentItem();
					treeItem.dispose();
					if(parentItem != null) {
						parentItem.dispose();
					}
					jspTemplateNames = propertiesHelper.jspPropertiesFlush(jspTemplateTree);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	    jspTab.setControl(jspComposite);

/* E : JSP Tab Folder */

	    noDefaultAndApplyButton();

		return panel;
	}

	public boolean performOk() {

		/*setSpecificSettings(specificSettings.getSelection());*/
/*
		if(typeAButton.getSelection()) {
			setType("A");
		} else {
			setType("B");
		}
*/
		setCompany(companyText.getText());
		setAuthor(authorText.getText());

		setCreateTest(createTestButton.getSelection());
		setCreateTestControllerFolder(createTestControllerFolderButton.getSelection());
		setCreateTestServiceFolder(createTestServiceFolderButton.getSelection());
		setCreateTestDaoFolder(createTestDaoFolderButton.getSelection());
		setTestPath(testPathText.getText());

		setCreateControllerFolder(createControllerFolderButton.getSelection());
		setAddPrefixControllerFolder(addPrefixControllerFolderButton.getSelection());
		setCreateControllerSubFolder(createControllerSubFolderButton.getSelection());
		/*setControllerTemplateFile(controllerTemplateFileName.getText());*/

		setCreateServiceFolder(createServiceFolderButton.getSelection());
		setAddPrefixServiceFolder(addPrefixServiceFolderButton.getSelection());
		setCreateServiceSubFolder(createServiceSubFolderButton.getSelection());

		setCreateServiceImpl(createServiceImplButton.getSelection());
		setCreateServiceImplFolder(createServiceImplFolderButton.getSelection());

		setCreateDaoFolder(createDaoFolderButton.getSelection());
		setAddPrefixDaoFolder(addPrefixDaoFolderButton.getSelection());
		setCreateDaoSubFolder(createDaoSubFolderButton.getSelection());

		setCreateMapper(createMapper.getSelection());
		setMapperPath(mapperPathText.getText());

		setCreateVo(createVoButton.getSelection());
		setCreateSearchVo(createSearchVoButton.getSelection());
		setCreateVoFolder(createVoFolderButton.getSelection());
		setVoFolder(voFolderText.getText());
		setVoPath(voPathText.getText());
		setVoSuperclass(voSuperclassText.getText());
		setMyBatisSettingFile(myBatisSettingsFileText.getText());

		setCreateJsp(createJspButton.getSelection());
		setJspPath(jspPathName.getText());

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

	public String getType() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.TYPE);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "A";
	}

	public void setType(String type) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(type)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.TYPE, type);
	    	}
	    } catch (CoreException e) {
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

	public void setAuthor(String author) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(author)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.AUTHOR, author);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public String getDatabaseConnectionProfileName() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.DATABASE_CONNECTION_PROFILE_NAME);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setDatabaseConnectionProfileName(String databaseConnectionProfileName) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(databaseConnectionProfileName)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.DATABASE_CONNECTION_PROFILE_NAME, databaseConnectionProfileName);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public boolean isCreateTest() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateTest(boolean createTest) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createTest) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST, "false");
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String getTestPath() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.TEST_PATH);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setTestPath(String company) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(!"".equals(company)) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.TEST_PATH, company);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isCreateTestControllerFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_CONTROLLER_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateTestControllerFolder(boolean createTestControllerFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createTestControllerFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_CONTROLLER_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_CONTROLLER_FOLDER, "false");
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isCreateTestServiceFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_SERVICE_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateTestServiceFolder(boolean createTestServiceFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createTestServiceFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_SERVICE_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_SERVICE_FOLDER, "false");
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isCreateTestDaoFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_DAO_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateTestDaoFolder(boolean createTestDaoFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createTestDaoFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_DAO_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_TEST_DAO_FOLDER, "false");
			}
		}
		catch (CoreException e) {
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


	public boolean isCreateControllerSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateControllerSubFolder(boolean createControllerSubFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createControllerSubFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER, "false");
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

	public boolean isCreateServiceSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateServiceSubFolder(boolean createServiceSubFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createServiceSubFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER, "false");
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
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER);
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

	public boolean isCreateDaoSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateDaoSubFolder(boolean createDaoSubFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createDaoSubFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER, "false");
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

	public boolean isCreateVo() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateVo(boolean createVo) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createVo) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateSearchVo() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateSearchVo(boolean createVo) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createVo) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateVoFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateVoFolder(boolean createVo) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createVo) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO_FOLDER, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getVoFolder() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.VO_FOLDER);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setVoFolder(String voFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(voFolder)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.VO_FOLDER, voFolder);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getVoPath() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.VO_PATH);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setVoPath(String voPath) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(voPath)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.VO_PATH, voPath);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getVoSuperclass() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.VO_SUPERCLASS);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setVoSuperclass(String voSuperclass) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(voSuperclass)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.VO_SUPERCLASS, voSuperclass);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getMyBatisSettingFile() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MYBATIS_SETTING_FILE);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setMyBatisSettingFile(String myBatisSettingFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(myBatisSettingFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.MYBATIS_SETTING_FILE, myBatisSettingFile);
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

	private void setEnabledCreateTestControllerFolder() {
	    createTestControllerFolderButton.setEnabled(true);
	}

	private void setDisabledCreateTestControllerFolder() {
	  createTestControllerFolderButton.setEnabled(false);
	}

	private void setEnabledCreateTestServiceFolder() {
	    createTestServiceFolderButton.setEnabled(true);
	}

	private void setDisabledCreateTestServiceFolder() {
	  createTestServiceFolderButton.setEnabled(false);
	}

	private void setEnabledCreateTestDaoFolder() {
	    createTestDaoFolderButton.setEnabled(true);
	}

	private void setDisabledCreateTestDaoFolder() {
	  createTestDaoFolderButton.setEnabled(false);
	}

	private void setEnabledTestPath() {
		testPathText.setEnabled(true);
		testPathButton.setEnabled(true);
	}

	private void setDisabledTestPath() {
		testPathText.setEnabled(false);
		testPathButton.setEnabled(false);
	}

	private void setEnabledAddPrefixControllerFolder() {
		addPrefixControllerFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixControllerFolder() {
		addPrefixControllerFolderButton.setEnabled(false);
	}

	private void setEnabledCreateControllerSubFolder() {
		createControllerSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateControllerSubFolder() {
		createControllerSubFolderButton.setEnabled(false);
	}

	private void setEnabledAddPrefixServiceFolder() {
		addPrefixServiceFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixServiceFolder() {
		addPrefixServiceFolderButton.setEnabled(false);
	}

	private void setEnabledCreateServiceSubFolder() {
		createServiceSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateServiceSubFolder() {
		createServiceSubFolderButton.setEnabled(false);
	}

	private void setEnabledAddPrefixDaoFolder() {
		addPrefixDaoFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixDaoFolder() {
		addPrefixDaoFolderButton.setEnabled(false);
	}

	private void setEnabledCreateDaoSubFolder() {
		createDaoSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateDaoSubFolder() {
		createDaoSubFolderButton.setEnabled(false);
	}

	private void setEnabledServiceImplFolder() {
		createServiceImplFolderButton.setEnabled(true);
	}

	private void setDisabledServiceImplFolder() {
		createServiceImplFolderButton.setEnabled(false);
	}

	private void setEnabledMapperPath() {
		mapperPathText.setEnabled(true);
		mapperPathButton.setEnabled(true);
	}

	private void setDisabledMapperPath() {
		mapperPathText.setEnabled(false);
		mapperPathButton.setEnabled(false);
	}

	private void setEnabledCreateSearchVo() {
		createSearchVoButton.setEnabled(true);
	}

	private void setDisabledCreateSearchVo() {
		createSearchVoButton.setEnabled(false);
	}

	private void setEnabledCreateVoFolder() {
		createVoFolderButton.setEnabled(true);
	}

	private void setDisabledCreateVoFolder() {
		createVoFolderButton.setEnabled(false);
	}

	private void setEnabledMyBatisSettingsFile() {
		myBatisSettingsFileText.setEnabled(true);
		myBatisSettingsFileButton.setEnabled(true);
	}

	private void setDisabledMyBatisSettingsFile() {
		myBatisSettingsFileText.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);
	}

	private void setEnabledVoFolder() {
	  voFolderText.setEnabled(true);
	}

	private void setDisabledVoFolder() {
	  voFolderText.setEnabled(false);
	}

	private void setEnabledVoPath() {
		voPathText.setEnabled(true);
		voPathButton.setEnabled(true);
	}

	private void setDisabledVoPath() {
		voPathText.setEnabled(false);
		voPathButton.setEnabled(false);
	}

	private void setEnabledJspPath() {
		jspPathName.setEnabled(true);
		jspPathButton.setEnabled(true);
	}

	private void setDisabledJspPath() {
		jspPathName.setEnabled(false);
		jspPathButton.setEnabled(false);
	}

	private void setEnabledVoSuperclass() {
		voSuperclassText.setEnabled(true);
		voSuperclassButton.setEnabled(true);
	}

	private void setDisabledVoSuperclass() {
		voSuperclassText.setEnabled(false);
		voSuperclassButton.setEnabled(false);
	}

	private void setEnabledSettings() {

		companyText.setEnabled(true);
		authorText.setEnabled(true);

		createControllerFolderButton.setEnabled(true);
		if(createControllerFolderButton.getSelection()) addPrefixControllerFolderButton.setEnabled(true);
		/*controllerTemplateFileName.setEnabled(true);*/
		controllerTemplateFileButton.setEnabled(true);

		createServiceFolderButton.setEnabled(true);
		if(createServiceFolderButton.getSelection()) addPrefixServiceFolderButton.setEnabled(true);
		serviceTemplateFileText.setEnabled(true);
		serviceTemplateFileButton.setEnabled(true);

		createServiceImplButton.setEnabled(true);
		if(createServiceImplButton.getSelection()) {
			createServiceImplFolderButton.setEnabled(true);
		}

		createDaoFolderButton.setEnabled(true);
		if(createDaoFolderButton.getSelection()) addPrefixDaoFolderButton.setEnabled(true);
		daoTemplateFileText.setEnabled(true);
		daoTemplateFileButton.setEnabled(true);

		createMapper.setEnabled(true);
		if(createMapper.getSelection()) {
			mapperPathButton.setEnabled(true);
			mapperPathText.setEnabled(true);
		}

		createVoButton.setEnabled(true);
		if(createVoButton.getSelection()) {
			voPathButton.setEnabled(true);
			voPathText.setEnabled(true);
			myBatisSettingsFileText.setEnabled(true);
			myBatisSettingsFileButton.setEnabled(true);
		}

		createJspButton.setEnabled(true);
		if(createJspButton.getSelection()) {
			jspPathButton.setEnabled(true);
			jspPathName.setEnabled(true);
		}
	}

	private void setDisabledSettings() {

		companyText.setEnabled(false);
		authorText.setEnabled(false);

		addPrefixControllerFolderButton.setEnabled(false);
		/*controllerTemplateFileName.setEnabled(false);*/
		controllerTemplateFileButton.setEnabled(false);

		addPrefixServiceFolderButton.setEnabled(false);
		serviceTemplateFileText.setEnabled(false);
		serviceTemplateFileButton.setEnabled(false);

		addPrefixDaoFolderButton.setEnabled(false);
		daoTemplateFileText.setEnabled(false);
		daoTemplateFileButton.setEnabled(false);

		createControllerFolderButton.setEnabled(false);

		createServiceFolderButton.setEnabled(false);
		createServiceImplButton.setEnabled(false);
		createServiceImplFolderButton.setEnabled(false);

		createDaoFolderButton.setEnabled(false);

		createMapper.setEnabled(false);
		mapperPathButton.setEnabled(false);
		mapperPathText.setEnabled(false);

		createVoButton.setEnabled(false);
		voPathButton.setEnabled(false);
		voPathText.setEnabled(false);
		myBatisSettingsFileText.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);

		createJspButton.setEnabled(false);
		jspPathButton.setEnabled(false);
		jspPathName.setEnabled(false);
	}

}
