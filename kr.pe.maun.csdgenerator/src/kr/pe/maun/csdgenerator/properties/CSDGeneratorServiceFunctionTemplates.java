package kr.pe.maun.csdgenerator.properties;

import kr.pe.maun.csdgenerator.CSDGeneratorPlugin;
import kr.pe.maun.csdgenerator.db.DatabaseResource;
import kr.pe.maun.csdgenerator.model.CSDGeneratorPropertiesItem;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class CSDGeneratorServiceFunctionTemplates extends PropertyPage implements
		IWorkbenchPropertyPage {

	private CSDGeneratorPropertiesHelper propertiesHelper;

	IProject project;

	StyledText selectCountStyledText;
	StyledText selectListStyledText;
	StyledText selectOneStyledText;
	StyledText insertStyledText;
	StyledText updateStyledText;
	StyledText deleteStyledText;

	@Override
	protected Control createContents(Composite parent) {

		Device device = Display.getCurrent();

		project = (IProject) getElement().getAdapter(IProject.class);
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout defalutLayout = new GridLayout(1, false);
		defalutLayout.marginHeight = 0;
		defalutLayout.marginWidth = 0;
		panel.setLayout(defalutLayout);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.widthHint = 300;
		layoutData.heightHint = 300;

		panel.setLayoutData(layoutData);
/*
		Label tempateLabel = new Label(panel, SWT.NONE);
		tempateLabel.setText("Template:");
		tempateLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
*/
	    TabFolder tabFolder = new TabFolder(panel, SWT.NONE);
	    tabFolder.setLayoutData(layoutData);

/* S : Select Count Tab Folder */

	    TabItem selectCountTab = new TabItem(tabFolder, SWT.NONE);
	    selectCountTab.setText("Select Count");

	    Composite selectCountComposite = new Composite(tabFolder, SWT.NULL);
	    selectCountComposite.setLayout(new GridLayout(1, false));
	    selectCountComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    selectCountStyledText = new StyledText(selectCountComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    selectCountStyledText.setLayoutData(layoutData);
	    selectCountStyledText.setText(propertiesHelper.getServiceFunctionSelectCountTemplate());

		selectCountTab.setControl(selectCountComposite);

/* E : Select Count Tab Folder */
/* S : Select List Tab Folder */

	    TabItem selectListTab = new TabItem(tabFolder, SWT.NONE);
	    selectListTab.setText("Select List");

	    Composite selectListComposite = new Composite(tabFolder, SWT.NULL);
	    selectListComposite.setLayout(new GridLayout(1, false));
	    selectListComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    selectListStyledText = new StyledText(selectListComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    selectListStyledText.setLayoutData(layoutData);
	    selectListStyledText.setText(propertiesHelper.getServiceFunctionSelectListTemplate());

		selectListTab.setControl(selectListComposite);

/* E : Select List Tab Folder */
/* S : Select One Tab Folder */

	    TabItem selectOneTab = new TabItem(tabFolder, SWT.NONE);
	    selectOneTab.setText("Select One");

	    Composite selectOneComposite = new Composite(tabFolder, SWT.NULL);
	    selectOneComposite.setLayout(new GridLayout(1, false));
	    selectOneComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    selectOneStyledText = new StyledText(selectOneComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    selectOneStyledText.setLayoutData(layoutData);
	    selectOneStyledText.setText(propertiesHelper.getServiceFunctionSelectOneTemplate());

		selectOneTab.setControl(selectOneComposite);

/* E : Select One Tab Folder */

/* S : Insert Tab Folder */

	    TabItem insertTab = new TabItem(tabFolder, SWT.NONE);
	    insertTab.setText("Insert");

	    Composite insertComposite = new Composite(tabFolder, SWT.NULL);
	    insertComposite.setLayout(new GridLayout(1, false));
	    insertComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    insertStyledText = new StyledText(insertComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    insertStyledText.setLayoutData(layoutData);
	    insertStyledText.setText(propertiesHelper.getServiceFunctionInsertTemplate());

		insertTab.setControl(insertComposite);

/* E : Insert Tab Folder */
/* S : Update Tab Folder */

	    TabItem updateTab = new TabItem(tabFolder, SWT.NONE);
	    updateTab.setText("Update");

	    Composite updateComposite = new Composite(tabFolder, SWT.NULL);
	    updateComposite.setLayout(new GridLayout(1, false));
	    updateComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    updateStyledText = new StyledText(updateComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    updateStyledText.setLayoutData(layoutData);
	    updateStyledText.setText(propertiesHelper.getServiceFunctionUpdateTemplate());

		updateTab.setControl(updateComposite);

/* E : Update Tab Folder */
/* S : Delete Tab Folder */

	    TabItem deleteTab = new TabItem(tabFolder, SWT.NONE);
	    deleteTab.setText("Delete");

	    Composite deleteComposite = new Composite(tabFolder, SWT.NULL);
	    deleteComposite.setLayout(new GridLayout(1, false));
	    deleteComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    deleteStyledText = new StyledText(deleteComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    deleteStyledText.setLayoutData(layoutData);
	    deleteStyledText.setText(propertiesHelper.getServiceFunctionDeleteTemplate());

		deleteTab.setControl(deleteComposite);

/* E : Delete Tab Folder */

	    noDefaultAndApplyButton();

		return panel;
	}

	@Override
	public boolean performOk() {

		propertiesHelper.setServiceFunctionSelectCountTemplate(selectCountStyledText.getText());
		propertiesHelper.setServiceFunctionSelectListTemplate(selectListStyledText.getText());
		propertiesHelper.setServiceFunctionSelectOneTemplate(selectOneStyledText.getText());
		propertiesHelper.setServiceFunctionInsertTemplate(insertStyledText.getText());
		propertiesHelper.setServiceFunctionUpdateTemplate(updateStyledText.getText());
		propertiesHelper.setServiceFunctionDeleteTemplate(deleteStyledText.getText());

		return super.performOk();
	}

}
