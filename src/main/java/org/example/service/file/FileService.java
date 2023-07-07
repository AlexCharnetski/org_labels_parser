package org.example.service.file;

import org.example.service.file.exception.ExceptionalFilesForLabelsSearchingException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<Path> getAllFiles(String path, List<String> exceptionalFilesForLabelsSearching) throws IOException;

    List<String> getLabelsToDelete(List<String> allLabels, List<Path> allFiles, List<String> labelsPrefixes) throws ExceptionalFilesForLabelsSearchingException, IOException;

    List<String> getListOfStringsFromFile(String filePath) throws IOException;

    void deleteLabelsOrNot(List<String> labelsToDelete, String xmlFileNameWithLabelsFromOrg) throws TransformerException;

}
