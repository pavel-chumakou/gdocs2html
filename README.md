# Gdocs2html
Gdocs2html is a tool for converting Google Docs to HTML files.

## Features:
Gdocs2html is command-line tool that helps you to convert Google Docs to static HTML files. The main idea of this project is to remove annoying google redirect notices from converted HTML files. Also Gdoc2html supports simple web templates.
As a result, Gdoc2html could be used as a simple static site generator, it allows you to build static web sites from your Google Docs.

## Installation:
Download the latest version of [gdocs2html.jar](https://github.com/pavel-chumakou/gdocs2html/releases/download/1.0.0/gdocs2html.jar) from the [releases](https://github.com/pavel-chumakou/gdocs2html/releases) and put it to any folder. Update your PATH environment variable by adding path to the folder containing gdocs2html.jar. Run ```java -jar gdocs2html.jar --version``` to check everything is working fine.

Gdoc2html requires java version 1.7 or higher.

## How To Use:
Publish your Google Docs documents (File -> Publish to the web...).
Run ```java -jar gdocs2html.jar <googleDocUrl>``` where _googleDocUrl_ is an URL to published Google Docs documents (looks like https://docs.google.com/document/d/e/XXXXXXXXXXXXXXXXXX/pub?embedded=true). The link could be copy-pasted from _File->Publish to the web...->Embed_ window.
Convered Google Docs document will be placed to ```./output.html``` file.

#### Provide output file name:
To define path for output HTML file use ```--outputFile <outputFile>``` option. Gdoc2html automatically creates absent folders.

#### Using templates:
By default simple HTML template used:
```html
<html>
<title>{{ title }}</title>
{{ style }}
<body>
{{ body }}
</body>
</html>
```
You can provide path to custom template by using the following option: ```--template <pathToTemplateFile>```.
Template could contain the following fields:
- {{ title }} – title of the document (provided by ```--title <title>``` option)
- {{ style }} – CSS styles of the Google Docs document
- {{ body }} – HTML content of the Google Docs document

#### Batch processing:
Use scripting for batch processing, below is an example:
```
SET TEMPLATE=D:\MyWebSites\MySite\mytemplate.html
SET OUTPUTDIR=D:\MyWebSites\MySite\generatedContent

@echo off

chcp 65001

java -jar gdocs2html.jar --title "Home Page" --outputFile %OUTPUTDIR%\index.html --template %TEMPLATE% https://docs.google.com/document/d/e/XXXXXXXXXX/pub?embedded=true
java -jar gdocs2html.jar --title "Projects" --outputFile %OUTPUTDIR%\project\projects.html --template %TEMPLATE% https://docs.google.com/document/d/e/XXXXXXXXXX/pub?embedded=true
java -jar gdocs2html.jar --title "Contact" --outputFile %OUTPUTDIR%\main\contacts.html --template %TEMPLATE% https://docs.google.com/document/d/e/XXXXXXXXXX/pub?embedded=true
```

## Command-line arguments:
```
    java -jar gdocs2html.jar [<options>] <googleDocUrl>");
        --outputFile <outputFile> path to output HTML file
        --template <template> path to custom template
        --title <title> title of the output HTML file

    java -jar gdocs2html.jar --version
    java -jar gdocs2html.jar --help
```

## License
Gdocs2html is open-source software licensed under the MIT License.

## Contact
Gdocs2html is developed by Pavel Chumakou. Please open an issue here with questions or bug/feature requests, or you can find my contact information here: http://chumakou.com/


