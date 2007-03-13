package com.google.enterprise.connector.file;

import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.Document;

import com.google.enterprise.connector.file.filewrap.IObjectStore;
import com.google.enterprise.connector.spi.ResultSet;

public class FileResultSet extends LinkedList implements ResultSet {

	private static final long serialVersionUID = 1L;

	private Document doc = null;

	private IObjectStore objectStore = null;

	private String displayUrl;

	private String isPublic;

	public FileResultSet() {
		super();
	}

	public FileResultSet(Document resultDoc, IObjectStore objectStore, String isPublic, String displayUrl ) {
		this.doc = resultDoc;
		this.objectStore = objectStore;
		this.displayUrl = displayUrl;
		this.isPublic = isPublic;
	}

	public Iterator iterator() {
		return new FileDocumentIterator(this.doc, this.objectStore, this.isPublic, this.displayUrl );
	}

}
