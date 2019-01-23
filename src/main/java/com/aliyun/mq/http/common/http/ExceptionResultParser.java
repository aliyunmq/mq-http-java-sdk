package com.aliyun.mq.http.common.http;

import java.io.IOException;

import com.aliyun.mq.http.common.ServiceException;
import com.aliyun.mq.http.common.parser.JAXBResultParser;
import com.aliyun.mq.http.common.parser.ResultParseException;
import com.aliyun.mq.http.common.parser.ResultParser;
import com.aliyun.mq.http.common.utils.IOUtils;
import com.aliyun.mq.http.model.ErrorMessage;

public class ExceptionResultParser implements ResultParser<Exception> {
    private String userRequestId;

    public ExceptionResultParser(String userRequestId) {
        super();
        this.userRequestId = userRequestId;
    }

    @Override
    public Exception parse(ResponseMessage response) throws ResultParseException {
        assert response != null;

        if (response.isSuccessful()) {
            return null;
        }

        ServiceException result = null;
        String content = null;
        try {
            content = IOUtils.readStreamAsString(response.getContent(), "UTF-8");
        } catch (IOException e) {
            return new ServiceException(e.getMessage(), userRequestId, e);
        }

        try {
            // 使用jaxb common parser
            JAXBResultParser d = new JAXBResultParser(ErrorMessage.class);
            Object obj = d.parse(content);
            if (obj instanceof ErrorMessage) {
                ErrorMessage err = (ErrorMessage) obj;
                result = new ServiceException(err.Message, null, err.Code, err.RequestId, err.HostId);
            }
        } catch (Exception e) {
            // now treat it as unknown formats
            result = new ServiceException(content, null, "InternalServerError", null, null);
        }

        return result;
    }

}
