package kr.pe.maun.csdgenerator.dialogs;

import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class CSDGeneratorDialog extends Dialog {

	private String prefix = "";
	private Text prefixField;

	private boolean isParentLocation = false;
	private boolean isCreateFolder = true;

	private ISelection selection;

	Button createParentLocation;
	Button createFolder;
	private Tree tree;

	IPackageFragment packageFragment;
	IResource resource;

	public CSDGeneratorDialog(Shell parentShell) {
		super(parentShell);
	}

	public CSDGeneratorDialog(Shell parentShell, ISelection selection) {
		super(parentShell);
		this.selection = selection;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		gridData.horizontalSpan = 2;

		createParentLocation = new Button(container, SWT.CHECK);
		createParentLocation.setText("Create parent location");
		createParentLocation.setLayoutData(gridData);
		createParentLocation.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					setParentLocation(button.getSelection());
					createTree();
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);

		createFolder = new Button(container, SWT.CHECK);
		createFolder.setText("Create folder");
		createFolder.setLayoutData(gridData);
		createFolder.addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					setCreateFolder(button.getSelection());
					createTree();
				}
				@Override public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			}
		);
		createFolder.setSelection(this.isCreateFolder);

		Label prefixLabel = new Label(container, SWT.NONE);
		prefixLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		prefixLabel.setText("Prefix: ");

		prefixField = new Text(container, SWT.BORDER);
		prefixField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		prefixField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				prefix = prefixField.getText();
				createTree();
			}
		});

		GridData treeGridData = new GridData();
		treeGridData.horizontalAlignment = GridData.FILL;
		treeGridData.verticalAlignment = GridData.FILL;
		treeGridData.grabExcessHorizontalSpace = false;
		treeGridData.grabExcessVerticalSpace = false;
		treeGridData.horizontalSpan = 2;
		/*treeGridData.heightHint = 120;*/

		if(selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			TreePath[] treePaths = treeSelection.getPaths();
			for(TreePath treePath : treePaths) {
				packageFragment = (IPackageFragment) treePath.getLastSegment();
			}
		}

		resource = (IResource) packageFragment.getJavaProject().getProject().getAdapter(IResource.class);

		tree = new Tree(container, SWT.MULTI | SWT.BORDER);
		tree.setLayoutData(treeGridData);
		createTree();

		return container;
	}

	private void createTree() {

		tree.removeAll();

		CSDGeneratorPropertiesItem propertiesItem = new CSDGeneratorPropertiesItem(resource);

		boolean isCreateControllerFolder = propertiesItem.getCreateControllerFolder();
		boolean isAddPrefixControllerFolder = propertiesItem.getAddPrefixControllerFolder();

		boolean isCreateServiceFolder = propertiesItem.getCreateServiceFolder();
		boolean isAddPrefixServiceFolder = propertiesItem.getAddPrefixServiceFolder();

		boolean isCreateServiceImpl = propertiesItem.getCreateServiceImpl();
		boolean isCreateServiceImplFolder = propertiesItem.getCreateServiceImplFolder();

		boolean isCreateDaoFolder = propertiesItem.getCreateDaoFolder();
		boolean isAddPrefixDaoFolder = propertiesItem.getAddPrefixDaoFolder();

		String packagePath = packageFragment.getElementName();

		String upperPrefix = "";

		if(prefix.length() > 1) upperPrefix = prefix.substring(0, 1).toUpperCase() + prefix.substring(1, prefix.length());
		else if(prefix.length() == 1) upperPrefix = prefix.substring(0, 1).toUpperCase();

		if(isParentLocation) {
			packagePath = packagePath.substring(0, packagePath.lastIndexOf("."));
		}

		if(isCreateFolder && !"".equals(prefix)) packagePath = packagePath + "." + prefix;

		TreeItem packageTreeItem = new TreeItem(tree, 0);
		packageTreeItem.setText(packagePath);

		if(isCreateControllerFolder) {

			String controllerFolder = "";

			if(isAddPrefixControllerFolder) {
				controllerFolder += upperPrefix;
			}

			controllerFolder += "Controller";

			TreeItem controllerFolderTreeItem = new TreeItem(packageTreeItem, 1);
			controllerFolderTreeItem.setText(controllerFolder);

			TreeItem controllerJavaTreeItem = new TreeItem(controllerFolderTreeItem, SWT.NONE);
			controllerJavaTreeItem.setText(upperPrefix + "Controller.java");

			controllerFolderTreeItem.setExpanded(true);

		} else {
			TreeItem controllerJavaTreeItem = new TreeItem(packageTreeItem, SWT.NONE);
			controllerJavaTreeItem.setText(upperPrefix + "Controller.java");
		}

		if(isCreateServiceFolder) {

			String serviceFolder = "";

			if(isAddPrefixServiceFolder) {
				serviceFolder += upperPrefix;
			}

			serviceFolder += "Service";

			TreeItem serviceFolderTreeItem = new TreeItem(packageTreeItem, 1);
			serviceFolderTreeItem.setText(serviceFolder);

			TreeItem serviceJavaTreeItem = new TreeItem(serviceFolderTreeItem, SWT.NONE);
			serviceJavaTreeItem.setText(upperPrefix + "Service.java");

			if(isCreateServiceImpl) {
				TreeItem serviceImplFolderTreeItem = null;
				if(isCreateServiceImplFolder) {
					String serviceImplFolder = "Impl";

					serviceImplFolderTreeItem = new TreeItem(serviceFolderTreeItem, 1);
					serviceImplFolderTreeItem.setText(serviceImplFolder);
				} else {
					serviceImplFolderTreeItem = serviceFolderTreeItem;
				}
				TreeItem serviceImplJavaTreeItem = new TreeItem(serviceImplFolderTreeItem, SWT.NONE);
				serviceImplJavaTreeItem.setText(upperPrefix + "ServiceImpl.java");
				serviceImplFolderTreeItem.setExpanded(true);
			}

			serviceFolderTreeItem.setExpanded(true);

		} else {
			TreeItem controllerJavaTreeItem = new TreeItem(packageTreeItem, SWT.NONE);
			controllerJavaTreeItem.setText(upperPrefix + "Service.java");
		}

		if(isCreateDaoFolder) {

			String daoFolder = "";

			if(isAddPrefixDaoFolder) {
				daoFolder += upperPrefix;
			}

			daoFolder += "Dao";

			TreeItem daoFolderTreeItem = new TreeItem(packageTreeItem, 1);
			daoFolderTreeItem.setText(daoFolder);

			TreeItem daoJavaTreeItem = new TreeItem(daoFolderTreeItem, SWT.NONE);
			daoJavaTreeItem.setText(upperPrefix + "Dao.java");

			daoFolderTreeItem.setExpanded(true);

		} else {
			TreeItem daoJavaTreeItem = new TreeItem(packageTreeItem, SWT.NONE);
			daoJavaTreeItem.setText(upperPrefix + "Dao.java");
		}

		packageTreeItem.setExpanded(true);

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("CSD Genernator");
	}
	@Override
	protected Point getInitialSize() {
		return new Point(500, 400);
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

}
