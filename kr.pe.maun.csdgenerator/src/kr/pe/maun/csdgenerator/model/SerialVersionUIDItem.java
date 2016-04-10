package kr.pe.maun.csdgenerator.model;

import java.io.Serializable;

import org.eclipse.jdt.core.ICompilationUnit;

public class SerialVersionUIDItem implements Serializable {

	private static final long serialVersionUID = -95259545692049143L;

	ICompilationUnit compilationUnit;
	String className;

	public SerialVersionUIDItem(ICompilationUnit compilationUnit,
			String className) {
		super();
		this.compilationUnit = compilationUnit;
		this.className = className;
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public String getClassName() {
		return className;
	}

	public void setCompilationUnit(ICompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
