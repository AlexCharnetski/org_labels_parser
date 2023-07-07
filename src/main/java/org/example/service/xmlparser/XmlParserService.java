package org.example.service.xmlparser;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public interface XmlParserService {

    List<String> getAllLabelsFromOrg(String xmlFileNameWithLabelsFromOrg) throws IOException, SAXException, ParserConfigurationException;

}
