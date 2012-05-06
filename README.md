
Less CSS Filter
===============

A LessCSS filter/processor for Java web applications.  (Blog post on the
'origins' of this little project: [Ultraq's Final MooCow &raquo; Blog &raquo; If only I'd waited](http://www.ultraq.net.nz/blog/IfOnlyIdWaited).)

This filter will process any requests for URLs ending with `.less`, processing
the LESS file pointed to by the URL and converting it to a standard CSS file.
Once the file is processed, the result is cached to save on having to process it
again.  Any changes to the LESS file will be picked up and cause the file to be
processed the next time it is requested.


Requirements
------------

 - Java 7
 - A Servlet 3.0 compliant servlet container


Installation
------------

1. Download a copy of of the pre-compiled JAR from [my website](http://www.ultraq.net.nz/lesscss-filter/)
   or build the project from the source code here on GitHub.
2. Place the JAR in the `WEB-INF/lib` directory of your web application.


Usage
-----

1. [Start writing LESS!](http://lesscss.org/)*
2. Don't change `<link rel="stylesheet" .../>` to `<link rel="stylesheet/less" .../>`.
   like the LESS website advises.  That change only applies to using LESS on the
   client.


Limitations
-----------

 - This filter only works on URLs which locate a file on the file system.  This
   is a limitation of the way I've chosen to detect changes to the underlying
   file, which I've done using Java 7's NIO 2 package.  I do have plans to fix
   this though by providing fallbacks for other ways a stylesheet can be
   retrieved.

