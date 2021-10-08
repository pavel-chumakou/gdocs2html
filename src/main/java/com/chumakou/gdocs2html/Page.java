package com.chumakou.gdocs2html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
        //2. remove redirects
        body = removeRedirect(body);

        //3. render template
        JtwigTemplate jtwigTemplate;
        if (template.isEmpty()) {
            jtwigTemplate = JtwigTemplate.classpathTemplate("template.html");
        } else {
            jtwigTemplate = JtwigTemplate.fileTemplate(template);
        }
        JtwigModel model = JtwigModel.newModel()
                .with("title", title)
                .with("style", style)
                .with("body", body);
        String html = jtwigTemplate.render(model);

        //4. save file
        File outputHtml = new File(outputFile);
        outputHtml.getParentFile().mkdirs();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
        writer.write(html);
        writer.close();

        //5. write message
        System.out.println("Done: " + outputFile);
    }

    private static String removeRedirect(String body){
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

}
