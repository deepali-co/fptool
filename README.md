# FP Tool

Deepali's FP Tool for Java Projects.

fp tool depends on [jira-rest-client for Rest API Integration], [jersey-client](https://jersey.java.net/documentation/latest/client.html), [Jackson Json Processor](https://github.com/FasterXML/jackson), [Project Lombok](http://projectlombok.org/), [Joda-Time](http://www.joda.org/joda-time/).
[PMD](https://github.com/pmd/pmd)


# Requirements

1. JDK 1.8 for compile

2. mvn clean install

3. Git ssh / Jira Access

# Installation

1. Checkout from Git - 

2. mvn clean install

3. update *jira-rest-client.properties* file into directory in the CLASS PATH variable and set your jira host and auth infos
	```
	jira.server.url="https://your-jira.host.com"
	jira.user.id="your-jira-username"
	jira.user.pwd="your-jira-password"
	```
4. update *FPToolSettings.properties* file into directory in the CLASS PATH variable based on the sample provided below
    a. ruleSet - Comment those which are not needed by adding a # in the begining of line.
    b. sourcePath - while run in stand alone mode, specify the source path on which the rules have to run
    c. reportPath - /path/to/folder where the reports are created
    d. finalReportFile - Name of the Final FP Report.
    e. productKey - The Software product name from Jira for which FPs are to be processed.
    f. project - Jira Project Key
    g. query - JQL (Jira Query)

    Note : "=" and ":" have to be escaped using a "\". # can be used to comment a line.




# Usage
 java com.myapps.fptool.FPTool [product]

 Note - standalone is the default setting
	

