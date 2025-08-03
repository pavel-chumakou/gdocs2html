package com.chumakou.gdocs2html;

import java.io.IOException;

/**
 * Created by Pavel Chumakou on 16.03.2019.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        //defaults
        String title = "";
        String outputFile = "output.html";
        String template = ""; // by default will be used resources/template.html
        String version = "1.1";

        if (args.length == 0) {
            printUsage();
            return;
        }
        if (args.length == 1) {
            String arg = args[0];
            if ( arg.equals("--version") || arg.equals("-v") ) {
                System.out.println(version);
                return;
            }
            if ( arg.equals("--help") || arg.equals("-h") ) {
                printUsage();
                return;
            }
            Page.convert(arg, outputFile, title, template);
            return;
        }
        int i = 0;
        while (i < args.length - 1) {
            String arg = args[i];
            if (i % 2 == 0) {
                if (i == args.length - 1) {
                    System.out.println("Wrong parameter: " + arg);
                    printUsage();
                    return;
                }
                if (arg.equals("--title")) {
                    title = args[i+1];
                    i++;
                } else if (arg.equals("--outputFile")) {
                    outputFile = args[i+1];
                    i++;
                } else if (arg.equals("--template")) {
                    template = args[i+1];
                    i++;
                } else {
                    System.out.println("Wrong parameter: " + arg);
                    printUsage();
                    return;
                }
            }
            i++;
        }
        Page.convert(args[args.length - 1], outputFile, title, template);

    }

    private static void printUsage(){
        System.out.println("Usage: java -jar gdocs2html.jar [<options>] <googleDocUrl>");
        System.out.println("\t--outputFile <outputFile> \t path to output HTML file ");
        System.out.println("\t--template <template> \t path to custom template");
        System.out.println("\t--title <title> \t title of the output HTML file");
        System.out.println("java -jar gdocs2html.jar --version");
        System.out.println("java -jar gdocs2html.jar --help");
    }
}
