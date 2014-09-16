package cn.joy.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlGroups;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlRun;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.uncommons.reportng.HTMLReporter;

import cn.joy.framework.kits.PathKit;

public class TestExecutor {
	public static void executeAll() {

		String baseDir = PathKit.getClassPath();

		List<String> suites = new ArrayList<String>();
		suites.add(baseDir + "/joy-test.xml");

		TestNG tng = new TestNG();
		tng.setTestSuites(suites);
		tng.setOutputDirectory("test-result/joy-test");
		tng.setUseDefaultListeners(false);
		
		HTMLReporter hrp = new HTMLReporter();
		tng.addListener(hrp);
		tng.run();

	}
	
	public static void executeGroup(String groupName) {
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		XmlSuite suite = new XmlSuite();
		suite.setName("simple-group-suite");
		suite.setVerbose(1);
		XmlTest test = new XmlTest(suite);
		test.setName("simple-group-test");
		
		XmlGroups xmlGroups = new XmlGroups();
		XmlRun run = new XmlRun();
		run.onInclude(groupName);
		xmlGroups.setRun(run);
		test.setGroups(xmlGroups);
		test.setIncludedGroups(test.getIncludedGroups());
		
		List<XmlPackage> packages = new ArrayList<XmlPackage>();
		XmlPackage pack = new XmlPackage();
		pack.setName("cn.joy.demo.test.cases.*");
		packages.add(pack);
		
		test.setXmlPackages(packages);
		suites.add(suite);

		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.setOutputDirectory("test-result/simple-group");
		tng.setUseDefaultListeners(false);
		
		HTMLReporter hrp = new HTMLReporter();
		tng.addListener(hrp);
		tng.run();
	}
}
