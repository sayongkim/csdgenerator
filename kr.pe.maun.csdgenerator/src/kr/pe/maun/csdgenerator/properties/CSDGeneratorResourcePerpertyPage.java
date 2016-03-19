package kr.pe.maun.csdgenerator.properties;

import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;

public class CSDGeneratorResourcePerpertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private Text company;
	private Text author;

	private Text controllerTemplateFileName;
	private Button controllerTemplateFileButton;

	private Text serviceTemplateFileName;
	private Button serviceTemplateFileButton;

	private Text serviceImplTemplateFileName;
	private Button serviceImplTemplateFileButton;

	private Text daoTemplateFileName;
	private Button daoTemplateFileButton;

	private Button specificSettings;

	private Button createControllerFolder;
	private Button addPrefixControllerFolder;

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

	@Override
	protected Control createContents(Composite parent) {

		boolean isSpecificSettings = isSpecificSettings();

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout defalutLayout = new GridLayout(3, false);
		defalutLayout.marginHeight = 0;
		defalutLayout.marginWidth = 0;
		panel.setLayout(defalutLayout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.heightHint = 16;
		gridData.horizontalSpan = 3;

		GridData specificGridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		specificGridData.horizontalSpan = 2;

		GridData textGridData = new GridData();
		textGridData.horizontalAlignment = GridData.FILL;
		textGridData.verticalAlignment = GridData.FILL;
		textGridData.grabExcessHorizontalSpace = false;
		textGridData.grabExcessVerticalSpace = false;
		textGridData.heightHint = 16;
		textGridData.horizontalSpan = 2;

		specificSettings = new Button(panel, SWT.CHECK);
		specificSettings.setText("Enable project specific settings");
		/*specificSettings.setLayoutData(new GridData(GridData.FILL));*/
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
		link.setLayoutData(specificGridData);
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
	    separator.setLayoutData(gridData);

		Label companyLabel = new Label(panel, SWT.NONE);
		companyLabel.setText("Company:");

		company = new Text(panel, SWT.FILL | SWT.BORDER);
		company.setLayoutData(textGridData);
		if(!isSpecificSettings) company.setEnabled(false);

		Label authorLabel = new Label(panel, SWT.NONE);
		authorLabel.setText("Author:");

		author = new Text(panel, SWT.FILL | SWT.BORDER);
		author.setLayoutData(textGridData);
		if(!isSpecificSettings) author.setEnabled(false);

		createControllerFolder = new Button(panel, SWT.CHECK);
		createControllerFolder.setText("Create controller folder");
		createControllerFolder.setLayoutData(gridData);
		if(!isSpecificSettings) createControllerFolder.setEnabled(false);
		createControllerFolder.addSelectionListener(new SelectionListener() {
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
		createControllerFolder.setSelection(isCreateControllerFolder());

		addPrefixControllerFolder = new Button(panel, SWT.CHECK);
		addPrefixControllerFolder.setText("Add prefix controller folder name");
		addPrefixControllerFolder.setLayoutData(gridData);
		if(!isSpecificSettings || !isCreateControllerFolder()) addPrefixControllerFolder.setEnabled(false);
		addPrefixControllerFolder.setSelection(isAddPrefixControllerFolder());

		/* S : select controller template file */
		Label controllerTemplateFileNameLabel = new Label(panel, SWT.NONE);
		controllerTemplateFileNameLabel.setText("Controller template file:");
		controllerTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		controllerTemplateFileName = new Text(panel, SWT.SINGLE | SWT.BORDER);
		controllerTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) controllerTemplateFileName.setEnabled(false);
		controllerTemplateFileName.setText(getControllerTemplateFile());

		controllerTemplateFileButton = new Button(panel, SWT.PUSH);
		controllerTemplateFileButton.setText("Browse...");
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

		createServiceFolder = new Button(panel, SWT.CHECK);
		createServiceFolder.setText("Create service folder");
		createServiceFolder.setLayoutData(gridData);
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

		addPrefixServiceFolder = new Button(panel, SWT.CHECK);
		addPrefixServiceFolder.setText("Add prefix service folder name");
		addPrefixServiceFolder.setLayoutData(gridData);
		if(!isSpecificSettings || !isCreateServiceFolder()) addPrefixServiceFolder.setEnabled(false);
		addPrefixServiceFolder.setSelection(isAddPrefixServiceFolder());

		/* S : select service template file */
		Label serviceTemplateFileNameLabel = new Label(panel, SWT.NONE);
		serviceTemplateFileNameLabel.setText("Service template file:");
		serviceTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		serviceTemplateFileName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		serviceTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) serviceTemplateFileName.setEnabled(false);
		serviceTemplateFileName.setText(getServiceImplTemplateFile());

		serviceTemplateFileButton = new Button(panel, SWT.PUSH);
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

		createServiceImpl = new Button(panel, SWT.CHECK);
		createServiceImpl.setText("Create ServiceImpl");
		createServiceImpl.setLayoutData(gridData);
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

		createServiceImplFolder = new Button(panel, SWT.CHECK);
		createServiceImplFolder.setText("Create ServiceImpl folder");
		createServiceImplFolder.setLayoutData(gridData);
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
		Label serviceImplTemplateFileNameLabel = new Label(panel, SWT.NONE);
		serviceImplTemplateFileNameLabel.setText("ServiceImpl template file:");
		serviceImplTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		serviceImplTemplateFileName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		serviceImplTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings || !isCreateServiceImpl()) serviceImplTemplateFileName.setEnabled(false);
		serviceImplTemplateFileName.setText(getServiceImplTemplateFile());

		serviceImplTemplateFileButton = new Button(panel, SWT.PUSH);
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

		createDaoFolder = new Button(panel, SWT.CHECK);
		createDaoFolder.setText("Create dao folder");
		createDaoFolder.setLayoutData(gridData);
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

		addPrefixDaoFolder = new Button(panel, SWT.CHECK);
		addPrefixDaoFolder.setText("Add prefix dao folder name");
		addPrefixDaoFolder.setLayoutData(gridData);
		if(!isSpecificSettings || !isCreateDaoFolder()) addPrefixDaoFolder.setEnabled(false);
		addPrefixDaoFolder.setSelection(isAddPrefixDaoFolder());

		/* S : select dao template file */
		Label daoTemplateFileNameLabel = new Label(panel, SWT.NONE);
		daoTemplateFileNameLabel.setText("Dao template file:");
		daoTemplateFileNameLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		daoTemplateFileName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		daoTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) daoTemplateFileName.setEnabled(false);
		daoTemplateFileName.setText(getDaoTemplateFile());

		daoTemplateFileButton = new Button(panel, SWT.PUSH);
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

		createMapper = new Button(panel, SWT.CHECK);
		createMapper.setText("Create mapper");
		createMapper.setLayoutData(gridData);
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
		Label mapperPathLabel = new Label(panel, SWT.NONE);
		mapperPathLabel.setText("Mapper Path:");
		mapperPathLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		mapperPathName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		mapperPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) mapperPathName.setEnabled(false);
		mapperPathName.setText(getMapperPath());

		mapperPathButton = new Button(panel, SWT.PUSH);
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
		Label mapperTemplateFileLabel = new Label(panel, SWT.NONE);
		mapperTemplateFileLabel.setText("Mapper template file:");
		mapperTemplateFileLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		mapperTemplateFileName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		mapperTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) mapperTemplateFileName.setEnabled(false);
		mapperTemplateFileName.setText(getMapperTemplateFile());

		mapperTemplateFileButton = new Button(panel, SWT.PUSH);
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

		createJsp = new Button(panel, SWT.CHECK);
		createJsp.setText("Create jsp");
		createJsp.setLayoutData(gridData);
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
		Label jspPathLabel = new Label(panel, SWT.NONE);
		jspPathLabel.setText("Jsp Path:");
		jspPathLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		jspPathName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		jspPathName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) jspPathName.setEnabled(false);
		jspPathName.setText(getJspPath());

		jspPathButton = new Button(panel, SWT.PUSH);
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
		Label jspTemplateFileLabel = new Label(panel, SWT.NONE);
		jspTemplateFileLabel.setText("Jsp template file:");
		jspTemplateFileLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		jspTemplateFileName=new Text(panel, SWT.SINGLE | SWT.BORDER);
		jspTemplateFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if(!isSpecificSettings) jspTemplateFileName.setEnabled(false);
		jspTemplateFileName.setText(getJspTemplateFile());

		jspTemplateFileButton = new Button(panel, SWT.PUSH);
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

		return panel;
	}


	public boolean performOk() {

		setSpecificSettings(specificSettings.getSelection());

		setCompany(company.getText());
		setAuthor(author.getText());

		setCreateControllerFolder(createControllerFolder.getSelection());
		setAddPrefixControllerFolder(addPrefixControllerFolder.getSelection());
		setControllerTemplateFile(controllerTemplateFileName.getText());

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
		addPrefixControllerFolder.setEnabled(true);
	}

	private void setDisabledAddPrefixControllerFolder() {
		addPrefixControllerFolder.setEnabled(false);
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

		company.setEnabled(true);
		author.setEnabled(true);

		createControllerFolder.setEnabled(true);
		if(createControllerFolder.getSelection()) addPrefixControllerFolder.setEnabled(true);
		controllerTemplateFileName.setEnabled(true);
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

		company.setEnabled(false);
		author.setEnabled(false);

		addPrefixControllerFolder.setEnabled(false);
		controllerTemplateFileName.setEnabled(false);
		controllerTemplateFileButton.setEnabled(false);

		addPrefixServiceFolder.setEnabled(false);
		serviceTemplateFileName.setEnabled(false);
		serviceTemplateFileButton.setEnabled(false);

		serviceImplTemplateFileName.setEnabled(false);
		serviceImplTemplateFileButton.setEnabled(false);

		addPrefixDaoFolder.setEnabled(false);
		daoTemplateFileName.setEnabled(false);
		daoTemplateFileButton.setEnabled(false);

		createControllerFolder.setEnabled(false);

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
