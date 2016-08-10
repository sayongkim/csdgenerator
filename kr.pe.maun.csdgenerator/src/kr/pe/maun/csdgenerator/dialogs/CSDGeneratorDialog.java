package kr.pe.maun.csdgenerator.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;
import kr.pe.maun.csdgenerator.utils.StringUtils;

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
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

public class CSDGeneratorDialog extends Dialog {

	private CSDGeneratorPropertiesItem propertiesItem;
	private CSDGeneratorPropertiesHelper propertiesHelper;

	private String templateGroupName = "";
	private String testTemplateGroupName = "";

	private String prefix = "";

	private Text prefixField;
	private Text voSuperclassField;
/*
	private String regExGroup = "";
	private Text regExGroupField;
*/
	private boolean isParentLocation = false;
	private boolean isCreateFolder = true;

	private boolean isCreateTest = true;
	private boolean isCreateTestController = true;
	private boolean isCreateTestService = false;
	private boolean isCreateTestDao = false;
	private boolean isCreateController = true;
	private boolean isCreateService = true;
	private boolean isCreateDao = true;
	private boolean isCreateMapper = true;
	private boolean isCreateJsp = true;
	private boolean isCreateVo = true;
	private boolean isCreateSearchVo = true;
	private boolean isExtendVoSuperclass = false;
	private boolean isCreateVoFolder = false;

	private boolean existsJspTemplateListFile = true;
	private boolean existsJspTemplatePostFile = true;
	private boolean existsJspTemplateViewFile = true;

	String testPath;
	String mapperPath;
	String voFolder;
	String voPath;
	String voSuperclass;
	String jspPath;

	private IConnectionProfile connectionProfile;

	private ISelection selection;

	private Combo templateCombo;
	private Combo connectionProfileCombo;
	private Combo testTemplateCombo;
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
	private Button createTestControllerButton;
	private Button createTestServiceButton;
	private Button createTestDaoButton;
	private Button voSuperclassButton;
	private Button voSuperclassSearchButton;
	private Button regExButton;

	private IConnectionProfile[] connectionProfiles;
	private DatabaseResource databaseResource;

	private Tree previewTree;
	private Table databaseTablesTable;

	private String[] databaseTables;

	private IPackageFragment javaPackageFragment;
	private IPackageFragment javaTestPackageFragment;

	private Button okButton;

	private Label voSupperClassBracket1Label;
	private Label voSupperClassBracket2Label;

	Device device;

	public CSDGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	String parameterType;
	String returnType;

	public CSDGeneratorDialog(Shell parentShell, ISelection selection) {
		super(parentShell);
		this.selection = selection;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		device = Display.getCurrent();

		Composite container = (Composite) super.createDialogArea(parent);

		if(selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for (TreePath treePath : treePaths) {
				if(treePath.getLastSegment() instanceof IPackageFragmentRoot) {
				} else {
					javaPackageFragment = (IPackageFragment) treePath.getLastSegment();
				}
			}
		}

		IProject project = javaPackageFragment.getJavaProject().getProject();

		propertiesItem = new CSDGeneratorPropertiesItem((IResource) project.getAdapter(IResource.class));
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		isCreateTest = propertiesItem.getCreateTest();
		testPath = propertiesItem.getTestPath();
		mapperPath = propertiesItem.getMapperPath();
		voFolder = propertiesItem.getVoFolder();
		voPath = propertiesItem.getVoPath();
		voSuperclass = propertiesItem.getVoSuperclass();
		jspPath = propertiesItem.getJspPath();

		isCreateMapper = propertiesItem.getCreateMapper() && mapperPath != null && !"".equals(mapperPath);
		isCreateJsp = propertiesItem.getCreateJsp() && jspPath != null && !"".equals(jspPath);
		isCreateVoFolder = propertiesItem.getCreateVoFolder();
		isCreateVo = propertiesItem.getCreateVo() && (isCreateVoFolder ? (voFolder != null && !"".equals(voFolder)) : (voPath != null && !"".equals(voPath)));
		isCreateSearchVo = propertiesItem.getCreateSearchVo();

		String databaseConnectionProfileName = propertiesItem.getDatabaseConnectionProfileName();

		String[] generalTemplateGroupNames = propertiesHelper.getGeneralTemplateGroupNames();
		List<String> generalTemplateGroupNameList = new ArrayList<String>();
		generalTemplateGroupNameList.add("Default");
		for(String generalTemplateGroupName : generalTemplateGroupNames) {
			generalTemplateGroupNameList.add(generalTemplateGroupName);
		}

		container.setLayout(new GridLayout(5, false));

		Label templateLabel = new Label(container, SWT.NONE);
		templateLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		templateLabel.setText("Template: ");

		GridData comboGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		comboGridData.widthHint = 150;

		templateCombo = new Combo(container, SWT.READ_ONLY);
		templateCombo.setLayoutData(comboGridData);
		templateCombo.setItems(generalTemplateGroupNameList.toArray(new String[generalTemplateGroupNameList.size()]));
		templateCombo.select(0);

		templateCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				templateGroupName = templateCombo.getText();

				if(!"Default".equals(templateGroupName)) {
					String generalTemplateController = propertiesHelper.getGeneralTemplateController(templateCombo.getText());
					String generalTemplateService = propertiesHelper.getGeneralTemplateService(templateCombo.getText());
					String generalTemplateDao = propertiesHelper.getGeneralTemplateDao(templateCombo.getText());
					/*String generalTemplateMapper = propertiesHelper.getGeneralTemplateMapper(templateCombo.getText());*/
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
		//if(!isCreateVo && !isCreateMapper) connectionProfileCombo.setEnabled(false);
		new Label(container, SWT.NONE);

		new Label(container, SWT.NONE);
		createParentLocationButton = new Button(container, SWT.CHECK);
		createParentLocationButton.setText("Create parent location");
		createParentLocationButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
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
		/*createParentLocationButton.setEnabled(false);*/

		new Label(container, SWT.NONE);
		createFolderButton = new Button(container, SWT.CHECK);
		createFolderButton.setText("Create folder");
		createFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
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

		GridLayout createButtonCompositeLayout = new GridLayout(9, false);
		createButtonCompositeLayout.marginWidth = 0;
		createButtonCompositeLayout.marginHeight = 0;
		createButtonCompositeLayout.horizontalSpacing = 0;

		new Label(container, SWT.NONE);
		Composite craeteButtonComposite = new Composite(container, SWT.NONE);
		craeteButtonComposite.setLayout(createButtonCompositeLayout);
		craeteButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 0));

		createControllerButton = new Button(craeteButtonComposite, SWT.CHECK);
		createControllerButton.setText("Controller");
		createControllerButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateController(button.getSelection());
				createTestControllerButton.setEnabled(button.getSelection());
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
				createTestServiceButton.setEnabled(button.getSelection());
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
				createTestDaoButton.setEnabled(button.getSelection());
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
		createMapperButton.setEnabled(false);

		createVoButton = new Button(craeteButtonComposite, SWT.CHECK);
		createVoButton.setText("Vo");
		createVoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateVo(button.getSelection());
				createTree();
				if(button.getSelection()) {
					voSuperclassButton.setEnabled(true);
					voSupperClassBracket1Label.setForeground(new Color(device, 0, 0, 0));
					voSupperClassBracket2Label.setForeground(new Color(device, 0, 0, 0));

					parameterCombo.setEnabled(false);
					returnCombo.setEnabled(false);
				} else {
					voSuperclassButton.setEnabled(false);
					voSupperClassBracket1Label.setForeground(new Color(device, 175, 174, 175));
					voSupperClassBracket2Label.setForeground(new Color(device, 175, 174, 175));

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
		createVoButton.setEnabled(false);

		voSupperClassBracket1Label = new Label(craeteButtonComposite, SWT.NONE);
		voSupperClassBracket1Label.setText("(");
		voSupperClassBracket1Label.setForeground(new Color(device, 175, 174, 175));

		voSuperclassButton = new Button(craeteButtonComposite, SWT.CHECK);
		voSuperclassButton.setText("Superclass");
		voSuperclassButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setExtendVoSuperclass(button.getSelection());
				if(button.getSelection()) {
					voSuperclassField.setEnabled(true);
					voSuperclassSearchButton.setEnabled(true);
				} else {
					voSuperclassField.setEnabled(false);
					voSuperclassSearchButton.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
			}
		});
		voSuperclassButton.setEnabled(false);

		voSupperClassBracket2Label = new Label(craeteButtonComposite, SWT.NONE);
		voSupperClassBracket2Label.setText(")");
		voSupperClassBracket2Label.setForeground(new Color(device, 175, 174, 175));

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

		String[] testTemplateGroupNames = propertiesHelper.getTestTemplateGroupNames();
		List<String> testTemplateGroupNameList = new ArrayList<String>();
		testTemplateGroupNameList.add("Default");
		for(String generalTemplateGroupName : testTemplateGroupNames) {
			testTemplateGroupNameList.add(generalTemplateGroupName);
		}

		Label testTemplateLabel = new Label(container, SWT.NONE);
		testTemplateLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		testTemplateLabel.setText("Test Template: ");

		testTemplateCombo = new Combo(container, SWT.READ_ONLY);
		testTemplateCombo.setLayoutData(comboGridData);
		testTemplateCombo.setItems(testTemplateGroupNameList.toArray(new String[testTemplateGroupNameList.size()]));
		testTemplateCombo.select(0);
		testTemplateCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				templateGroupName = testTemplateCombo.getText();

				if(!"Default".equals(templateGroupName)) {
					String testTemplateController = propertiesHelper.getTestTemplateController(testTemplateCombo.getText());
					String testTemplateService = propertiesHelper.getTestTemplateService(testTemplateCombo.getText());
					String testTemplateDao = propertiesHelper.getTestTemplateDao(testTemplateCombo.getText());

					if(testTemplateController != null
							&& !"".equals(propertiesHelper.getControllerTemplateFile(testTemplateController))) {
						createTestControllerButton.setEnabled(true);
					} else {
						createTestControllerButton.setEnabled(false);
					}

					if(testTemplateService != null
							&& !"".equals(propertiesHelper.getServiceTemplateFile(testTemplateService))) {
						createTestServiceButton.setEnabled(true);
					} else {
						createTestServiceButton.setEnabled(false);
					}

					if(testTemplateDao != null
							&& !"".equals(propertiesHelper.getDaoTemplateFile(testTemplateDao))) {
						createTestDaoButton.setEnabled(true);
					} else {
						createTestDaoButton.setEnabled(false);
					}

				}

				createTree();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		if(!isCreateTest) testTemplateCombo.setEnabled(false);

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		GridLayout createTestButtonCompositeLayout = new GridLayout(3, false);
		createTestButtonCompositeLayout.marginWidth = 0;
		createTestButtonCompositeLayout.marginHeight = 0;
		createTestButtonCompositeLayout.horizontalSpacing = 0;

		new Label(container, SWT.NONE);
		Composite craeteTestButtonComposite = new Composite(container, SWT.NONE);
		craeteTestButtonComposite.setLayout(createTestButtonCompositeLayout);
		craeteTestButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 0));

		createTestControllerButton = new Button(craeteTestButtonComposite, SWT.CHECK);
		createTestControllerButton.setText("Test Controller");
		createTestControllerButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateTestController(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createTestControllerButton.setSelection(isCreateTestController);
		if(!isCreateTest) createTestControllerButton.setEnabled(false);

		createTestServiceButton = new Button(craeteTestButtonComposite, SWT.CHECK);
		createTestServiceButton.setText("Test Service");
		createTestServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateTestService(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createTestServiceButton.setSelection(isCreateTestService);
		if(!isCreateTest) createTestServiceButton.setEnabled(false);

		createTestDaoButton = new Button(craeteTestButtonComposite, SWT.CHECK);
		createTestDaoButton.setText("Test Dao");
		createTestDaoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateTestDao(button.getSelection());
				createTree();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createTestDaoButton.setSelection(isCreateTestDao);
		if(!isCreateTest) createTestDaoButton.setEnabled(false);

		/* E : DB 연결 */

		Label voSuperclassLabel = new Label(container, SWT.NONE);
		voSuperclassLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		voSuperclassLabel.setText("      Vo Superclass: ");

		voSuperclassField = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		voSuperclassField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		voSuperclassField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				voSuperclass = voSuperclassField.getText().trim();
			}
		});
		if(voSuperclass != null) voSuperclassField.setText(voSuperclass);
		voSuperclassField.setEnabled(false);
		voSuperclassField.setBackground(new Color(device, 255, 255, 255));

		voSuperclassSearchButton = new Button(container, SWT.PUSH);
		voSuperclassSearchButton.setText("Browse...");
		voSuperclassSearchButton.setLayoutData(new GridData(100, 20));

		voSuperclassSearchButton.addSelectionListener(new SelectionListener() {
		@SuppressWarnings("restriction")
			@Override public void widgetSelected(SelectionEvent e) {
				try {
					SelectionDialog dialog = JavaUI.createTypeDialog(getShell(),
					new ProgressMonitorDialog(getShell()),
					javaPackageFragment.getJavaProject().getProject(),
					IJavaElementSearchConstants.CONSIDER_CLASSES,
					false);
					dialog.setTitle("Superclass Selection");
				dialog.open();
				Object[] result = dialog.getResult();
				if(result != null && result.length == 1) voSuperclassField.setText(((SourceType) result[0]).getFullyQualifiedName());
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}
			}
			@Override public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		voSuperclassSearchButton.setEnabled(false);

		Label parameterLabel = new Label(container, SWT.NONE);
		parameterLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		parameterLabel.setText("Parameter: ");

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
		parameterCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		parameterCombo.setItems(objectList.toArray(new String[objectList.size()]));
		parameterCombo.select(0);
		new Label(container, SWT.NONE);

		Label returnLabel = new Label(container, SWT.NONE);
		returnLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		returnLabel.setText("Return: ");

		returnCombo = new Combo(container, SWT.READ_ONLY);
		returnCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		returnCombo.setItems(objectList.toArray(new String[objectList.size()]));
		returnCombo.select(0);
		new Label(container, SWT.NONE);

		Label prefixLabel = new Label(container, SWT.NONE);
		prefixLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		prefixLabel.setText("Prefix: ");

		prefixField = new Text(container, SWT.BORDER);
		prefixField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 0));
		prefixField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				prefix = prefixField.getText().trim();
				createTree();

				okButtonEnabled();

			}
		});
		prefixField.setFocus();
		new Label(container, SWT.NONE);

		new Label(container, SWT.NONE);
		regExButton = new Button(container, SWT.CHECK);
		regExButton.setText("Regular expressions");
		regExButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 0));
		regExButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				if(button.getSelection()) {
					prefixField.setText("|[A-Za-z0-9]+_([A-Za-z0-9_]+)|");
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
		tablesLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 0));
		tablesLabel.setText("Tables: ");

		GridData previewTreeGridData = new GridData(SWT.FILL, SWT.FILL, false, true, 3, 0);
		previewTreeGridData.widthHint = 300;

		previewTree = new Tree(container, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		previewTree.setLayoutData(previewTreeGridData);
		previewTree.setHeaderVisible(false);
		TreeColumn previewTreeColumn = new TreeColumn(previewTree, SWT.NONE);
		previewTreeColumn.setWidth(400);

		createTree();

		GridData databaseTablesGridData = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 0);
		databaseTablesGridData.widthHint = 200;

		databaseTablesTable = new Table(container, SWT.CHECK | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		databaseTablesTable.setLayoutData(databaseTablesGridData);
		databaseTablesTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTables = null;
				TableItem[] tableItems = databaseTablesTable.getItems();
				List<String> databaseTableList = new ArrayList<String>();
/*
				TableItem[] selectTableItems = databaseTablesTable.getSelection();
				for(TableItem tableItem : selectTableItems) {
					if(databaseTablesTable.indexOf(tableItem) > 0) {
						if(tableItem.getChecked()) {
							tableItem.setChecked(false);
						} else {
							tableItem.setChecked(true);
						}
					}
				}
*/
				for(TableItem tableItem : tableItems) {
					if(tableItem.getChecked()) {
						databaseTableList.add(tableItem.getText());
					}
				}

				if(databaseTableList.size() > 0) {

					databaseTables = databaseTableList.toArray(new String[databaseTableList.size()]);

					if(isCreateMapper) createMapperButton.setEnabled(true);
					if(isCreateVo) {
						createVoButton.setEnabled(true);
						voSuperclassButton.setEnabled(true);
					}
					regExButton.setEnabled(true);

				} else {

					createMapperButton.setEnabled(false);
					createVoButton.setEnabled(false);
					voSuperclassButton.setEnabled(false);
					regExButton.setEnabled(false);

					if(regExButton.getSelection()) prefixField.setText("[A-Za-z0-9]+_([A-Za-z0-9_]+)");
				}

				if(createVoButton.getEnabled() && createVoButton.getSelection()) {
					voSupperClassBracket1Label.setForeground(new Color(device, 0, 0, 0));
					voSupperClassBracket2Label.setForeground(new Color(device, 0, 0, 0));
					parameterCombo.setEnabled(false);
					returnCombo.setEnabled(false);
				} else {
					voSupperClassBracket1Label.setForeground(new Color(device, 175, 174, 175));
					voSupperClassBracket2Label.setForeground(new Color(device, 175, 174, 175));
					parameterCombo.setEnabled(true);
					returnCombo.setEnabled(true);
				}

				databaseTablesTable.deselectAll();

				createTree();

				okButtonEnabled();
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
				if(profile.getName().equals(databaseConnectionProfileName)) {
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

		boolean isCreateTest = propertiesItem.getCreateTest();
		boolean isCreateTestControllerFolder = propertiesItem.getCreateTestControllerFolder();
		boolean isCreateTestServiceFolder = propertiesItem.getCreateTestServiceFolder();
		boolean isCreateTestDaoFolder = propertiesItem.getCreateTestDaoFolder();


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
		String javaTestBuildPath = "";
		String javaVoBuildPath = "";
		String resourceBuildPath = "";

		try {
			IClasspathEntry[] classpaths = javaProject.getRawClasspath();
			for (IClasspathEntry classpath : classpaths) {
				IPath path = classpath.getPath();
				if(javaPackageFragment.getPath().toString().indexOf(path.toString()) > -1) {
					javaBuildPath = path.removeFirstSegments(1).toString();
				} else if(isCreateMapper && mapperPath != null && mapperPath.indexOf(path.toString()) > -1) {
					resourceBuildPath = path.removeFirstSegments(1).toString();
				}

				if(isCreateTest && testPath != null && testPath.indexOf(path.toString()) > -1) {
					javaTestBuildPath = path.removeFirstSegments(1).toString();
				}

				if(isCreateVo && voPath != null && voPath.indexOf(path.toString()) > -1) {
					javaVoBuildPath = path.removeFirstSegments(1).toString();
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		try {
			javaTestPackageFragment = javaProject.findPackageFragment(new Path(testPath));
		} catch (JavaModelException | NullPointerException e) {
			isCreateTest = false;
		}

		String javaPackagePath = javaPackageFragment.getElementName();
		String javaTestPackagePath = !isCreateTest ? "" : javaTestPackageFragment.getElementName();

		org.eclipse.jdt.ui.ISharedImages sharedImages = JavaUI.getSharedImages();
		org.eclipse.ui.ISharedImages workbenchSharedImages = PlatformUI.getWorkbench().getSharedImages();

		Image packageRootIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
		Image packageIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
		Image javaIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		Image folderIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
		Image fileIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);

		if(isParentLocation) {
			javaPackagePath = javaPackagePath.substring(0, javaPackagePath.lastIndexOf("."));
		}

		/* S : Create Java File */

		TreeItem javaBuildTreeItem = new TreeItem(previewTree, SWT.NONE);
		javaBuildTreeItem.setText(javaBuildPath);
		javaBuildTreeItem.setImage(packageRootIcon);

		String[] javaPackagePaths = javaPackagePath.split("\\.");

		TreeItem javaPackageTreeItem = javaBuildTreeItem;

		for(String packagePathName : javaPackagePaths) {

			TreeItem javaTreeItem = new TreeItem(javaPackageTreeItem, SWT.NONE);
			javaTreeItem.setText(packagePathName);
			javaTreeItem.setImage(packageIcon);

			javaPackageTreeItem.setExpanded(true);
			javaPackageTreeItem = javaTreeItem;
		}


		TreeItem javaTestBuildTreeItem = null;
		TreeItem javaTestPackageTreeItem = null;

		if(isCreateTest) {
			if(!javaBuildPath.equals(javaTestBuildPath)) {
				javaTestBuildTreeItem = new TreeItem(previewTree, SWT.NONE);
				javaTestBuildTreeItem.setText(javaTestBuildPath);
				javaTestBuildTreeItem.setImage(packageRootIcon);

				String[] javaTestPackagePaths = javaTestPackagePath.split("\\.");

				javaTestPackageTreeItem = javaTestBuildTreeItem;

				for(String packagePathName : javaTestPackagePaths) {
					if(!"".equals(packagePathName)) {
						TreeItem javaTestTreeItem = new TreeItem(javaTestPackageTreeItem, SWT.NONE);
						javaTestTreeItem.setText(packagePathName);
						javaTestTreeItem.setImage(packageIcon);

						javaTestPackageTreeItem.setExpanded(true);
						javaTestPackageTreeItem = javaTestTreeItem;
					}
				}
			} else {
				javaTestPackageTreeItem = javaPackageTreeItem;
			}
		}

/*
		TreeItem javaPackageTreeItem = javaBuildTreeItem;

		TreeItem javaTreeItem = new TreeItem(javaPackageTreeItem, SWT.NONE);
		javaTreeItem.setText(packagePath);
		javaTreeItem.setImage(packageIcon);

		javaPackageTreeItem.setExpanded(true);
		javaPackageTreeItem = javaTreeItem;
*/
		TreeItem javaRootTreeItem = null;
		TreeItem javaTestRootTreeItem = null;

		String[] names = null;

		if(databaseTables == null || databaseTables.length == 0) {
			names = new String[]{prefix};
		} else {
			names = databaseTables;
		}

		TreeItem controllerFolderTreeItem = null;
		TreeItem controllerSubFolderTreeItem = null;
		TreeItem serviceFolderTreeItem = null;
		TreeItem serviceSubFolderTreeItem = null;
		TreeItem serviceImplFolderTreeItem = null;
		/*TreeItem serviceImplSubFolderTreeItem = null;*/
		TreeItem daoFolderTreeItem = null;
		TreeItem daoSubFolderTreeItem = null;
		TreeItem voFolderTreeItem = null;

		TreeItem testControllerFolderTreeItem = null;
		TreeItem testControllerSubFolderTreeItem = null;
		TreeItem testServiceFolderTreeItem = null;
		TreeItem testServiceSubFolderTreeItem = null;
		TreeItem testDaoFolderTreeItem = null;
		TreeItem testDaoSubFolderTreeItem = null;

		String addFirstPrefix = "";
		String addLastPrefix = "";

		Pattern pattern = null;

		try {

			String regex = "";

			int prefixFirstIndex = prefix.indexOf("|");
			int prefixLastIndex = prefix.lastIndexOf("|");
			if(prefixFirstIndex != -1
					&& prefixFirstIndex != prefixLastIndex) {
				if(prefixFirstIndex > 0) addFirstPrefix = prefix.substring(0, prefixFirstIndex) + "_";
				if(prefixLastIndex < prefix.length() - 1) addLastPrefix = "_" + prefix.substring(prefixLastIndex + 1);
				regex = prefix.substring(prefixFirstIndex + 1, prefixLastIndex);
			} else {
				regex = prefix;
			}

			if(!"".equals(regex)) {
				pattern = Pattern.compile(regex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(String name : names) {

			if(pattern != null) {
				Matcher matcher = pattern.matcher(name);
				if(matcher.matches() && matcher.groupCount() > 0) {
					name = matcher.group(1);
				}
			}

			name = addFirstPrefix + name + addLastPrefix;
			name = StringUtils.toCamelCase(name);

			String capitalizePrefix = "";

			if(name.length() > 1) {
				capitalizePrefix = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			} else if(name.length() == 1) {
				capitalizePrefix = name.substring(0, 1).toUpperCase();
			}

			if(isCreateFolder && !"".equals(name)
					&& (isCreateController || isCreateService || isCreateDao || isCreateVo)) {
				javaRootTreeItem = new TreeItem(javaPackageTreeItem, SWT.NONE);
				javaRootTreeItem.setText(name);
				javaRootTreeItem.setImage(packageIcon);

				if(isCreateTest) {
					javaTestRootTreeItem = new TreeItem(javaTestPackageTreeItem, SWT.NONE);
					javaTestRootTreeItem.setText(name);
					javaTestRootTreeItem.setImage(packageIcon);
				}
			} else {
				javaRootTreeItem = javaPackageTreeItem;
				if(isCreateTest) javaTestRootTreeItem = javaTestPackageTreeItem;
			}

			if(isCreateVo && createVoButton.isEnabled() && (isCreateVoFolder ? voFolder != null && !"".equals(voFolder) : voPath != null && !"".equals(voPath))) {

				if(isCreateVoFolder && (isCreateFolder || voFolderTreeItem == null)) {
					voFolderTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
					voFolderTreeItem.setText(voFolder);
					voFolderTreeItem.setImage(packageIcon);
				} else {
					if(voFolderTreeItem == null) {
						String voPackage = voPath.replace("/", ".").substring(voPath.lastIndexOf(javaBuildPath) + javaBuildPath.length() + 1);
						String[] voPackagePath = voPackage.split("\\.");

						int i = 0;

						if(javaBuildPath.indexOf(javaVoBuildPath) > -1) {
							for(; i < voPackagePath.length; i++) {
								TreeItem parentJavaTreeItem = findChildJavaTreeItem(javaBuildTreeItem, voPackagePath[i]);
								if(parentJavaTreeItem != null) {
									voFolderTreeItem = parentJavaTreeItem;
								} else {
									break;
								}
							}

						} else {
							TreeItem javaVoBuildTreeItem = new TreeItem(previewTree, SWT.NONE, 0);
							javaVoBuildTreeItem.setText(javaVoBuildPath);
							javaVoBuildTreeItem.setImage(packageRootIcon);

							voFolderTreeItem = javaVoBuildTreeItem;
						}

						if(voFolderTreeItem != null) {
							for (; i < voPackagePath.length; i++) {
								TreeItem treeItem = new TreeItem(voFolderTreeItem, SWT.NONE, 0);
								treeItem.setText(voPackagePath[i]);
								treeItem.setImage(packageIcon);

								voFolderTreeItem = treeItem;
							}
						}
					}
				}

				if(isCreateSearchVo) {
					TreeItem voJavaTreeItem = new TreeItem(voFolderTreeItem, SWT.NONE);
					voJavaTreeItem.setText("Search" + capitalizePrefix + "Vo.java");
					voJavaTreeItem.setImage(javaIcon);
				}

				TreeItem voJavaTreeItem = new TreeItem(voFolderTreeItem, SWT.NONE);
				voJavaTreeItem.setText(capitalizePrefix + "Vo.java");
				voJavaTreeItem.setImage(javaIcon);

				voFolderTreeItem.setExpanded(true);
			}

			if(isCreateController && createControllerButton.isEnabled()) {
				if(isCreateControllerFolder) {

					String controllerFolder = "";

					if(isAddPrefixControllerFolder) {
						controllerFolder = name + "Controller";
					} else {
						controllerFolder = "controller";
					}

					if(isCreateFolder || isAddPrefixControllerFolder || controllerFolderTreeItem == null) {
						controllerFolderTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
						controllerFolderTreeItem.setText(controllerFolder);
					}

					if((isCreateFolder || isAddPrefixControllerFolder || controllerSubFolderTreeItem == null) && isCreateControllerSubFolder) {
						controllerSubFolderTreeItem = new TreeItem(controllerFolderTreeItem, SWT.NONE);
						controllerSubFolderTreeItem.setText(name);
					}

					TreeItem controllerJavaTreeItem = new TreeItem(controllerSubFolderTreeItem == null ? controllerFolderTreeItem : controllerSubFolderTreeItem, SWT.NONE);
					controllerJavaTreeItem.setText(capitalizePrefix + "Controller.java");
					controllerJavaTreeItem.setImage(javaIcon);

					if(controllerSubFolderTreeItem != null) {
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

				if(isCreateTest && isCreateTestController && createTestControllerButton.isEnabled()) {
					if(isCreateTestControllerFolder && isCreateControllerFolder) {

						String testControllerFolder = "";

						if(isAddPrefixControllerFolder) {
							testControllerFolder = name + "Controller";
						} else {
							testControllerFolder = "controller";
						}

						if(javaTestBuildTreeItem == null) {
							testControllerFolderTreeItem = controllerFolderTreeItem;
							testControllerSubFolderTreeItem = controllerSubFolderTreeItem;
						} else {
							if(isCreateFolder || isAddPrefixControllerFolder || testControllerFolderTreeItem == null) {
								testControllerFolderTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
								testControllerFolderTreeItem.setText(testControllerFolder);
							}

							if((isCreateFolder || isAddPrefixControllerFolder || testControllerSubFolderTreeItem == null) && isCreateControllerSubFolder) {
								controllerSubFolderTreeItem = new TreeItem(controllerFolderTreeItem, SWT.NONE);
								controllerSubFolderTreeItem.setText(name);
							}
						}

						TreeItem testControllerJavaTreeItem = new TreeItem(testControllerSubFolderTreeItem == null ? testControllerFolderTreeItem : testControllerSubFolderTreeItem, SWT.NONE);
						testControllerJavaTreeItem.setText("Test" + capitalizePrefix + "Controller.java");
						testControllerJavaTreeItem.setImage(javaIcon);

						if(testControllerSubFolderTreeItem != null) {
							testControllerSubFolderTreeItem.setExpanded(true);
							testControllerSubFolderTreeItem.setImage(packageIcon);
						}

						testControllerFolderTreeItem.setExpanded(true);
						testControllerFolderTreeItem.setImage(packageIcon);
					} else {
						TreeItem testControllerJavaTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
						testControllerJavaTreeItem.setText("Test" + capitalizePrefix + "Controller.java");
						testControllerJavaTreeItem.setImage(javaIcon);
					}
				}
			}

			if(isCreateService && createServiceButton.isEnabled()) {
				if(isCreateServiceFolder) {

					String serviceFolder = "";

					if(isAddPrefixServiceFolder) {
						serviceFolder = name + "Service";
					} else {
						serviceFolder = "service";
					}

					if(isCreateFolder || isAddPrefixServiceFolder || serviceFolderTreeItem == null) {
						serviceFolderTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
						serviceFolderTreeItem.setText(serviceFolder);
					}

					if((isCreateFolder || isAddPrefixServiceFolder || serviceSubFolderTreeItem == null) && isCreateServiceSubFolder) {
						serviceSubFolderTreeItem = new TreeItem(serviceFolderTreeItem, SWT.NONE);
						serviceSubFolderTreeItem.setText(name);
					}

					if(isCreateServiceImpl) {
						if(isCreateServiceImplFolder) {
							if(isAddPrefixServiceFolder || isCreateServiceSubFolder || serviceImplFolderTreeItem == null) {
								String serviceImplFolder = "impl";

								serviceImplFolderTreeItem = new TreeItem(serviceSubFolderTreeItem == null ? serviceFolderTreeItem : serviceSubFolderTreeItem, SWT.NONE);
								serviceImplFolderTreeItem.setText(serviceImplFolder);
								serviceImplFolderTreeItem.setImage(packageIcon);
							}
						} else {
							serviceImplFolderTreeItem = serviceSubFolderTreeItem == null ? serviceFolderTreeItem : serviceSubFolderTreeItem;
						}
						TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
						serviceImplJavaTreeItem.setText(capitalizePrefix + "ServiceImpl.java");
						serviceImplJavaTreeItem.setImage(javaIcon);
						serviceImplFolderTreeItem.setExpanded(true);
					}

					TreeItem serviceJavaTreeItem = new TreeItem(serviceSubFolderTreeItem == null ? serviceFolderTreeItem : serviceSubFolderTreeItem, SWT.NONE);
					serviceJavaTreeItem.setText(capitalizePrefix + "Service.java");
					serviceJavaTreeItem.setImage(javaIcon);

					if(serviceSubFolderTreeItem != null) {
						serviceSubFolderTreeItem.setExpanded(true);
						serviceSubFolderTreeItem.setImage(packageIcon);
					}

					serviceFolderTreeItem.setExpanded(true);
					serviceFolderTreeItem.setImage(packageIcon);
				} else {

					if(isCreateServiceImpl) {
						if(isCreateServiceImplFolder) {
							if(isCreateServiceSubFolder || serviceImplFolderTreeItem == null) {
								String serviceImplFolder = "impl";

								serviceImplFolderTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
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

				if(isCreateTest && isCreateTestService && createTestServiceButton.isEnabled()) {
					if(isCreateTestServiceFolder && isCreateServiceFolder) {

						String testServiceFolder = "";

						if(isAddPrefixServiceFolder) {
							testServiceFolder = name + "Service";
						} else {
							testServiceFolder = "service";
						}

						if(javaTestBuildTreeItem == null) {
							testServiceFolderTreeItem = serviceFolderTreeItem;
							testServiceSubFolderTreeItem = serviceSubFolderTreeItem;
						} else {
							if(isCreateFolder || isAddPrefixServiceFolder || testServiceFolderTreeItem == null) {
								testServiceFolderTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
								testServiceFolderTreeItem.setText(testServiceFolder);
							}

							if((isCreateFolder || isAddPrefixServiceFolder || testServiceSubFolderTreeItem == null) && isCreateServiceSubFolder) {
								testServiceSubFolderTreeItem = new TreeItem(testServiceFolderTreeItem, SWT.NONE);
								testServiceSubFolderTreeItem.setText(name);
							}
						}

						TreeItem testServiceJavaTreeItem = new TreeItem(testServiceSubFolderTreeItem == null ? testServiceFolderTreeItem : testServiceSubFolderTreeItem, SWT.NONE);
						testServiceJavaTreeItem.setText("Test" + capitalizePrefix + "Service.java");
						testServiceJavaTreeItem.setImage(javaIcon);

						if(testServiceSubFolderTreeItem != null) {
							testServiceSubFolderTreeItem.setExpanded(true);
							testServiceSubFolderTreeItem.setImage(packageIcon);
						}

						testServiceFolderTreeItem.setExpanded(true);
						testServiceFolderTreeItem.setImage(packageIcon);
					} else {
						TreeItem testServiceJavaTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
						testServiceJavaTreeItem.setText("Test" + capitalizePrefix + "Service.java");
						testServiceJavaTreeItem.setImage(javaIcon);
					}
				}

			}

			if(isCreateDao && createDaoButton.isEnabled()) {
				if(isCreateDaoFolder) {

					String daoFolder = "";

					if(isAddPrefixDaoFolder) {
						daoFolder = name + "Dao";
					} else {
						daoFolder = "dao";
					}

					if(isCreateFolder || isAddPrefixDaoFolder || daoFolderTreeItem == null) {
						daoFolderTreeItem = new TreeItem(javaRootTreeItem, SWT.NONE);
						daoFolderTreeItem.setText(daoFolder);
					}

					if((isCreateFolder || isAddPrefixDaoFolder || daoFolderTreeItem == null) && isCreateDaoSubFolder) {
						daoSubFolderTreeItem = new TreeItem(daoFolderTreeItem, SWT.NONE);
						daoSubFolderTreeItem.setText(name);
					}

					TreeItem daoJavaTreeItem = new TreeItem(daoSubFolderTreeItem == null ? daoFolderTreeItem : daoSubFolderTreeItem, SWT.NONE);
					daoJavaTreeItem.setText(capitalizePrefix + "Dao.java");
					daoJavaTreeItem.setImage(javaIcon);

					if(daoSubFolderTreeItem != null) {
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

				if(isCreateTest && isCreateTestDao && createTestDaoButton.isEnabled()) {
					if(isCreateTestDaoFolder && isCreateDaoFolder) {

						String testDaoFolder = "";

						if(isAddPrefixDaoFolder) {
							testDaoFolder = name + "Dao";
						} else {
							testDaoFolder = "dao";
						}

						if(javaTestBuildTreeItem == null) {
							testDaoFolderTreeItem = daoFolderTreeItem;
							testDaoSubFolderTreeItem = daoSubFolderTreeItem;
						} else {
							if(isCreateFolder || isAddPrefixDaoFolder || testDaoFolderTreeItem == null) {
								testDaoFolderTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
								testDaoFolderTreeItem.setText(testDaoFolder);
							}

							if((isCreateFolder || isAddPrefixDaoFolder || daoFolderTreeItem == null) && isCreateDaoSubFolder) {
								testDaoSubFolderTreeItem = new TreeItem(testDaoFolderTreeItem, SWT.NONE);
								testDaoSubFolderTreeItem.setText(name);
							}
						}

						TreeItem testDaoJavaTreeItem = new TreeItem(testDaoSubFolderTreeItem == null ? testDaoFolderTreeItem : testDaoSubFolderTreeItem, SWT.NONE);
						testDaoJavaTreeItem.setText("Test" + capitalizePrefix + "Dao.java");
						testDaoJavaTreeItem.setImage(javaIcon);

						if(testDaoSubFolderTreeItem != null) {
							testDaoSubFolderTreeItem.setExpanded(true);
							testDaoSubFolderTreeItem.setImage(packageIcon);
						}

						testDaoFolderTreeItem.setExpanded(true);
						testDaoFolderTreeItem.setImage(packageIcon);
					} else {
						TreeItem testDaoJavaTreeItem = new TreeItem(javaTestRootTreeItem, SWT.NONE);
						testDaoJavaTreeItem.setText("Test" + capitalizePrefix + "Dao.java");
						testDaoJavaTreeItem.setImage(javaIcon);
					}
				}

			}

			javaPackageTreeItem.setExpanded(true);
			javaRootTreeItem.setExpanded(true);
			if(javaTestPackageTreeItem != null) javaTestPackageTreeItem.setExpanded(true);
			if(javaTestRootTreeItem != null) javaTestRootTreeItem.setExpanded(true);
		}

		javaBuildTreeItem.setExpanded(true);
		if(javaTestBuildTreeItem != null) javaTestBuildTreeItem.setExpanded(true);

		/* E : Create Java File */

		//previewTree.setSortDirection(SWT.DOWN);

		/* S : Create Resource File */

		if(isCreateMapper && createMapperButton.isEnabled() && !"".equals(resourceBuildPath)) {
			TreeItem resourceBuildTreeItem = new TreeItem(previewTree, SWT.NONE);
			resourceBuildTreeItem.setText(resourceBuildPath);
			resourceBuildTreeItem.setImage(packageRootIcon);

			String mapperPackage = mapperPath.replace("/", ".").substring(mapperPath.lastIndexOf(resourceBuildPath) + resourceBuildPath.length() + 1);
			String[] mapperPackagePath = mapperPackage.split("\\.");

			TreeItem parentMapperFolderTreeItem = resourceBuildTreeItem;

			for (int i = 0; i < mapperPackagePath.length; i++) {
				TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, SWT.NONE);
				mapperFolderTreeItem.setText(mapperPackagePath[i]);
				mapperFolderTreeItem.setImage(folderIcon);

				parentMapperFolderTreeItem.setExpanded(true);
				parentMapperFolderTreeItem = mapperFolderTreeItem;
			}

			for(String name : names) {

				if(pattern != null) {
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches() && matcher.groupCount() > 0) {
						name = matcher.group(1);
					}
				}

				name = addFirstPrefix + name + addLastPrefix;
				name = StringUtils.toCamelCase(name);

				if(!"".equals(name)) {
					TreeItem mapperFolderTreeItem = new TreeItem(parentMapperFolderTreeItem, SWT.NONE);
					mapperFolderTreeItem.setText(name);
					mapperFolderTreeItem.setImage(folderIcon);

					TreeItem mapperFileTreeItem = new TreeItem(mapperFolderTreeItem, SWT.NONE);
					mapperFileTreeItem.setText(name + "Mapper.xml");
					mapperFileTreeItem.setImage(fileIcon);

					mapperFolderTreeItem.setExpanded(true);
				} else {
					TreeItem mapperFileTreeItem = new TreeItem(parentMapperFolderTreeItem, SWT.NONE);
					mapperFileTreeItem.setText(name + "Mapper.xml");
					mapperFileTreeItem.setImage(fileIcon);
				}
			}

			parentMapperFolderTreeItem.setExpanded(true);
			resourceBuildTreeItem.setExpanded(true);
		}

		/* E : Create Resource File */

		/* S : Create JSP File */

		if(isCreateJsp && createJspButton.isEnabled()) {
			TreeItem jspBuildTreeItem = new TreeItem(previewTree, SWT.NONE);
			jspBuildTreeItem.setText(jspPath.substring(javaProject.getProject().getName().length() + 2));
			jspBuildTreeItem.setImage(folderIcon);

			for(String name : names) {

				if(pattern != null) {
					Matcher matcher = pattern.matcher(name);
					if(matcher.matches() && matcher.groupCount() > 0) {
						name = matcher.group(1);
					}
				}

				name = addFirstPrefix + name + addLastPrefix;
				name = StringUtils.toCamelCase(name);

				TreeItem jspFolderTreeItem = null;

				if(!"".equals(name)) {
					jspFolderTreeItem = new TreeItem(jspBuildTreeItem, SWT.NONE);
					jspFolderTreeItem.setText(name);
					jspFolderTreeItem.setImage(folderIcon);
				}

				if(existsJspTemplateListFile) {
					TreeItem jspListFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, SWT.NONE);
					jspListFileTreeItem.setText(name + "List.jsp");
					jspListFileTreeItem.setImage(fileIcon);
				}

				if(existsJspTemplatePostFile) {
					TreeItem jspPostFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, SWT.NONE);
					jspPostFileTreeItem.setText(name + "Post.jsp");
					jspPostFileTreeItem.setImage(fileIcon);
				}

				if(existsJspTemplateViewFile) {
					TreeItem jspViewFileTreeItem = new TreeItem(jspFolderTreeItem == null ? jspBuildTreeItem : jspFolderTreeItem, SWT.NONE);
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
		return new Point(730, 670);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if(id == IDialogConstants.OK_ID) {
			okButton = super.createButton(parent, id, label, defaultButton);
			okButtonEnabled();
			return okButton;
		} else {
			return super.createButton(parent, id, label, defaultButton);
		}
	}

	protected void okButtonEnabled() {
		if(okButton == null)
			return;

		if(!"".equals(templateCombo.getText()) && (databaseTables != null || !"".equals(prefix))) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	public String getTestTemplateGroupName() {
		return testTemplateGroupName;
	}

	public void setTestTemplateGroupName(String testTemplateGroupName) {
		this.testTemplateGroupName = testTemplateGroupName;
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

	public boolean isExtendVoSuperclass() {
		return isExtendVoSuperclass;
	}

	public void setExtendVoSuperclass(boolean isExtendVoSuperclass) {
		this.isExtendVoSuperclass = isExtendVoSuperclass;
	}

	public String getVoSuperclass() {
		return voSuperclass;
	}

	public void setVoSuperclass(String voSuperclass) {
		this.voSuperclass = voSuperclass;
	}

	public boolean isCreateController() {
		return isCreateController;
	}

	public void setCreateController(boolean isCreateController) {
		this.isCreateController = isCreateController;
	}

	public boolean isCreateTestController() {
		return isCreateTestController;
	}

	public void setCreateTestController(boolean isCreateTestController) {
		this.isCreateTestController = isCreateTestController;
	}

	public boolean isCreateTestService() {
		return isCreateTestService;
	}

	public void setCreateTestService(boolean isCreateTestService) {
		this.isCreateTestService = isCreateTestService;
	}

	public boolean isCreateTestDao() {
		return isCreateTestDao;
	}

	public void setCreateTestDao(boolean isCreateTestDao) {
		this.isCreateTestDao = isCreateTestDao;
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

	public String getParameterType() {
		return parameterType;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	private void getDatabaseTrees() {
		databaseTablesTable.removeAll();
		connectionProfile = null;
		for (IConnectionProfile profile : connectionProfiles) {
			if(profile.getName().equals(connectionProfileCombo.getText())) {
				connectionProfile = profile;
				break;
			}
		}
		if(connectionProfile != null) {
			List<String> databaseTables = databaseResource.getDatabaseTables(connectionProfile);
			for(String databaseTable : databaseTables) {
				TableItem treeItem = new TableItem(databaseTablesTable, SWT.NONE);
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
		isCreateVo = isCreateVo && createVoButton.isEnabled();
		isExtendVoSuperclass = isExtendVoSuperclass && voSuperclassButton.isEnabled();
		isCreateMapper = isCreateMapper && createMapperButton.isEnabled();
		isCreateJsp = isCreateJsp && createJspButton.isEnabled();

		isCreateTestController = isCreateTestController && createTestControllerButton.isEnabled();
		isCreateTestService = isCreateTestService && createTestServiceButton.isEnabled();
		isCreateTestDao = isCreateTestDao && createTestDaoButton.isEnabled();

		parameterType = parameterCombo.getText();
		returnType = returnCombo.getText();

		voSuperclass = voSuperclassField.getText();

		super.okPressed();
	}

}
