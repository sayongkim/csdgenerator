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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class CSDGeneratorControllerFunctionTemplate extends PropertyPage implements
		IWorkbenchPropertyPage {

	private CSDGeneratorPropertiesHelper propertiesHelper;

	IProject project;

	StyledText styledText;

	String[] generalTemplateGroupNames;
	String[] controllerTemplateNames;
	String[] serviceTemplateNames;
	String[] daoTemplateNames;
	String[] mapperTemplateNames;
	String[] jspTemplateNames;
	String[] dataTypes;

	Tree generalTemplateTree;
	Tree controllerTemplateTree;
	Tree serviceTemplateTree;
	Tree daoTemplateTree;
	Tree mapperTemplateTree;
	Tree jspTemplateTree;

	Button typeAButton;
	Button typeBButton;

	private Text companyText;
	private Text authorText;

	/*private Text controllerTemplateFileName;*/
	private Button controllerTemplateFileButton;

	private Text serviceTemplateFileName;
	private Button serviceTemplateFileButton;

	private Text daoTemplateFileName;
	private Button daoTemplateFileButton;

	private Button specificSettings;

	private Button createControllerFolderButton;
	private Button addPrefixControllerFolderButton;
	private Button createControllerSubFolderButton;

	private Button createServiceFolderButton;
	private Button addPrefixServiceFolderButton;
	private Button createServiceSubFolderButton;
	private Button createServiceImplButton;
	private Button createServiceImplFolderButton;

	private Button createDaoFolderButton;
	private Button addPrefixDaoFolderButton;
	private Button createDaoSubFolderButton;

	private Button createMapper;

	private Text mapperPathName;
	private Button mapperPathButton;

	private Button createVo;
	private Button createSearchVo;

	private Text voPathName;
	private Button voPathButton;
	private Text myBatisSettingsFileName;
	private Button myBatisSettingsFileButton;

	Table dataTypeMappingTable;

	private Button createJsp;

	private Text jspPathName;
	private Button jspPathButton;

	Combo connectionCombo;

	IConnectionProfile[] connectionProfiles;
	DatabaseResource databaseResource;



	@Override
	protected Control createContents(Composite parent) {

		Device device = Display.getCurrent();

		project = (IProject) getElement().getAdapter(IProject.class);
		propertiesHelper = new CSDGeneratorPropertiesHelper(new ProjectScope(project).getNode(CSDGeneratorPlugin.PLUGIN_ID));

		boolean isSpecificSettings = true;

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout defalutLayout = new GridLayout(3, false);
		defalutLayout.marginHeight = 0;
		defalutLayout.marginWidth = 0;
		panel.setLayout(defalutLayout);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    TabFolder tabFolder = new TabFolder(panel, SWT.NONE);
	    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 0));

/* S : General Tab Folder */

	    TabItem generalTab = new TabItem(tabFolder, SWT.NONE);
	    generalTab.setText("Select count");

	    panel.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				styledText.setText("\t\t/**\r\t * Desc : 목록 (Ajax)\r\t * @Method : [lowerPrefix]ListData\r\t * @Company : [company]\r\t * @Author : [author]\r\t * @Date : [date]\r\t * @return\r\t */\r\t@RequestMapping(value=\"/[lowerPrefix]/list/data\", method=RequestMethod.GET)\r\t@ResponseBody\r\tpublic HashMap<String, Object> [lowerPrefix]ListData(@RequestParam(value=\"current\", defaultValue=\"1\") int current\r\t\t\t,@RequestParam(value=\"rowCount\", defaultValue=\"10\") int rowCount\r\t\t\t,@RequestParam HashMap<String, Object> searchMap) {\r\r\t\tHashMap<String, Object> resultMap = new HashMap<String, Object>();\r\r\t\tresultMap.put(\"current\", current);\r\t\tresultMap.put(\"rowCount\", rowCount);\r\r\t\tint totalCnt = [lowerPrefix]Service.select[capitalizePrefix]TotalCnt(searchMap);\r\t\tresultMap.put(\"total\", totalCnt);\r\r\t\tint startNum = (current - 1) * rowCount;\r\t\tint endNum = current * rowCount;\r\r\t\tsearchMap.put(\"startNum\", Integer.toString(startNum));\r\t\tsearchMap.put(\"endNum\", Integer.toString(endNum));\r\r\t\tList<HashMap<String, String>> [lowerPrefix]List = [lowerPrefix]Service.select[capitalizePrefix]List(searchMap);\r\r\t\tint row_num = totalCnt - ((current - 1) * rowCount);\r\t\tint [lowerPrefix]Size = [lowerPrefix]List.size();\r\r\t\tfor(int i = 0; i < [lowerPrefix]Size; i++) {\r\t\t\tHashMap<String, String> [lowerPrefix] = [lowerPrefix]List.get(i);\r\t\t\t[lowerPrefix].put(\"row_num\", Integer.toString(row_num));\r\t\t\t[lowerPrefix]List.set(i, [lowerPrefix]);\r\t\t\trow_num--;\r\t\t}\r\r\t\tresultMap.put(\"rows\", [lowerPrefix]List);\r\r\t\treturn resultMap;\r\t}");
			}
		});

	    Composite generalComposite = new Composite(tabFolder, SWT.NULL);
	    generalComposite.setLayout(new GridLayout(1, false));
	    generalComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));

	    styledText = new StyledText(generalComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
	    styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    styledText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
System.out.println(styledText.getText());
			}
		});

		generalTab.setControl(generalComposite);

/* E : General Tab Folder */

	    noDefaultAndApplyButton();

		return panel;
	}

	public boolean performOk() {

		/*setSpecificSettings(specificSettings.getSelection());*/
/*
		if(typeAButton.getSelection()) {
			setType("A");
		} else {
			setType("B");
		}
*/
		setCompany(companyText.getText());
		setAuthor(authorText.getText());

		setCreateControllerFolder(createControllerFolderButton.getSelection());
		setAddPrefixControllerFolder(addPrefixControllerFolderButton.getSelection());
		setCreateControllerSubFolder(createControllerSubFolderButton.getSelection());
		/*setControllerTemplateFile(controllerTemplateFileName.getText());*/

		setCreateServiceFolder(createServiceFolderButton.getSelection());
		setAddPrefixServiceFolder(addPrefixServiceFolderButton.getSelection());
		setCreateServiceSubFolder(createServiceSubFolderButton.getSelection());

		setCreateServiceImpl(createServiceImplButton.getSelection());
		setCreateServiceImplFolder(createServiceImplFolderButton.getSelection());

		setCreateDaoFolder(createDaoFolderButton.getSelection());
		setAddPrefixDaoFolder(addPrefixDaoFolderButton.getSelection());
		setCreateDaoSubFolder(createDaoSubFolderButton.getSelection());

		setCreateMapper(createMapper.getSelection());
		setMapperPath(mapperPathName.getText());

		setCreateVo(createVo.getSelection());
		setCreateSearchVo(createSearchVo.getSelection());
		setVoPath(voPathName.getText());
		setMyBatisSettingFile(myBatisSettingsFileName.getText());

		setCreateJsp(createJsp.getSelection());
		setJspPath(jspPathName.getText());

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

	public String getType() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.TYPE);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "A";
	}

	public void setType(String type) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(type)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.TYPE, type);
	    	}
	    } catch (CoreException e) {
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

	public void setAuthor(String author) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(author)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.AUTHOR, author);
	    	}
	    } catch (CoreException e) {
	    	e.printStackTrace();
	    }
	}

	public String getDatabaseConnectionProfileName() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.DATABASE_CONNECTION_PROFILE_NAME);
			if(value != null && !"".equals(value)) return value;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void setDatabaseConnectionProfileName(String databaseConnectionProfileName) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
	    try {
	    	if(!"".equals(databaseConnectionProfileName)) {
	    		resource.setPersistentProperty(CSDGeneratorPropertiesItem.DATABASE_CONNECTION_PROFILE_NAME, databaseConnectionProfileName);
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


	public boolean isCreateControllerSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateControllerSubFolder(boolean createControllerSubFolder) {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);
		try {
			if(createControllerSubFolder) {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER, "true");
			} else {
				resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_CONTROLLER_SUB_FOLDER, "false");
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

	public boolean isCreateServiceSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateServiceSubFolder(boolean createServiceSubFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createServiceSubFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SERVICE_SUB_FOLDER, "false");
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
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.ADD_PREFIX_DAO_FOLDER);
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

	public boolean isCreateDaoSubFolder() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateDaoSubFolder(boolean createDaoSubFolder) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createDaoSubFolder) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_DAO_SUB_FOLDER, "false");
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

	public boolean isCreateVo() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateVo(boolean createVo) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createVo) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_VO, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public boolean isCreateSearchVo() {
		IResource resource = (IResource) getElement().getAdapter(IResource.class);

		try {
			String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO);
			if("true".equals(value)) {
				return true;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setCreateSearchVo(boolean createVo) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(createVo) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO, "true");
	    	  } else {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.CREATE_SEARCH_VO, "false");
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getVoPath() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.VO_PATH);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setVoPath(String voPath) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(voPath)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.VO_PATH, voPath);
	    	  }
	      }
	      catch (CoreException e) {
	    	  e.printStackTrace();
	      }
	}

	public String getMyBatisSettingFile() {
			IResource resource = (IResource) getElement().getAdapter(IResource.class);

			try {
				String value = resource.getPersistentProperty(CSDGeneratorPropertiesItem.MYBATIS_SETTING_FILE);
				if(value != null && !"".equals(value)) return value;
			} catch (CoreException e) {
				e.printStackTrace();
			}

			return "";
	}

	public void setMyBatisSettingFile(String myBatisSettingFile) {
	      IResource resource = (IResource) getElement().getAdapter(IResource.class);
	      try {
	    	  if(!"".equals(myBatisSettingFile)) {
	    		  resource.setPersistentProperty(CSDGeneratorPropertiesItem.MYBATIS_SETTING_FILE, myBatisSettingFile);
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
		addPrefixControllerFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixControllerFolder() {
		addPrefixControllerFolderButton.setEnabled(false);
	}

	private void setEnabledCreateControllerSubFolder() {
		createControllerSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateControllerSubFolder() {
		createControllerSubFolderButton.setEnabled(false);
	}

	private void setEnabledAddPrefixServiceFolder() {
		addPrefixServiceFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixServiceFolder() {
		addPrefixServiceFolderButton.setEnabled(false);
	}

	private void setEnabledCreateServiceSubFolder() {
		createServiceSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateServiceSubFolder() {
		createServiceSubFolderButton.setEnabled(false);
	}

	private void setEnabledAddPrefixDaoFolder() {
		addPrefixDaoFolderButton.setEnabled(true);
	}

	private void setDisabledAddPrefixDaoFolder() {
		addPrefixDaoFolderButton.setEnabled(false);
	}

	private void setEnabledCreateDaoSubFolder() {
		createDaoSubFolderButton.setEnabled(true);
	}

	private void setDisabledCreateDaoSubFolder() {
		createDaoSubFolderButton.setEnabled(false);
	}

	private void setEnabledServiceImplFolder() {
		createServiceImplFolderButton.setEnabled(true);
	}

	private void setDisabledServiceImplFolder() {
		createServiceImplFolderButton.setEnabled(false);
	}

	private void setEnabledMapperPath() {
		mapperPathName.setEnabled(true);
		mapperPathButton.setEnabled(true);
	}

	private void setDisabledMapperPath() {
		mapperPathName.setEnabled(false);
		mapperPathButton.setEnabled(false);
	}

	private void setEnabledCreateSearchVo() {
		createSearchVo.setEnabled(true);
	}

	private void setDisabledCreateSearchVo() {
		createSearchVo.setEnabled(false);
	}

	private void setEnabledMyBatisSettingsFile() {
		myBatisSettingsFileName.setEnabled(true);
		myBatisSettingsFileButton.setEnabled(true);
	}

	private void setDisabledMyBatisSettingsFile() {
		myBatisSettingsFileName.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);
	}

	private void setEnabledVoPath() {
		voPathName.setEnabled(true);
		voPathButton.setEnabled(true);
	}

	private void setDisabledVoPath() {
		voPathName.setEnabled(false);
		voPathButton.setEnabled(false);
	}

	private void setEnabledJspPath() {
		jspPathName.setEnabled(true);
		jspPathButton.setEnabled(true);
	}

	private void setDisabledJspPath() {
		jspPathName.setEnabled(false);
		jspPathButton.setEnabled(false);
	}

	private void setEnabledSettings() {

		companyText.setEnabled(true);
		authorText.setEnabled(true);

		createControllerFolderButton.setEnabled(true);
		if(createControllerFolderButton.getSelection()) addPrefixControllerFolderButton.setEnabled(true);
		/*controllerTemplateFileName.setEnabled(true);*/
		controllerTemplateFileButton.setEnabled(true);

		createServiceFolderButton.setEnabled(true);
		if(createServiceFolderButton.getSelection()) addPrefixServiceFolderButton.setEnabled(true);
		serviceTemplateFileName.setEnabled(true);
		serviceTemplateFileButton.setEnabled(true);

		createServiceImplButton.setEnabled(true);
		if(createServiceImplButton.getSelection()) {
			createServiceImplFolderButton.setEnabled(true);
		}

		createDaoFolderButton.setEnabled(true);
		if(createDaoFolderButton.getSelection()) addPrefixDaoFolderButton.setEnabled(true);
		daoTemplateFileName.setEnabled(true);
		daoTemplateFileButton.setEnabled(true);

		createMapper.setEnabled(true);
		if(createMapper.getSelection()) {
			mapperPathButton.setEnabled(true);
			mapperPathName.setEnabled(true);
		}

		createVo.setEnabled(true);
		if(createVo.getSelection()) {
			voPathButton.setEnabled(true);
			voPathName.setEnabled(true);
			myBatisSettingsFileName.setEnabled(true);
			myBatisSettingsFileButton.setEnabled(true);
		}

		createJsp.setEnabled(true);
		if(createJsp.getSelection()) {
			jspPathButton.setEnabled(true);
			jspPathName.setEnabled(true);
		}
	}

	private void setDisabledSettings() {

		companyText.setEnabled(false);
		authorText.setEnabled(false);

		addPrefixControllerFolderButton.setEnabled(false);
		/*controllerTemplateFileName.setEnabled(false);*/
		controllerTemplateFileButton.setEnabled(false);

		addPrefixServiceFolderButton.setEnabled(false);
		serviceTemplateFileName.setEnabled(false);
		serviceTemplateFileButton.setEnabled(false);

		addPrefixDaoFolderButton.setEnabled(false);
		daoTemplateFileName.setEnabled(false);
		daoTemplateFileButton.setEnabled(false);

		createControllerFolderButton.setEnabled(false);

		createServiceFolderButton.setEnabled(false);
		createServiceImplButton.setEnabled(false);
		createServiceImplFolderButton.setEnabled(false);

		createDaoFolderButton.setEnabled(false);

		createMapper.setEnabled(false);
		mapperPathButton.setEnabled(false);
		mapperPathName.setEnabled(false);

		createVo.setEnabled(false);
		voPathButton.setEnabled(false);
		voPathName.setEnabled(false);
		myBatisSettingsFileName.setEnabled(false);
		myBatisSettingsFileButton.setEnabled(false);

		createJsp.setEnabled(false);
		jspPathButton.setEnabled(false);
		jspPathName.setEnabled(false);
	}

}
