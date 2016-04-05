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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PropertiesDataTypeMappingDialog extends Dialog {

	private final String[] javaObjects = {"String", "BigDecimal", "Date", "Timestamp", "boolean", "char", "byte", "short", "int", "float", "double"};

	private String[] dataTypes;

	private Text dataTypeField;
	private Combo javaObjectCombo;

	private String dataType;
	private String javaObject;

	private Button okButton;

	public PropertiesDataTypeMappingDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Device device = Display.getCurrent();

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		Label dataTypeLabel = new Label(container, SWT.NONE);
		dataTypeLabel.setText("Data Type:");

		dataTypeField = new Text(container, SWT.SINGLE | SWT.BORDER);
		dataTypeField.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		dataTypeField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dataType = dataTypeField.getText();
				okButtonEnabled();
			}
		});

		if (dataType != null) {
			dataTypeField.setText(dataType);
			dataTypeField.setEditable(false);
			dataTypeField.setBackground(new Color(device, 255, 255, 255));
		}

		Label javaObjectLabel = new Label(container, SWT.NONE);
		javaObjectLabel.setText("Java Object:");

		javaObjectCombo = new Combo(container, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		javaObjectCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		javaObjectCombo.setItems(javaObjects);
		javaObjectCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				javaObject = javaObjectCombo.getText();
				okButtonEnabled();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if(javaObject != null && !"".equals(javaObject)) javaObjectCombo.select(javaObjectCombo.indexOf(javaObject));

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
		newShell.setText("Mapper Template");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 140);
	}

	protected void okButtonEnabled() {
		if (okButton == null)
			return;

		if (dataType != null && !"".equals(dataType) && javaObject != null && !"".equals(javaObject)) {
			okButton.setEnabled(true);
			if(dataTypes != null && dataTypeField.getEditable()) {
				for(String name : dataTypes) {
					if(name.equals(dataType)) {
						okButton.setEnabled(false);
						return;
					}
				}
			}
		} else {
			okButton.setEnabled(false);
		}
	}

	public String[] getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(String[] dataTypes) {
		this.dataTypes = dataTypes;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getJavaObject() {
		return javaObject;
	}

	public void setJavaObject(String javaObject) {
		this.javaObject = javaObject;
	}

}
