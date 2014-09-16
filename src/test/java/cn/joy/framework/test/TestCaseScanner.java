package cn.joy.framework.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import cn.joy.framework.kits.ClassKit;

public class TestCaseScanner {
	public static Map<String, List<TestCaseScanner>> scan(String packageName) {
		Map<String, List<TestCaseScanner>> cases = new HashMap();

		List classes = ClassKit.getClasses(packageName);
		
		Set<String> classSet = new HashSet<String>();

		for (Object co : classes) {
			Class clz = (Class) co;
			Test testAnnotation = (Test) clz.getAnnotation(Test.class);
			if(testAnnotation==null)
				continue;
			String[] groups = testAnnotation.groups();
			for (String group : groups) {
				if (group.startsWith("case.")) {
					String className = clz.getName();
					if(classSet.contains(className))
						continue;
					classSet.add(className);
					int idx1 = className.indexOf(".cases.") + 7;
					int idx2 = className.indexOf(".", idx1);
					String module = className.substring(idx1, idx2);
					List moduleCases = cases.get(module);
					if (moduleCases == null) {
						moduleCases = new ArrayList();
						cases.put(module, moduleCases);
					}
					TestCase stCase = new TestCase();
					stCase.setModule(module);
					stCase.setGroup(group);
					stCase.setName(testAnnotation.description());
					stCase.setClassName(className);
					moduleCases.add(stCase);
				}
			}
		}

		return cases;
	}
	
	public static void main(String[] args) {
		Map cases = scan("cn.shangtan.test.cases");
		System.out.println(cases);
	}
}
