package com.aliyun.mq.http.common.parser;

import com.aliyun.mq.http.common.http.ResponseMessage;
import com.aliyun.mq.http.common.utils.ResourceManager;
import com.aliyun.mq.http.common.utils.ServiceConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class JAXBResultParser implements ResultParser<Object> {

    private static final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    // Because JAXBContext.newInstance() is a very slow method,
    // it can improve performance a lot to cache the instances of JAXBContext
    // for used context paths or class types.
    private static HashMap<Object, JAXBContext> cachedContexts = new HashMap<Object, JAXBContext>();

    static {
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setValidating(false);
    }

    // It allows to specify the class type, if the class type is specified,
    // the contextPath will be ignored.
    private Class<?> modelClass;

    public JAXBResultParser(Class<?> modelClass) {
        assert (modelClass != null);
        this.modelClass = modelClass;
    }

    private static synchronized void initJAXBContext(Class<?> c) throws JAXBException {
        if (!cachedContexts.containsKey(c)) {
            JAXBContext jc = JAXBContext.newInstance(c);
            cachedContexts.put(c, jc);
        }
    }

    private static SAXSource getSAXSource(InputStream content) throws SAXException, ParserConfigurationException {

        SAXParser saxParser = saxParserFactory.newSAXParser();
        return new SAXSource(saxParser.getXMLReader(), new InputSource(content));
    }

    public Object parse(String content) throws ResultParseException {
        assert (content != null);
        return parse(new ByteArrayInputStream(content.getBytes()));
    }

    public Object parse(ResponseMessage response) throws ResultParseException {
        assert (response != null && response.getContent() != null);
        return parse(response.getContent());
    }

    public Object parse(InputStream is) throws ResultParseException {
        assert (is != null);

        try {
            if (!cachedContexts.containsKey(modelClass)) {
                initJAXBContext(modelClass);
            }

            assert (cachedContexts.containsKey(modelClass));
            JAXBContext jc = cachedContexts.get(modelClass);
            Unmarshaller um = jc.createUnmarshaller();
            // It performs better to call Unmarshaller#unmarshal(Source)
            // than to call Unmarshaller#unmarshall(InputStream)
            // if XMLReader is specified in the SAXSource instance.
            return um.unmarshal(getSAXSource(is));
        } catch (JAXBException e) {
            throw new ResultParseException(ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                    .getString("FailedToParseResponse"), e);
        } catch (SAXException e) {
            throw new ResultParseException(ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                    .getString("FailedToParseResponse"), e);
        } catch (ParserConfigurationException e) {
            throw new ResultParseException(ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                    .getString("FailedToParseResponse"), e);
        }
    }
}