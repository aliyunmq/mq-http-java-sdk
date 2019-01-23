package com.aliyun.mq.http.model;

import com.aliyun.mq.http.common.Constants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error", namespace = Constants.DEFAULT_XML_NAMESPACE)
public class ErrorMessage {
    @XmlElement(name = "Code", namespace = Constants.DEFAULT_XML_NAMESPACE)
    public String Code;

    @XmlElement(name = "Message", namespace = Constants.DEFAULT_XML_NAMESPACE)
    public String Message;

    @XmlElement(name = "RequestId", namespace = Constants.DEFAULT_XML_NAMESPACE)
    public String RequestId;

    @XmlElement(name = "Method", namespace = Constants.DEFAULT_XML_NAMESPACE)
    public String Method;

    @XmlElement(name = "HostId", namespace = Constants.DEFAULT_XML_NAMESPACE)
    public String HostId;
}
