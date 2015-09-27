# Introduction #

If you use any libraries, whether those are your own or external ones, you need to tell the phanromjs-qunit-runner which javascript file need to be available when running the tests.

# Example #

You can do this by including a libraries section in the configuration for the plugin (for both the qunit test and generate-html goals), as in the following example:

```
	<configuration>
		<jsSourceDirectory>${js.source.dir}</jsSourceDirectory>
		<jsTestDirectory>${js.test.dir}</jsTestDirectory>
		<includeLibsInDir>
			<directory>${js.libs.include.dir}</directory>
			<includes>
				<include>**/*.js</include>
			</includes>
		</includeLibsInDir>
	</configuration>
```

The libraries tag is a normal [maven file management](http://maven.apache.org/shared/file-management/examples/mojo.html) section, supports includes and excludes. You can currently add only one directory to this section, so all library files should be in the same place.