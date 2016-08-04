package kr.pe.maun.csdgenerator.dialogs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public class PropertiesTestTemplateDialog extends Dialog {

	private String[] templateGroupNames;
	private String[] controllerTemplateNames;
	private String[] serviceTemplateNames;
	private String[] daoTemplateNames;

	private Text templateGroupNameField;

	private Text templatePathField;
	private Button templatePathButton;

	private IPath templatePath;

	private String templateGroupName;
	private String controllerTemplateName = "";
	private String serviceTemplateName = "";
	private String daoTemplateName = "";

	Combo controllerTempateNameCombo;
	Combo serviceTempateNameCombo;
	Combo daoTempateNameCombo;

	private Button okButton;

	public PropertiesTestTemplateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Device device = Display.getCurrent();

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Label templateGroupNameLabel = new Label(container, SWT.NONE);
		templateGroupNameLabel.setText("Template Group Name:");

		templateGroupNameField = new Text(container, SWT.SINGLE | SWT.BORDER);
		templateGroupNameField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 0));
		templateGroupNameField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				templateGroupName = templateGroupNameField.getText();
				okButtonEnabled();
			}
		});

		if(templateGroupName != null) {
			templateGroupNameField.setText(templateGroupName);
			templateGroupNameField.setEditable(false);
			templateGroupNameField.setBackground(new Color(device, 255, 255, 255));
		} else {
			Label templateFolderLabel = new Label(container, SWT.NONE);
			templateFolderLabel.setText("Template Folder:");
			templateFolderLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

			templatePathField=new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			templatePathField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			templatePathField.setBackground(new Color(device, 255, 255, 255));

			templatePathButton = new Button(container, SWT.PUSH);
			templatePathButton.setText("Browse...");
			templatePathButton.setLayoutData(new GridData(100, 20));
			templatePathButton.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), null, false, "Template Selection:");
						dialog.open();
						Object[] result=dialog.getResult();
						if (result != null && result.length == 1) {
							String templatePathString = ((IPath) result[0]).toString();
							templatePathField.setText(templatePathString);
							templateGroupNameField.setText(templatePathString.substring(templatePathString.lastIndexOf("/") + 1));
							templatePath = (IPath) result[0];

							controllerTempateNameCombo.setEnabled(false);
							serviceTempateNameCombo.setEnabled(false);
							daoTempateNameCombo.setEnabled(false);
						}
					}
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				}
			);
		}

		Label controllerTemplateNameLabel = new Label(container, SWT.NONE);
		controllerTemplateNameLabel.setText("Controller Template:");

		controllerTempateNameCombo = new Combo(container, SWT.READ_ONLY);
		controllerTempateNameCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 0));
		controllerTempateNameCombo.setItems(controllerTemplateNames);
		controllerTempateNameCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controllerTemplateName = controllerTempateNameCombo.getText();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if(!"".equals(controllerTemplateName)) controllerTempateNameCombo.select(controllerTempateNameCombo.indexOf(controllerTemplateName));

		Label serviceTemplateNameLabel = new Label(container, SWT.NONE);
		serviceTemplateNameLabel.setText("Service Template:");

		serviceTempateNameCombo = new Combo(container, SWT.READ_ONLY);
		serviceTempateNameCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 0));
		serviceTempateNameCombo.setItems(serviceTemplateNames);
		serviceTempateNameCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceTemplateName = serviceTempateNameCombo.getText();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if(!"".equals(serviceTemplateName)) serviceTempateNameCombo.select(serviceTempateNameCombo.indexOf(serviceTemplateName));

		Label daoTemplateNameLabel = new Label(container, SWT.NONE);
		daoTemplateNameLabel.setText("Dao Template:");

		daoTempateNameCombo = new Combo(container, SWT.READ_ONLY);
		daoTempateNameCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 0));
		daoTempateNameCombo.setItems(daoTemplateNames);
		daoTempateNameCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				daoTemplateName = daoTempateNameCombo.getText();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if(!"".equals(daoTemplateName)) daoTempateNameCombo.select(daoTempateNameCombo.indexOf(daoTemplateName));

		return container;
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

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Test Template");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 230);
	}

	protected void okButtonEnabled() {
		if (okButton == null)
			return;

		if (templateGroupName != null && !"".equals(templateGroupName)) {
			okButton.setEnabled(true);
			if(templateGroupNames != null && templateGroupNameField.getEditable()) {
				for(String name : templateGroupNames) {
					if(name.equals(templateGroupName)) {
						okButton.setEnabled(false);
						return;
					}
				}
			}
		} else {
			okButton.setEnabled(false);
		}
	}

	public String[] getTemplateGroupNames() {
		return templateGroupNames;
	}

	public String[] getControllerTemplateNames() {
		return controllerTemplateNames;
	}

	public String[] getServiceTemplateNames() {
		return serviceTemplateNames;
	}

	public String[] getDaoTemplateNames() {
		return daoTemplateNames;
	}

	public String getTemplateGroupName() {
		return templateGroupName;
	}

	public String getControllerTemplateName() {
		return controllerTemplateName;
	}

	public String getServiceTemplateName() {
		return serviceTemplateName;
	}

	public String getDaoTemplateName() {
		return daoTemplateName;
	}

	public void setTemplateGroupNames(String[] templateGroupNames) {
		this.templateGroupNames = templateGroupNames;
	}

	public void setControllerTemplateNames(String[] controllerTemplateNames) {
		this.controllerTemplateNames = controllerTemplateNames;
	}

	public void setServiceTemplateNames(String[] serviceTemplateNames) {
		this.serviceTemplateNames = serviceTemplateNames;
	}

	public void setDaoTemplateNames(String[] daoTemplateNames) {
		this.daoTemplateNames = daoTemplateNames;
	}

	public void setTemplateGroupName(String templateGroupName) {
		this.templateGroupName = templateGroupName;
	}

	public void setControllerTemplateName(String controllerTemplateName) {
		this.controllerTemplateName = controllerTemplateName;
	}

	public void setServiceTemplateName(String serviceTemplateName) {
		this.serviceTemplateName = serviceTemplateName;
	}

	public void setDaoTemplateName(String daoTemplateName) {
		this.daoTemplateName = daoTemplateName;
	}

	public IPath getTemplatePath() {
		return templatePath;
	}

}
