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
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.dialogs.PropertiesControllerTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesDaoTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesDataTypeMappingDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesGeneralTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesJspTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesMapperTemplateDialog;
import kr.pe.maun.csdgenerator.dialogs.PropertiesServiceTemplateDialog;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

public class CSDGeneratorResourcePerpertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private CSDGeneratorPropertiesHelper propertiesHelper;

	IProject project;

	String[] generalTemplateGroupNames;
	String[] controllerTemplateNames;
	String[] serviceTemplateNames;
	String[] daoTemplateNames;
	String[] mapperTemplateNames;
	String[] jspTemplateNames;
	String[] dataTypes;

	Tree generalTemplateTree;
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

	private Button createVo;
	private Button createSearchVo;

	private Text voPathName;
	private Button voPathButton;
	private Text myBatisSettingsFileName;
	private Button myBatisSettingsFileButton;

	Table dataTypeMappingTable;

	private Button createJsp;

	private Text jspPathName;
	private Button jspPathButton;

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
	    separator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
*/
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
	    generalComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Composite typeComposite = new Composite(generalComposite, SWT.NULL);
	    typeComposite.setLayout(new GridLayout(4, false));
	    typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0));

	    /* S : Type A */

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
		/* S : Type B */

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
		companyText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 0));
		if(!isSpecificSettings) companyText.setEnabled(false);
		companyText.setText(getCompany());

		Label authorLabel = new Label(generalComposite, SWT.NONE);
		authorLabel.setText("Author:");
		authorLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		authorText = new Text(generalComposite, SWT.FILL | SWT.BORDER);
		authorText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 0));
		if(!isSpecificSettings) authorText.setEnabled(false);
		authorText.setText(getAuthor());

		Label databaseConnectionLabel = new Label(generalComposite, SWT.NONE);
		databaseConnectionLabel.setText("Database Connection:");
		databaseConnectionLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		databaseResource = new DatabaseResource();

		connectionProfiles = ProfileManager.getInstance().getProfiles();
		connectionCombo = new Combo(generalComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		connectionCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 0));
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
						String jspTemplateRegFile = "";
						String jspTemplateViewFile = "";

						try {
							IFolder templateFolder = project.getParent().getFolder(templatePath);
							IResource[] members = templateFolder.members();

							for(IResource member : members) {
								File templateFile = member.getLocation().toFile();
								if(templateFile.exists()) {
									String name = member.getName();
									if(name.indexOf("CT_") > -1) {
										controllerTemplateName = templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("CT_", "");
										TreeItem controllerTemplateNameTreeItem = new TreeItem(controllerTemplateTree, SWT.NULL);
										controllerTemplateNameTreeItem.setText(controllerTemplateName);
										TreeItem controllerTemplatePathTreeItem = new TreeItem(controllerTemplateNameTreeItem, SWT.NULL);
										controllerTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										controllerTemplateNameTreeItem.setExpanded(true);
										controllerTemplateNames = propertiesHelper.controllerPropertiesFlush(controllerTemplateTree);
									} else if(name.indexOf("ST_") > -1) {
										serviceTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("ST_", "");
										TreeItem serviceTemplateNameTreeItem = new TreeItem(serviceTemplateTree, SWT.NULL);
										serviceTemplateNameTreeItem.setText(serviceTemplateName);
										TreeItem serviceTemplatePathTreeItem = new TreeItem(serviceTemplateNameTreeItem, SWT.NULL);
										serviceTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										serviceTemplateNameTreeItem.setExpanded(true);
										serviceTemplateNames = propertiesHelper.servicePropertiesFlush(serviceTemplateTree);
									} else if(name.indexOf("DT_") > -1) {
										daoTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("DT_", "");
										TreeItem daoTemplateNameTreeItem = new TreeItem(daoTemplateTree, SWT.NULL);
										daoTemplateNameTreeItem.setText(daoTemplateName);
										TreeItem daoTemplatePathTreeItem = new TreeItem(daoTemplateNameTreeItem, SWT.NULL);
										daoTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										daoTemplateNameTreeItem.setExpanded(true);
										daoTemplateNames = propertiesHelper.daoPropertiesFlush(daoTemplateTree);
									} else if(name.indexOf("MT_") > -1) {
										mapperTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("MT_", "");
										TreeItem mapperTemplateNameTreeItem = new TreeItem(mapperTemplateTree, SWT.NULL);
										mapperTemplateNameTreeItem.setText(mapperTemplateName);
										TreeItem mapperTemplatePathTreeItem = new TreeItem(mapperTemplateNameTreeItem, SWT.NULL);
										mapperTemplatePathTreeItem.setText(templateFile.getAbsolutePath());
										mapperTemplateNameTreeItem.setExpanded(true);
										mapperTemplateNames = propertiesHelper.mapperPropertiesFlush(mapperTemplateTree);
									} else if(name.indexOf("JT") > -1) {
										if(name.indexOf("JTL_") > -1) {
											jspTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("JTL_", "");
											jspTemplateListFile = templateFile.getAbsolutePath();
										} else if(name.indexOf("JTR_") > -1) {
											if("".equals(jspTemplateName)) jspTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("JTR_", "");
											jspTemplateRegFile = templateFile.getAbsolutePath();
										} else if(name.indexOf("JTV_") > -1) {
											if("".equals(jspTemplateName)) jspTemplateName =  templateFolder.getName() + "_" + name.substring(0, name.indexOf(".")).replace("JTV_", "");
											jspTemplateViewFile = templateFile.getAbsolutePath();
										}
									}
								}
							}

							if(!"".equals(jspTemplateName)) {
								TreeItem jspTemplateNameTreeItem = new TreeItem(jspTemplateTree, SWT.NULL);
								jspTemplateNameTreeItem.setText(jspTemplateName);
								TreeItem jspTemplateListTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
								jspTemplateListTreeItem.setText("List: " + jspTemplateListFile);
								jspTemplateListTreeItem.setData(jspTemplateListFile);
								TreeItem jspTemplateRegTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
								jspTemplateRegTreeItem.setText("Reg: " + jspTemplateRegFile);
								jspTemplateRegTreeItem.setData(jspTemplateRegFile);
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
		createMapper.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
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

		mapperPathName=new Text(mapperComposite, SWT.SINGLE | SWT.BORDER);
		mapperPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) mapperPathName.setEnabled(false);
		mapperPathName.setText(getMapperPath());

		mapperPathButton = new Button(mapperComposite, SWT.PUSH);
		mapperPathButton.setText("Browse...");
		mapperPathButton.setLayoutData(new GridData(100, 24));
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
	    voTab.setText("VO");

	    Composite voComposite = new Composite(tabFolder, SWT.NULL);
	    voComposite.setLayout(new GridLayout(3, false));

		createVo = new Button(voComposite, SWT.CHECK);
		createVo.setText("Create VO");
		createVo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createVo.setEnabled(false);
		createVo.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if(button.getSelection()) {
						setEnabledCreateSearchVo();
						setEnabledVoPath();
						setEnabledMyBatisSettingsFile();
					} else {
						setDisabledCreateSearchVo();
						setDisabledVoPath();
						setDisabledMyBatisSettingsFile();
					}
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);

		createSearchVo = new Button(voComposite, SWT.CHECK);
		createSearchVo.setText("Create Search VO");
		createSearchVo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings || !isCreateVo()) createSearchVo.setEnabled(false);
		createSearchVo.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);

		/* S : select vo path */
		Label voPathLabel = new Label(voComposite, SWT.NONE);
		voPathLabel.setText("VO Path:");
		voPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		voPathName=new Text(voComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		voPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings || !isCreateVo()) voPathName.setEnabled(false);
		voPathName.setBackground(new Color(device, 255, 255, 255));

		voPathButton = new Button(voComposite, SWT.PUSH);
		voPathButton.setText("Browse...");
		voPathButton.setLayoutData(new GridData(100, 24));

		voPathButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), null, false, "");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) voPathName.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateVo()) voPathButton.setEnabled(false);
		/* E : select vo path */

		/* S : select vo path */
		Label myBatisSettingsFileLabel = new Label(voComposite, SWT.NONE);
		myBatisSettingsFileLabel.setText("MyBatis Settings File:");
		myBatisSettingsFileLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		myBatisSettingsFileName=new Text(voComposite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		myBatisSettingsFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings || !isCreateVo()) myBatisSettingsFileName.setEnabled(false);
		myBatisSettingsFileName.setBackground(new Color(device, 255, 255, 255));

		myBatisSettingsFileButton = new Button(voComposite, SWT.PUSH);
		myBatisSettingsFileButton.setText("Browse...");
		myBatisSettingsFileButton.setLayoutData(new GridData(100, 24));
		myBatisSettingsFileButton.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), null, false, "");
					dialog.open();
					Object[] result=dialog.getResult();
					if (result != null && result.length == 1) myBatisSettingsFileName.setText(((IPath) result[0]).toString());
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		if(!isSpecificSettings || !isCreateVo()) myBatisSettingsFileButton.setEnabled(false);
		/* E : select vo path */

		dataTypeMappingTable = new Table(voComposite, SWT.BORDER);
		dataTypeMappingTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 0));
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

		createJsp = new Button(jspComposite, SWT.CHECK);
		createJsp.setText("Create Jsp");
		createJsp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 0));
		if(!isSpecificSettings) createJsp.setEnabled(false);
		createJsp.addSelectionListener(new SelectionListener() {
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
		createJsp.setSelection(isCreateJsp());

		/* S : select jsp path */
		Label jspPathLabel = new Label(jspComposite, SWT.NONE);
		jspPathLabel.setText("Jsp Path:");
		jspPathLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));

		jspPathName=new Text(jspComposite, SWT.SINGLE | SWT.BORDER);
		jspPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings || !isCreateJsp()) jspPathName.setEnabled(false);
		jspPathName.setText(getJspPath());

		jspPathButton = new Button(jspComposite, SWT.PUSH);
		jspPathButton.setText("Browse...");
		jspPathButton.setLayoutData(new GridData(100, 24));
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
			TreeItem jspTemplateRegTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
			jspTemplateRegTreeItem.setText("Reg: " + propertiesHelper.getJspTemplateRegFile(key));
			jspTemplateRegTreeItem.setData(propertiesHelper.getJspTemplateRegFile(key));
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
					TreeItem jspTemplateRegTreeItem = new TreeItem(jspTemplateNameTreeItem, SWT.NULL);
					jspTemplateRegTreeItem.setText("Reg: " + propertiesJspTemplateDialog.getTemplateRegFile());
					jspTemplateRegTreeItem.setData(propertiesJspTemplateDialog.getTemplateRegFile());
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
					propertiesJspTemplateDialog.setTemplateRegFile((String) regTemplateName.getData());
					propertiesJspTemplateDialog.setTemplateViewFile((String) viewTemplateName.getData());

					if(propertiesJspTemplateDialog.open() == Window.OK) {
						templateName.getItems()[0].setText("List: " + propertiesJspTemplateDialog.getTemplateListFile());
						templateName.getItems()[0].setData(propertiesJspTemplateDialog.getTemplateListFile());
						templateName.getItems()[1].setText("Reg: " + propertiesJspTemplateDialog.getTemplateRegFile());
						templateName.getItems()[1].setData(propertiesJspTemplateDialog.getTemplateRegFile());
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

		if(typeAButton.getSelection()) {
			setType("A");
		} else {
			setType("B");
		}

		setCompany(companyText.getText());
		setAuthor(authorText.getText());

		setCreateControllerFolder(createControllerFolderButton.getSelection());
		setAddPrefixControllerFolder(addPrefixControllerFolderButton.getSelection());
		/*setControllerTemplateFile(controllerTemplateFileName.getText());*/

		setCreateServiceFolder(createServiceFolder.getSelection());
		setAddPrefixServiceFolder(addPrefixServiceFolder.getSelection());

		setCreateServiceImpl(createServiceImpl.getSelection());
		setCreateServiceImplFolder(createServiceImplFolder.getSelection());

		setCreateDaoFolder(createDaoFolder.getSelection());
		setAddPrefixDaoFolder(addPrefixDaoFolder.getSelection());

		setCreateMapper(createMapper.getSelection());
		setMapperPath(mapperPathName.getText());

		setCreateJsp(createJsp.getSelection());
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

	private void setEnabledCreateSearchVo() {
		createSearchVo.setEnabled(true);
	}

	private void setDisabledCreateSearchVo() {
		createSearchVo.setEnabled(false);
	}

	private void setEnabledMyBatisSettingsFile() {
		myBatisSettingsFileName.setEnabled(true);
		myBatisSettingsFileButton.setEnabled(true);
	}

	private void setDisabledMyBatisSettingsFile() {
		myBatisSettingsFileName.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);
	}

	private void setEnabledVoPath() {
		voPathName.setEnabled(true);
		voPathButton.setEnabled(true);
	}

	private void setDisabledVoPath() {
		voPathName.setEnabled(false);
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
		}

		createVo.setEnabled(true);
		if(createVo.getSelection()) {
			voPathButton.setEnabled(true);
			voPathName.setEnabled(true);
			myBatisSettingsFileName.setEnabled(true);
			myBatisSettingsFileButton.setEnabled(true);
		}

		createJsp.setEnabled(true);
		if(createJsp.getSelection()) {
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

		createVo.setEnabled(false);
		voPathButton.setEnabled(false);
		voPathName.setEnabled(false);
		myBatisSettingsFileName.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);

		createJsp.setEnabled(false);
		jspPathButton.setEnabled(false);
		jspPathName.setEnabled(false);
	}

}