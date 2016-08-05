package kr.pe.maun.csdgenerator.dialogs;

import java.util.ArrayList;
import java.util.List;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.TextSelection;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.texteditor.ITextEditor;

public class CSDFunctionGeneratorDialog extends Dialog {

	private CSDGeneratorPropertiesItem propertiesItem;
	private CSDGeneratorPropertiesHelper propertiesHelper;

	private String prefix = "";
	private Text prefixField;
	private Text voSuperclassField;
/*
	private String regExGroup = "";
	private Text regExGroupField;
*/
	private boolean isCreateService = true;
	private boolean isCreateDao = true;
	private boolean isCreateMapper = true;
	private boolean isCreateVo = true;
	private boolean isExtendVoSuperclass = false;
	private boolean isCreateVoFolder = false;

	private boolean isCreateSelectCount = true;
	private boolean isCreateSelectList = true;
	private boolean isCreateSelectOne = true;
	private boolean isCreateInsert = true;
	private boolean isCreateUpdate = true;
	private boolean isCreateDelete = true;

	String mapperPath;
	String voFolder;
	String voPath;
	String voSuperclass;

	private IConnectionProfile connectionProfile;

	private ISelection selection;

	private Combo databaseTablesCombo;
	private Combo connectionProfileCombo;
	private Combo parameterCombo;
	private Combo returnCombo;
	private Combo importServiceInterfaceCombo;
	private Combo importDaoCombo;

	private Button createServiceButton;
	private Button createDaoButton;
	private Button createMapperButton;
	private Button createVoButton;
	private Button voSuperclassButton;
	private Button voSuperclassSearchButton;

	private Button createSelectCountButton;
	private Button createSelectListButton;
	private Button createSelectOneButton;
	private Button createInsertButton;
	private Button createUpdateButton;
	private Button createDeleteButton;

	private IConnectionProfile[] connectionProfiles;
	private DatabaseResource databaseResource;

	private String databaseTableName;

	private ICompilationUnit compilationUnit;

	private Button okButton;

	private String parameterType;
	private String returnType;

	List<String> importServiceInterfaces;
	List<String> importDaos;

	String selectImportServiceInterface;
	String selectImportDao;

	private Label voSupperClassBracket1Label;
	private Label voSupperClassBracket2Label;

	Device device;

	public CSDFunctionGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	public CSDFunctionGeneratorDialog(Shell parentShell, ISelection selection) {
		super(parentShell);
		this.selection = selection;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		device = Display.getCurrent();

		Composite container = (Composite) super.createDialogArea(parent);

		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for (TreePath treePath : treePaths) {
				if (treePath.getLastSegment() instanceof IPackageFragmentRoot) {
				} else {
					compilationUnit = (ICompilationUnit) treePath.getLastSegment();
				}
			}
		} else if(selection instanceof TextSelection) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page.getActiveEditor();
			IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
			if(javaElement instanceof ICompilationUnit) compilationUnit = (ICompilationUnit) javaElement;
		}

		final IProject project = compilationUnit.getJavaProject().getProject();

		propertiesItem = new CSDGeneratorPropertiesItem((IResource) project.getAdapter(IResource.class));
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		mapperPath = propertiesItem.getMapperPath();
		voFolder = propertiesItem.getVoFolder();
		voPath = propertiesItem.getVoPath();

		isCreateMapper = propertiesItem.getCreateMapper() && mapperPath != null && !"".equals(mapperPath);
		isCreateVo = propertiesItem.getCreateVo() && (isCreateVoFolder ? (voFolder != null && !"".equals(voFolder)) : (voPath != null && !"".equals(voPath)));

		container.setLayout(new GridLayout(4, false));

		GridData comboGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		comboGridData.widthHint = 150;

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
				databaseTableName = null;
				databaseTablesCombo.removeAll();
				databaseTablesCombo.setEnabled(false);
				connectionProfile = null;

				for (IConnectionProfile profile : connectionProfiles) {
					if (profile.getName().equals(connectionProfileCombo.getText())) {
						connectionProfile = profile;
						break;
					}
				}
				if (connectionProfile != null) {
					createVoButton.setEnabled(false);
					voSuperclassButton.setEnabled(false);
					voSupperClassBracket1Label.setForeground(new Color(device, 175, 174, 175));
					voSupperClassBracket2Label.setForeground(new Color(device, 175, 174, 175));
					createMapperButton.setEnabled(false);
					parameterCombo.setEnabled(true);
					returnCombo.setEnabled(true);

					List<String> tables = databaseResource.getDatabaseTables(connectionProfile);
					if (tables != null) {
						for (String table : tables) {
							databaseTablesCombo.add(table);
						}
						databaseTablesCombo.setEnabled(true);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		if(!isCreateVo && !isCreateMapper) connectionProfileCombo.setEnabled(false);

		Label templateLabel = new Label(container, SWT.NONE);
		templateLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		templateLabel.setText("Tables: ");

		databaseTablesCombo = new Combo(container, SWT.READ_ONLY);
		databaseTablesCombo.setLayoutData(comboGridData);
		databaseTablesCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTableName = databaseTablesCombo.getText();
				createVoButton.setEnabled(isCreateVo);
				voSuperclassButton.setEnabled(isCreateVo);
				createMapperButton.setEnabled(isCreateMapper);

				if(createVoButton.getEnabled() && createVoButton.getSelection()) {
					voSupperClassBracket1Label.setForeground(new Color(device, 0, 0, 0));
					voSupperClassBracket2Label.setForeground(new Color(device, 0, 0, 0));
					parameterCombo.setEnabled(false);
					returnCombo.setEnabled(false);
					prefixField.setText(databaseTableName);
				} else {
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
		databaseTablesCombo.setEnabled(false);

		GridLayout updateObjectButtonCompositeLayout = new GridLayout(7, false);
		updateObjectButtonCompositeLayout.marginWidth = 0;
		updateObjectButtonCompositeLayout.marginHeight = 0;

		new Label(container, SWT.NONE);
		Composite updateObjectButtonComposite = new Composite(container, SWT.NONE);
		updateObjectButtonComposite.setLayout(updateObjectButtonCompositeLayout);
		updateObjectButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0));

		createServiceButton = new Button(updateObjectButtonComposite, SWT.CHECK);
		createServiceButton.setText("Service");
		createServiceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateService(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createServiceButton.setSelection(isCreateService);

		createDaoButton = new Button(updateObjectButtonComposite, SWT.CHECK);
		createDaoButton.setText("Dao");
		createDaoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateDao(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createDaoButton.setSelection(isCreateDao);

		createMapperButton = new Button(updateObjectButtonComposite, SWT.CHECK);
		createMapperButton.setText("Mapper");
		createMapperButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateMapper(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createMapperButton.setSelection(isCreateMapper);
		createMapperButton.setEnabled(false);

		createVoButton = new Button(updateObjectButtonComposite, SWT.CHECK);
		createVoButton.setText("Vo");
		createVoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateVo(button.getSelection());

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

		voSupperClassBracket1Label = new Label(updateObjectButtonComposite, SWT.NONE);
		voSupperClassBracket1Label.setText("(");
		voSupperClassBracket1Label.setForeground(new Color(device, 175, 174, 175));

		voSuperclassButton = new Button(updateObjectButtonComposite, SWT.CHECK);
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

		voSupperClassBracket2Label = new Label(updateObjectButtonComposite, SWT.NONE);
		voSupperClassBracket2Label.setText(")");
		voSupperClassBracket2Label.setForeground(new Color(device, 175, 174, 175));

		GridLayout createFunctionButtonCompositeLayout = new GridLayout(6, false);
		createFunctionButtonCompositeLayout.marginWidth = 0;
		createFunctionButtonCompositeLayout.marginHeight = 0;

		new Label(container, SWT.NONE);
		Composite createFunctionButtonComposite = new Composite(container, SWT.NONE);
		createFunctionButtonComposite.setLayout(createFunctionButtonCompositeLayout);
		createFunctionButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 0));

		createSelectCountButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createSelectCountButton.setText("Select Count");
		createSelectCountButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateSelectCount(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createSelectCountButton.setSelection(isCreateSelectCount);

		createSelectListButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createSelectListButton.setText("Select List");
		createSelectListButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateSelectList(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createSelectListButton.setSelection(isCreateSelectList);

		createSelectOneButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createSelectOneButton.setText("Select One");
		createSelectOneButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateSelectOne(button.getSelection());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		createSelectOneButton.setSelection(isCreateSelectOne);
		if(!isCreateSelectOne) createSelectOneButton.setEnabled(false);

		createInsertButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createInsertButton.setText("Insert");
		createInsertButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateInsert(button.getSelection());

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
		createInsertButton.setSelection(isCreateInsert);
		if(!isCreateInsert) createInsertButton.setEnabled(false);

		createUpdateButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createUpdateButton.setText("Update");
		createUpdateButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateUpdate(button.getSelection());

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
		createUpdateButton.setSelection(isCreateUpdate);
		if(!isCreateUpdate) createUpdateButton.setEnabled(false);

		createDeleteButton = new Button(createFunctionButtonComposite, SWT.CHECK);
		createDeleteButton.setText("Delete");
		createDeleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateDelete(button.getSelection());

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
		createDeleteButton.setSelection(isCreateDelete);
		if(!isCreateDelete) createDeleteButton.setEnabled(false);

		/* E : DB 연결 */

		GridData layoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, false, 3, 0);
		layoutData.widthHint = 400;

		Label voSuperclassLabel = new Label(container, SWT.NONE);
		voSuperclassLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		voSuperclassLabel.setText("Vo Superclass: ");

		voSuperclassField = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		voSuperclassField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 0));
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
						project,
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
		parameterCombo.select(0);

		Label returnLabel = new Label(container, SWT.NONE);
		returnLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		returnLabel.setText("Return: ");

		returnCombo = new Combo(container, SWT.READ_ONLY);
		returnCombo.setLayoutData(layoutData);
		returnCombo.setItems(objectList.toArray(new String[objectList.size()]));
		returnCombo.select(0);

		Label prefixLabel = new Label(container, SWT.NONE);
		prefixLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		prefixLabel.setText("Prefix: ");

		prefixField = new Text(container, SWT.BORDER);
		prefixField.setLayoutData(layoutData);
		prefixField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				prefix = prefixField.getText();
				okButtonEnabled();

			}
		});

		Label importServiceInterfaceLabel = new Label(container, SWT.NONE);
		importServiceInterfaceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		importServiceInterfaceLabel.setText("Service Interface: ");

		importServiceInterfaceCombo = new Combo(container, SWT.READ_ONLY);
		importServiceInterfaceCombo.setLayoutData(layoutData);
		importServiceInterfaceCombo.setItems(importServiceInterfaces.toArray(new String[importServiceInterfaces.size()]));
		importServiceInterfaceCombo.select(0);

		Label importDaoLabel = new Label(container, SWT.NONE);
		importDaoLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		importDaoLabel.setText("Dao: ");

		importDaoCombo = new Combo(container, SWT.READ_ONLY);
		importDaoCombo.setLayoutData(layoutData);
		importDaoCombo.setItems(importDaos.toArray(new String[importDaos.size()]));
		importDaoCombo.select(0);

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Function Genernator");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 340);
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

		if (!"".equals(prefix)) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	public String getPrefix() {
		return prefix;
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

	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}

	public String getDatabaseTableName() {
		return databaseTableName;
	}

	public void setDatabaseTableName(String databaseTableName) {
		this.databaseTableName = databaseTableName;
	}

	public boolean isCreateSelectCount() {
		return isCreateSelectCount;
	}

	public void setCreateSelectCount(boolean isCreateSelectCount) {
		this.isCreateSelectCount = isCreateSelectCount;
	}

	public boolean isCreateSelectList() {
		return isCreateSelectList;
	}

	public void setCreateSelectList(boolean isCreateSelectList) {
		this.isCreateSelectList = isCreateSelectList;
	}

	public boolean isCreateSelectOne() {
		return isCreateSelectOne;
	}

	public void setCreateSelectOne(boolean isCreateSelectOne) {
		this.isCreateSelectOne = isCreateSelectOne;
	}

	public boolean isCreateInsert() {
		return isCreateInsert;
	}

	public void setCreateInsert(boolean isCreateInsert) {
		this.isCreateInsert = isCreateInsert;
	}

	public boolean isCreateUpdate() {
		return isCreateUpdate;
	}

	public void setCreateUpdate(boolean isCreateUpdate) {
		this.isCreateUpdate = isCreateUpdate;
	}

	public boolean isCreateDelete() {
		return isCreateDelete;
	}

	public void setCreateDelete(boolean isCreateDelete) {
		this.isCreateDelete = isCreateDelete;
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

	public List<String> getImportServiceInterfaces() {
		return importServiceInterfaces;
	}

	public void setImportServiceInterfaces(List<String> importServiceInterfaces) {
		this.importServiceInterfaces = importServiceInterfaces;
	}

	public String getSelectImportServiceInterface() {
		return selectImportServiceInterface;
	}

	public void setSelectImportServiceInterface(String selectImportServiceInterface) {
		this.selectImportServiceInterface = selectImportServiceInterface;
	}

	public List<String> getImportDaos() {
		return importDaos;
	}

	public String getSelectImportDao() {
		return selectImportDao;
	}

	public void setImportDaos(List<String> importDaos) {
		this.importDaos = importDaos;
	}

	public void setSelectImportDao(String selectImportDao) {
		this.selectImportDao = selectImportDao;
	}

	@Override
	protected void okPressed() {

		isCreateService = isCreateService && createServiceButton.isEnabled();
		isCreateDao = isCreateDao && createDaoButton.isEnabled();
		isCreateMapper = isCreateMapper && createMapperButton.isEnabled();
		isCreateVo = isCreateVo && createVoButton.isEnabled();
		isExtendVoSuperclass = isExtendVoSuperclass && voSuperclassButton.isEnabled();

		isCreateSelectCount = isCreateSelectCount && createSelectCountButton.isEnabled();
		isCreateSelectList = isCreateSelectList && createSelectListButton.isEnabled();
		isCreateSelectOne = isCreateSelectOne && createSelectOneButton.isEnabled();
		isCreateInsert = isCreateInsert && createInsertButton.isEnabled();
		isCreateUpdate = isCreateUpdate && createUpdateButton.isEnabled();
		isCreateDelete = isCreateDelete && createDeleteButton.isEnabled();

		parameterType = parameterCombo.getText();
		returnType = returnCombo.getText();

		selectImportServiceInterface = importServiceInterfaceCombo.getText();
		selectImportDao = importDaoCombo.getText();

		voSuperclass = voSuperclassField.getText();

		super.okPressed();
	}

}
