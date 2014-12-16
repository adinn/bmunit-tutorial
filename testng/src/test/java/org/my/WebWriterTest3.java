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

package org.my;

import org.jboss.byteman.contrib.bmunit.BMNGListener;
import org.jboss.byteman.contrib.bmunit.BMNGRunner;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.my.app.WebWriter;


import java.io.PrintStream;

/**
 * simple unit test class for WebWriter app
 */
@BMUnitConfig(loadDirectory="target/test-classes")
@BMScript(value="check.btm")
// @Listeners(BMNGListener.class)
public class WebWriterTest3 extends BMNGRunner
{
    @Test
    @BMRule(name = "handle file not found",
            targetClass = "FileOutputStream",
            targetMethod = "<init>(File)",
            action = "throw new FileNotFoundException( \"Ha ha Byteman fooled you again!\" )"
            )
    public void handleFileNotFound()
    {
        System.out.println("-------- handleFileNotFound ---------");
        WebWriter writer = new WebWriter("foo.html", "Andrew");
        PrintStream ps = writer.openFile();
        Assert.assertTrue(ps == null);
        System.out.println("-------- handleFileNotFound ---------\n");
    }

    @Test
    @BMRule(name = "test write body",
            targetClass = "FileOutputStream",
            targetMethod = "write(byte[],int,int)",
            condition = "incrementCounter($0) == 2",
            action = "throw new java.io.IOException( \"Ha ha Byteman fooled you!\" )"
            )
    public void testWriteBody()
    {
        System.out.println("-------- testWriteBody ---------");
        WebWriter writer = new WebWriter("foo.html", "Andrew");
        PrintStream ps = writer.openFile();
        boolean result = writer.writeHeader(ps);
        Assert.assertTrue(result);
        result = writer.writeBody(ps);
        ps.close();
        Assert.assertFalse(result);
        System.out.println("-------- testWriteBody ---------\n");
    }
}
