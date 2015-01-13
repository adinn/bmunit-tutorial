bmunit-tutorial
===============

This git repository contains a maven project which provides all the code
and Byteman rule scripts used for the introductory tutorial for BMUnit,
the package which integrates Byteman into JUnit and TestNG. It provides
a simple example of how you can extend the range of your JUnit/TestNG tests
using Byteman and shows how you would configure the maven surefire plugin to
support the use of BMUnit.

A full explanation of how the application and tests operate with
instructions on how to run both is provided in the tutorial at

    https://developer.jboss.org/wiki/BMUnitUsingBytemanWithJUnitOrTestNGFromMavenAndAnt#top

Tutorial Summary
================

The project contains three submodules, junit, junit2 and testng. The first
module provides an example of a simple Byteman test using JUnit. The second
module provides a more complex JUnit test which uses Byteman to inject faults
into the application, exercising the application's error handling code. The
third module repeats the same tests as the second module but using TestNG to
run the tests instead of JUnit.

The project also includes ant scripts which allow the application to be built,
run and tested using ant in place of maven. See the build.xml files for details
of which targets to run.

n.b. In order for ant builds to work you must obtain a local copy of the byteman
jars. Download a release zip from the Byteman downloads page (follow the link
from byteman.jboss.org), unzip it into a directory on your local disk and then
set environment variable BYTEMAN_HOME to point to the directory into which you
unzipped the release. Byteman release 2.2.0 or later is recommended.

n.b.b. on Windows it is simplest to unzip into a top level directory on the C
drive e.g. C:\tmp\Byteman. In particular, note that if you unzip into a directory
whose pathname contains spaces (e.g. C:\Program Files\Byteman) then you will
have to ensure that environment variable BYTEMAN_HOME refers to the directory
using the equivalent translated DOS format filename which omits the spaces
(e.g. you might might need to set BYTEMAN_HOME=C:\Progra~1\Byteman)

Instructions
============

To build the app execute

    mvn install

To run the application execute

    mvn package
    java -classpath app/target/bmunit-tutorial-app-1.0.0.jar org.my.app.WebWriter foo.html Andrew

You should find a newly written file called foo.html. Open it in a browser and
check that the title and body mention the name Andrew. Rerun with a different file or
user name and verify that the relevant details change.

To run the first set of BMUnit tests execute

    mvn -P junit test

This will execute the tests found in the junit subdirectory

The first test should print the HTML header content to System.out.

    -------- testWriteHead ---------
    <HEAD>
      <TITLE>
      Welcome to the web page for Andrew
      </TITLE>
    </HEAD>
    
    -------- testWriteHead ---------

The validation rule found in junit/src/test/resources/check.btm checks
that the header test contains a <HEAD> and </HEAD> element. Note that
the debug call in the condition always returns true but only prints
 output if debug or verbose trace is enabled (see follow ups below).

Satisfy yourself that the Byteman rule is really being executed by modifying
the BMUnitConfig annotation to add attribute debug=true

    . . .
    @BMUnitConfig(loadDirectory="target/test-classes", debug=true)
    @BMScript(value="check.btm")
    public class WebWriterTest
    {
       . . .
     
You should see debug trace showing the first debug statement being executed
but not the second one:

    -------- testWriteHead ---------
    rule.debug{check head} : checking for HEAD and /HEAD tags
    <HEAD>
      <TITLE>
      Welcome to the web page for Andrew
      </TITLE>
    </HEAD>
    
    -------- testWriteHead ---------

Alternatively, modify method makeHeader so that it fails to generate the
necessary tags

    . . .
    private void makeHeader(StringBuilder builder)
    {
        builder.append("<HEED>\n");
        . . .
        
Now watch Byteman throw an exception causing the test to fail.

             -------- testWriteHead ---------
    Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.365 sec <<< FAILURE!
    
    Results :
    
    Tests in error: 
      testWriteHead(org.my.WebWriterTest)

Be sure to revert the change to makeHeader efore running the next set of tests.

To run the second set of BMUnit tests execute

    mvn -P junit2 test

The second test class contains two tests which use byteman to simulate file system
problems. IN both cases the rule text is supplied inline in a BMRule annotation on
the test method rather than in a separate script.

The first test is supposed to ensure that the app correctly detects and handles
a file not found failure. The Byteman rule attached to method handleFileNotFound
throws a FileNotFoundException from constructor FileStream(File) as soon as it
is entered (n.b. Byteman rules refer to a constructor method by using the JVM's
internal name <init>). This exception should be caught and handled by the application,
giving the following output:

    -------- handleFileNotFound ---------
    Unable to open file foo.html
    java.io.FileNotFoundException: Ha ha Byteman fooled you again!
    -------- handleFileNotFound ---------

The second test is supposed to ensure that the app correctly detects and handles
a write failure. The rule attached to method testWriteBody is injected into
method write(byte[], int, int) of class Filestream. This method gets called twice,
first when writeHeader is called and the when writeBody is called. The rule uses
a condition to ensure that the first write succeeds and the second write fails.

Builtin method incrementCounter is used to increment a counter associated with $0,
the file stream being written to. At the first call to Filestream.write when the rule
condition is tested incrementCounter detects that there is no existing counter
associated with $0. So, it creates a counter, initialises it to 0 and then increments
it, returning value 1. The condition evaluates to false and the call to Filestream.write
continues.

At the second call to Filestream.write  when the rule condition is tested incrementCounter
retreives the counter associated with $0, increments it again, returning the new value 2.
The condition evaluates to true and the action executes, throwing an IOException which
should be caught and handled by the application giving the following output:

    -------- testWriteBody ---------
    Error writing body
    -------- testWriteBody ---------

n.b. if you see an exception here then the test has failed.

Follow-up Exercises
===================

Look at the rule in check.btm which validates the data generated by
makeHeader. Make sure you understand how it works.

Do the same for the rules specified in the BMRule annotations on class
WebWriterTest2 in the junit2 module.

Try to add some of your own rules either to add more verification or just
to generate some trace output so you can see what is being called when
the test is run.

Modify the BMUnitConfig annotation on the test class by setting either of
the following attributes

    debug = true
    bmunitVerbose = true
    verbose = true

Look at the resulting debug and trace output generated by the BMUnit code
and the underlying Byteman agent code.

See what other BMUnitConfig attributes you can configure and identify what
they do.
