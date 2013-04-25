
Less CSS Filter
===============

A LessCSS filter/compiler for Java web applications.  (Blog post on the 'origins'
of this little project: [Ultraq's Final MooCow &raquo; Blog &raquo; If only I'd waited](http://www.ultraq.net.nz/blog/IfOnlyIdWaited).)

This filter will process any requests for URLs ending with `.less`, processing
the LESS file pointed to by the URL and converting it to a standard CSS file.
Once the file is processed, the result is cached to save on having to process it
again.  Any changes to the LESS file will be picked up and cause the file to be
processed the next time it is requested.

 - Current version: 1.1
 - Released: ?? April 2013


Requirements
------------

 - Java 7
 - A Servlet 3.0 compliant servlet container if you're taking advantage of
   servlet 3.0 annotations, otherwise a Servlet 2.5 compliant servlet container


Installation
------------

### Standalone distribution
1. Copy the JAR from [the latest download bundle](http://www.ultraq.net.nz/downloads/programming/LessCSS Filter 1.1.zip),
   or build the project from the source code here on GitHub.
2. Place the JAR in the `WEB-INF/lib` directory of your web application.

### For Maven and Maven-compatible dependency managers
Add a dependency to your project with the following co-ordinates:

 - GroupId: `nz.net.ultraq.lesscss`
 - ArtifactId: `lesscss-filter`
 - Version: `1.1`


Usage
-----

[Start writing LESS!](http://lesscss.org/)  Also, don't change `<link rel="stylesheet" .../>`
to `<link rel="stylesheet/less" .../>` like the LESS website advises.  That
change only applies to using LESS on the client.

If you're _not_ taking advantage of servlet 3.0 annotations, then you'll need to
specify the filter in your `web.xml` file, ideally as the last filter in the
processing chain so that a proper CSS file can be generated as soon as possible:

```xml
<filter>
	<filter-name>LessCSSFilter</filter-name>
	<filter-class>nz.net.ultraq.web.lesscss.LessCSSFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>LessCSSFilter</filter-name>
	<url-pattern>*.less</url-pattern>
</filter-mapping>
<mime-mapping>
	<extension>less</extension>
	<mime-type>text/css</mime-type>
</mime-mapping>
```

The mime-mapping element is optional, but helps some web development tools (eg:
FireBug) identify your `.less` files as CSS.


Limitations
-----------

This filter only works on URLs which locate a file on the file system.  This is
a limitation of the way I've chosen to detect changes to the underlying file,
which I've done using Java 7's NIO 2 package.  I do have plans to fix this
though by providing fallbacks for other ways a stylesheet can be retrieved.


Changelog
---------

### 1.1
 - Extract the processor/compiler part and put into [its own project](https://github.com/ultraq/lesscss-compiler).
 - Change package and Maven group to `nz.net.ultraq.lesscss` (dropped the 'web'
   part).

### 1.0.3
 - Project structure reorganization after changes to the Gradle build
   scripts.

### 1.0.2
 - Update [post-processing-filter](https://github.com/ultraq/post-processing-filter)
   dependency to 1.0.2
 - Minor fixes from the updated [maven-support](https://github.com/ultraq/gradle-support)
   Gradle script.

### 1.0.1
 - Switched from Ant to Gradle as a build tool.
 - Made project available from Maven Central.  Maven co-ordinates added to the
   [Installation](#installation) section.

### 1.0
 - Initial release
