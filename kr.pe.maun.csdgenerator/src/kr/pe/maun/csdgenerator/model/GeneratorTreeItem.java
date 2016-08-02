package kr.pe.maun.csdgenerator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import kr.pe.maun.csdgenerator.GeneratorItemConstants;

public class GeneratorTreeItem {

	private static final org.eclipse.jdt.ui.ISharedImages sharedImages = JavaUI.getSharedImages();
	private static final org.eclipse.ui.ISharedImages workbenchSharedImages = PlatformUI.getWorkbench().getSharedImages();

	private static final Image packageRootIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT);
	private static final Image packageIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
	private static final Image javaIcon = sharedImages.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
	private static final Image folderIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER);
	private static final Image fileIcon = workbenchSharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);

	private GeneratorTreeItem parentItem = null;
	private String name;
	private IPath path;
	private byte type;
	private Image image;
	private List<GeneratorTreeItem> childItems = new ArrayList<GeneratorTreeItem>();

	public GeneratorTreeItem(byte type, String name, IPath path, Image image) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
		this.image = image;
	}

	public GeneratorTreeItem getParentItem() {
		return parentItem;
	}

	public IPath getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public Image getImage() {
		return image;
	}

	public List<GeneratorTreeItem> getChildItems() {
		return childItems;
	}

	public void setParentItem(GeneratorTreeItem parentItem) {
		this.parentItem = parentItem;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void setChildItems(List<GeneratorTreeItem> childItems) {
		this.childItems = childItems;
	}

	public void addChildItem(GeneratorTreeItem generatorItem) {

		System.out.println("S : ------------------------------------------------------------------------------------------");
		System.out.println("ok ---------------------> name : " + generatorItem.getName());
		System.out.println("ok ---------------------> path : " + generatorItem.getPath());
		System.out.println("E : ------------------------------------------------------------------------------------------");

		if(generatorItem.getType() == GeneratorItemConstants.PACKAGE) {
			String[] paths = generatorItem.getName().split("\\.");
			List<GeneratorTreeItem> childItems = this.childItems;
			for(String path : paths) {

				GeneratorTreeItem childItem = null;

				for(GeneratorTreeItem item : childItems) {
					if(item.getName().equals(path)) childItem = item;
				}

				if(childItem == null) {
					childItem = new GeneratorTreeItem(GeneratorItemConstants.PACKAGE, path, null, generatorItem.getImage());
					childItems.add(childItem);
				}

				childItems = childItem.getChildItems();
			}
		} else {

		}
	}

	private GeneratorTreeItem findParentGeneratorItem(GeneratorTreeItem generatorItem) {
		GeneratorTreeItem findItem = null;
		if(childItems.size() == 0) return this;
		System.out.println("S : ------------------------------------------------------------------------------------------");
		for(GeneratorTreeItem item : childItems) {
			System.out.println("name : " + item.getName());
			if(item.getType() == GeneratorItemConstants.PACKAGE
					&& item.getName().equals(generatorItem.getName())) {
				findItem = item;
				System.out.println("ok ---------------------> name : " + item.getName());
			} else {
				GeneratorTreeItem findChildItem = findParentGeneratorItem(item);
				findItem = findChildItem == null ? findItem : findChildItem;
			}
		}
		System.out.println("E : ------------------------------------------------------------------------------------------");
		System.out.println("Package Path : " + getPackagePath(generatorItem));
		return findItem;
	}

	private String getPackagePath(GeneratorTreeItem generatorItem) {
		if(generatorItem.getParentItem() != null && generatorItem.getType() == GeneratorItemConstants.PACKAGE) return getPackagePath(generatorItem.getParentItem()) + "." + generatorItem.getName();
		return generatorItem.getName();
	}

}
