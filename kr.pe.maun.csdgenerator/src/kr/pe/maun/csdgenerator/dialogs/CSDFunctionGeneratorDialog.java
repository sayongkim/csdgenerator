package kr.pe.maun.csdgenerator.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragmentRoot;
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

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;
import kr.pe.maun.csdgenerator.properties.CSDGeneratorPropertiesHelper;

public class CSDFunctionGeneratorDialog extends Dialog {

	private CSDGeneratorPropertiesItem propertiesItem;
	private CSDGeneratorPropertiesHelper propertiesHelper;

	private String prefix = "";
	private Text prefixField;
/*
	private String regExGroup = "";
	private Text regExGroupField;
*/
	private boolean isCreateService = true;
	private boolean isCreateDao = true;
	private boolean isCreateMapper = true;
	private boolean isCreateVo = true;
	private boolean isCreateSearchVo = true;

	private boolean isCreateSelectCount = true;
	private boolean isCreateSelectList = true;
	private boolean isCreateSelectOne = true;
	private boolean isCreateInsert = true;
	private boolean isCreateUpdate = true;
	private boolean isCreateDelete = true;

	String mapperPath;
	String voPath;

	private IConnectionProfile connectionProfile;

	private ISelection selection;

	private Combo databaseTablesCombo;
	private Combo connectionProfileCombo;
	private Combo parameterCombo;
	private Combo returnCombo;

	private Button createServiceButton;
	private Button createDaoButton;
	private Button createMapperButton;
	private Button createVoButton;

	private Button createSelectCountButton;
	private Button createSelectListButton;
	private Button createSelectOneButton;
	private Button createInsertButton;
	private Button createUpdateButton;
	private Button createDeleteButton;

	private IConnectionProfile[] connectionProfiles;
	private DatabaseResource databaseResource;

	private String databaseTable;

	private ICompilationUnit compilationUnit;

	private Button okButton;

	public CSDFunctionGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	public CSDFunctionGeneratorDialog(Shell parentShell, ISelection selection) {
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
					compilationUnit = (ICompilationUnit) treePath.getLastSegment();
				}
			}
		}

		IProject project = compilationUnit.getJavaProject().getProject();

		propertiesItem = new CSDGeneratorPropertiesItem((IResource) project.getAdapter(IResource.class));
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		mapperPath = propertiesItem.getMapperPath();
		voPath = propertiesItem.getVoPath();

		isCreateMapper = propertiesItem.getCreateMapper() && mapperPath != null && !"".equals(mapperPath);
		isCreateVo = propertiesItem.getCreateVo() && voPath != null && !"".equals(voPath);
		isCreateSearchVo = propertiesItem.getCreateSearchVo();

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
				databaseTable = null;
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

		Label templateLabel = new Label(container, SWT.NONE);
		templateLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		templateLabel.setText("Tables: ");

		databaseTablesCombo = new Combo(container, SWT.READ_ONLY);
		databaseTablesCombo.setLayoutData(comboGridData);
		databaseTablesCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				databaseTable = databaseTablesCombo.getText();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		GridLayout updateObjectButtonCompositeLayout = new GridLayout(4, false);
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
		if(!isCreateMapper) createMapperButton.setEnabled(false);

		createVoButton = new Button(updateObjectButtonComposite, SWT.CHECK);
		createVoButton.setText("Vo");
		createVoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.widget;
				setCreateVo(button.getSelection());

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
				setCreateService(button.getSelection());

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
				okButtonEnabled();

			}
		});

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Genernator");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 240);
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

	public IConnectionProfile getConnectionProfile() {
		return connectionProfile;
	}

	public void setConnectionProfile(IConnectionProfile connectionProfile) {
		this.connectionProfile = connectionProfile;
	}

	public String getDatabaseTable() {
		return databaseTable;
	}

	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
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

	@Override
	protected void okPressed() {
		isCreateService = isCreateService && createServiceButton.isEnabled();
		isCreateDao = isCreateDao && createDaoButton.isEnabled();
		isCreateMapper = isCreateMapper && createMapperButton.isEnabled();

		isCreateSelectCount = isCreateSelectCount && createSelectCountButton.isEnabled();
		isCreateSelectList = isCreateSelectList && createSelectListButton.isEnabled();
		isCreateSelectOne = isCreateSelectList && createSelectOneButton.isEnabled();
		isCreateInsert = isCreateInsert && createInsertButton.isEnabled();
		isCreateUpdate = isCreateUpdate && createUpdateButton.isEnabled();
		isCreateDelete = isCreateDelete && createDeleteButton.isEnabled();

		super.okPressed();
	}

}
