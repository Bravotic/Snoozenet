package com.bravotic.nntp;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ServerConfigReader {

    private final Document settings;

    public ServerConfigReader() {
        File file = new File("config.xml");

        if (file.exists()) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                settings = documentBuilder.parse(file);
            }
            catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            settings = null;
        }
    }

    protected String getSetting(String settingName, String orDefault) {
        if (settings != null && settings.getElementsByTagName(settingName).getLength() != 0) {
            return settings.getElementsByTagName(settingName).item(0).getTextContent();
        }
        else {
            return orDefault;
        }
    }

    public String getHost() {
        return this.getSetting("host", "127.0.0.1");
    }

    public int getPort() {
        return Integer.parseInt(this.getSetting("port", "119"));
    }

    public String getBackend() {
        return this.getSetting("backend", "fs");
    }

    public String getFSPath() {
        return this.getSetting("fs-path", "/tmp/news");
    }
}
