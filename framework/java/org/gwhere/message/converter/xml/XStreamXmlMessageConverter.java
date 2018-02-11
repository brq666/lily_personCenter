package org.gwhere.message.converter.xml;

import org.gwhere.message.converter.AbstractMessageConverter;
import org.gwhere.exception.ErrorCode;
import org.gwhere.exception.SystemException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangtao on 15-7-16.
 */
public class XStreamXmlMessageConverter extends AbstractMessageConverter<Object, Object, InputStream, OutputStream> {

    private XStream xstream;

    private SAXReader reader;

    public XStreamXmlMessageConverter() {
        reader = new SAXReader();
        xstream = new XStream(new StaxDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    // 对所有xml节点的转换都增加CDATA标记
                    boolean cdata = true;

                    public void startNode(String name, Class clazz) {
                        super.startNode(name, clazz);
                    }

                    protected void writeText(QuickWriter writer, String text) {
                        if (cdata) {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }
                    }
                };
            }
        });
    }

    public void processAnnotations(Class clazz) {
        xstream.processAnnotations(clazz);
    }

    public void processAnnotations(Class[] classes) {
        xstream.processAnnotations(classes);
    }

    @Override
    protected Object readInternal(Class clazz, InputStream inputMessage) throws IOException {
        if (Map.class.isAssignableFrom(clazz)) {
            Map<String, String> map = new HashMap<>();
            Document doc = null;
            try {
                doc = reader.read(inputMessage);
            } catch (DocumentException e) {
                throw new SystemException(ErrorCode.MESSAGE_CONVERT_FAILED, "Could not read input message", e);
            }
            Element root = doc.getRootElement();
            List<Element> list = root.elements();
            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
            return map;
        } else if (clazz.getAnnotation(XStreamAlias.class) != null) {
            try {
                return xstream.fromXML(inputMessage, clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new SystemException(ErrorCode.MESSAGE_CONVERT_FAILED, "Could not read message with " + clazz.getName(), e);
            }
        } else if (clazz == byte[].class || clazz == String.class) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputMessage.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            if (clazz == byte[].class) {
                return out.toByteArray();
            } else {
                return new String(out.toByteArray(), "UTF-8");
            }
        } else {
            throw new SystemException(ErrorCode.MESSAGE_CONVERT_FAILED, "Unsupported message type: " + clazz.getName());
        }
    }

    public String toXML(Object writeData){
        return   xstream.toXML(writeData);
    }

    @Override
    protected void writeInternal(Object writeData, OutputStream outputMessage) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage, "UTF-8");
        if (logger.isDebugEnabled()) {
            logger.debug(xstream.toXML(writeData));
        }
        xstream.toXML(writeData, writer);
    }

    @Override
    public boolean canRead(Class<?> clazz) {
        return clazz == String.class || clazz == byte[].class || clazz.isAssignableFrom(Map.class) || clazz.getAnnotation(XStreamAlias.class) != null;
    }

    @Override
    public boolean canWrite(Class<?> clazz) {
        return clazz.isAnnotationPresent(XStreamAlias.class);
    }
}
