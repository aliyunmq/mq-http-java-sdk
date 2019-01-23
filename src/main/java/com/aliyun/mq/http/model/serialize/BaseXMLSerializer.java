package com.aliyun.mq.http.model.serialize;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BaseXMLSerializer<T> {

    protected static DocumentBuilderFactory factory = DocumentBuilderFactory
            .newInstance();

    private static ThreadLocal<DocumentBuilder> sps = new ThreadLocal<DocumentBuilder>();

    protected DocumentBuilder getDocmentBuilder() throws ParserConfigurationException {
        DocumentBuilder db = sps.get();
        if (db == null) {
            db = factory.newDocumentBuilder();
            sps.set(db);
        }
        return db;
    }
}
