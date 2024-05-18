package com.bravotic.nntp.protocol;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Represents an article stored on the server.
 */
public class Article {
    private Map<String, String> head;
    private String body;

    /**
     * Construct an article given a proper XRef.
     * @param parse The content of the article.
     * @param xref The XRef.
     */
    public Article (String parse, String xref) {
        this(parse);

        head.put("Xref", xref);
    }

    /**
     * Construct an article from content, without an XRef.
     * @param parse The content of the article.
     */
    public Article (String parse) {
        Scanner sc = new Scanner(new StringReader(parse));
        boolean readingHeaders = true;

        int lines = 0;
        head = new HashMap<>();
        StringBuilder bbody = new StringBuilder();
        String lastKey = "";

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (readingHeaders && line.isEmpty()) {
                readingHeaders = false;
            }
            else if (readingHeaders) {
                if (line.startsWith(" ") || line.startsWith("\t")) {
                    head.put(lastKey, line.strip()  + "\r\n " + head.get(lastKey));
                }
                else {
                    try {
                        String key = line.substring(0, line.indexOf(':')).strip();
                        String value = line.substring(line.indexOf(':') + 1).strip();
                        head.put(key, value);
                        lastKey = key;
                    }
                    catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Error: The line \"" + line + "\" is invalid for the header paserer... Ignoring");
                    }
                }
            }
            else {
                bbody.append(line);
                bbody.append("\r\n");
                lines++;
            }
        }

        // Fill in our additional headers if they are not present.
        head.putIfAbsent("Relay-Version", "Snoozenet 1");
        head.putIfAbsent("Posting-Version", "Snoozenet 1");
        head.putIfAbsent("Path", "Snoozenet");
        head.putIfAbsent("Lines", Integer.toString(lines));

        head.putIfAbsent("Message-ID", "<" + UUID.randomUUID().toString()+ ">");
        body = bbody.toString();
        head.putIfAbsent("Bytes", Integer.toString(body.length()));
    }

    public String getHead() {
        StringBuilder b = new StringBuilder();
        for (String header : head.keySet()) {
            b.append(header);
            b.append(": ");
            b.append(head.get(header));
            b.append("\r\n");
        }

        return b.toString();
    }

    public String getBody() {
        return body;
    }

    public String getArticle() {
        return getHead() + "\r\n" + getBody();
    }

    public String getHeader(String key) {
        return head.getOrDefault(key, "");
    }
}
