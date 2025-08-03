package com.chumakou.gdocs2html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Created by Pavel Chumakou on 16.03.2019.
 */
public class Page {

    public static void convert(String googleDocUrl, String outputFile, String title, String template) throws IOException{
        Document doc = getDocument(googleDocUrl);
        String style = doc.head().getElementsByTag("style").toString();
        String body = doc.body().toString();
        //1. replace body with div tag
        body = body.replace("<body", "<div").replace("</body>", "</div>");

        //2. extract and save images
        body = extractImages(body, outputFile);

        //3. remove redirects
        body = removeRedirect(body);

        //4. render template
        if (template.isEmpty()) {
            template = loadResourceFileToString("template.html");
        } else {
            Path path = Paths.get(template);
            template = new String(Files.readAllBytes(path));
        }
        template = template.replace("{{ title }}", title);
        template = template.replace("{{ body }}", body);
        template = template.replace("{{ style }}", style);

        //5. save html
        File outputHtml = new File(outputFile);
        if (outputHtml.getParentFile() != null) {
            outputHtml.getParentFile().mkdirs();
        }

        OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFile)), StandardCharsets.UTF_8);
        writer.write(template);
        writer.close();

        //5. write message
        System.out.println("Done: " + outputFile);
    }

    public static String loadResourceFileToString(String fileName) throws IOException {
        ClassLoader classLoader = Page.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }

            // Read the InputStream content using BufferedReader and Collectors.joining
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    private static String removeRedirect(String body) {
        String redirectPrefix = "https://www\\.google\\.com/url\\?q=";
        String redirectPostfix = "&amp;sa=D&amp;";

        body = body.replaceAll(redirectPrefix, "");
        while (body.indexOf(redirectPostfix) > 0) {
            int startPos = body.indexOf(redirectPostfix);
            int endPos = body.indexOf("\"", startPos);
            if (endPos > 0) {
                String toRemove = body.substring(startPos, endPos);
                body = body.replaceAll(toRemove, "");
            }
        }

        return body;
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).userAgent("Mozilla").maxBodySize(0).header("Cache-control", "no-cache").header("Cache-store", "no-store").header("Accept-Encoding", "gzip, deflate").header("User-Agent", "Mozilla/5.0").timeout(20000).get();
    }

    public static String extractImages(String body, String outputFile) throws IOException {
        String outputBody = body;
        File outputDir = getOutputDir(outputFile);
        //"<img alt=\"xxx yyy\" src=\"";
        String imgTag = "<img alt=";
        int startIndex = 0;
        int i = 0;
        while (outputBody.indexOf(imgTag, startIndex) > 0) {
            outputDir.mkdir();
            startIndex = outputBody.indexOf(imgTag, startIndex);
            startIndex = outputBody.indexOf("src=", startIndex);
            startIndex += 5;
            int endIndex = outputBody.indexOf("\"", startIndex);
            String imgSrc = outputBody.substring(startIndex, endIndex);
            i++;
            String imgFileName = "image" + i + ".png";
            saveImage(imgSrc, outputDir, imgFileName);
            outputBody = outputBody.substring(0, startIndex) + "./" + outputDir.getName() + "/" + imgFileName +
                    outputBody.substring(startIndex + imgSrc.length());
        }
        return outputBody;
    }

    private static File getOutputDir(String outputFile) {
        File file = new File(outputFile);
        String outputDirName = file.getName();
        if (outputDirName.endsWith(".html")) {
            outputDirName = outputDirName.substring(0, outputDirName.length() - 5);
        }
        outputDirName += "_files";

        File outputDir;
        if (file.getParentFile() != null) {
            outputDir = new File (file.getParentFile().getAbsolutePath() + File.separator + outputDirName);
        } else {
            outputDir = new File(outputDirName);
        }
        return outputDir;
    }

    public static void saveImage(String imgScr, File outputDir,  String fileName) throws IOException {
        URL imageUrl = new URL(imgScr);
        BufferedImage image = ImageIO.read(imageUrl);
        ImageIO.write(image, "png", new File(outputDir.getAbsolutePath() + File.separator + fileName));
    }

}
