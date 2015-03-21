# Troubleshooting guide for FileNet P8 4.0,4.5 Connector 2.x #



# Introduction #
This document has a list of FAQs and troubleshooting tips to quickly identify if connector has been configured correctly and is discovering & crawling as intended.
Add your content here.


# Details #

|**1. Unable to configure the connector instance:**|
|:-------------------------------------------------|

Problem Description:

Following Error message is displayed in the connector logs:
Caused by: javax.xml.rpc.JAXRPCException: Exception while performing call. Unable to read server response. Server returned status code: 200 OK (Content-type: text/xml; charset=utf-8) Incoming message is not SOAP 1.1 nor SOAP 1.2

**Root Cause:**

Looking at the logs and exception it was found that, connector is unable to refer to the classes which are provided at the time of installation of connector. Currently the classpath in Service.bat file under Tomcat/bin is set with absolute path for all the dependency jar files. But somehow connector is unable to refer those files and throwing the above exception.

**Resolution/Quick Workaround:**

Need to copy all the dependency jars file needed by FileNet Connector in the WEB-INF/lib directory. This can be done by following the below mentioned steps:

  1. Stop the connector service (If running)
  1. Copy all the 9 jars (i.e. activation.jar, builtin\_serialization.jar, javaapi.jar, jaxrpc.jar, jetty.jar, log4j.1.2.15.jar, saaj.jar, wasp.jar, wsdl\_api.jar) from wsi\lib to CONNECTOR\_HOME\Tomcat\webapps\connector-manager\WEB-INF\lib
  1. Navigate to CONNECTOR\_HOME\Tomcat\bin folder
  1. Open service.bat in text editor
  1. Go to line number 99. Set PR\_CLASSPATH with “%CATALINA\_HOME%\bin\bootstrap.jar;”. Remove all other classpath entries. For e.g.
> > set PR\_CLASSPATH=%CATALINA\_HOME%\bin\bootstrap.jar;
> > Save service.bat and close it.
  1. Open DOS command window
  1. Change directory to CONNECTOR\_HOME\Tomcat\bin
  1. Run following command in the same order, as mentioned below, on command prompt:
    1. service.bat remove
    1. service.bat install
  1. Start the connector service.
  1. Try to register connector instance.

|**2. Search Results not displayed:**|
|:-----------------------------------|

**Problem Description:**

I have crawled certain documents with user U1. I am searching documents with user U2. I am not getting any search results. I got following in the logs:
"User U2 is not authorized for document Id %7B11225236-5855-499E-9C27-00C670190DC0%7D"

**Root Cause:**

FileNet requires Full Distinguished Name or User Principle Name at search time. If the username provided is shortname then authorization fails.

**Resolution/Quick Workaround:**

Provide the username in Full Distinguished Name or User Principle Name, depending on the LDAP server configured.

|**3. Unable to configure connector instance:**|
|:---------------------------------------------|

**Problem Description:**

Following error message is displayed in red on the configuration page:
"Unable to validate Content Engine URL. Could not connect to the Content Engine. Either Content Engine server is down or URL is not in proper format."

**Root Cause:**

Following can be the probable reasons:
  1. Content Engine URL is in-correct or FileNet server is down
  1. There can be three forms of Content Engine URL:
    1. http://<server-ip or name>:`<`port>/wsi/FNCEWS40DIME: This is used to connect Content Engine Web Service through JAVA client.
    1. http://<server-ip or name>:`<`port>/wsi/FNCEWS40MTOM: This is used to connect Content Engine Web Service through .NET client.
    1. http://<server-ip or name>:`<`port>/FileNet/Engine: This is used for EJB as the transport protocol.

FileNet connector uses Content Engine Web Service (CEWS) as the transport protocol and it uses JAVA client. Thus URL mentioned in 2.1 should be used as content engine URL while configuring the FileNet connector instance. URL in any other format will cause the above error in red.

**Resolution/Quick Workaround:**

  1. Check the connection to FileNet Server.
  1. Use the Content Engine URL in the format shown in 2.1 above.

|**4. Some documents are not getting crawled and indexed:**|
|:---------------------------------------------------------|

**Problem Description:**

I configured FileNet connector to crawl an ObjectStore. It seems that some of the documents under the target ObjectStore are left out and not indexed.

**Root Cause:**

Following may be, one of the reasons for documents getting skipped:
  1. **Versioning**: FileNet Connector crawls only released documents, whereas FileNet version systems can have documents in 4 states i.e. Released, Superseded, In Process, Reservation. FileNet connector crawls only documents which are in released state i.e. VERSIONSTATUS=1. So if the target ObjectStore is having documents in states other than Released one then those documents are the candidates to be skipped and will not be crawled by connector.
  1. **TimeZone**: Last Modified Date stored as checkpoint is not same as the Last Modified Date stored in database for a particular document. Both the dates for the same document should be same.

**Resolution/Quick Workaround:**

  1. Make sure that target ObjectStore contains documents in released state i.e. in VERSIONSTATUS=1.
  1. Please verify that Last Modified Date stored in checkpoint in file
\GoogleConnectors\<FileNET Connector Name>\Tomcat\webapps\connector-manager\WEB-INF\connectors\Filenet\_P8\`<`connector-instance>\`<`connector-instance>_state.txt
and the Last Modified Date in the Table "DocVersion" of the configured Database for a particular document. Both the Dates should be same for the same document and should be present in "UTC" TimeZone._

|**5. Delete Feeds are not sent by connector:**|
|:---------------------------------------------|

**Problem Description:**

I have deleted some documents from the ObjectStore, still those document's indexes are present on GSA.

**Root Cause:**

This can be due to the reason that Auditing is disabled on the FileNet Server. Until the Auditing is the disabled on FileNet Server for a particular ObjectStore, nothing gets logged into the Event table of the Database. FileNet connector compares the dates from checkpoint and Event table and send Delete feed accordingly. Thus if Auditing is not enabled on ObjectStore then, Event table will be empty and hence no delete feed will be send to GSA.

**Resolution/Quick Workaround:**

Enable the Auditing for delete event on FileNet server for target Object Store through FileNet Enterprise Manager as follows:
  1. Right click on the target ObjectStore and select "Properties".
  1. Go to "General" tab and select "Auditing Enabled" check-box.
  1. Apply the changes and select Ok.
  1. Expand the target Object Store and right click on the "Document Class" and select "Properties".
  1. Go to the "Audit Definitions" tab.
  1. Select event as "Deletion", select "Success" check-box.
  1. Click "Add" button

All the documents deleted after Auditing is enabled, will be sent for Delete Feed to GSA

|**6. Unable to configure the connector instance for FileNet Version 4.5.1 and above:**|
|:-------------------------------------------------------------------------------------|

**Problem Description:**

I am trying to configure FileNet conenctor for FileNet Server whose version is 4.5.1 or above, but I am getting following exception in the log

`com.filenet.api.exception.EngineRuntimeException: TRANSPORT_WSI_SERIALIZATION_ERROR: Error serializing a web services instance of GetObjectsRequest.`

**Root Cause:**

For FileNet Server with version 4.5.1 and above following three extra jars are required as dependency jar files:
  1. stax-api.jar
  1. xlxpScanner.jar
  1. xlxpScannerUtils.jar
These jars should be present in the classpath. Due to unavailability of these jars in classpath, the above mentioned exception is generated.


**Resolution/Quick Workaround:**

  1. Stop the connector service.
  1. Copy those jars from FileNet Server and paste them under the following directory:
> > `CONNECTOR_HOME\Tomcat\webapps\connector-manager\WEB-INF\lib`
  1. Restart the connector service.


|**7. After one complete crawl cycle, connector is crawling first batch of documents repetitively.**|
|:--------------------------------------------------------------------------------------------------|

**Problem Description:**

I configured FileNet connector to crawl an ObjectStore. It seems that after one complete crawl cycle, connector is crawling first batch of documents repetitively.

**Root Cause:**

Following may be, one of the reasons for documents getting skipped:
  1. **TimeZone**: Last Modified Date stored as checkpoint is not same as the Last Modified Date stored in database for a particular document. Both the dates for the same document should be same.

**Resolution/Quick Workaround:**

  1. Configure Time-Zone in Advanced Configuration: If still customer gets any issues with the timezone then a Time-Zone Delta value can be configured in
> > Advanced Configuration as follows:
      1. Open "CONNECTOR\_HOME\Tomcat\webapps\connector-manager\WEB-INF\connectors\Filenet\_P8\_connector-instance_\connectorInstance.xml"
      1. Uncomment the property tag: _property name="db\_timezone" value=""/_ and give the Time-Zone Delta value in the format GMT(+|-)hh:ss
      1. Time-Zone Delta is solely based on the TimeZone which is maintained by your database server. Thus there can be following three cases in which
> > > > Database Server stores the time-stamp for any stored document:
> > > > > i. (Current Time + Current Machine's TimeZone): For e.g. Current Time is 1:00 PM and Current Machine's TimeZone is GMT+05:30. Thus if
> > > > > > the document is stored at 1:00 PM then the time-stamp stored in database is (01:00PM + 05:30) i.e. 06:30PM. In this case TimeZone Delta
> > > > > > value should be GMT-<Current Machine's TimeZone> For E.g. GMT-05:30.

> > > > > ii. (Current Time - Current Machine's TimeZone): For e.g. Current Time is 1:00 PM and Current Machine's TimeZone is GMT+05:30. Thus if
> > > > > > the document is stored at 1:00 PM then the time-stamp stored in database is (01:00PM - 05:30) i.e. 07:30AM. In this case TimeZone Delta
> > > > > > value should be GMT+00:00.

> > > > > iii. (Current Time): For e.g. Current Time is 1:00 PM and Current Machine's TimeZone is GMT+05:30. Thus if the document is stored at 1:00 PM
> > > > > > then the time-stamp stored in database is (01:00PM). In this case TimeZone Delta value should be GMT+<Current Machine's TimeZone> For
> > > > > > E.g. GMT+05:30.

|**8. After upgrading to connector version 2.8.0, we noticed that 'Make Public' option is not visible on the connector configuration UI. How to make the URLs fed by the connector public? .**|
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

**Resolution/Quick Workaround:**

To make all documents fed by the connector public, use Crawl and Index -> Crawler Access to add '^googleconnector://connectorname.localhost/' URL pattern, add credentials and check the 'Make Public' check-box. You need to wait for about 5 minutes for the change to take effect.


---

### Diagnosing the Connector Logs ###

This section details some of the important log messages that are written into the connector’s log:

| **Initialize Connector instance** |
|:----------------------------------|
| **Log Message** | **Description** | **Level** |
| Unable to instantiate the class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory | Unable to instantiate the class| WARNING |
| Access denied to class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory | IllegalAccessException | WARNING |
| The class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory not found | ClassNotFoundException | WARNING |
| **Login** |
| **Log Message** | **Description** | **Level** |
| Unable to instantiate the class com.google.enterprise.connector.file.filejavawrap.FnObjectFactory | Setting system property  | INFO |
| Unable to find URL of file jaas.conf | Jaas.conf not found at giver location | WARNING |
| Unable to find the resource bundle file for language << language >> | Unable to find the resource bundle file for locale language in resource bundle | SEVERE |
| No configuration information is available | Resource bundle file is empty | SEVERE |
| Unable to establish connection with FileNet server | Fail to connect to FileNET server | SEVERE |
| Connection to Content Engine URL Failed | Connection to Content Engine URL failed | INFO |
| **Connector configuration**  |
| **Log Message** | **Description** | **Level** |
| Invalid credentials. Please enter correct credentials | Invalid credentials  | SEVERE |
| Either the Jaas.conf file is missing or corrupted. Please provide a valid Jaas.conf file.| Invalid Jaas.conf file path | SEVERE |
| Unable to connect remote server | Unable to connect remote server | SEVERE  |
| Unable to validate Workplace URL. Could not connect to the Workplace. Either remote server is down or URL is not in proper format | Invalid  Workplace URL | SEVERE |
| Unable to validate Content Engine URL. Could not connect to the Content Engine. Either Content Engine server is down or URL is not in proper format | Invalid Content Engine URL | SEVERE |
| Required field not specified | Required field not specified | SEVERE |
| Unsupported protocol. Please use http protocol | Used unsupported protocol | SEVERE |
| Username cannot be blank. Please enter valid username | Blank username | SEVERE |
| Password cannot be blank. Please enter valid password | Blank Password | SEVERE |
| Object Store cannot be blank. Please enter a valid Object Store name | Blank object\_store name | SEVERE |
| Workplace URL cannot be blank. Please enter a valid Workplace URL | Blank Workplace display URL | SEVERE |
| Content Engine URL cannot be blank. Please enter a valid Content Engine URL | Blank content engine URL | SEVERE |
| Object Store is invalid. Please enter a valid Object Store name | Invalid object\_store | SEVERE |
| Where Clause is syntactically incorrect | Invalid additional where clause | SEVERE |
| Connector dependency file activation.jar is corrupted or its classpath has been reset | activation jar error | SEVERE |
| Connector dependency file jetty.jar is corrupted or its classpath has been reset | jetty jar error | SEVERE |
|  Connector dependency file builtin\_serialization.jar is corrupted or its classpath has been reset |  builtin serialization jar\_error | SEVERE |
|  Connector dependency file jaxrpc.jar is corrupted or its classpath has been reset |  jaxrpc\_jar\_error | SEVERE |
|  Connector dependency file saaj.jar is corrupted or its classpath has been reset |  saaj\_jar\_error | SEVERE |
| Connector dependency file wsdl\_api.jar is corrupted or its classpath has been reset |  wsdl\_api\_jar\_error | SEVERE |
| **Traversal**  |
| **Log Message** | **Description** | **Level** |
| Query to Add document SELECT TOP 200 Id,DateLastModified FROM Document AS d WHERE VersionStatus=1 and ContentSize IS NOT NULL  AND ((DateLastModified=<<Date Lat Modified>> AND (‘<<Document ID>>’<id)) OR (DateLastModified>=<<Date Lat Modified>>)) ORDER BY DateLastModified,Id | FileNet query to add documents  | INFO |
| Query to get deleted documents SELECT TOP 200 Id,DateCreated,VersionSeriesId FROM {<< version series ID >>} WHERE (DateCreated> <<Date created >>) ORDER BY DateCreated,Id  | FileNet query to delete documents   | INFO |
| Number of documents sent to GSA << number >>| Number of documents sent to GSA at the end of the traversal  | INFO |
| Number of documents whose index will be deleted from GSA << number >>| Number of documents whose index will be deleted from GSA | INFO |
| Result set is NULL, no documents returned for traversal | End of last traversal  | INFO |
| Resuming traversal...| Resuming traversal...| FINER |
| Number of new documents discovered << number >> | Number of new documents discovered| INFO |
| checkpoint {"uuidToDelete""<<Doc id to delete>>","lastRemoveDate""<<Last removal date>>","lastModified""<<Last modified date>>","uuid""<<Doc id to add>>"}| Checkpoint | INFO |
| **AuthN**  |
| **Log Message** | **Description** | **Level** |
| Unable to GET connection or user is not authenticated | Connection failed due to unauthenticated user | INFO |
| Authentication Failed for user << user name >> | Authentication failed due to invalid credentials | INFO |
| Trying to authenticate with Short Name << short name >> | Authentication with short name | INFO |
| User << username >> is authenticated, Authentication Succeeded for user << name >> | Authentication succeeded for user | INFO |
| **AuthZ**  |
| **Log Message** | **Description** | **Level** |
| Got null docids for authZ .. returning null | Empty collection of Document ID’s for authorization  | SEVERE |
| Authorizing docids for user << User name >> | Started Authorizing document lists | INFO |
| Getting version series for document DocID %< ID >%| Getting version series for document | INFO |
| Unable to Decode: Encoding is not supported for the document with ID: << docid >> | UnsupportedEncodingException | WARNING |
| Error : document Version Series Id < docid > may no longer exist. Message: << exception message >>  | RepositoryException | WARNING |
| Authorizing DocID %<  ID>%  for user << Username >>  |  Started Authorizing single document  | INFO|
| Authorizing user << username >>|  Started Authorizing single document  | INFO|
| Authorizing user << username  >> | Authorizing user for permissions | INFO|
| Access Mask is  << access mask >>  | Checking permissions access mask | INFO|
| Grantee Name is  << user name >> is of type USER | Getting grantee name and type over permissions | INFO|
| Grantee Name  << Grantee name >>  does not match with search user  << user name >> . Authorization will continue with the next Grantee Name | Grantee name doesn’t match with Search user name | INFO|
| Authorization for user  << username >> is Successful | Authorization successful  | INFO|