package net.kennychua.phantomjs_qunit_runner;

/*
 * This is not the cleanest code you will ever see....
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;

/**
 * Goal which runs QUnit tests in PhantomJs (by convention)
 * 
 * @goal generate-html
 * 
 * @phase test
 */
public class QunitHtmlWrapperMojo extends AbstractMojo {
	/**
	 * Directory of JS src files to be tested.
	 * 
	 * @parameter expression="${qunit.jssrc.directory}"
	 * @required
	 */
	private File jsSourceDirectory;

	/**
	 * Directory of JS test files.
	 * 
	 * @parameter expression="${qunit.jstest.directory}"
	 * @required
	 */
	private File jsTestDirectory;

	/**
	 * Directory containing the build files
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	private File buildDirectory;

	/**
	 * Optional parameter to add additional js libraries to test-run. This
	 * allows you to use things like a mocking framework...
	 * 
	 * @parameter
	 */
	private FileSet includeLibsInDir;

	private static final String jQueryFileName = "jquery-1.7.1-min.js";
	private static final String domTestUtilsFileName = "DOMTestUtils.js";
	private static final String qUnitJsFileName = "qunit-git.js";
	private static final String qUnitCssFileName = "qunit-git.css";
	private static final String jsTestFileSuffix = "Test.js";
	private static final String qUnitHtmlOutputDirectoryName = "qunit-html";
	private static final String qUnitHeader = "<html><head><title>QUnit Test Suite</title><link rel=\"stylesheet\" href=\"qunit-git.css\" type=\"text/css\" media=\"screen\"><script type=\"text/javascript\" src=\"qunit-git.js\"></script><script type=\"text/javascript\" src=\"jquery-1.7.1-min.js\"></script><script type=\"text/javascript\" src=\"DOMTestUtils.js\"></script>";
	private static final String qUnitFooter = "</head><body><h1 id=\"qunit-header\">QUnit Test Suite</h1><h2 id=\"qunit-banner\"></h2><div id=\"qunit-testrunner-toolbar\"></div><h2 id=\"qunit-userAgent\"></h2><ol id=\"qunit-tests\"></ol></body></html>";
	private static String qUnitHtmlOutputPath;

	private static FileSetManager fileSetManager = new FileSetManager();

	public void execute() throws MojoExecutionException, MojoFailureException {
		qUnitHtmlOutputPath = buildDirectory + "/"
				+ qUnitHtmlOutputDirectoryName;

		// Go over all the js test files in jsTestDirectory
		for (File temp : getJsTestFiles(jsTestDirectory.toString())) {
			// Run each through phantomJs to test
			generateQunitHtmlOutput(temp.getName().toString(),
					jsTestDirectory.toString());
		}
	}

	private void generateQunitHtmlOutput(String testFile,
			String testFileDirectory) {
		// Create folder
		new File(qUnitHtmlOutputPath).mkdir();
		// Create the QUnit HTML wrapper files
		writeQunitHtmlFile(testFile);
		// Copy the QUnit Js and Css files
		copyQunitResources();
		// Copy the Js source files to be tested
		copyJsFiles(testFile);
		// Copy the user defined libraries
		copyUserDefinedLibraries();
	}

	/**
	 * Copy all library files to output directory, so that they're available to
	 * phantomjs.
	 */
	private void copyUserDefinedLibraries() {
		if (includeLibsInDir != null) {
			for (String libraryFileName : fileSetManager
					.getIncludedFiles(includeLibsInDir)) {
				try {
					File libraryFile = new File(includeLibsInDir.getDirectory()
							+ libraryFileName);
					FileUtils.copyFile(libraryFile, new File(
							qUnitHtmlOutputPath + "/" + libraryFile.getName()));
				} catch (IOException e) {
					getLog().error(e);
				}
			}
		}
	}

	private void copyQunitResources() {
		// copy qunit js & css
		try {
			FileUtils.copyInputStreamToFile(this.getClass().getClassLoader()
					.getResourceAsStream(qUnitCssFileName), new File(
					qUnitHtmlOutputPath + "/" + qUnitCssFileName));
			FileUtils.copyInputStreamToFile(this.getClass().getClassLoader()
					.getResourceAsStream(qUnitJsFileName), new File(
					qUnitHtmlOutputPath + "/" + qUnitJsFileName));
			FileUtils.copyInputStreamToFile(this.getClass().getClassLoader()
					.getResourceAsStream(jQueryFileName), new File(
					qUnitHtmlOutputPath + "/" + jQueryFileName));
			FileUtils.copyInputStreamToFile(this.getClass().getClassLoader()
					.getResourceAsStream(domTestUtilsFileName), new File(
					qUnitHtmlOutputPath + "/" + domTestUtilsFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyJsFiles(String jsTestFile) {
		// work out what the source js file name is
		// eg abcTest.js resolves to abc.js
		// then, copy BOTH files for qunit to run nicely in a browser
		String jsSrcFile = jsTestFile.substring(0,
				jsTestFile.indexOf(jsTestFileSuffix))
				+ ".js";

		// Copy from current plugin to the buildDir of the calling project..
		try {
			FileUtils.copyFile(new File(jsTestDirectory + "/" + jsTestFile),
					new File(qUnitHtmlOutputPath + "/" + jsTestFile));
			FileUtils.copyFile(new File(jsSourceDirectory + "/" + jsSrcFile),
					new File(qUnitHtmlOutputPath + "/" + jsSrcFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File[] getJsTestFiles(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(jsTestFileSuffix);
			}
		});
	}

	private String generateScriptTag(String fileName) {
		return "<script type=\"text/javascript\" src=\"" + fileName
				+ "\"></script>";
	}

	private void writeQunitHtmlFile(String jsTestFile) {
		String jsSrcFile = jsTestFile.substring(0,
				jsTestFile.indexOf(jsTestFileSuffix))
				+ ".js";
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(qUnitHtmlOutputPath
					+ "/" + jsTestFile + ".html"));
			output.write(qUnitHeader);
			output.write(generateScriptTag(jsSrcFile));
			output.write(generateScriptTag(jsTestFile));

			if (includeLibsInDir != null) {
				for (String libraryFileName : fileSetManager
						.getIncludedFiles(includeLibsInDir)) {
					try {
						output.write(generateScriptTag(libraryFileName));
					} catch (IOException e) {
						getLog().error(e);
					}
				}
			}

			output.write(qUnitFooter);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
