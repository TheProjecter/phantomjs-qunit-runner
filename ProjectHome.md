This plugin allows you to run QUnit tests in a Maven build. It works by running the QUnit test framework in PhantomJs (a webkit compliant headless browser) and parses the output to report success or failure. This is especially useful in a continuous integration environment.

See http://kennychua.net/blog/running-qunit-tests-in-a-maven-continuous-integration-build-with-phantomjs for example use


## News ##
  * **7 Oct, 2012:** Version 1.0.15 released. Now allows QUnit async tests & better logging of test failures. Credit to Gabor Paller.
  * **21 Jul, 2012:** Version 1.0.13 released to enable inclusion of user defined librarie when testings. [See how here](UsingLibraries.md)
  * **4 Feb, 2011:** Version 1.0.11 released to enable DOM tests. [See how here](DOMTests.md) **Plugin config params have changed. See below**
  * **23 Dec, 2011:** Version 1.0.6 released to address js file not found bug
  * **12 Dec, 2011:** Initial release.

## Usage ##
Include the following in your pom.xml file. See the Configuration section for more info about the config parameters.
```
<plugins>
...
  <plugin>
    <groupId>net.kennychua</groupId>
    <artifactId>phantomjs-qunit-runner</artifactId>
    <configuration>
      <jsSourceDirectory>../src/javascript/location/..</jsSourceDirectory>
      <jsTestDirectory>../test/javascript/location/..</jsTestDirectory>
      <ignoreFailures>false</ignoreFailures>
      <phantomJsExec>../path/to/phantomjsexecutable/..</phantomJsExec>
      <!-- Optional -->
      <includeLibsInDir>
                  <directory>${js.libs.include.dir}</directory>
                        <includes>
                                <include>**/*.js</include>
                        </includes>
       </includeLibsInDir>
    </configuration>
  </plugin>
...
</plugins>
```



This Maven plugin works by convention. For each of your JavaScript files specified in the source directory, the plugin will search for the JavaScript test of the same name in the test directory, suffixed with 'Test'.

An example makes this clearer.

`${jsSourceDirectory}/Foo.js` will be tested with `${jsTestDirectory}/FooTest.js`

If a JavaScript file does not have a corresponding JavaScript test file, it will be skipped over.

## Configuration ##
| **Configuration Parameter** | **Required** | **Description** |
|:----------------------------|:-------------|:----------------|
| jsSourceDirectory           | Yes          | Specify the location in the Maven project where the JavaScript files reside. _eg src/main/js_ |
| jsTestDirectory             | Yes          | Specify the location in the Maven project where the JavaScript test files reside. _eg src/test/js_ |
| includeLibsInDir            | No           | Specify user defined libraries to include for your tests. [See here for more information](UsingLibraries.md) |
| ignoreFailures              | No. Default is false | Specify whether to continue with the build if tests failed. |
| phantomJsExec               | No           | Specify the path to the PhantomJs executable. See http://www.phantomjs.org to download. Assumes that phantomjs executable is in the system PATH if not specified|

## Goals ##
#### Run QUnit tests in a PhantomJs ####

| **Goal** | test |
|:---------|:-----|
| **Description** | Searches `${jsTestDirectory}` for test JavaScript files to run in PhantomJs & executes the QUnit tests. JavaScript test file is tested against JavaScript src file by convention described above. |
| **Bound to** | Maven _test_ lifecycle phase |
| **Output** | JUnit readable xml output to `target/junitxml/*.xml` |

#### Generate browser-runnable QUnit test HTML wrappers ####

| **Goal** | generate-html |
|:---------|:--------------|
| **Description** | Searches `${jsTestDirectory}` for test JavaScript files. Wraps up these JavaScript tests to produce QUnit HTML test files to execute in browser, just as you would normally do with QUnit test writing. |
| **Bound to** | Maven _test_ lifecycle phase |
| **Output** | QUnit HTML files to `target/qunit-html/*.html`, one HTML file per test !Javascript file |