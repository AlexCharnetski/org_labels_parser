package org.example.service.file.impl;

import org.example.service.file.FileService;
import org.example.service.file.exception.ExceptionalFilesForLabelsSearchingException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.App.*;

public class FileServiceImpl implements FileService {
    private static final String OPENING_TAG_NAME_FULL_NAME = "<fullName>";
    private static final String CLOSING_TAG_NAME_FULL_NAME = "</fullName>";
    private static final String OPENING_TAG_NAME_CATEGORIES = "<categories>";
    private static final byte MAX_LINE_NUMBER_TO_DELETE = 8;

    @Override
    public List<Path> getAllFiles(String path, List<String> exceptionalFilesForLabelsSearching) throws IOException {
        List<Path> filePaths = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(Paths.get(path))) {
            stream
                .filter(Files::isRegularFile)
                .forEach(filePaths::add);
        }
        return getFilePathsWithoutCustomLabelXmlFileName(filePaths, exceptionalFilesForLabelsSearching);
    }

    @Override
    public List<String> getLabelsToDelete(List<String> allLabels, List<Path> allFiles, List<String> labelsPrefixes) throws ExceptionalFilesForLabelsSearchingException, IOException {
        List<String> labelsToDelete = new ArrayList<>();
        if (allLabels == null || allLabels.isEmpty()) {
            throw new ExceptionalFilesForLabelsSearchingException("There are no labels");
        }
        for (String label : allLabels) {
            if (!isLabelUsed(label, allFiles, labelsPrefixes)) {
                labelsToDelete.add(label);
            }
        }
        return labelsToDelete;
    }

    @Override
    public List<String> getListOfStringsFromFile(String filePath) throws IOException {
        List<String> stringsFromFile = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(stringsFromFile::add);
        }
        return stringsFromFile;
    }

    @Override
    public void deleteLabelsOrNot(List<String> labelsToDelete, String xmlFileNameWithLabelsFromOrg) throws TransformerException {
        Scanner kbd = new Scanner(System.in);
        String decision;

        System.out.println("================");
        System.out.println(ANSI_GREEN + "Do you really want to delete these labels?" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "y/n" + ANSI_RESET);
        decision = kbd.nextLine();

        switch (decision) {
            case "y": {
                deleteLabels(labelsToDelete, xmlFileNameWithLabelsFromOrg);
                break;
            }
            case "n": {
                System.out.println("================");
                System.out.println(ANSI_GREEN + "You can try again by running this console app again" + ANSI_RESET);
                break;
            }
            default: {
                System.out.println(ANSI_GREEN + "Please enter \"y\" or \"n\"" + ANSI_RESET);
                boolean repeat = true;

                while (repeat) {
                    decision = kbd.nextLine();

                    switch (decision) {
                        case "y": {
                            repeat = false;
                            deleteLabels(labelsToDelete, xmlFileNameWithLabelsFromOrg);
                            break;
                        }
                        case "n": {
                            repeat = false;
                            System.out.println("================");
                            System.out.println(ANSI_GREEN + "You can try again by running this console app again" + ANSI_RESET);
                            break;
                        }
                        default: {
                            System.out.println(ANSI_GREEN + "Please enter \"y\" or \"n\"" + ANSI_RESET);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private void deleteLabels(List<String> labelsToDelete, String xmlFileNameWithLabelsFromOrg) throws TransformerException {
        List<String> labelsWithoutLabelsToDelete = getLabelsWithoutLabelsToDelete(labelsToDelete, xmlFileNameWithLabelsFromOrg);
        createNewXmlFileWithoutLabelsToDelete(labelsWithoutLabelsToDelete, xmlFileNameWithLabelsFromOrg);
        System.out.println("================");
        System.out.println(ANSI_RED + "The labels were deleted" + ANSI_RESET);
    }

    private static void createNewXmlFileWithoutLabelsToDelete(List<String> labelsWithoutLabelsToDelete, String xmlFileNameWithLabelsFromOrg) throws TransformerException {
        String joinedXmlString = String.join("", labelsWithoutLabelsToDelete);
        Document xmlDocument = convertStringToXMLDocument(joinedXmlString);
        writeDocumentToXmlFile(xmlFileNameWithLabelsFromOrg, xmlDocument);
    }

    private static void writeDocumentToXmlFile(String xmlFileNameWithLabelsFromOrg, Document xmlDocument) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(xmlDocument);

        StreamResult result = new StreamResult(new File(xmlFileNameWithLabelsFromOrg));
        transformer.transform(source, result);
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            StringReader characterStream = new StringReader(xmlString);
            InputSource inputSource = new InputSource(characterStream);
            Document doc = builder.parse(inputSource);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<String> getLabelsWithoutLabelsToDelete(List<String> labelsToDelete, String xmlFileNameWithLabelsFromOrg) {
        List<String> copyOfAllLinesFromXmlFileWithLabelsFromOrg = new ArrayList<>();
        try {
            List<String> allLinesFromXmlFileWithLabelsFromOrg = Files.readAllLines(Paths.get(xmlFileNameWithLabelsFromOrg));
            copyOfAllLinesFromXmlFileWithLabelsFromOrg = new ArrayList<>(allLinesFromXmlFileWithLabelsFromOrg);
            for (String label : labelsToDelete) {
                for (String line : allLinesFromXmlFileWithLabelsFromOrg) {
                    String trimmedLine;
                    if (line != null && !line.isBlank()) {
                        trimmedLine = line.trim();
                        if (trimmedLine.startsWith(OPENING_TAG_NAME_FULL_NAME) && trimmedLine.endsWith(CLOSING_TAG_NAME_FULL_NAME)) {
                            trimmedLine = trimmedLine.replace(OPENING_TAG_NAME_FULL_NAME, "");
                            trimmedLine = trimmedLine.replace(CLOSING_TAG_NAME_FULL_NAME, "");
                            if (label.equals(trimmedLine)) {
                                int currentIndex = copyOfAllLinesFromXmlFileWithLabelsFromOrg.indexOf(line);
                                String categories = copyOfAllLinesFromXmlFileWithLabelsFromOrg.get(currentIndex + 1);
                                try {
                                    if (categories.trim().startsWith(OPENING_TAG_NAME_CATEGORIES)) {
                                        for (byte i = 0; i < MAX_LINE_NUMBER_TO_DELETE; i++) {
                                            copyOfAllLinesFromXmlFileWithLabelsFromOrg.remove(currentIndex - 1);
                                        }
                                    } else {
                                        for (byte i = 0; i < MAX_LINE_NUMBER_TO_DELETE - 1; i++) {
                                            copyOfAllLinesFromXmlFileWithLabelsFromOrg.remove(currentIndex - 1);
                                        }
                                    }
                                } catch (RuntimeException e) {
                                    System.out.println("There is no such element in the list with an index equal to " + currentIndex + " or from " + (currentIndex - 1) + " to " + (currentIndex + 6));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Something went wrong while trying to read the file: " + xmlFileNameWithLabelsFromOrg);
        }

        return copyOfAllLinesFromXmlFileWithLabelsFromOrg;
    }

    private static List<Path> getFilePathsWithoutCustomLabelXmlFileName(List<Path> filePaths, List<String> exceptionalFilesForLabelsSearching) {
        return Optional.ofNullable(filePaths).stream()
            .flatMap(Collection::stream)
            .filter(filePath -> isCorrectFilePath(filePath, exceptionalFilesForLabelsSearching))
            .collect(Collectors.toList());
    }

    private static boolean isCorrectFilePath(Path filePath, List<String> exceptionalFilesForLabelsSearching) {
        for (String exceptionalFile : exceptionalFilesForLabelsSearching) {
            if (filePath.toString().contains(exceptionalFile)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLabelUsed(String label, List<Path> files, List<String> labelsPrefixes) throws IOException {
        for (Path file : files) {
            if (doesFileContainLabel(label, file, labelsPrefixes)) {
                return true;
            }
        }
        return false;
    }

    private static boolean doesFileContainLabel(String label, Path file, List<String> labelsPrefixes) throws IOException {
        final Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();
            for (String labelPrefix : labelsPrefixes) {
                if (lineFromFile.contains(labelPrefix + label)) {
                    return true;
                }
            }
        }
        return false;
    }
}
