## SWEN301 Assignment 1 Template

Please refer to the assignment brief for details. 

The project uses Maven. If you run this on your private computer, you need to install this first. Maven can be downloaded from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi).

To set up the project run the following command first from a terminal:

`mvn install:install-file -Dfile=lib/studentdb-2.0.0.jar -DgroupId=nz.ac.wgtn.swen301 -DartifactId=studentdb -Dversion=2.0.0 -Dpackaging=jar`


Then open the project in an IDE. Major IDEs (Eclipse, IntelliJ) support the Maven project format. VSCode, Sublime etc may require additional plugins to support Maven. 