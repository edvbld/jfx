/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.sun.javafx;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class UtilsTest {
    @Test
    public void testSplit() {
        // normal use case
        String s = "VK_ENTER";
        String[] split = Utils.split(s, "_");
        assertEquals("Array content: " + Arrays.toString(split),2, split.length);
        assertEquals("VK", split[0]);
        assertEquals("ENTER", split[1]);

        // normal use case
        s = "VK_LEFT_ARROW";
        split = Utils.split(s, "_");
        assertEquals("Array content: " + Arrays.toString(split),3, split.length);
        assertEquals("VK", split[0]);
        assertEquals("LEFT", split[1]);
        assertEquals("ARROW", split[2]);

        // split with same string as separator - expect empty array
        s = "VK_LEFT_ARROW";
        split = Utils.split(s, "VK_LEFT_ARROW");
        assertEquals("Array content: " + Arrays.toString(split),0, split.length);

        // split with longer string as separator - expect empty array
        s = "VK_LEFT_ARROW";
        split = Utils.split(s, "VK_LEFT_ARROW_EXT");
        assertEquals("Array content: " + Arrays.toString(split),0, split.length);
    }
}