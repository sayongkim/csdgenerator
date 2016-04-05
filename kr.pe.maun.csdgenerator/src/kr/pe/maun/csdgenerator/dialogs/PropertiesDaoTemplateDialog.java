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

public class PropertiesDaoTemplateDialog extends Dialog {

	private String[] templateNames;

	private Text templateNameField;

	private Text templateFileField;
	private Button templateFileButton;

	private String templateName;
	private String templateFile;

	private Button okButton;

	public PropertiesDaoTemplateDialog(Shell parentShell) {
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
		if (templateName != null) {
			templateNameField.setText(templateName);
			templateNameField.setEditable(false);
		} else {
			templateNameField.setEditable(true);
		}
		templateNameField.setBackground(new Color(device, 255, 255, 255));

		Label templateFileLabel = new Label(container, SWT.NONE);
		templateFileLabel.setText("Template File:");

		templateFileField = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		templateFileField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		templateFileField.setBackground(new Color(device, 255, 255, 255));
		if (templateFile != null)
			templateFileField.setText(templateFile);

		templateFileButton = new Button(container, SWT.PUSH);
		templateFileButton.setText("Browse...");
		templateFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				String fileName = dialog.open();
				if (fileName != null) {
					templateFileField.setText(fileName);
					templateFile = fileName;
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
		newShell.setText("Dao Template");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 140);
	}

	protected void okButtonEnabled() {
		if (okButton == null)
			return;

		if (templateName != null && !"".equals(templateName)
				&& templateFile != null && !"".equals(templateFile)) {
			okButton.setEnabled(true);
			if(templateNames != null && templateNameField.isEnabled()) {
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

	public String getTemplateName() {
		return templateName;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public String[] getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(String[] templateNames) {
		this.templateNames = templateNames;
	}

}
