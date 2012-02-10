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


package javafx.scene.layout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.shape.Rectangle;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class GridPaneTest {
    GridPane gridpane;

    @Before public void setUp() {
        this.gridpane = new GridPane();
    }

    @Test public void testGridPaneDefaults() {
        assertEquals(0, gridpane.getHgap(), 0);
        assertEquals(0, gridpane.getVgap(), 0);
        assertEquals(Pos.TOP_LEFT, gridpane.getAlignment());
        assertFalse(gridpane.isGridLinesVisible());
        assertEquals(0, gridpane.getColumnConstraints().size());
        assertEquals(0, gridpane.getRowConstraints().size());
    }

    @Test public void testSimpleGridPane() {
        // populate 2x2 grid
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        assertEquals(200, gridpane.minWidth(-1), 0);
        assertEquals(400, gridpane.minHeight(-1), 0);
        assertEquals(500, gridpane.prefWidth(-1), 0);
        assertEquals(400, gridpane.prefHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(0,   child0_0.getLayoutX(), 1e-100);
        assertEquals(0,   child0_0.getLayoutY(), 1e-100);
        assertEquals(300, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_0.getLayoutX(), 1e-100);
        assertEquals(0,   child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0,   child0_1.getLayoutX(), 1e-100);
        assertEquals(100, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }


    @Test public void testGridPaneAlignmentTopLeft() {
        gridpane.setAlignment(Pos.TOP_LEFT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(0,   child0_0.getLayoutX(), 1e-100);
        assertEquals(0,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_0.getLayoutX(), 1e-100);
        assertEquals(0,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0,   child0_1.getLayoutX(), 1e-100);
        assertEquals(200, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_1.getLayoutX(), 1e-100);
        assertEquals(200, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentTopCenter() {
        gridpane.setAlignment(Pos.TOP_CENTER);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(200,   child0_0.getLayoutX(), 1e-100);
        assertEquals(0,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_0.getLayoutX(), 1e-100);
        assertEquals(0,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200,   child0_1.getLayoutX(), 1e-100);
        assertEquals(200, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_1.getLayoutX(), 1e-100);
        assertEquals(200, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentTopRight() {
        gridpane.setAlignment(Pos.TOP_RIGHT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(400,   child0_0.getLayoutX(), 1e-100);
        assertEquals(0,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_0.getLayoutX(), 1e-100);
        assertEquals(0,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400,   child0_1.getLayoutX(), 1e-100);
        assertEquals(200, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_1.getLayoutX(), 1e-100);
        assertEquals(200, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentCenterLeft() {
        gridpane.setAlignment(Pos.CENTER_LEFT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(0,   child0_0.getLayoutX(), 1e-100);
        assertEquals(200,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_0.getLayoutX(), 1e-100);
        assertEquals(200,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0,   child0_1.getLayoutX(), 1e-100);
        assertEquals(400, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_1.getLayoutX(), 1e-100);
        assertEquals(400, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentCenter() {
        gridpane.setAlignment(Pos.CENTER);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(200,   child0_0.getLayoutX(), 1e-100);
        assertEquals(200,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_0.getLayoutX(), 1e-100);
        assertEquals(200,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200,   child0_1.getLayoutX(), 1e-100);
        assertEquals(400, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_1.getLayoutX(), 1e-100);
        assertEquals(400, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentCenterRight() {
        gridpane.setAlignment(Pos.CENTER_RIGHT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(400,   child0_0.getLayoutX(), 1e-100);
        assertEquals(200,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_0.getLayoutX(), 1e-100);
        assertEquals(200,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400,   child0_1.getLayoutX(), 1e-100);
        assertEquals(400, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_1.getLayoutX(), 1e-100);
        assertEquals(400, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentBottomLeft() {
        gridpane.setAlignment(Pos.BOTTOM_LEFT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(0,   child0_0.getLayoutX(), 1e-100);
        assertEquals(400,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_0.getLayoutX(), 1e-100);
        assertEquals(400,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0,   child0_1.getLayoutX(), 1e-100);
        assertEquals(600, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200, child1_1.getLayoutX(), 1e-100);
        assertEquals(600, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentBottomCenter() {
        gridpane.setAlignment(Pos.BOTTOM_CENTER);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(200,   child0_0.getLayoutX(), 1e-100);
        assertEquals(400,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_0.getLayoutX(), 1e-100);
        assertEquals(400,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(200,   child0_1.getLayoutX(), 1e-100);
        assertEquals(600, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400, child1_1.getLayoutX(), 1e-100);
        assertEquals(600, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneAlignmentBottomRight() {
        gridpane.setAlignment(Pos.BOTTOM_RIGHT);

        // populate 2x2 grid   pref size = 400 x 400
        MockResizable child0_0 = new MockResizable(100,100);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(200, 200);
        GridPane.setConstraints(child1_0, 1, 0);
        Rectangle child0_1 = new Rectangle(200,200);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child0_1, child1_1);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(400,   child0_0.getLayoutX(), 1e-100);
        assertEquals(400,   child0_0.getLayoutY(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_0.getLayoutX(), 1e-100);
        assertEquals(400,   child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(400,   child0_1.getLayoutX(), 1e-100);
        assertEquals(600, child0_1.getLayoutY(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(600, child1_1.getLayoutX(), 1e-100);
        assertEquals(600, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneSetRowIndexConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getRowIndex(child1));

        GridPane.setRowIndex(child1, 2);
        assertEquals((int)2, (int)GridPane.getRowIndex(child1));

        GridPane.setRowIndex(child1, null);
        assertNull(GridPane.getRowIndex(child1));
    }

    @Test public void testGridPaneSetColumnIndexConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getColumnIndex(child1));

        GridPane.setColumnIndex(child1, 2);
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));

        GridPane.setColumnIndex(child1, null);
        assertNull(GridPane.getColumnIndex(child1));
    }

    @Test public void testGridPaneSetRowSpanConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getRowSpan(child1));

        GridPane.setRowSpan(child1, 2);
        assertEquals((int)2, (int)GridPane.getRowSpan(child1));

        GridPane.setRowSpan(child1, null);
        assertNull(GridPane.getRowSpan(child1));
    }

    @Test public void testGridPaneSetColumnSpanConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getColumnSpan(child1));

        GridPane.setColumnSpan(child1, 2);
        assertEquals((int)2, (int)GridPane.getColumnSpan(child1));

        GridPane.setColumnSpan(child1, null);
        assertNull(GridPane.getColumnSpan(child1));
    }


    @Test public void testGridPaneSetMarginConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getMargin(child1));

        Insets margin = new Insets(10,20,30,40);
        GridPane.setMargin(child1, margin);
        assertEquals(margin, GridPane.getMargin(child1));

        GridPane.setMargin(child1, null);
        assertNull(GridPane.getMargin(child1));
    }

    @Test public void testGridPaneSetHgrowConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getHgrow(child1));

        GridPane.setHgrow(child1, Priority.ALWAYS);
        assertEquals(Priority.ALWAYS, GridPane.getHgrow(child1));

        GridPane.setHgrow(child1, null);
        assertNull(GridPane.getHgrow(child1));
    }

    @Test public void testGridPaneSetVgrowConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getVgrow(child1));

        GridPane.setVgrow(child1, Priority.ALWAYS);
        assertEquals(Priority.ALWAYS, GridPane.getVgrow(child1));

        GridPane.setVgrow(child1, null);
        assertNull(GridPane.getVgrow(child1));
    }

    @Test public void testGridPaneSetHalignmentConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getHalignment(child1));

        GridPane.setHalignment(child1, HPos.CENTER);
        assertEquals(HPos.CENTER, GridPane.getHalignment(child1));

        GridPane.setHalignment(child1, null);
        assertNull(GridPane.getHalignment(child1));
    }

    @Test public void testGridPaneSetValignmentConstraint() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        assertNull(GridPane.getValignment(child1));

        GridPane.setValignment(child1, VPos.CENTER);
        assertEquals(VPos.CENTER, GridPane.getValignment(child1));

        GridPane.setValignment(child1, null);
        assertNull(GridPane.getValignment(child1));
    }

    @Test public void testGridPaneSetConstraints() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        GridPane.setConstraints(child1, 2,6);
        assertEquals((int)6, (int)GridPane.getRowIndex(child1));
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));
    }

    @Test public void testGridPaneSetConstraintsWithSpans() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        GridPane.setConstraints(child1, 2,6, 3,8);
        assertEquals((int)6, (int)GridPane.getRowIndex(child1));
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));
        assertEquals((int)8, (int)GridPane.getRowSpan(child1));
        assertEquals((int)3, (int)GridPane.getColumnSpan(child1));
    }

    @Test public void testGridPaneSetConstraintsWithSpansAlignments() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        GridPane.setConstraints(child1, 2,6, 3,8, HPos.CENTER, VPos.TOP);
        assertEquals((int)6, (int)GridPane.getRowIndex(child1));
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));
        assertEquals((int)8, (int)GridPane.getRowSpan(child1));
        assertEquals((int)3, (int)GridPane.getColumnSpan(child1));
        assertEquals(HPos.CENTER, GridPane.getHalignment(child1));
        assertEquals(VPos.TOP, GridPane.getValignment(child1));
    }

    @Test public void testGridPaneSetConstraintsWithSpansAlignmentsGrow() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        GridPane.setConstraints(child1, 2,6, 3,8, HPos.CENTER, VPos.TOP, Priority.SOMETIMES, Priority.ALWAYS);
        assertEquals((int)6, (int)GridPane.getRowIndex(child1));
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));
        assertEquals((int)8, (int)GridPane.getRowSpan(child1));
        assertEquals((int)3, (int)GridPane.getColumnSpan(child1));
        assertEquals(HPos.CENTER, GridPane.getHalignment(child1));
        assertEquals(VPos.TOP, GridPane.getValignment(child1));
        assertEquals(Priority.SOMETIMES, GridPane.getHgrow(child1));
        assertEquals(Priority.ALWAYS, GridPane.getVgrow(child1));
    }

    @Test public void testGridPaneSetConstraintsAll() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        Insets margin = new Insets(10,20,30,40);
        GridPane.setConstraints(child1, 2,6, 3,8, HPos.CENTER, VPos.TOP,
                Priority.SOMETIMES, Priority.ALWAYS, margin);
        assertEquals((int)6, (int)GridPane.getRowIndex(child1));
        assertEquals((int)2, (int)GridPane.getColumnIndex(child1));
        assertEquals((int)8, (int)GridPane.getRowSpan(child1));
        assertEquals((int)3, (int)GridPane.getColumnSpan(child1));
        assertEquals(HPos.CENTER, GridPane.getHalignment(child1));
        assertEquals(VPos.TOP, GridPane.getValignment(child1));
        assertEquals(Priority.SOMETIMES, GridPane.getHgrow(child1));
        assertEquals(Priority.ALWAYS, GridPane.getVgrow(child1));
        assertEquals(margin, GridPane.getMargin(child1));
    }

    @Test public void testGridPaneClearConstraints() {
        MockResizable child1 = new MockResizable(100,200, 300,400, 500,600);

        Insets margin = new Insets(10,20,30,40);
        GridPane.setConstraints(child1, 2,6, 3,8, HPos.CENTER, VPos.TOP,
                Priority.SOMETIMES, Priority.ALWAYS, margin);

        GridPane.clearConstraints(child1);

        assertNull(GridPane.getRowIndex(child1));
        assertNull(GridPane.getColumnIndex(child1));
        assertNull(GridPane.getRowSpan(child1));
        assertNull(GridPane.getColumnSpan(child1));
        assertNull(GridPane.getHalignment(child1));
        assertNull(GridPane.getValignment(child1));
        assertNull(GridPane.getHgrow(child1));
        assertNull(GridPane.getVgrow(child1));
        assertNull(GridPane.getMargin(child1));
    }

    @Test public void testGridPaneCreateRow() {
        MockResizable child1 = new MockResizable(100,100);
        MockResizable child2 = new MockResizable(100,100);
        MockResizable child3 = new MockResizable(100,100);

        GridPane.createRow(2, child1,child2,child3);

        assertEquals((Integer)2, GridPane.getRowIndex(child1));
        assertEquals((Integer)2, GridPane.getRowIndex(child2));
        assertEquals((Integer)2, GridPane.getRowIndex(child3));

        assertEquals((Integer)0, GridPane.getColumnIndex(child1));
        assertEquals((Integer)1, GridPane.getColumnIndex(child2));
        assertEquals((Integer)2, GridPane.getColumnIndex(child3));
    }

    @Test public void testGridPaneCreateColumn() {
        MockResizable child1 = new MockResizable(100,100);
        MockResizable child2 = new MockResizable(100,100);
        MockResizable child3 = new MockResizable(100,100);

        GridPane.createColumn(2, child1,child2,child3);

        assertEquals((Integer)2, GridPane.getColumnIndex(child1));
        assertEquals((Integer)2, GridPane.getColumnIndex(child2));
        assertEquals((Integer)2, GridPane.getColumnIndex(child3));

        assertEquals((Integer)0, GridPane.getRowIndex(child1));
        assertEquals((Integer)1, GridPane.getRowIndex(child2));
        assertEquals((Integer)2, GridPane.getRowIndex(child3));
    }

    @Test public void testGridPaneAdd() {
        MockResizable child1 = new MockResizable(100,100);
        gridpane.add(child1, 2, 3);

        assertEquals((Integer)2, GridPane.getColumnIndex(child1));
        assertEquals((Integer)3, GridPane.getRowIndex(child1));
    }

    @Test public void testGridPaneAddWithSpans() {
        MockResizable child1 = new MockResizable(100,100);
        gridpane.add(child1, 2, 3, 4, 6);

        assertEquals((Integer)2, GridPane.getColumnIndex(child1));
        assertEquals((Integer)3, GridPane.getRowIndex(child1));
        assertEquals((Integer)4, GridPane.getColumnSpan(child1));
        assertEquals((Integer)6, GridPane.getRowSpan(child1));
    }

    @Test public void testGridPaneAddRow() {
        MockResizable child1 = new MockResizable(100,100);
        MockResizable child2 = new MockResizable(100,100);
        MockResizable child3 = new MockResizable(100,100);
        gridpane.addRow(2, child1,child2,child3);

        assertEquals((Integer)2, GridPane.getRowIndex(child1));
        assertEquals((Integer)2, GridPane.getRowIndex(child2));
        assertEquals((Integer)2, GridPane.getRowIndex(child3));

        assertEquals((Integer)0, GridPane.getColumnIndex(child1));
        assertEquals((Integer)1, GridPane.getColumnIndex(child2));
        assertEquals((Integer)2, GridPane.getColumnIndex(child3));

        assertEquals(3, gridpane.getChildren().size());
        assertEquals(child1, gridpane.getChildren().get(0));
        assertEquals(child2, gridpane.getChildren().get(1));
        assertEquals(child3, gridpane.getChildren().get(2));
    }

    @Test public void testGridPaneAddColumn() {
        MockResizable child1 = new MockResizable(100,100);
        MockResizable child2 = new MockResizable(100,100);
        MockResizable child3 = new MockResizable(100,100);
        gridpane.addColumn(2, child1,child2,child3);

        assertEquals((Integer)2, GridPane.getColumnIndex(child1));
        assertEquals((Integer)2, GridPane.getColumnIndex(child2));
        assertEquals((Integer)2, GridPane.getColumnIndex(child3));

        assertEquals((Integer)0, GridPane.getRowIndex(child1));
        assertEquals((Integer)1, GridPane.getRowIndex(child2));
        assertEquals((Integer)2, GridPane.getRowIndex(child3));

        assertEquals(3, gridpane.getChildren().size());
        assertEquals(child1, gridpane.getChildren().get(0));
        assertEquals(child2, gridpane.getChildren().get(1));
        assertEquals(child3, gridpane.getChildren().get(2));
    }

    @Test public void testColumnConstraintsDefaults() {
        ColumnConstraints cc = new ColumnConstraints();

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsWidthConstructor() {
        ColumnConstraints cc = new ColumnConstraints(100);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_PREF_SIZE, cc.getMinWidth(), 0);
        assertEquals(100, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_PREF_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSizeRangeConstructor() {
        ColumnConstraints cc = new ColumnConstraints(50, 100, Double.MAX_VALUE);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(50, cc.getMinWidth(), 0);
        assertEquals(100, cc.getPrefWidth(), 0);
        assertEquals(Double.MAX_VALUE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsMultiParamConstructor() {
        ColumnConstraints cc = new ColumnConstraints(50, 100, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(50, cc.getMinWidth(), 0);
        assertEquals(100, cc.getPrefWidth(), 0);
        assertEquals(Double.MAX_VALUE, cc.getMaxWidth(), 0);
        assertEquals(Priority.ALWAYS, cc.getHgrow());
        assertEquals(HPos.CENTER, cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetPercentWidth() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setPercentWidth(75);

        assertEquals(75, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetMinWidth() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setMinWidth(75);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(75, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetPrefWidth() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setPrefWidth(100);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(100, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetMaxWidth() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setMaxWidth(500);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(500, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetHgrow() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setHgrow(Priority.SOMETIMES);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertEquals(Priority.SOMETIMES, cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetHalignment() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setHalignment(HPos.LEFT);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertEquals(HPos.LEFT, cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testColumnConstraintsSetFillWidth() {
        ColumnConstraints cc = new ColumnConstraints();

        cc.setFillWidth(true);

        assertEquals(-1, cc.getPercentWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMinWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getPrefWidth(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, cc.getMaxWidth(), 0);
        assertNull(cc.getHgrow());
        assertNull(cc.getHalignment());
        assertTrue(cc.isFillWidth());
    }

    @Test public void testRowConstraintsDefaults() {
        RowConstraints rc = new RowConstraints();

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsHeightConstructor() {
        RowConstraints rc = new RowConstraints(50);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_PREF_SIZE, rc.getMinHeight(), 0);
        assertEquals(50, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_PREF_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSizeRangeConstructor() {
        RowConstraints rc = new RowConstraints(10, 50, Double.MAX_VALUE);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(10, rc.getMinHeight(), 0);
        assertEquals(50, rc.getPrefHeight(), 0);
        assertEquals(Double.MAX_VALUE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsMultiParamConstructor() {
        RowConstraints rc = new RowConstraints(10, 50, Double.MAX_VALUE, Priority.ALWAYS, VPos.BASELINE, true);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(10, rc.getMinHeight(), 0);
        assertEquals(50, rc.getPrefHeight(), 0);
        assertEquals(Double.MAX_VALUE, rc.getMaxHeight(), 0);
        assertEquals(Priority.ALWAYS, rc.getVgrow());
        assertEquals(VPos.BASELINE, rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetPercentHeight() {
        RowConstraints rc = new RowConstraints();

        rc.setPercentHeight(80);

        assertEquals(80, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetMinHeight() {
        RowConstraints rc = new RowConstraints();

        rc.setMinHeight(50);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(50, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetPrefHeight() {
        RowConstraints rc = new RowConstraints();

        rc.setPrefHeight(200);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(200, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetMaxHeight() {
        RowConstraints rc = new RowConstraints();

        rc.setMaxHeight(400);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(400, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetVgrow() {
        RowConstraints rc = new RowConstraints();

        rc.setVgrow(Priority.NEVER);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertEquals(Priority.NEVER, rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetValignment() {
        RowConstraints rc = new RowConstraints();

        rc.setValignment(VPos.TOP);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertEquals(VPos.TOP, rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testRowConstraintsSetFillHeight() {
        RowConstraints rc = new RowConstraints();

        rc.setFillHeight(true);

        assertEquals(-1, rc.getPercentHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMinHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getPrefHeight(), 0);
        assertEquals(Region.USE_COMPUTED_SIZE, rc.getMaxHeight(), 0);
        assertNull(rc.getVgrow());
        assertNull(rc.getValignment());
        assertTrue(rc.isFillHeight());
    }

    @Test public void testGridPaneAddColumnConstraints() {
        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();

        gridpane.getColumnConstraints().addAll(column1, column2);

        assertEquals(2, gridpane.getColumnConstraints().size());
        assertEquals(column1, gridpane.getColumnConstraints().get(0));
        assertEquals(column2, gridpane.getColumnConstraints().get(1));
    }

    @Test public void testGridPaneAddRowConstraints() {
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();

        gridpane.getRowConstraints().addAll(row1, row2);

        assertEquals(2, gridpane.getRowConstraints().size());
        assertEquals(row1, gridpane.getRowConstraints().get(0));
        assertEquals(row2, gridpane.getRowConstraints().get(1));
    }

    @Test public void testFixedWidthColumns() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 1, 0);

        gridpane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(150));

        assertEquals(250, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(250, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(100, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(150, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(100, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testFixedRangeColumns() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 1, 0);

        gridpane.getColumnConstraints().addAll(new ColumnConstraints(100, 150, 500),
                                               new ColumnConstraints(200, 250, 800));

        assertEquals(300, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(400, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(150, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(250, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(150, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testPercentageWidthColumns() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 1, 0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        gridpane.getColumnConstraints().addAll(column1,column2);

        assertEquals(200, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(600, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(300, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testPercentageColumnsWeightedIfOver100() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 1, 0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(70);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        gridpane.getColumnConstraints().addAll(column1,column2);

        assertEquals(200, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(600, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(300, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testFixedHeightRows() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 0, 1);

        gridpane.getRowConstraints().addAll(new RowConstraints(100), new RowConstraints(150));

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(250, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(250, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(100, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(150, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(100, child2.getLayoutY(), 0);
    }

    @Test public void testFixedRangeRows() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 0, 1);

        gridpane.getRowConstraints().addAll(new RowConstraints(100, 150, 500),
                                               new RowConstraints(200, 250, 800));

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(300, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(400, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(150, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(250, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(150, child2.getLayoutY(), 0);
    }

    @Test public void testPercentageHeightRows() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 0, 1);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        gridpane.getRowConstraints().addAll(row1,row2);

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(200, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(600, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(300, child2.getLayoutY(), 0);
    }

    @Test public void testPercentageRowsWeightedIfOver100() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 300,300);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 0, 1);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(80);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(80);
        gridpane.getRowConstraints().addAll(row1,row2);

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(200, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(600, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(300, child2.getLayoutY(), 0);
    }

    @Test public void testMixedRowSizeTypes() {
        MockResizable child1 = new MockResizable(100,100, 200,200, 500,500);
        MockResizable child2 = new MockResizable(100,100, 200,200, 500,500);
        MockResizable child3 = new MockResizable(100,100, 200,200, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 0, 1);
        gridpane.add(child3, 0, 2);

        RowConstraints row1 = new RowConstraints(); // computed
        RowConstraints row2 = new RowConstraints(100); // fixed at 100
        RowConstraints row3 = new RowConstraints(); // percentage 50%
        row3.setPercentHeight(50);
        gridpane.getRowConstraints().addAll(row1,row2,row3);

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(400, gridpane.minHeight(-1), 0);
        assertEquals(200, gridpane.prefWidth(-1), 0);
        assertEquals(600, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(200, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(200, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(200, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(100, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(200, child2.getLayoutY(), 0);
        assertEquals(200, child3.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child3.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child3.getLayoutX(), 0);
        assertEquals(300, child3.getLayoutY(), 0);

    }

    @Test public void testMixedColumnSizeTypes() {
        MockResizable child1 = new MockResizable(100,100, 200,200, 500,500);
        MockResizable child2 = new MockResizable(100,100, 200,200, 500,500);
        MockResizable child3 = new MockResizable(100,100, 200,200, 500,500);
        gridpane.add(child1, 0, 0);
        gridpane.add(child2, 1, 0);
        gridpane.add(child3, 2, 0);

        ColumnConstraints column1 = new ColumnConstraints(); // computed
        ColumnConstraints column2 = new ColumnConstraints(100); // fixed at 100
        ColumnConstraints column3 = new ColumnConstraints(); // percentage 50%
        column3.setPercentWidth(50);
        gridpane.getColumnConstraints().addAll(column1,column2,column3);

        assertEquals(400, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(600, gridpane.prefWidth(-1), 0);
        assertEquals(200, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(200, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(200, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(100, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(200, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(200, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
        assertEquals(300, child3.getLayoutBounds().getWidth(), 0);
        assertEquals(200, child3.getLayoutBounds().getHeight(), 0);
        assertEquals(300, child3.getLayoutX(), 0);
        assertEquals(0, child3.getLayoutY(), 0);

    }

    @Test public void testFixedWidthColumnsWithChildSpanGreaterThan1() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 800,800);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0, 2, 1);
        gridpane.add(child2, 1, 0);

        gridpane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(150));

        assertEquals(250, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(250, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(250, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(150, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(100, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testFixedWidthColumnsWithChildSpanRemaining() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 800,800);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0, GridPane.REMAINING, 1);
        gridpane.add(child2, 1, 0);

        gridpane.getColumnConstraints().addAll(
                new ColumnConstraints(100),
                new ColumnConstraints(150),
                new ColumnConstraints(200));

        assertEquals(450, gridpane.minWidth(-1), 0);
        assertEquals(100, gridpane.minHeight(-1), 0);
        assertEquals(450, gridpane.prefWidth(-1), 0);
        assertEquals(300, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(450, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(150, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(300, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(100, child2.getLayoutX(), 0);
        assertEquals(0, child2.getLayoutY(), 0);
    }

    @Test public void testFixedHeightRowsWidthChildSpanGreaterThan1() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 800,800);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0, 1, 2);
        gridpane.add(child2, 0, 1);

        gridpane.getRowConstraints().addAll(new RowConstraints(100), new RowConstraints(150));

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(250, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(250, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(250, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(150, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(100, child2.getLayoutY(), 0);
    }

    @Test public void testFixedHeightRowsWidthChildSpanRemaining() {
        MockResizable child1 = new MockResizable(50,50, 200,200, 800,800);
        MockResizable child2 = new MockResizable(100,100, 300,300, 500,500);
        gridpane.add(child1, 0, 0, 1, GridPane.REMAINING);
        gridpane.add(child2, 0, 1);

        gridpane.getRowConstraints().addAll(
                new RowConstraints(100),
                new RowConstraints(150),
                new RowConstraints(200));

        assertEquals(100, gridpane.minWidth(-1), 0);
        assertEquals(450, gridpane.minHeight(-1), 0);
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(450, gridpane.prefHeight(-1), 0);
        assertEquals(Double.MAX_VALUE, gridpane.maxWidth(-1),0);
        assertEquals(Double.MAX_VALUE, gridpane.maxHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(300, child1.getLayoutBounds().getWidth(), 0);
        assertEquals(450, child1.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child1.getLayoutX(), 0);
        assertEquals(0, child1.getLayoutY(), 0);
        assertEquals(300, child2.getLayoutBounds().getWidth(), 0);
        assertEquals(150, child2.getLayoutBounds().getHeight(), 0);
        assertEquals(0, child2.getLayoutX(), 0);
        assertEquals(100, child2.getLayoutY(), 0);
    }

    @Test public void testColumnWidthIsPrefWhenPrefBetweenMinAndMax() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(100,200,300));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getWidth(), 0);
    }

    @Test public void testColumnWidthIsMinWhenMinGreaterThanPref() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(200, 100, 300));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getWidth(), 0);
    }

    @Test public void testColumnWidthIsMinWhenMinGreaterThanPrefAndMax() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(300, 100, 200));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(300, child.getWidth(), 0);
    }

    @Test public void testColumnWidthIsMaxWhenMaxLessThanPref() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(100, 300, 200));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getWidth(), 0);
    }

    @Test public void testColumnWidthIsMinWhenMaxLessThanMin() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(200, 300, 100));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getWidth(), 0);
    }

    @Test public void testColumnWidthIsMinWhenMaxLessThanPrefAndMin() {
        gridpane.getColumnConstraints().add(new ColumnConstraints(300, 200, 100));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(300, child.getWidth(), 0);
    }

    @Test public void testRowHeightIsPrefWhenPrefBetweenMinAndMax() {
        gridpane.getRowConstraints().add(new RowConstraints(100,200,300));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getHeight(), 0);
    }

    @Test public void testRowHeightIsMinWhenMinGreaterThanPref() {
        gridpane.getRowConstraints().add(new RowConstraints(200, 100, 300));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getHeight(), 0);
    }

    @Test public void testRowHeightIsMinWhenMinGreaterThanPrefAndMax() {
        gridpane.getRowConstraints().add(new RowConstraints(300, 100, 200));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(300, child.getHeight(), 0);
    }

    @Test public void testRowHeightIsMaxWhenMaxLessThanPref() {
        gridpane.getRowConstraints().add(new RowConstraints(100, 300, 200));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getHeight(), 0);
    }

    @Test public void testRowHeightIsMinWhenMaxLessThanMin() {
        gridpane.getRowConstraints().add(new RowConstraints(200, 300, 100));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(200, child.getHeight(), 0);
    }

    @Test public void testRowHeightIsMinWhenMaxLessThanPrefAndMin() {
        gridpane.getRowConstraints().add(new RowConstraints(300, 200, 100));
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(300, child.getHeight(), 0);
    }

    @Test public void testRowHeightHonorsMaxWhenGrowing() {
        RowConstraints row = new RowConstraints(100, 200, 300);
        RowConstraints row2 = new RowConstraints(100, 200, 200);
        row.setVgrow(Priority.ALWAYS);
        row2.setVgrow(Priority.ALWAYS);
        gridpane.getRowConstraints().addAll(row,row2);
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        MockRegion child2 = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addRow(0,child);
        gridpane.addRow(1,child2);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(300, child.getHeight(), 0);
        assertEquals(200, child2.getHeight(), 0);
    }

    @Test public void testColumnWidthHonorsMaxWhenGrowing() {
        ColumnConstraints column = new ColumnConstraints(100, 200, 300);
        ColumnConstraints column2 = new ColumnConstraints(100, 200, 200);
        column.setHgrow(Priority.ALWAYS);
        column2.setHgrow(Priority.ALWAYS);
        gridpane.getColumnConstraints().addAll(column,column2);
        MockRegion child = new MockRegion(10,10,100,100,1000,1000);
        MockRegion child2 = new MockRegion(10,10,100,100,1000,1000);
        gridpane.addColumn(0,child);
        gridpane.addColumn(1,child2);

        gridpane.resize(800,800);
        gridpane.layout();
        assertEquals(300, child.getWidth(), 0);
        assertEquals(200, child2.getWidth(), 0);
    }

    @Test public void testGridPaneContentBiasNullNoChildHasContentBias() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockResizable child2_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertNull(gridpane.getContentBias());
    }

    @Test public void testGridPaneContentBiasHORIZONTALIfChildHORIZONTAL() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.HORIZONTAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.HORIZONTAL, gridpane.getContentBias());
    }

    @Test public void testGridPaneWithHorizontalContentBiasAtPrefSize() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.HORIZONTAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.HORIZONTAL, gridpane.getContentBias());
        assertEquals(800, gridpane.prefWidth(-1), 0);
        assertEquals(400, gridpane.prefHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();
        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(300, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_0.getLayoutX(), 1e-100);
        assertEquals(0, child2_0.getLayoutY(), 1e-100);
        assertEquals(300, child2_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child2_0.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(100, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_1.getLayoutX(), 1e-100);
        assertEquals(100, child2_1.getLayoutY(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithHorizontalContentBiasHorizontalShrinking() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.HORIZONTAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.HORIZONTAL, gridpane.getContentBias());
        assertEquals(429, gridpane.prefHeight(600), 0);

        gridpane.resize(600, 429);
        gridpane.layout();
        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(233, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(129, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(233, child1_0.getLayoutX(), 1e-100);
        assertEquals(14.5, child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(366, child2_0.getLayoutX(), 1e-100);
        assertEquals(0, child2_0.getLayoutY(), 1e-100);
        assertEquals(233, child2_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(129, child2_0.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(129, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(233, child1_1.getLayoutX(), 1e-100);
        assertEquals(129, child1_1.getLayoutY(), 1e-100);
        assertEquals(133, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(366, child2_1.getLayoutX(), 1e-100);
        assertEquals(129, child2_1.getLayoutY(), 1e-100);
        assertEquals(233, child2_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithHorizontalContentBiasWithHorizontalGrowingFillHeightFalse() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.HORIZONTAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        RowConstraints row = new RowConstraints();
        row.setFillHeight(false);
        gridpane.getRowConstraints().add(row);

        GridPane.setHgrow(child2_0, Priority.ALWAYS);
        
        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.HORIZONTAL, gridpane.getContentBias());
        assertEquals(400, gridpane.prefHeight(1000), 0);

        gridpane.resize(1000, 400);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(300, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_0.getLayoutX(), 1e-100);
        assertEquals(20, child2_0.getLayoutY(), 1e-100);
        assertEquals(500, child2_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(60, child2_0.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(100, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_1.getLayoutX(), 1e-100);
        assertEquals(100, child2_1.getLayoutY(), 1e-100);
        assertEquals(500, child2_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithHorizontalContentBiasWithHorizontalGrowingFillHeightTrue() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.HORIZONTAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        RowConstraints row = new RowConstraints();
        row.setFillHeight(true);
        gridpane.getRowConstraints().add(row);
        GridPane.setHgrow(child2_0, Priority.ALWAYS);
        
        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        gridpane.resize(1000, 400);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(300, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_0.getLayoutX(), 1e-100);
        assertEquals(20, child2_0.getLayoutY(), 1e-100);
        assertEquals(500, child2_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(60, child2_0.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(100, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_1.getLayoutX(), 1e-100);
        assertEquals(100, child2_1.getLayoutY(), 1e-100);
        assertEquals(500, child2_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithHorizontalContentBiasOnlyTheRowWithContentBiasIsAffected_RT19068() {
        MockResizable child0_0 = new MockResizable(100, 100);
        GridPane.setConstraints(child0_0, 0, 0);
        MockResizable child1_0 = new MockResizable(200, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child1_1 = new MockBiased(Orientation.HORIZONTAL, 300, 400);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child1_1);
        
        gridpane.resize(500, 600);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(300, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(400, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneContentBiasVERTICALIfChildVERTICAL() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.VERTICAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.VERTICAL, gridpane.getContentBias());
    }

    @Test public void testGridPaneWithVerticalContentBiasAtPrefSize() {
        MockResizable child0_0 = new MockResizable(100,10, 300,100, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child1_0 = new Rectangle(100, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child2_0 = new MockBiased(Orientation.VERTICAL, 300, 100);
        GridPane.setConstraints(child2_0, 2, 0);
        Rectangle child0_1 = new Rectangle(100, 300);
        GridPane.setConstraints(child0_1, 0, 1);
        MockResizable child1_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child2_1 = new MockResizable(100,100, 200, 200, 800, 800);
        GridPane.setConstraints(child2_1, 2, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0,
                                      child0_1, child1_1, child2_1);

        assertEquals(Orientation.VERTICAL, gridpane.getContentBias());
        assertEquals(800, gridpane.prefWidth(-1), 0);
        assertEquals(400, gridpane.prefHeight(-1), 0);

        gridpane.autosize();
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(300, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_0.getLayoutX(), 1e-100);
        assertEquals(0, child2_0.getLayoutY(), 1e-100);
        assertEquals(300, child2_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child2_0.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(100, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(300, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(500, child2_1.getLayoutX(), 1e-100);
        assertEquals(100, child2_1.getLayoutY(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child2_1.getLayoutBounds().getHeight(), 1e-100);       
    }

    @Test public void testGridPaneWithVerticalContentBiasVerticalShrinking() {
        MockResizable child0_0 = new MockResizable(10,10, 100,200, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child0_1 = new Rectangle(100, 100);
        GridPane.setConstraints(child0_1, 0, 1);
        MockBiased child0_2 = new MockBiased(Orientation.VERTICAL, 100, 200);
        GridPane.setConstraints(child0_2, 0, 2);
        Rectangle child1_0 = new Rectangle(200, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockResizable child1_1 = new MockResizable(100,100, 200, 100, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child1_2 = new MockResizable(100,100, 200, 150, 800, 800);
        GridPane.setConstraints(child1_2, 1, 2);

        gridpane.getChildren().addAll(child0_0, child1_0,
                                      child0_1, child1_1,
                                      child0_2, child1_2);
        
        assertEquals(Orientation.VERTICAL, gridpane.getContentBias());
        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(500, gridpane.prefHeight(-1), 0);
        assertEquals(334, gridpane.prefWidth(400), 0);

        gridpane.resize(334, 400);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(134, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(150, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(150, child0_1.getLayoutY(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_2.getLayoutX(), 1e-100);
        assertEquals(250, child0_2.getLayoutY(), 1e-100);
        assertEquals(134, child0_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(150, child0_2.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(134, child1_0.getLayoutX(), 1e-100);
        assertEquals(25, child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(134, child1_1.getLayoutX(), 1e-100);
        assertEquals(150, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(134, child1_2.getLayoutX(), 1e-100);
        assertEquals(250, child1_2.getLayoutY(), 1e-100);
        assertEquals(200, child1_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(150, child1_2.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithVerticalContentBiasWithVerticalGrowingFillWidthFalse() {
        MockResizable child0_0 = new MockResizable(10,10, 100,200, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child0_1 = new Rectangle(50, 100);
        GridPane.setConstraints(child0_1, 0, 1);
        MockBiased child0_2 = new MockBiased(Orientation.VERTICAL, 100, 200);
        GridPane.setConstraints(child0_2, 0, 2);
        Rectangle child1_0 = new Rectangle(200, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockResizable child1_1 = new MockResizable(100,100, 200, 100, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child1_2 = new MockResizable(100,100, 200, 150, 800, 800);
        GridPane.setConstraints(child1_2, 1, 2);

        gridpane.getChildren().addAll(child0_0, child1_0,
                                      child0_1, child1_1,
                                      child0_2, child1_2);

        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(false);
        gridpane.getColumnConstraints().add(col);
        GridPane.setVgrow(child0_2, Priority.ALWAYS);
               
        assertEquals(Orientation.VERTICAL, gridpane.getContentBias());
        assertEquals(300, gridpane.prefWidth(600), 0);
        gridpane.resize(300, 600);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(200, child0_1.getLayoutY(), 1e-100);
        assertEquals(50, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_2.getLayoutX(), 1e-100);
        assertEquals(300, child0_2.getLayoutY(), 1e-100);
        assertEquals(67, child0_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_2.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(100, child1_0.getLayoutX(), 1e-100);
        assertEquals(50, child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_1.getLayoutX(), 1e-100);
        assertEquals(200, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_2.getLayoutX(), 1e-100);
        assertEquals(300, child1_2.getLayoutY(), 1e-100);
        assertEquals(200, child1_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_2.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithVerticalContentBiasWithVerticalGrowingFillWidthTrue() {
        MockResizable child0_0 = new MockResizable(10,10, 100,200, 500,600);
        GridPane.setConstraints(child0_0, 0, 0);
        Rectangle child0_1 = new Rectangle(50, 100);
        GridPane.setConstraints(child0_1, 0, 1);
        MockBiased child0_2 = new MockBiased(Orientation.VERTICAL, 100, 200);
        GridPane.setConstraints(child0_2, 0, 2);
        Rectangle child1_0 = new Rectangle(200, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockResizable child1_1 = new MockResizable(100,100, 200, 100, 800, 800);
        GridPane.setConstraints(child1_1, 1, 1);
        MockResizable child1_2 = new MockResizable(100,100, 200, 150, 800, 800);
        GridPane.setConstraints(child1_2, 1, 2);

        gridpane.getChildren().addAll(child0_0, child1_0,
                                      child0_1, child1_1,
                                      child0_2, child1_2);

        ColumnConstraints col = new ColumnConstraints();
        col.setFillWidth(true);
        gridpane.getColumnConstraints().add(col);
        GridPane.setVgrow(child0_2, Priority.ALWAYS);
               
        assertEquals(Orientation.VERTICAL, gridpane.getContentBias());
        assertEquals(300, gridpane.prefWidth(600), 0);
        gridpane.resize(300, 600);
        gridpane.layout();

        gridpane.resize(300, 600);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(200, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_1.getLayoutX(), 1e-100);
        assertEquals(200, child0_1.getLayoutY(), 1e-100);
        assertEquals(50, child0_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(0, child0_2.getLayoutX(), 1e-100);
        assertEquals(300, child0_2.getLayoutY(), 1e-100);
        assertEquals(67, child0_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child0_2.getLayoutBounds().getHeight(), 1e-100);

        assertEquals(100, child1_0.getLayoutX(), 1e-100);
        assertEquals(50, child1_0.getLayoutY(), 1e-100);
        assertEquals(200, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_1.getLayoutX(), 1e-100);
        assertEquals(200, child1_1.getLayoutY(), 1e-100);
        assertEquals(200, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_1.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_2.getLayoutX(), 1e-100);
        assertEquals(300, child1_2.getLayoutY(), 1e-100);
        assertEquals(200, child1_2.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(300, child1_2.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void testGridPaneWithVerticalContentBiasOnlyTheColumnWithContentBiasIsAffected_RT19068() {
        MockResizable child0_0 = new MockResizable(100, 100);
        GridPane.setConstraints(child0_0, 0, 0);
        MockResizable child1_0 = new MockResizable(200, 100);
        GridPane.setConstraints(child1_0, 1, 0);
        MockBiased child1_1 = new MockBiased(Orientation.VERTICAL, 300, 400);
        GridPane.setConstraints(child1_1, 1, 1);

        gridpane.getChildren().addAll(child0_0, child1_0, child1_1);

        gridpane.resize(500, 600);
        gridpane.layout();

        assertEquals(0, child0_0.getLayoutX(), 1e-100);
        assertEquals(0, child0_0.getLayoutY(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child0_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_0.getLayoutX(), 1e-100);
        assertEquals(0, child1_0.getLayoutY(), 1e-100);
        assertEquals(300, child1_0.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(100, child1_0.getLayoutBounds().getHeight(), 1e-100);
        assertEquals(100, child1_1.getLayoutX(), 1e-100);
        assertEquals(100, child1_1.getLayoutY(), 1e-100);
        assertEquals(300, child1_1.getLayoutBounds().getWidth(), 1e-100);
        assertEquals(400, child1_1.getLayoutBounds().getHeight(), 1e-100);
    }

    @Test public void test_RT18518_sizeIsNotUpdatedAfterRemovingChild() {
        MockResizable child0_0 = new MockResizable(100,200);
        GridPane.setConstraints(child0_0, 0, 0);
        MockResizable child1_0 = new MockResizable(100,200);
        GridPane.setConstraints(child1_0, 1, 0);
        MockResizable child2_0 = new MockResizable(100,200);
        GridPane.setConstraints(child2_0, 2, 0);

        gridpane.getChildren().addAll(child0_0, child1_0, child2_0);

        assertEquals(300, gridpane.prefWidth(-1), 0);
        assertEquals(200, gridpane.prefHeight(-1), 0);

        gridpane.getChildren().remove(child1_0);

        assertEquals(200, gridpane.prefWidth(-1), 0);
        assertEquals(200, gridpane.prefHeight(-1), 0);
    }
}