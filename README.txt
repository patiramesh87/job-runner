Non Blocking Job Runner API Java Quickstart

INTRODUCTION
------------
JOB RUNNER can process multiple requests parallelly. One request couldn't utilize all the available resources of the application.
Caller has to wait till all the jobs provided as part of request completed. After completion of all the jobs, it will return report 
that contains the success and failure status of jobs. If some job blocks, then it will be cancelled manually and set status as 'failed'.
in the report.

TECHNOLOGIES
------------

Project is created with:

JDK 1.8
Maven 3.1
Junit 4.12

CONTENTS
--------

This sample zip contains:

    /README.txt - this file
    /pom.xml - maven build files to compile and run the test case 
    /src - code to execute the JobRunner APIs
    /.classpath & .project -
	
	
COMPILING AND RUNNING THE EXAMPLE
---------------------------------

	1.Clone or download the project from GIT repository in to linux environment
	2. From command line go to folder 'job-runner' and run build command 
	3. After successfully build, jar file 'job-runner-0.0.1-SNAPSHOT.jar' will be created inside 'target' folder of 'job-runner'
	4. Add the jar as dependency into your application and provide implementation of the interfaces (Job & JobQueue) and call class ‘ReportingJobRunner’ by passing required argument to process jobs.


	GIT clone:- 
		git clone https://github.com/patiramesh87/job-runner.git

	Build :- 
		mvn -U clean install