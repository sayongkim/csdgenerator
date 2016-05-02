package kr.pe.maun.csdgenerator.dialogs;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CSDTemplateGeneratorDialog extends Dialog {

	boolean existTemplateFolderName = true;
	private String[] templateFolderNames = null;

	private String name = "";
	private Text nameField;

	private ISelection selection;

	private ICompilationUnit controllerCompilationUnit;

	private Button okButton;

	public CSDTemplateGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	public CSDTemplateGeneratorDialog(Shell parentShell, ISelection selection) {
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
					controllerCompilationUnit = (ICompilationUnit) treePath.getLastSegment();
				}
			}
		}

		IProject project = controllerCompilationUnit.getJavaProject().getProject();
		IFolder templateRootFolder = project.getWorkspace().getRoot().getFolder(new Path("/" + project.getName() + "/template"));

		if(templateRootFolder.exists()) {
			String templateFolderName = "";
			try {
				IResource[] members = templateRootFolder.members();
				for(IResource member : members) {
					if(member instanceof IFolder) {
						if(!"".equals(templateFolderName)) templateFolderName += ",";
						templateFolderName += member.getName();
					}
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			templateFolderNames = templateFolderName.split(",");
		}

		container.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		nameLabel.setText("Name: ");

		nameField = new Text(container, SWT.BORDER);
		nameField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				existTemplateFolderName = false;
				if(templateFolderNames != null) {
					for(String templateFolderName : templateFolderNames) {
						if(templateFolderName.equals(nameField.getText())) existTemplateFolderName = true;
					}
				}
				if(!existTemplateFolderName) name = nameField.getText();
				okButtonEnabled();
			}
		});

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Template Genernator");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(300, 110);
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

		if (!existTemplateFolderName && !"".equals(name.trim())) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	public String getName() {
		return name;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

}
