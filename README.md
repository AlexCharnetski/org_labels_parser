## Java version
At least version 11 of Java

## How to use

* At the _"Please enter the full path and name of the XML file with labels from Org:"_ prompt, type the following (_instead of `[path_to_project]` enter the path to your project_):
  ```
  [path_to_project]/force-app/main/default/labels/CustomLabels.labels-meta.xml
  ```

* At the _"Please enter the full path to the directory (including directory name) to find all occurring labels:"_ prompt, type the following (_instead of `[path_to_project]` enter the path to your project_):
  ```
  [path_to_project]/force-app/main/default
  ```
  or
  ```
  [path_to_project]
  ```

* At the _"Please enter the full path and name of the file with labels prefixes:"_ prompt, type the following (_instead of `[path_to_file_with_labels_prefixes]` enter the path to file with labels prefixes and file name `[file_name]`_):
  ```
  [path_to_file_with_labels_prefixes]/[file_name]
  ```
   Example of contains of this file (_copy these values to this file_):
  ```
  System.Label.
  Label.
  $Label.
  c.
  ```

* At the _"Please enter the full path and name of the file with exceptional files for searching labels:"_ prompt, type the following (_instead of `[path_to_file_with_exceptional_files]` enter the path to file with list of files where we do not need searching existing labels and file name `[file_name]`_):
  ```
  [path_to_file_with_exceptional_files]/[file_name]
  ```

  Example of contains of this file (_copy these values to this file_):
    ```
  CustomLabels.labels-meta.xml
  package.xml
  workspace.xml
    ```
