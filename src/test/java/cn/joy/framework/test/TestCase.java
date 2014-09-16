package cn.joy.framework.test;

public class TestCase {
	private String module;
	private String name;
	private String group;
	private String className;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "用例信息：\n名称："+name+"\n"+"组："+group+"\n"+"类："+className+"\n";
	}
}
