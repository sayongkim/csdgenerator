package kr.pe.maun.csdgenerator.dialogs;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PropertiesJspTemplateDialog extends Dialog {

	private String[] templateNames;

	private Text templateNameField;

	private Text templateListFileField;
	private Button templateListFileButton;
	private Text templatePostFileField;
	private Button templatePostFileButton;
	private Text templateViewFileField;
	private Button templateViewFileButton;

	private String templateName;
	private String templateListFile = "";
	private String templatePostFile = "";
	private String templateViewFile = "";

	private Button okButton;

	public PropertiesJspTemplateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Device device = Display.getCurrent();

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));

		Label templateNameLabel = new Label(container, SWT.NONE);
		templateNameLabel.setText("Template Name:");

		templateNameField = new Text(container, SWT.SINGLE | SWT.BORDER);
		templateNameField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 0));
		templateNameField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				templateName = templateNameField.getText();
				okButtonEnabled();
			}
		});

		if(templateName != null) {
			templateNameField.setText(templateName);
			templateNameField.setEditable(false);
			templateNameField.setBackground(new Color(device, 255, 255, 255));
		}

		Label templateListFileLabel = new Label(container, SWT.NONE);
		templateListFileLabel.setText("List Template File:");

		templateListFileField = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		templateListFileField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		templateListFileField.setBackground(new Color(device, 255, 255, 255));
		if (templateListFile != null)
			templateListFileField.setText(templateListFile);

		templateListFileButton = new Button(container, SWT.PUSH);
		templateListFileButton.setText("Browse...");
		templateListFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String fileName = dialog.open();
				if (fileName != null) {
					templateListFileField.setText(fileName);
					templateListFile = fileName;
					okButtonEnabled();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label templatePostFileLabel = new Label(container, SWT.NONE);
		templatePostFileLabel.setText("Post Template File:");

		templatePostFileField = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		templatePostFileField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		templatePostFileField.setBackground(new Color(device, 255, 255, 255));
		if (templatePostFile != null)
			templatePostFileField.setText(templatePostFile);

		templatePostFileButton = new Button(container, SWT.PUSH);
		templatePostFileButton.setText("Browse...");
		templatePostFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String fileName = dialog.open();
				if (fileName != null) {
					templatePostFileField.setText(fileName);
					templatePostFile = fileName;
					okButtonEnabled();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label templateViewFileLabel = new Label(container, SWT.NONE);
		templateViewFileLabel.setText("View Template File:");

		templateViewFileField = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		templateViewFileField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		templateViewFileField.setBackground(new Color(device, 255, 255, 255));
		if (templateViewFile != null)
			templateViewFileField.setText(templateViewFile);

		templateViewFileButton = new Button(container, SWT.PUSH);
		templateViewFileButton.setText("Browse...");
		templateViewFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String fileName = dialog.open();
				if (fileName != null) {
					templateViewFileField.setText(fileName);
					templateViewFile = fileName;
					okButtonEnabled();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

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
		newShell.setText("JSP Template");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 280);
	}

	protected void okButtonEnabled() {
		if (okButton == null)
			return;

		if (templateName != null && !"".equals(templateName)) {
			okButton.setEnabled(true);
			if(templateNames != null && templateNameField.getEditable()) {
				for(String name : templateNames) {
					if(name.equals(templateName)) {
						okButton.setEnabled(false);
						return;
					}
				}
			}
		} else {
			okButton.setEnabled(false);
		}
	}

	public String[] getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(String[] templateNames) {
		this.templateNames = templateNames;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateListFile() {
		return templateListFile;
	}

	public void setTemplateListFile(String templateListFile) {
		this.templateListFile = templateListFile;
	}

	public String getTemplatePostFile() {
		return templatePostFile;
	}

	public void setTemplatePostFile(String templatePostFile) {
		this.templatePostFile = templatePostFile;
	}

	public String getTemplateViewFile() {
		return templateViewFile;
	}

	public void setTemplateViewFile(String templateViewFile) {
		this.templateViewFile = templateViewFile;
	}

}
