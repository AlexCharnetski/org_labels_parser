package org.example.service.label.impl;

import org.example.service.file.FileService;
import org.example.service.file.exception.ExceptionalFilesForLabelsSearchingException;
import org.example.service.file.impl.FileServiceImpl;
import org.example.service.label.ProcessLabelService;
import org.example.service.xmlparser.XmlParserService;
import org.example.service.xmlparser.impl.XmlParserServiceImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.example.App.*;

public class ProcessLabelServiceImpl implements ProcessLabelService {
    @Override
    public void deleteLabelsFromOrgLabelsXmlFile() {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Please enter the full path and name of the XML file with labels from Org:");
        String xmlFileNameWithLabelsFromOrg = scanner.nextLine();
        System.out.println("Please enter the full path to the directory (including directory name) to find all occurring labels:");
        String pathToDirectoryWithLabels = scanner.nextLine();
        System.out.println("Please enter the full path and name of the file with labels prefixes:");
        String fileWithLabelsPrefixes = scanner.nextLine();
        System.out.println("Please enter the full path and name of the file with exceptional files for searching labels:");
        String fileWithListOfExceptionalFilesForLabelsSearching = scanner.nextLine();

        FileService fileService = new FileServiceImpl();

        List<String> exceptionalFilesForLabelsSearching = null;
        try {
            exceptionalFilesForLabelsSearching = fileService.getListOfStringsFromFile(fileWithListOfExceptionalFilesForLabelsSearching);

            System.out.println("================");
            System.out.println(ANSI_GREEN + "The list of exceptional files for labels searching is:" + ANSI_RESET);
            exceptionalFilesForLabelsSearching.forEach(element -> System.out.println(ANSI_YELLOW + element + ANSI_RESET));

            List<Path> allFiles = null;
            try {
                allFiles = fileService.getAllFiles(pathToDirectoryWithLabels, exceptionalFilesForLabelsSearching);

                XmlParserService xmlParserService = new XmlParserServiceImpl();
                List<String> allLabelsFromOrg = null;
                try {
                    allLabelsFromOrg = xmlParserService.getAllLabelsFromOrg(xmlFileNameWithLabelsFromOrg);

                    List<String> labelsPrefixes = null;
                    try {
                        labelsPrefixes = fileService.getListOfStringsFromFile(fileWithLabelsPrefixes);

                        System.out.println("================");
                        System.out.println(ANSI_GREEN + "The list of labels prefixes is:" + ANSI_RESET);
                        labelsPrefixes.forEach(element -> System.out.println(ANSI_YELLOW + element + ANSI_RESET));

                        List<String> labelsToDelete = null;
                        try {
                            labelsToDelete = fileService.getLabelsToDelete(allLabelsFromOrg, allFiles, labelsPrefixes);

                            System.out.println("================");
                            System.out.println(ANSI_GREEN + "The list of labels to delete is:" + ANSI_RESET);
                            labelsToDelete.forEach(element -> System.out.println(ANSI_RED + element + ANSI_RESET));

                            System.out.println("================");
                            System.out.println(ANSI_GREEN + "The size of the list labels to delete is:" + ANSI_RESET);
                            System.out.println(ANSI_RED + labelsToDelete.size() + ANSI_RESET);
                            try {
                                fileService.deleteLabelsOrNot(labelsToDelete, xmlFileNameWithLabelsFromOrg);
                            } catch (TransformerException e) {
                                System.out.println("Error: something went wrong deleting labels: " + e.getMessage());
                            }
                        } catch (ExceptionalFilesForLabelsSearchingException | IOException e) {
                            System.out.println("Error: something went wrong getting labels to delete" + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.out.println("Error: something went wrong parsing labels prefixes: " + e.getMessage());
                    }
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    System.out.println("Error: something went wrong parsing " + xmlFileNameWithLabelsFromOrg + " file from Org: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error: something went wrong getting all files to parse: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error: something went wrong getting exceptional files for labels searching: " + e.getMessage());
        }
    }
}
