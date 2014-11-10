/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @authors Andrew Dinn
 */

package org.my.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * simple app which opens a file and writes some HTML content into it
 */
public class WebWriter {
    /**
     * the name of the html file to be written
     */
    private final String filename;
    /**
     * the name of the user for whom the file is being written
     */
    private final String username;

    /**
     * main routine must be  called with two arguments
     * filename username
     * @param args
     */
    public static void main(String[] args) {
        WebWriter webWriter = new WebWriter(args[0], args[1]);
        PrintStream pos = webWriter.openFile();
        if (pos == null) {
            System.exit(1);
        }
        boolean result;
        result = webWriter.writeHeader(pos);
        if (!result) {
            System.exit(2);
        }
        result = webWriter.writeBody(pos);
        if (!result) {
            System.exit(3);
        }
        webWriter.closeFile(pos);
        if (!result) {
            System.exit(4);
        }
    }

    /**
     * create an instance to write a file for a given user
     *
     * @param filename
     * @param username
     */
    public WebWriter(String filename, String username)
    {
        this.filename = filename;
        this.username = username;
    }

    public PrintStream openFile() {
        File file = new File(filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos, true);
            return ps;
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file " + file.getName());
            System.out.println(e);
            return null;
        }
    }

    public boolean writeHeader(PrintStream pos)
    {
        StringBuilder builder = new StringBuilder();
        makeHeader(builder);
        pos.print(builder.toString());
        if (pos.checkError()) {
            System.out.println("Error writing header");
            return false;
        }
        return true;
    }

    public boolean writeBody(PrintStream pos)
    {
        StringBuilder builder = new StringBuilder();
        makeBody(builder);
        pos.print(builder.toString());
        if (pos.checkError()) {
            System.out.println("Error writing header");
            return false;
        }
        return true;
    }

    public boolean closeFile(PrintStream pos)
    {
        pos.close();
        if (pos.checkError()) {
            System.out.println("Error closing file " + filename);
            return false;
        }
        return true;
    }
    private void makeHeader(StringBuilder builder)
    {
        builder.append("<HEAD>\n");
        builder.append(" <TITLE>\n");
        builder.append("  Welcome to the web page for ");
        builder.append(username);
        builder.append('\n');
        builder.append("  </TITLE>\n");
        builder.append("</HEAD>\n");
        builder.append('\n');
    }

    private void makeBody(StringBuilder builder)
    {
        builder.append("<BODY>\n");
        builder.append(" <H1>\n");
        builder.append("This is the web page for <em>");
        builder.append(username);
        builder.append("</em>\n");
        builder.append("  </H1>\n");
        builder.append("</BODY>\n");
    }
}
