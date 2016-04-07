package kr.pe.maun.csdgenerator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.pe.maun.csdgenerator.GeneratorItemConstants;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;

public class GeneratorItem implements Serializable {

	private static final long serialVersionUID = 2214068803450274465L;

	private GeneratorItem parentItem = null;
	private String name;
	private IPath path;
	private byte type;
	private Image image;
	private List<GeneratorItem> childItems = new ArrayList<GeneratorItem>();

	public GeneratorItem(byte type, String name, IPath path, Image image) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
		this.image = image;
	}

	public GeneratorItem getParentItem() {
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

	public List<GeneratorItem> getChildItems() {
		return childItems;
	}

	public void setParentItem(GeneratorItem parentItem) {
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

	public void setChildItems(List<GeneratorItem> childItems) {
		this.childItems = childItems;
	}

	public void addChildItem(GeneratorItem generatorItem) {
		
		System.out.println("S : ------------------------------------------------------------------------------------------");
		System.out.println("ok ---------------------> name : " + generatorItem.getName());
		System.out.println("E : ------------------------------------------------------------------------------------------");
		
		if(generatorItem.getType() == GeneratorItemConstants.PACKAGE) {
			String[] paths = generatorItem.getName().split("\\.");
			List<GeneratorItem> childItems = this.childItems;
			for(String path : paths) {

				GeneratorItem childItem = null;

				for(GeneratorItem item : childItems) {
					if(item.getName().equals(path)) childItem = item;
				}

				if(childItem == null) {
					childItem = new GeneratorItem(GeneratorItemConstants.PACKAGE, path, null, generatorItem.getImage());
					childItems.add(childItem);
				}

				childItems = childItem.getChildItems();
			}
		} else {
			
		}
	}

	private GeneratorItem findParentGeneratorItem(GeneratorItem generatorItem) {
		GeneratorItem findItem = null;
		if(childItems.size() == 0) return this;
		System.out.println("S : ------------------------------------------------------------------------------------------");
		for(GeneratorItem item : childItems) {
			System.out.println("name : " + item.getName());
			if(item.getType() == GeneratorItemConstants.PACKAGE
					&& item.getName().equals(generatorItem.getName())) {
				findItem = item;
				System.out.println("ok ---------------------> name : " + item.getName());
			} else {
				GeneratorItem findChildItem = findParentGeneratorItem(item);
				findItem = findChildItem == null ? findItem : findChildItem;
			}
		}
		System.out.println("E : ------------------------------------------------------------------------------------------");
		System.out.println("Package Path : " + getPackagePath(generatorItem));
		return findItem;
	}

	private String getPackagePath(GeneratorItem generatorItem) {
		if(generatorItem.getParentItem() != null && generatorItem.getType() == GeneratorItemConstants.PACKAGE) return getPackagePath(generatorItem.getParentItem()) + "." + generatorItem.getName();
		return generatorItem.getName();
	}

}
