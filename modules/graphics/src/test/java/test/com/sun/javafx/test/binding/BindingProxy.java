/*
 * Copyright (c) 2011, 2015, Oracle and/or its affiliates. All rights reserved.
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

package test.com.sun.javafx.test.binding;

public class BindingProxy {
    private final Class<?> typeClass;
    private final ObservableValueProxy observableValueProxy;
    private final WritableValueProxy writableValueProxy;
    private final VariableFactory variableFactory;
    private final PropertyModelProxy propertyModelProxy;

    public BindingProxy(final Class<?> typeClass,
                        final ObservableValueProxy observableValueProxy,
                        final WritableValueProxy writableValueProxy,
                        final VariableFactory variableFactory,
                        final PropertyModelProxy propertyModelProxy) {
        this.typeClass = typeClass;
        this.observableValueProxy = observableValueProxy;
        this.writableValueProxy = writableValueProxy;
        this.variableFactory = variableFactory;
        this.propertyModelProxy = propertyModelProxy;
    }

    public final Class<?> getTypeClass() {
        return typeClass;
    }

    public final ObservableValueProxy getObservableValueProxy() {
        return observableValueProxy;
    }

    public final WritableValueProxy getWritableValueProxy() {
        return writableValueProxy;
    }

    public final VariableFactory getVariableFactory() {
        return variableFactory;
    }

    public final PropertyModelProxy getPropertyModelProxy() {
        return propertyModelProxy;
    }
}
