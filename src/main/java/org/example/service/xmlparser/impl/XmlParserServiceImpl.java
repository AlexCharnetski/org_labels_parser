package org.example.service.xmlparser.impl;

import org.example.service.xmlparser.XmlParserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParserServiceImpl implements XmlParserService {
    private static final String TAG_NAME_LABELS = "labels";
    private static final String TAG_NAME_FULL_NAME = "fullName";

    @Override
    public List<String> getAllLabelsFromOrg(String xmlFileNameWithLabelsFromOrg) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFileNameWithLabelsFromOrg));
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName(TAG_NAME_LABELS);
        List<String> fullNameOfLabels = new ArrayList<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                fullNameOfLabels.add(eElement.getElementsByTagName(TAG_NAME_FULL_NAME).item(0).getTextContent());
            }
        }
        return fullNameOfLabels;
    }
}
