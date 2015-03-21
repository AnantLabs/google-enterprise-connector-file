# Advanced configuration for the FileNet connector #
The details mentioned in this document are applicable to connector version 2.0.0 and above.

It applies to connectors for both IBM FileNet server installation v3.5 and v4.0


## Configure the connectorInstance.xml file ##
The advanced configuration is done through the modification of the file "connectorInstance.xml". This file is available in the connector instance directory:

**For FileNet connector for server version 3.5:
```
\GoogleConnectors\FileNET>\Tomcat\webapps\connector-manager\WEB-INF\connectors\Filenet_P8_3.5.2\<connector-instance>
```**

**For FileNet connector for server version 4.0:
```
\GoogleConnectors\FileNET>\Tomcat\webapps\connector-manager\WEB-INF\connectors\Filenet_P8\<connector-instance>
```**

The default values for any attribute that are supposed to be configured as part of the Advanced configuration are set in a file named connectorDefaults.xml in the connector-file.jar file located in connector-manager/WEB-INF/lib. The connectorInstance.xml is empty and has a reference to the Spring bean defined in connectorDefaults.xml.

For overriding the default value of any attribute, the attribute needs to be uncommented in connectorInstance.xml along with the new value and restart the connector. The connector will then respect and use the value set in connectorInstance.xml and not connectorDefaults.xml


By changing the connectorInstance.xml file, you can change the included/excluded metadata list for the FileNet documents.

### Change Included/Excluded metadata ###

By modifying the value of the property named “excluded\_meta”, you can provide the list of metadata which should not be fed to the appliance
Following is the default list of excluded metadata configured in connectorDefaults.xml:
```
<property name="excluded_meta">
  <set>
	<value>AccessMask</value>
	<value>ActiveMarkings</value>
	<value>Annotations</value>
	<value>AuditedEvents</value>
	<value>ClassDescription</value>
	<value>ContentElements</value>
	<value>ContentElementsPresent</value>
	<value>ContentRetentionDate</value>
	<value>CreatePending</value>
	<value>CurrentVersion</value>
	<value>DateContentLastAccessed</value>
	<value>DeletePending</value>
	<value>DestinationDocuments</value>
	<value>DocumentLifecyclePolicy</value>
	<value>EntryTemplateId</value>
	<value>EntryTemplateLaunchedWorkflowNumber</value>
	<value>EntryTemplateObjectStoreName</value>
	<value>FoldersFiledIn</value>
	<value>IsInExceptionState</value>
	<value>IsVersioningEnabled</value>
	<value>LockOwner</value>
	<value>ObjectStore</value>
	<value>ObjectType</value>
	<value>OIID</value>
	<value>OwnerDocument</value>
	<value>PendingOperation</value>
	<value>Properties</value>
	<value>PublicationInfo</value>
	<value>ReleasedVersion</value>
	<value>Reservation</value>
	<value>ReservationType</value>
	<value>SecurityParent</value>
	<value>SecurityPolicy</value>
	<value>SourceDocument</value>
	<value>StoragePolicy</value>
	<value>UpdatePending</value>
	<value>VersionSeries</value>
	<value>WorkflowSubscriptions</value>
  </set>
</property>
```

These metadata values are commented in connectorInstance.xml. The `<value>` node contains the metadata attribute name to be excluded. Add/delete any node as required.

By modifying the value of the property named "included\_meta", you can provide the list of metadata which appliance can crawl for a document.

Following is the default list of included metadata configured in connectorDefaults.xml:

```
<property name="included_meta">
  <set>
	<value>ClassificationStatus</value>
	<value>ContentSize</value>
	<value>CurrentState</value>
	<value>DateCreated</value>
	<value>DateLastModified</value>
	<value>DocumentTitle</value>
	<value>Id</value>
	<value>IsCurrentVersion</value>
	<value>IsFrozenVersion</value>
	<value>IsReserved</value>
	<value>LastModifier</value>
	<value>LockTimeout</value>
	<value>LockToken</value>
	<value>MajorVersionNumber</value>
	<value>MimeType</value>
	<value>MinorVersionNumber</value>
	<value>Name</value>
	<value>Owner</value>
	<value>StorageLocation</value>
	<value>VersionStatus</value>
  </set>
</property>
```

#### Scenarios while specifying metadata ####
a) If you want to index all the metadata values of the documents, keep "included\_meta" and "excluded\_meta" as blank.

> E.g.
```
	<property name="included_meta">
          <set>	</set>
        </property>

	<property name="excluded_meta">
	   <set>	</set>
        </property>
```

b) If you want to filter\skip specific metadata values of the documents from indexing, put respective metadata values under ="excluded\_meta".

> E.g.
```
        <property name="excluded_meta">
	  <set>
		<value>AccessMask</value>
                <value>ObjectType</value>
          </set>
        </property>
```

> This will allow connector to filter out metadata AccessMask & ObjectType while submitting documents to GSA for indexing

c) If you want to index only specific metadata values of the documents, put respective metadata values under ="included\_meta".

> E.g.
```
        <property name="included_meta">
	  <set>
		<value>Name</value>
                <value>Owner</value>
          </set>
        </property>
```

> This will allow connector to include only metadata Name & Owner while submitting documents to GSA for indexing

For more details regarding each of the attributes and their impact on the connector traversal please refer to the connector documentation.