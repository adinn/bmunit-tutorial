byteman-tutorial2a
==================

Code used for the introductory tutorial for BMUnit which integrates Byteman into JUnit and TestNG

To build the app execute

  mvn compile

To run the application execute

  mvn compile
  java -classpath app/target/tutorial2a-app-1.0.0.jar WebWriter foo.html Andrew

You should find a newly written file called foo.html. Open it in a browser and
check that the title and body mention the name Andrew. Rerun with a different file or
user name and verify that the relevant details change.

To run the BMUnit tests execute

  mvn -P junit test

The first test should print the HTML header content to System.out. Byteman
should check the header contents are as expected.

The second test should detect and handle a write failure and the third test
should detect and handle a file not found failure. Byteman is used to ensure
that the file write and file open throw exceptions.

Look at the rules in check.btm which validate the data generated by
makeHeader and makeBody. Play with these rules to ensure they are actually
being run and try to add some of your own rules either to add more verification
or just to generate some trace output so you can see what is being called when
the test is run.
