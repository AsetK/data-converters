package com.epam.dataconverters.transformer;

import net.sf.saxon.TransformerFactoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.*;

@Component
public class XSLTTransformer {

    private final Resource template;

    public XSLTTransformer(@Value("classpath:xslt/template.xslt") Resource template){
        this.template = template;
    }

    public String transform(String xml, Map<String, Object> propsMap) throws IOException, TransformerException {
        InputStream inputStream = template.getInputStream();
        StreamSource streamSource = new StreamSource(inputStream);


        TransformerFactory transformerFactory = new TransformerFactoryImpl();
        Transformer transformer = transformerFactory.newTransformer(streamSource);

        Iterator<String> key = propsMap.keySet().iterator();
        Iterator<Object> value = propsMap.values().iterator();

        transformer.setParameter(key.next(), value.next());
        transformer.setParameter(key.next(), value.next());
        transformer.setParameter(key.next(), value.next());

        Source xmlSrc = new StreamSource(IOUtils.toInputStream(xml));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Result output = new StreamResult(os);

        transformer.transform(xmlSrc, output);

        return new String(os.toByteArray(), StandardCharsets.UTF_8);

    }


}
