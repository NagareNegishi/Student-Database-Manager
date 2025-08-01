## SWEN301 Assignment 1 Template

Please refer to the assignment brief for details. 

The project uses Maven. If you run this on your private computer, you need to install this first. Maven can be downloaded from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).

To set up the project run the following command first from a terminal:

`mvn install:install-file -Dfile=lib/studentdb-2.0.0.jar -DgroupId=nz.ac.wgtn.swen301 -DartifactId=studentdb -Dversion=2.0.0 -Dpackaging=jar`


Then open the project in an IDE. Major IDEs (Eclipse, IntelliJ) support the Maven project format. VSCode, Sublime etc may require additional plugins to support Maven. 



## Memory Leak Analysis

The design uses WeakHashMap for caching Student and Degree instances, reducing memory leak risk by allowing Garbage Collection (GC) of unreferenced objects. HashMap maintains strong references that prevent GC, causing potential memory leaks as cached objects accumulate indefinitely. 

Initially HashMap was used for better performance, as WeakHashMap failed to consistently pass the 500 queries/second requirement on development hardware. In lab environments, both implementations exceed the performance requirement, making WeakHashMap's memory safety advantageous. 

However, WeakHashMap provides no control over GC timing. Guava Cache would offer better control with configurable eviction policies while maintaining reasonable performance and memory safety.