/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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
package javafx.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.javafx.scene.CssFlags;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import com.sun.javafx.Logging;
import com.sun.javafx.Utils;
import com.sun.javafx.css.CalculatedValue;
import com.sun.javafx.css.CascadingStyle;
import com.sun.javafx.css.CssError;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.css.Rule;
import com.sun.javafx.css.Selector;
import com.sun.javafx.css.Style;
import com.sun.javafx.css.StyleCache;
import com.sun.javafx.css.StyleCacheEntry;
import com.sun.javafx.css.StyleConverterImpl;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.css.StyleMap;
import com.sun.javafx.css.Stylesheet;
import com.sun.javafx.css.converters.FontConverter;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

import static com.sun.javafx.css.CalculatedValue.*;

/**
 * The StyleHelper is a helper class used for applying CSS information to Nodes.
 */
final class CssStyleHelper {

    private static final PlatformLogger LOGGER = com.sun.javafx.Logging.getCSSLogger();

    private CssStyleHelper() {
        this.triggerStates = new PseudoClassState();
    }

    /**
     * Creates a new StyleHelper.
     */
    static CssStyleHelper createStyleHelper(Node node) {

        // need to know how far we are to root in order to init arrays.
        // TODO: should we hang onto depth to avoid this nonsense later?
        // TODO: is there some other way of knowing how far from the root a node is?
        Styleable parent = node;
        int depth = 0;
        while(parent != null) {
            depth++;
            parent = parent.getStyleableParent();
        }

        // The List<CacheEntry> should only contain entries for those
        // pseudo-class states that have styles. The StyleHelper's
        // pseudoclassStateMask is a bitmask of those pseudoclasses that
        // appear in the node's StyleHelper's smap. This list of
        // pseudo-class masks is held by the StyleCacheKey. When a node is
        // styled, its pseudoclasses and the pseudoclasses of its parents
        // are gotten. By comparing the actual pseudo-class state to the
        // pseudo-class states that apply, a CacheEntry can be created or
        // fetched using only those pseudoclasses that matter.
        final PseudoClassState[] triggerStates = new PseudoClassState[depth];

        final StyleMap styleMap =
                StyleManager.getInstance().findMatchingStyles(node, triggerStates);

        if (styleMap == null || styleMap.isEmpty()) {

            boolean mightInherit = false;

            final List<CssMetaData<? extends Styleable, ?>> props = node.getCssMetaData();

            final int pMax = props != null ? props.size() : 0;
            for (int p=0; p<pMax; p++) {

                final CssMetaData<? extends Styleable, ?> prop = props.get(p);
                if (prop.isInherits()) {
                    mightInherit = true;
                    break;
                }
            }

            if (mightInherit == false) {

                // If this node had a style helper, then reset properties to their initial value
                // since the node won't have a style helper after this call
                if (node.styleHelper != null) {
                    node.styleHelper.resetToInitialValues(node);
                }

                return null;
            }

        }

        final CssStyleHelper helper = new CssStyleHelper();
        helper.triggerStates.addAll(triggerStates[0]);

        // make sure parent's transition states include the pseudo-classes
        // found when matching selectors
        parent = node.getStyleableParent();
        for(int n=1; n<depth; n++) {

            // TODO: this means that a style like .menu-item:hover won't work. Need to separate CssStyleHelper tree from scene-graph tree
            if (parent instanceof Node == false) {
                parent=parent.getStyleableParent();
                continue;
            }
            Node parentNode = (Node)parent;

            final PseudoClassState triggerState = triggerStates[n];

            // if there is nothing in triggerState, then continue since there
            // isn't any pseudo-class state that might trigger a state change
            if (triggerState != null && triggerState.size() > 0) {

                // Create a StyleHelper for the parent, if necessary.
                if (parentNode.styleHelper == null) {
                    parentNode.styleHelper = new CssStyleHelper();
                }
                parentNode.styleHelper.triggerStates.addAll(triggerState);

            }

            parent=parent.getStyleableParent();
        }

        helper.cacheContainer = new CacheContainer(node, styleMap, depth);

        // If this node had a style helper, then reset properties to their initial value
        // since the style map might now be different
        if (node.styleHelper != null) {
            node.styleHelper.resetToInitialValues(node);
        }

        return helper;
    }

    private CacheContainer cacheContainer;

    private final static class CacheContainer {

        // Set internal internalState structures
        private CacheContainer(
                Node node,
                StyleMap styleMap,
                int depth) {

            int ctr = 0;
            int[] smapIds = new int[depth];
            smapIds[ctr++] = this.smapId = styleMap.getId();

            //
            // Create a set of StyleMap id's from the parent's smapIds.
            // The resulting smapIds array may have less than depth elements.
            // If a parent doesn't have a styleHelper or the styleHelper's
            // internal state is null, then that parent doesn't contribute
            // to the selection of a style. Any Node that has the same
            // set of smapId's can potentially share previously calculated
            // values.
            //
            Styleable parent = node.getStyleableParent();
            for(int d=1; d<depth; d++) {

                // TODO: won't work for something like .menu-item:hover. Need to separate CssStyleHelper tree from scene-graph tree
                if ( parent instanceof Node) {
                    Node parentNode = (Node)parent;
                final CssStyleHelper helper = parentNode.styleHelper;
                    if (helper != null && helper.cacheContainer != null) {
                        smapIds[ctr++] = helper.cacheContainer.smapId;
                    }
                }
                parent = parent.getStyleableParent();

            }

            this.styleCacheKey = new StyleCache.Key(smapIds, ctr);

            CssMetaData<Styleable,Font> styleableFontProperty = null;

            final List<CssMetaData<? extends Styleable, ?>> props = node.getCssMetaData();
            final int pMax = props != null ? props.size() : 0;
            for (int p=0; p<pMax; p++) {
                final CssMetaData<? extends Styleable, ?> prop = props.get(p);

                if ("-fx-font".equals(prop.getProperty())) {
                    // unchecked!
                    styleableFontProperty = (CssMetaData<Styleable, Font>) prop;
                    break;
                }
            }

            this.fontProp = styleableFontProperty;
            this.fontSizeCache = new HashMap<>();

            this.cssSetProperties = new HashMap<>();

        }

        private StyleMap getStyleMap(Styleable styleable) {
            if (styleable != null) {
                return StyleManager.getInstance().getStyleMap(styleable, smapId);
            } else {
                return StyleMap.EMPTY_MAP;
            }

        }

        // This is the key we use to find the shared cache
        private final StyleCache.Key styleCacheKey;

        // If the node has a fontProperty, we hang onto the CssMetaData for it
        // so we can get at it later.
        // TBD - why not the fontProperty itself?
        private final CssMetaData<Styleable,Font> fontProp;

        // The id of StyleMap that contains the styles that apply to this node
        private final int smapId;

        // All nodes with the same set of styles share the same cache of
        // calculated values. But one node might have a different font-size
        // than another so the values are stored in cache by font-size.
        // This map associates a style cache entry with the font to use when
        // getting a value from or putting a value into cache.
        private final Map<StyleCacheEntry.Key, CalculatedValue> fontSizeCache;

        // Any properties that have been set by this style helper are tracked
        // here so the property can be reset without expanding properties that
        // were not set by css.
        private Map<CssMetaData, CalculatedValue> cssSetProperties;
    }

    private void resetToInitialValues(Styleable styleable) {

        if (cacheContainer == null ||
                cacheContainer.cssSetProperties == null ||
                cacheContainer.cssSetProperties.isEmpty()) return;

        // RT-31714 - make a copy of the entry set and clear the cssSetProperties immediately.
        Set<Entry<CssMetaData, CalculatedValue>> entrySet = new HashSet<>(cacheContainer.cssSetProperties.entrySet());
        cacheContainer.cssSetProperties.clear();

        for (Entry<CssMetaData, CalculatedValue> resetValues : entrySet) {

            final CssMetaData metaData = resetValues.getKey();
            final StyleableProperty styleableProperty = metaData.getStyleableProperty(styleable);

            final StyleOrigin styleOrigin = styleableProperty.getStyleOrigin();
            if (styleOrigin != null && styleOrigin != StyleOrigin.USER) {
                final CalculatedValue calculatedValue = resetValues.getValue();
                styleableProperty.applyStyle(calculatedValue.getOrigin(), calculatedValue.getValue());
            }
        }
    }


    private StyleMap getStyleMap(Styleable styleable) {
        if (cacheContainer == null || styleable == null) return null;
        return cacheContainer.getStyleMap(styleable);
    }

    private Map<String, List<CascadingStyle>> getCascadingStyles(Styleable styleable) {
        StyleMap styleMap = getStyleMap(styleable);
        // code looks for null return to indicate that the cache was blown away
        return (styleMap != null) ? styleMap.getCascadingStyles() : null;
    }

    /**
     * A Set of all the pseudo-class states which, if they change, need to
     * cause the Node to be set to UPDATE its CSS styles on the next pulse.
     * For example, your stylesheet might have:
     * <pre><code>
     * .button { ... }
     * .button:hover { ... }
     * .button *.label { text-fill: black }
     * .button:hover *.label { text-fill: blue }
     * </code></pre>
     * In this case, the first 2 rules apply to the Button itself, but the
     * second two rules apply to the label within a Button. When the hover
     * changes on the Button, however, we must mark the Button as needing
     * an UPDATE. StyleHelper though only contains styles for the first two
     * rules for Button. The pseudoclassStateMask would in this case have
     * only a single bit set for "hover". In this way the StyleHelper associated
     * with the Button would know whether a change to "hover" requires the
     * button and all children to be update. Other pseudo-class state changes
     * that are not in this hash set are ignored.
     * *
     * Called "triggerStates" since they would trigger a CSS update.
     */
    private PseudoClassState triggerStates = new PseudoClassState();

    boolean pseudoClassStateChanged(PseudoClass pseudoClass) {
        return triggerStates.contains(pseudoClass);
    }

    /**
     * Dynamic pseudo-class state of the node and its parents.
     * Only valid during a pulse.
     *
     * The StyleCacheEntry to choose depends on the Node's pseudo-class state
     * and the pseudo-class state of its parents. Without the parent
     * pseudo-class state, the fact that the the node in this pseudo-class state
     * matched foo:blah bar { } is lost.
     */
    // TODO: this should work on Styleable, not Node
    private Set<PseudoClass>[] getTransitionStates(Node node) {

        // if cacheContainer is null, then CSS just doesn't apply to this node
        if (cacheContainer == null) return null;

        int depth = 0;
        Node parent = node;
        while (parent != null) {
            depth += 1;
            parent = parent.getParent();
        }

        //
        // StyleHelper#triggerStates is the set of pseudo-classes that appear
        // in the style maps of this StyleHelper. Calculated values are
        // cached by pseudo-class state, but only the pseudo-class states
        // that mater are used in the search. So we take the transition states
        // and intersect them with triggerStates to remove the
        // transition states that don't matter when it comes to matching states
        // on a  selector. For example if the style map contains only
        // .foo:hover { -fx-fill: red; } then only the hover state matters
        // but the transtion state could be [hover, focused]
        //
        final Set<PseudoClass>[] retainedStates = new PseudoClassState[depth];

        //
        // Note Well: The array runs from leaf to root. That is,
        // retainedStates[0] is the pseudo-class state for node and
        // retainedStates[1..(states.length-1)] are the retainedStates for the
        // node's parents.
        //

        int count = 0;
        parent = node;
        while (parent != null) {
            final CssStyleHelper helper = (parent instanceof Node) ? ((Node)parent).styleHelper : null;
            if (helper != null) {
                final Set<PseudoClass> pseudoClassState = ((Node)parent).pseudoClassStates;
                retainedStates[count] = new PseudoClassState();
                retainedStates[count].addAll(pseudoClassState);
                // retainAll method takes the intersection of pseudoClassState and helper.triggerStates
                retainedStates[count].retainAll(helper.triggerStates);
                count += 1;
            }
            parent = parent.getParent();
        }

        final Set<PseudoClass>[] transitionStates = new PseudoClassState[count];
        System.arraycopy(retainedStates, 0, transitionStates, 0, count);

        return transitionStates;

    }

    ObservableMap<StyleableProperty<?>, List<Style>> observableStyleMap;
     /**
      * RT-17293
      */
     ObservableMap<StyleableProperty<?>, List<Style>> getObservableStyleMap() {
         return (observableStyleMap != null)
             ? observableStyleMap
             : FXCollections.<StyleableProperty<?>, List<Style>>emptyObservableMap();
     }

     /**
      * RT-17293
      */
     void setObservableStyleMap(ObservableMap<StyleableProperty<?>, List<Style>> observableStyleMap) {
         this.observableStyleMap = observableStyleMap;
     }

    /**
     * Called by the Node whenever it has transitioned from one set of
     * pseudo-class states to another. This function will then lookup the
     * new values for each of the styleable variables on the Node, and
     * then either set the value directly or start an animation based on
     * how things are specified in the CSS file. Currently animation support
     * is disabled until the new parser comes online with support for
     * animations and that support is detectable via the API.
     */
    void transitionToState(Node node, CssFlags cssFlag) {

        if (cacheContainer == null) {
            return;
        }

        //
        // Styles that need lookup can be cached provided none of the styles
        // are from Node.style.
        //
        final StyleCache sharedCache = StyleManager.getInstance().getSharedCache(node, cacheContainer.styleCacheKey);

        if (sharedCache == null) {
            // Shared cache was blown away by StyleManager.
            // Therefore, this CssStyleHelper is no good.
            cacheContainer = null;
            node.impl_reapplyCSS();
            return;

        }

        final Set<PseudoClass>[] transitionStates = getTransitionStates(node);

        final StyleCacheEntry.Key fontCacheKey = new StyleCacheEntry.Key(transitionStates, Font.getDefault());
        CalculatedValue cachedFont = cacheContainer.fontSizeCache.get(fontCacheKey);

        if (cachedFont == null) {

            cachedFont = lookupFont(node, "-fx-font", cachedFont, null);

            if (cachedFont == SKIP) cachedFont = getCachedFont(node.getStyleableParent());
            if (cachedFont == null) cachedFont = new CalculatedValue(Font.getDefault(), null, false);

            cacheContainer.fontSizeCache.put(fontCacheKey,cachedFont);

        }

        final Font fontForRelativeSizes = (Font)cachedFont.getValue();

        final StyleCacheEntry.Key cacheEntryKey = new StyleCacheEntry.Key(transitionStates, fontForRelativeSizes);
        StyleCacheEntry cacheEntry = sharedCache.getStyleCacheEntry(cacheEntryKey);

        // if no one is watching for styles and the cacheEntry already exists,
        // then we can definitely take the fastpath
        final boolean fastpath = this.observableStyleMap == null && cacheEntry != null;

        if (cacheEntry == null) {
            cacheEntry = new StyleCacheEntry();
            sharedCache.addStyleCacheEntry(cacheEntryKey, cacheEntry);
        }

        final List<CssMetaData<? extends Styleable,  ?>> styleables = node.getCssMetaData();

        // Used in the for loop below, and a convenient place to stop when debugging.
        final int max = styleables.size();

        // RT-20643
        CssError.setCurrentScene(node.getScene());

        // For each property that is settable, we need to do a lookup and
        // transition to that value.
        for(int n=0; n<max; n++) {

            @SuppressWarnings("unchecked") // this is a widening conversion
            final CssMetaData<Styleable,Object> cssMetaData =
                    (CssMetaData<Styleable,Object>)styleables.get(n);

            if (observableStyleMap != null) {
                final StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
                if (styleableProperty != null && observableStyleMap.containsKey(styleableProperty)) {
                    observableStyleMap.remove(styleableProperty);
                }
            }


            // Skip the lookup if we know there isn't a chance for this property
            // to be set (usually due to a "bind").
            if (!cssMetaData.isSettable(node)) continue;

            final String property = cssMetaData.getProperty();

            // Create a List to hold the Styles if the node has
            // a Map<WritableValue, List<Style>>
            final List<Style> styleList = (observableStyleMap != null)
                    ? new ArrayList<Style>()
                    : null;

            CalculatedValue calculatedValue = cacheEntry.get(property);

            // If there is no calculatedValue and we're on the fast path,
            // take the slow path if cssFlags is REAPPLY (RT-31691)
            final boolean forceSlowpath =
                    fastpath && calculatedValue == null && cssFlag == CssFlags.REAPPLY;

            final boolean addToCache =
                    (!fastpath && calculatedValue == null) || forceSlowpath;

            if (fastpath && !forceSlowpath) {

                // calculatedValue may be null,
                // but we should never put SKIP in cache.
                if (calculatedValue == SKIP) {
                    assert false : "cache returned SKIP for " + property;
                    continue;
                }

            } else if (calculatedValue == null) {

                // slowpath!
                calculatedValue = lookup(node, cssMetaData, transitionStates[0],
                        node, cachedFont, styleList);

                // lookup is not supposed to return null.
                if (calculatedValue == null) {
                    assert false : "lookup returned null for " + property;
                    continue;
                }

            }

            // StyleableProperty#applyStyle might throw an exception and it is called
            // from two places in this try block.
            try {

                //
                // RT-19089
                // If the current value of the property was set by CSS
                // and there is no style for the property, then reset this
                // property to its initial value. If it was not set by CSS
                // then leave the property alone.
                //
                if (calculatedValue == null || calculatedValue == SKIP) {

                    // cssSetProperties keeps track of the StyleableProperty's that were set by CSS in the previous state.
                    // If this property is not in cssSetProperties map, then the property was not set in the previous state.
                    // This accomplishes two things. First, it lets us know if the property was set in the previous state
                    // so it can be reset in this state if there is no value for it. Second, it calling
                    // CssMetaData#getStyleableProperty which is rather expensive as it may cause expansion of lazy
                    // properties.
                    CalculatedValue initialValue = cacheContainer.cssSetProperties.get(cssMetaData);

                    // if the current value was set by CSS and there
                    // is no calculated value for the property, then
                    // there was no style for the property in the current
                    // state, so reset the property to its initial value.
                    if (initialValue != null) {
                        StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
                        styleableProperty.applyStyle(initialValue.getOrigin(), initialValue.getValue());
                    }

                    continue;

                }

                if (addToCache) {

                    // If we're not on the fastpath, then add the calculated
                    // value to cache.
                    cacheEntry.put(property, calculatedValue);
                }

                StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);

                // need to know who set the current value - CSS, the user, or init
                final StyleOrigin originOfCurrentValue = styleableProperty.getStyleOrigin();


                // RT-10522:
                // If the user set the property and there is a style and
                // the style came from the user agent stylesheet, then
                // skip the value. A style from a user agent stylesheet should
                // not override the user set style.
                //
                final StyleOrigin originOfCalculatedValue = calculatedValue.getOrigin();

                // A calculated value should never have a null style origin since that would
                // imply the style didn't come from a stylesheet or in-line style.
                if (originOfCalculatedValue == null) {
                    assert false : styleableProperty.toString();
                    continue;
                }

                if (originOfCurrentValue == StyleOrigin.USER) {
                    if (originOfCalculatedValue == StyleOrigin.USER_AGENT) {
                        continue;
                    }
                }

                final Object value = calculatedValue.getValue();
                final Object currentValue = styleableProperty.getValue();

                // RT-21185: Only apply the style if something has changed.
                if ((originOfCurrentValue != originOfCalculatedValue)
                        || (currentValue != null
                        ? currentValue.equals(value) == false
                        : value != null)) {

                    if (LOGGER.isLoggable(Level.FINER)) {
                        LOGGER.finer(property + ", call applyStyle: " + styleableProperty + ", value =" +
                                String.valueOf(value) + ", originOfCalculatedValue=" + originOfCalculatedValue);
                    }

                    styleableProperty.applyStyle(originOfCalculatedValue, value);

                    if (cacheContainer.cssSetProperties.containsKey(cssMetaData) == false) {
                        // track this property
                        CalculatedValue initialValue = new CalculatedValue(currentValue, originOfCurrentValue, false);
                        cacheContainer.cssSetProperties.put(cssMetaData, initialValue);
                    }

                }

                if (observableStyleMap != null) {
                    observableStyleMap.put(styleableProperty, styleList);
                }

            } catch (Exception e) {

                // RT-27155: if setting value raises exception, reset value
                // the value to initial and thereafter skip setting the property
                cacheEntry.put(property, null);

                List<CssError> errors = null;
                if ((errors = StyleManager.getErrors()) != null) {
                    final String msg = String.format("Failed to set css [%s] due to %s\n", cssMetaData, e.getMessage());
                    final CssError error = new CssError.PropertySetError(cssMetaData, node, msg);
                    errors.add(error);
                }
                // TODO: use logger here
                PlatformLogger logger = Logging.getCSSLogger();
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(String.format("Failed to set css [%s]\n", cssMetaData), e);
                }
            }

        }

        // RT-20643
        CssError.setCurrentScene(null);

    }

    /**
     * Gets the CSS CascadingStyle for the property of this node in these pseudo-class
     * states. A null style may be returned if there is no style information
     * for this combination of input parameters.
     *
     * @param styleable
     * @param property
     * @param states
     * @return
     */
    private CascadingStyle getStyle(Styleable styleable, String property, Set<PseudoClass> states){

        // Get all of the Styles which may apply to this particular property
        final StyleMap smap = getStyleMap(styleable);
        if (smap == null || smap.isEmpty()) return null;

        final Map<String, List<CascadingStyle>> cascadingStyleMap = smap.getCascadingStyles();
        if (cascadingStyleMap == null || cascadingStyleMap.isEmpty()) return null;

        List<CascadingStyle> styles = cascadingStyleMap.get(property);

        // If there are no styles for this property then we can just bail
        if ((styles == null) || styles.isEmpty()) return null;

        // Go looking for the style. We do this by visiting each CascadingStyle in
        // order finding the first that matches the current node & set of
        // pseudo-class states. We use an iteration style that avoids creating
        // garbage iterators (and wish javac did it for us...)
       CascadingStyle style = null;
        final int max = (styles == null) ? 0 : styles.size();
        for (int i=0; i<max; i++) {
            final CascadingStyle s = styles.get(i);
            final Selector sel = s == null ? null : s.getSelector();
            if (sel == null) continue; // bail if the selector is null.
//System.out.println(node.toString() + "\n\tstates=" + PseudoClassSet.getPseudoClasses(states) + "\n\tstateMatches? " + sel.stateMatches(node, states) + "\n\tsel=" + sel.toString());
            if (sel.stateMatches(styleable, states)) {
                style = s;
                break;
            }
        }

        return style;
    }

    /**
     * The main workhorse of this class, the lookup method walks up the CSS
     * style tree looking for the style information for the Node, the
     * property associated with the given styleable, in these states for this font.
     *
     *
     * @param node
     * @param styleable
     * @param states
     * @return
     */
    private CalculatedValue lookup(Node node,
                                   CssMetaData styleable,
                                   Set<PseudoClass> states,
                                   Node originatingNode,
                                   CalculatedValue cachedFont,
                                   List<Style> styleList) {

        if (styleable.getConverter() == FontConverter.getInstance()) {

            return lookupFont(node, styleable.getProperty(), cachedFont, styleList);
        }

        final String property = styleable.getProperty();

        // Get the CascadingStyle which may apply to this particular property
        CascadingStyle style = getStyle(node, property, states);

        // If no style was found and there are no sub styleables, then there
        // are no matching styles for this property. We will then either SKIP
        // or we will INHERIT. We will inspect the default value for the styleable,
        // and if it is INHERIT then we will inherit otherwise we just skip it.
        final List<CssMetaData<? extends Styleable, ?>> subProperties = styleable.getSubProperties();
        final int numSubProperties = (subProperties != null) ? subProperties.size() : 0;
        if (style == null) {

            if (numSubProperties == 0) {

                return handleNoStyleFound(node, styleable,
                        originatingNode, cachedFont, styleList);

            } else {

                // If style is null then it means we didn't successfully find the
                // property we were looking for. However, there might be sub styleables,
                // in which case we should perform a lookup for them. For example,
                // there might not be a style for "font", but there might be one
                // for "font-size" or "font-weight". So if the style is null, then
                // we need to check with the sub-styleables.

                // Build up a list of all SubProperties which have a constituent part.
                // I default the array to be the size of the number of total
                // sub styleables to avoid having the array grow.
                Map<CssMetaData,Object> subs = null;
                StyleOrigin origin = null;

                boolean isRelative = false;

                for (int i=0; i<numSubProperties; i++) {
                    CssMetaData subkey = subProperties.get(i);
                    CalculatedValue constituent =
                        lookup(node, subkey, states,
                            originatingNode, cachedFont, styleList);
                    if (constituent != SKIP) {
                        if (subs == null) {
                            subs = new HashMap<CssMetaData,Object>();
                        }
                        subs.put(subkey, constituent.getValue());

                        // origin of this style is the most specific
                        if ((origin != null && constituent.getOrigin() != null)
                                ? origin.compareTo(constituent.getOrigin()) < 0
                                : constituent.getOrigin() != null) {
                            origin = constituent.getOrigin();
                        }

                        // if the constiuent uses relative sizes, then
                        // isRelative is true;
                        isRelative = isRelative || constituent.isRelative();

                    }
                }

                // If there are no subkeys which apply...
                if (subs == null || subs.isEmpty()) {
                    return handleNoStyleFound(node, styleable,
                            originatingNode, cachedFont, styleList);
                }

                try {
                    final StyleConverter keyType = styleable.getConverter();
                    if (keyType instanceof StyleConverterImpl) {
                        Object ret = ((StyleConverterImpl)keyType).convert(subs);
                        return new CalculatedValue(ret, origin, isRelative);
                    } else {
                        assert false; // TBD: should an explicit exception be thrown here?
                        return SKIP;
                    }
                } catch (ClassCastException cce) {
                    final String msg = formatExceptionMessage(node, styleable, null, cce);
                    List<CssError> errors = null;
                    if ((errors = StyleManager.getErrors()) != null) {
                        final CssError error = new CssError.PropertySetError(styleable, node, msg);
                        errors.add(error);
                    }
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.warning("caught: ", cce);
                        LOGGER.warning("styleable = " + styleable);
                        LOGGER.warning("node = " + node.toString());
                    }
                    return SKIP;
                }
            }

        } else { // style != null

            // RT-10522:
            // If the user set the property and there is a style and
            // the style came from the user agent stylesheet, then
            // skip the value. A style from a user agent stylesheet should
            // not override the user set style.
            if (style.getOrigin() == StyleOrigin.USER_AGENT) {

                StyleableProperty styleableProperty = styleable.getStyleableProperty(originatingNode);
                // if styleableProperty is null, then we're dealing with a sub-property.
                if (styleableProperty != null && styleableProperty.getStyleOrigin() == StyleOrigin.USER) {
                    return SKIP;
                }
            }

            // If there was a style found, then we want to check whether the
            // value was "inherit". If so, then we will simply inherit.
            final ParsedValueImpl cssValue = style.getParsedValueImpl();
            if (cssValue != null && "inherit".equals(cssValue.getValue())) {
                if (styleList != null) styleList.add(style.getStyle());
                style = getInheritedStyle(node, property);

                if (style == null) return SKIP;

            }
        }

//        System.out.println("lookup " + property +
//                ", selector = \'" + style.selector.toString() + "\'" +
//                ", node = " + node.toString());

        if (styleList != null) {
            styleList.add(style.getStyle());
        }

        return calculateValue(style, node, styleable, states,
                originatingNode, cachedFont, styleList);
    }

    /**
     * Called when there is no style found.
     */
    private CalculatedValue handleNoStyleFound(Node node,
                                               CssMetaData cssMetaData,
                                               Node originatingNode,
                                               CalculatedValue cachedFont,
                                               List<Style> styleList) {

        if (cssMetaData.isInherits()) {


            StyleableProperty styleableProperty = cssMetaData.getStyleableProperty(node);
            StyleOrigin origin = styleableProperty != null ? styleableProperty.getStyleOrigin() : null;

            // RT-16308: if there is no matching style and the user set
            // the property, do not look for inherited styles.
            if (origin == StyleOrigin.USER) {

                    return SKIP;

            }

            CascadingStyle style = getInheritedStyle(node, cssMetaData.getProperty());
            if (style == null) return SKIP;

            CalculatedValue cv =
                    calculateValue(style, node, cssMetaData,
                                   node.pseudoClassStates, originatingNode,
                                   cachedFont, styleList );

            return cv;

        } else {

            // Not inherited. There is no style
            return SKIP;

        }
    }
    /**
     * Called when we must getInheritedStyle a value from a parent node in the scenegraph.
     */
    private CascadingStyle getInheritedStyle(
            Node styleable,
            String property) {

        Node parent = styleable != null ? styleable.getParent() : null;

        while (parent != null) {

            CssStyleHelper parentStyleHelper = parent.styleHelper;
            if (parentStyleHelper != null) {

                Set<PseudoClass> transitionStates = parent.pseudoClassStates;
                CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, transitionStates);

                if (cascadingStyle != null) {

                    final ParsedValueImpl cssValue = cascadingStyle.getParsedValueImpl();

                    if ("inherit".equals(cssValue.getValue())) {
                        return getInheritedStyle(parent, property);
                    }
                    return cascadingStyle;
                }

                return null;
            }

            parent = parent.getParent();

        }

        return null;
    }


    // helps with self-documenting the code
    static final Set<PseudoClass> NULL_PSEUDO_CLASS_STATE = null;

    /**
     * Find the property among the styles that pertain to the Node
     */
    private CascadingStyle resolveRef(Styleable styleable, String property, Set<PseudoClass> states) {

        final CascadingStyle style = getStyle(styleable, property, states);
        if (style != null) {
            return style;
        } else {
            // if style is null, it may be because there isn't a style for this
            // node in this state, or we may need to look up the parent chain
            if (states != null && states.size() > 0) {
                // if states > 0, then we need to check this node again,
                // but without any states.
                return resolveRef(styleable,property,NULL_PSEUDO_CLASS_STATE);
            } else {
                // TODO: This block was copied from inherit. Both should use same code somehow.
                Styleable styleableParent = styleable.getStyleableParent();
                CssStyleHelper parentStyleHelper = null;
                if (styleableParent != null && styleableParent instanceof Node) {
                    parentStyleHelper = ((Node)styleableParent).styleHelper;
                }
                while (styleableParent != null && parentStyleHelper == null) {
                    styleableParent = styleableParent.getStyleableParent();
                    if (styleableParent != null && styleableParent instanceof Node) {
                        parentStyleHelper = ((Node)styleableParent).styleHelper;
                    }
                }

                if (styleableParent == null || parentStyleHelper == null) {
                    return null;
                }
                Set<PseudoClass> styleableParentPseudoClassStates =
                    styleableParent instanceof Node
                        ? ((Node)styleableParent).pseudoClassStates
                        : styleable.getPseudoClassStates();

                return parentStyleHelper.resolveRef(styleableParent, property,
                        styleableParentPseudoClassStates);
            }
        }
    }

    // to resolve a lookup, we just need to find the parsed value.
    private ParsedValueImpl resolveLookups(
            Styleable styleable,
            ParsedValueImpl parsedValue,
            Set<PseudoClass> states,
            ObjectProperty<StyleOrigin> whence,
            List<Style> styleList) {

        return resolveLookups(styleable, parsedValue, states, whence, null, styleList);
    }

    // to resolve a lookup, we just need to find the parsed value.
    private ParsedValueImpl resolveLookups(
            Styleable styleable,
            ParsedValueImpl parsedValue,
            Set<PseudoClass> states,
            ObjectProperty<StyleOrigin> whence,
            Set<ParsedValue> resolves,
            List<Style> styleList) {

        //
        // either the value itself is a lookup, or the value contain a lookup
        //
        if (parsedValue.isLookup()) {

            // The value we're looking for should be a Paint, one of the
            // containers for linear, radial or ladder, or a derived color.
            final Object val = parsedValue.getValue();
            if (val instanceof String) {

                final String sval = (String)val;

                CascadingStyle resolved =
                    resolveRef(styleable, sval, states);

                if (resolved != null) {

                    if (resolves != null ) {

                        if (resolves.contains(resolved.getParsedValueImpl())) {

                            if (LOGGER.isLoggable(Level.WARNING)) {
                                LOGGER.warning("Loop detected in " + resolved.getRule().toString() + " while resolving '" + sval + "'");
                            }
                            throw new IllegalArgumentException("Loop detected in " + resolved.getRule().toString() + " while resolving '" + sval + "'");

                        } else {
                            resolves.add(parsedValue);
                        }

                    } else {
                        resolves = new HashSet<>();
                        resolves.add(parsedValue);
                    }

                    if (styleList != null) {
                        final Style style = resolved.getStyle();
                        if (style != null && !styleList.contains(style)) {
                            styleList.add(style);
                        }
                    }

                    // The origin of this parsed value is the greatest of
                    // any of the resolved reference. If a resolved reference
                    // comes from an inline style, for example, then the value
                    // calculated from the resolved lookup should have inline
                    // as its origin. Otherwise, an inline style could be
                    // stored in shared cache.
                    final StyleOrigin wOrigin = whence.get();
                    final StyleOrigin rOrigin = resolved.getOrigin();
                    if (rOrigin != null && (wOrigin == null ||  wOrigin.compareTo(rOrigin) < 0)) {
                        whence.set(rOrigin);
                    }

                    // the resolved value may itself need to be resolved.
                    // For example, if the value "color" resolves to "base",
                    // then "base" will need to be resolved as well.
                    ParsedValueImpl pv = resolveLookups(styleable, resolved.getParsedValueImpl(), states, whence, resolves, styleList);

                    if (resolves != null) {
                        resolves.remove(parsedValue);
                    }

                    return pv;

                }
            }
        }

        // If the value doesn't contain any values that need lookup, then bail
        if (!parsedValue.isContainsLookups()) {
            return parsedValue;
        }

        final Object val = parsedValue.getValue();
        if (val instanceof ParsedValueImpl[][]) {
        // If ParsedValueImpl is a layered sequence of values, resolve the lookups for each.
            final ParsedValueImpl[][] layers = (ParsedValueImpl[][])val;
            for (int l=0; l<layers.length; l++) {
                for (int ll=0; ll<layers[l].length; ll++) {
                    if (layers[l][ll] == null) continue;
                    layers[l][ll].setResolved(
                        resolveLookups(styleable, layers[l][ll], states, whence, null, styleList)
                    );
                }
            }

        } else if (val instanceof ParsedValueImpl[]) {
        // If ParsedValueImpl is a sequence of values, resolve the lookups for each.
            final ParsedValueImpl[] layer = (ParsedValueImpl[])val;
            for (int l=0; l<layer.length; l++) {
                if (layer[l] == null) continue;
                layer[l].setResolved(
                    resolveLookups(styleable, layer[l], states, whence, null, styleList)
                );
            }
        }

        return parsedValue;

    }

    private String getUnresolvedLookup(ParsedValueImpl resolved) {

        Object value = resolved.getValue();

        if (resolved.isLookup() && value instanceof String) {
            return (String)value;
        }

        if (value instanceof ParsedValueImpl[][]) {
            final ParsedValueImpl[][] layers = (ParsedValueImpl[][])value;
            for (int l=0; l<layers.length; l++) {
                for (int ll=0; ll<layers[l].length; ll++) {
                    if (layers[l][ll] == null) continue;
                    String unresolvedLookup = getUnresolvedLookup(layers[l][ll]);
                    if (unresolvedLookup != null) return unresolvedLookup;
                }
            }

        } else if (value instanceof ParsedValueImpl[]) {
        // If ParsedValueImpl is a sequence of values, resolve the lookups for each.
            final ParsedValueImpl[] layer = (ParsedValueImpl[])value;
            for (int l=0; l<layer.length; l++) {
                if (layer[l] == null) continue;
                String unresolvedLookup = getUnresolvedLookup(layer[l]);
                if (unresolvedLookup != null) return unresolvedLookup;
            }
        }

        return null;
    }

    private String formatUnresolvedLookupMessage(Styleable styleable, CssMetaData cssMetaData, Style style, ParsedValueImpl resolved) {

        // find value that could not be looked up
        String missingLookup = resolved != null ? getUnresolvedLookup(resolved) : null;
        if (missingLookup == null) missingLookup = "a lookup value";

        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Could not resolve '")
            .append(missingLookup)
            .append("'")
            .append(" while resolving lookups for '")
            .append(cssMetaData.getProperty())
            .append("'");

        final Rule rule = style != null ? style.getDeclaration().getRule(): null;
        final Stylesheet stylesheet = rule != null ? rule.getStylesheet() : null;
        final java.net.URL url = stylesheet != null ? stylesheet.getUrl() : null;
        if (url != null) {
            sbuf.append(" from rule '")
                .append(style.getSelector())
                .append("' in stylesheet ").append(url.toExternalForm());
        } else if (stylesheet != null && StyleOrigin.INLINE == stylesheet.getOrigin()) {
            sbuf.append(" from inline style on " )
                .append(styleable.toString());
        }

        return sbuf.toString();
    }

    private String formatExceptionMessage(Styleable styleable, CssMetaData cssMetaData, Style style, Exception e) {

        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Caught ")
            .append(e.toString())
            .append("'")
            .append(" while calculating value for '")
            .append(cssMetaData.getProperty())
            .append("'");

        final Rule rule = style != null ? style.getDeclaration().getRule(): null;
        final Stylesheet stylesheet = rule != null ? rule.getStylesheet() : null;
        final java.net.URL url = stylesheet != null ? stylesheet.getUrl() : null;
        if (url != null) {
            sbuf.append(" from rule '")
                .append(style.getSelector())
                .append("' in stylesheet ").append(url.toExternalForm());
        } else if (stylesheet != null && StyleOrigin.INLINE == stylesheet.getOrigin()) {
            sbuf.append(" from inline style on " )
                .append(styleable.toString());
        }

        return sbuf.toString();
    }


    private CalculatedValue calculateValue(
            final CascadingStyle style,
            final Styleable node,
            final CssMetaData cssMetaData,
            final Set<PseudoClass> states,
            final Styleable originatingNode,
            final CalculatedValue fontFromCacheEntry,
            final List<Style> styleList) {

        final ParsedValueImpl cssValue = style.getParsedValueImpl();
        if (cssValue != null && !("null").equals(cssValue.getValue())) {

            ParsedValueImpl resolved = null;
            try {

                ObjectProperty<StyleOrigin> whence = new SimpleObjectProperty<StyleOrigin>(style.getOrigin());
                resolved = resolveLookups(node, cssValue, states, whence, styleList);

                final String property = cssMetaData.getProperty();

                // The computed value
                Object val = null;
                boolean isFontProperty =
                        "-fx-font".equals(property) ||
                        "-fx-font-size".equals(property);

                boolean isRelative = ParsedValueImpl.containsFontRelativeSize(resolved, isFontProperty);

                //
                // Avoid using a font calculated from a relative size
                // to calculate a font with a relative size.
                // For example:
                // Assume the default font size is 13 and we have a style with
                // -fx-font-size: 1.5em, then the cacheEntry font value will
                // have a size of 13*1.5=19.5.
                // Now, when converting that same font size again in response
                // to looking up a value for -fx-font, we do not want to use
                // 19.5 as the font for relative size conversion since this will
                // yield a font 19.5*1.5=29.25 when really what we want is
                // a font size of 19.5.
                // In this situation, then, we use the font from the parent's
                // cache entry.
                Font fontForFontRelativeSizes = null;

                if (isRelative && isFontProperty &&
                    (fontFromCacheEntry == null || fontFromCacheEntry.isRelative())) {

                    Styleable parent = node;
                    CalculatedValue childsCachedFont = fontFromCacheEntry;
                    do {

                        CalculatedValue parentsCachedFont = getCachedFont(parent.getStyleableParent());

                        if (parentsCachedFont != null)  {

                            if (parentsCachedFont.isRelative()) {

                                //
                                // If the cached fonts are the same, then the cached font came from the same
                                // style and we need to keep looking. Otherwise, use the font we found.
                                //
                                if (childsCachedFont == null || parentsCachedFont.equals(childsCachedFont)) {
                                    childsCachedFont = parentsCachedFont;
                                } else {
                                    fontForFontRelativeSizes = (Font)parentsCachedFont.getValue();
                                }

                            } else  {
                                // fontValue.isRelative() == false!
                                fontForFontRelativeSizes = (Font)parentsCachedFont.getValue();
                            }

                        }

                    } while(fontForFontRelativeSizes == null &&
                            (parent = parent.getStyleableParent()) != null);
                }

                // did we get a fontValue from the preceding block?
                // if not, get it from our cacheEntry or choose the default
                if (fontForFontRelativeSizes == null) {
                    if (fontFromCacheEntry != null && fontFromCacheEntry.isRelative() == false) {
                        fontForFontRelativeSizes = (Font)fontFromCacheEntry.getValue();
                    } else {
                        fontForFontRelativeSizes = Font.getDefault();
                    }
                }

                if (resolved.getConverter() != null)
                    val = resolved.convert(fontForFontRelativeSizes);
                else
                    val = cssMetaData.getConverter().convert(resolved, fontForFontRelativeSizes);

                final StyleOrigin origin = whence.get();
                return new CalculatedValue(val, origin, isRelative);

            } catch (ClassCastException cce) {
                final String msg = formatUnresolvedLookupMessage(node, cssMetaData, style.getStyle(),resolved);
                List<CssError> errors = null;
                if ((errors = StyleManager.getErrors()) != null) {
                    final CssError error = new CssError.PropertySetError(cssMetaData, node, msg);
                    errors.add(error);
                }
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(msg);
                    LOGGER.fine("node = " + node.toString());
                    LOGGER.fine("cssMetaData = " + cssMetaData);
                    LOGGER.fine("styles = " + getMatchingStyles(node, cssMetaData));
                }
                return SKIP;
            } catch (IllegalArgumentException iae) {
                final String msg = formatExceptionMessage(node, cssMetaData, style.getStyle(), iae);
                List<CssError> errors = null;
                if ((errors = StyleManager.getErrors()) != null) {
                    final CssError error = new CssError.PropertySetError(cssMetaData, node, msg);
                    errors.add(error);
                }
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning("caught: ", iae);
                    LOGGER.fine("styleable = " + cssMetaData);
                    LOGGER.fine("node = " + node.toString());
                }
                return SKIP;
            } catch (NullPointerException npe) {
                final String msg = formatExceptionMessage(node, cssMetaData, style.getStyle(), npe);
                List<CssError> errors = null;
                if ((errors = StyleManager.getErrors()) != null) {
                    final CssError error = new CssError.PropertySetError(cssMetaData, node, msg);
                    errors.add(error);
                }
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning("caught: ", npe);
                    LOGGER.fine("styleable = " + cssMetaData);
                    LOGGER.fine("node = " + node.toString());
                }
                return SKIP;
            } finally {
                if (resolved != null) resolved.setResolved(null);
            }

        }
        // either cssValue was null or cssValue's value was "null"
        return new CalculatedValue(null, style.getOrigin(), false);

    }

    private static final CssMetaData dummyFontProperty =
            new FontCssMetaData<Node>("-fx-font", Font.getDefault()) {

        @Override
        public boolean isSettable(Node node) {
            return true;
        }

        @Override
        public StyleableProperty<Font> getStyleableProperty(Node node) {
            return null;
        }
    };

    private CalculatedValue getCachedFont(Styleable styleable) {

        if (styleable instanceof Node == false) return null;

        CalculatedValue cachedFont = null;

        Node parent = (Node)styleable;

        final CssStyleHelper parentHelper = parent.styleHelper;

        // if there is no parentHelper,
        // or there is a parentHelper but no cacheContainer,
        // then look to the next parent
        if (parentHelper == null || parentHelper.cacheContainer == null) {

            cachedFont = getCachedFont(parent.getStyleableParent());

        // there is a parent helper and a cacheContainer,
        } else  {

            CacheContainer parentCacheContainer = parentHelper.cacheContainer;
            if ( parentCacheContainer != null
                    && parentCacheContainer.fontSizeCache != null
                    && parentCacheContainer.fontSizeCache.isEmpty() == false) {

                Set<PseudoClass>[] transitionStates = parentHelper.getTransitionStates(parent);
                StyleCacheEntry.Key parentCacheEntryKey = new StyleCacheEntry.Key(transitionStates, Font.getDefault());
                cachedFont = parentCacheContainer.fontSizeCache.get(parentCacheEntryKey);

            } else {

                Set<PseudoClass> pseudoClassState = parent.getPseudoClassStates();
                cachedFont = parentHelper.lookup(parent, dummyFontProperty, pseudoClassState, parent, null, null);
            }
        }

        return cachedFont;
    }

    /*package access for testing*/ FontPosture getFontPosture(Font font) {
        if (font == null) return FontPosture.REGULAR;

        String fontName = font.getName().toLowerCase();

        if (fontName.contains("italic")) {
            return FontPosture.ITALIC;
        }

        return FontPosture.REGULAR;
    }

    /*package access for testing*/ FontWeight getFontWeight(Font font) {
        if (font == null) return FontWeight.NORMAL;

        String fontName = font.getName().toLowerCase();

        if (fontName.contains("bold")) {
            if (fontName.contains("extra")) return FontWeight.EXTRA_BOLD;
            if (fontName.contains("ultra")) return FontWeight.EXTRA_BOLD;
            else if (fontName.contains("semi")) return FontWeight.SEMI_BOLD;
            else if (fontName.contains("demi")) return FontWeight.SEMI_BOLD;
            else return FontWeight.BOLD;

        } else if (fontName.contains("light")) {
            if (fontName.contains("extra")) return FontWeight.EXTRA_LIGHT;
            if (fontName.contains("ultra")) return FontWeight.EXTRA_LIGHT;
            else return FontWeight.LIGHT;

        } else if (fontName.contains("black")) {
            return FontWeight.BLACK;

        } else if (fontName.contains("heavy")) {
            return FontWeight.BLACK;

        } else if (fontName.contains("medium")) {
            return FontWeight.MEDIUM;
        }

        return FontWeight.NORMAL;

    }

    /*package access for testing*/ String getFontFamily(Font font) {
        if (font == null) return Font.getDefault().getFamily();
        return font.getFamily();
    }


    /*package access for testing*/ Font deriveFont(
            String fontFamily, FontWeight fontWeight, FontPosture fontPosture, double fontSize) {

        return  Font.font(
                Utils.stripQuotes(fontFamily),
                fontWeight != FontWeight.NORMAL ? fontWeight : null,
                fontPosture != FontPosture.REGULAR ? fontPosture : null,
                fontSize);
    }

    /*package access for testing*/ Font deriveFont(Font font, String fontFamily) {

        if (font == null) return Font.getDefault();

        FontPosture fontPosture = getFontPosture(font);
        FontWeight fontWeight = getFontWeight(font);
        double fontSize = font.getSize();

        return deriveFont(fontFamily, fontWeight, fontPosture, fontSize);
    }

    /*package access for testing*/ Font deriveFont(Font font, double fontSize) {
        if (font == null) return Font.getDefault();

        String fontFamily = getFontFamily(font);
        FontPosture fontPosture = getFontPosture(font);
        FontWeight fontWeight = getFontWeight(font);

        return deriveFont(fontFamily, fontWeight, fontPosture, fontSize);
    }

    /*package access for testing*/ Font deriveFont(Font font, FontPosture fontPosture) {

        if (font == null) return Font.getDefault();

        String fontFamily = getFontFamily(font);
        FontWeight fontWeight = getFontWeight(font);
        double fontSize = font.getSize();

        return deriveFont(fontFamily, fontWeight, fontPosture, fontSize);
    }

    /*package access for testing*/ Font deriveFont(Font font, FontWeight fontWeight) {

        if (font == null) return Font.getDefault();

        String fontFamily = getFontFamily(font);
        FontPosture fontPosture = getFontPosture(font);
        double fontSize = font.getSize();

        return deriveFont(fontFamily, fontWeight, fontPosture, fontSize);
    }

    /**
     * Look up a font property. This is handled separately from lookup since
     * font is inherited and has sub-properties. One should expect that the
     * text font for the following would be 16px Arial. The lookup method would
     * give 16px system since it would look <em>only</em> for font-size,
     * font-family, etc <em>only</em> if the lookup on font failed.
     * <pre>
     * Text text = new Text("Hello World");
     * text.setStyle("-fx-font-size: 16px;");
     * Group group = new Group();
     * group.setStyle("-fx-font: 12px Arial;");
     * group.getChildren().add(text);
     * </pre>
     */
     /*package access for testing*/ CalculatedValue lookupFont(
            final Node styleable,
            final String property,
            final CalculatedValue cachedFont,
            final List<Style> styleList)
    {

        StyleOrigin origin = null;
        int distance = 0;

        // Each time a sub-property is encountered, cvFont is passed along to
        // calculateValue and the resulting font becomes cvFont. In the end
        // cvFont is returned.
        CalculatedValue cvFont = cachedFont;

        Set<PseudoClass> states = styleable.pseudoClassStates;

        // RT-20145 - if looking for font size and the node has a font,
        // use the font property's value if it was set by the user and
        // there is not an inline or author style.

        if (cacheContainer.fontProp != null) {
            StyleableProperty<Font> styleableProp = cacheContainer.fontProp.getStyleableProperty(styleable);
            StyleOrigin fpOrigin = styleableProp.getStyleOrigin();
            if (fpOrigin == StyleOrigin.USER) {
                origin = fpOrigin;
                Font font = styleableProp.getValue();
                cvFont = new CalculatedValue(font, origin, false);
            }
        }

        final boolean userSetFont = (origin == StyleOrigin.USER);

        //
        // Look up the font- properties
        //
        CascadingStyle fontShorthand = getStyle(styleable, property, states);

        // don't look past current node for font shorthand if user set the font
        // or if we're looking up the font for font cache.
        if (fontShorthand == null && origin != StyleOrigin.USER) {

            Node parent = styleable != null ? styleable.getParent() : null;

            while (parent != null) {

                CssStyleHelper parentStyleHelper = parent.styleHelper;
                if (parentStyleHelper != null) {

                    distance += 1;

                    Set<PseudoClass> transitionStates = parent.pseudoClassStates;
                    CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, transitionStates);

                    if (cascadingStyle != null) {

                        final ParsedValueImpl cssValue = cascadingStyle.getParsedValueImpl();

                        if ("inherit".equals(cssValue.getValue()) == false) {
                            fontShorthand = cascadingStyle;
                            break;
                        }
                    }

                }

                parent = parent.getParent();

            }

        }

        if (fontShorthand != null) {

            //
            // If we don't have an existing font, or if the origin of the
            // existing font is less than that of the shorthand, then
            // take the shorthand. If the origins compare equals, then take
            // the shorthand since the fontProp value will not have been
            // updated yet.
            //
            if (origin == null || origin.compareTo(fontShorthand.getOrigin()) <= 0) {

                final CalculatedValue cv =
                        calculateValue(fontShorthand, styleable, dummyFontProperty,
                                       states, styleable, cvFont, styleList);

                // cv could be SKIP
                if (cv.getValue() instanceof Font) {
                    origin = cv.getOrigin();
                    cvFont = cv;
                }

            }
        }

        CascadingStyle fontSize = getStyle(styleable, property.concat("-size"), states);
        // don't look past current node for font size if user set the font
        // or if we're looking up the font for font cache.
        if (fontSize == null && origin != StyleOrigin.USER) {
            fontSize = lookupInheritedFont(styleable, property.concat("-size"), distance);
        }

        // font-size must be closer and more specific than font shorthand
        if (fontSize != null) {

            if (fontShorthand == null || fontShorthand.compareTo(fontSize) >= 0) {

                if (origin == null || origin.compareTo(fontSize.getOrigin()) <= 0) {

                    final CalculatedValue cv =
                            calculateValue(fontSize, styleable, dummyFontProperty,
                                    states, styleable, cvFont, styleList);

                    if (cv.getValue() instanceof Double) {
                        origin = cv.getOrigin();

                        if (cvFont != null) {
                            boolean isRelative = cvFont.isRelative() || cv.isRelative();
                            Font font = deriveFont((Font) cvFont.getValue(), ((Double) cv.getValue()).doubleValue());
                            cvFont = new CalculatedValue(font, origin, isRelative);
                        } else {
                            boolean isRelative = cv.isRelative();
                            Font font = deriveFont(Font.getDefault(), ((Double) cv.getValue()).doubleValue());
                            cvFont = new CalculatedValue(font, origin, isRelative);
                        }
                    }
                }

            }

        }

        // if cachedFont is null, then we're in this method to look up a font for the CacheContainer's fontSizeCache
        // and we only care about font-size or the size from font shorthand.
        if (cachedFont != null ) {

            CascadingStyle fontWeight = getStyle(styleable, property.concat("-weight"), states);
            // don't look past current node for font weight if user set the font
            if (fontWeight == null && origin != StyleOrigin.USER) {
                fontWeight = lookupInheritedFont(styleable,property.concat("-weight"), distance);
            }

            if (fontWeight != null) {

                if (fontShorthand == null || fontShorthand.compareTo(fontWeight) >= 0) {

                    if (origin == null || origin.compareTo(fontWeight.getOrigin()) <= 0) {

                        final CalculatedValue cv =
                                calculateValue(fontWeight, styleable, dummyFontProperty,
                                        states, styleable, null, null);

                        if (cv.getValue() instanceof FontWeight) {
                            origin = cv.getOrigin();

                            if (cvFont != null) {
                                boolean isRelative = cvFont.isRelative();
                                Font font = deriveFont((Font) cvFont.getValue(), (FontWeight) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, isRelative);
                            } else {
                                Font font = deriveFont(Font.getDefault(), (FontWeight) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, false);
                            }
                        }
                    }
                }

            }

            CascadingStyle fontStyle = getStyle(styleable, property.concat("-style"), states);
            // don't look past current node for font style if user set the font
            if (fontStyle == null && origin != StyleOrigin.USER) {
                fontStyle = lookupInheritedFont(styleable, property.concat("-style"), distance);
            }

            if (fontStyle != null) {

                if (fontShorthand == null || fontShorthand.compareTo(fontStyle) >= 0) {

                    if (origin == null || origin.compareTo(fontStyle.getOrigin()) <= 0) {

                        final CalculatedValue cv =
                                calculateValue(fontStyle, styleable, dummyFontProperty,
                                        states, styleable, null, null);

                        if (cv.getValue() instanceof FontPosture) {
                            origin = cv.getOrigin();

                            if (cvFont != null) {
                                boolean isRelative = cvFont.isRelative();
                                Font font = deriveFont((Font) cvFont.getValue(), (FontPosture) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, isRelative);
                            } else {
                                boolean isRelative = cv.isRelative();
                                Font font = deriveFont(Font.getDefault(), (FontPosture) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, false);
                            }
                        }
                    }

                }
            }

            CascadingStyle fontFamily = getStyle(styleable, property.concat("-family"), states);
            // don't look past current node for font family if user set the font
            if (fontFamily == null && origin != StyleOrigin.USER) {
                fontFamily = lookupInheritedFont(styleable,property.concat("-family"), distance);
            }

            if (fontFamily != null) {

                if (fontShorthand == null || fontShorthand.compareTo(fontFamily) >= 0) {

                    if (origin == null || origin.compareTo(fontFamily.getOrigin()) <= 0) {

                        final CalculatedValue cv =
                                calculateValue(fontFamily, styleable, dummyFontProperty,
                                        states, styleable, null, null);

                        if (cv.getValue() instanceof String) {
                            origin = cv.getOrigin();

                            if (cvFont != null) {
                                boolean isRelative = cvFont.isRelative();
                                Font font = deriveFont((Font) cvFont.getValue(), (String) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, isRelative);
                            } else {
                                Font font = deriveFont(Font.getDefault(), (String) cv.getValue());
                                cvFont = new CalculatedValue(font, origin, false);
                            }
                        }
                    }
                }

            }
        }

        // If cvFont is null, then the node doesn't have a font property and
        // there are no font styles.
        // If cvFont is not null but the origin is null, then cvFont is from
        // font property that hasn't been set by the user or by css.
        // If the origin is USER, then skip the value.
        if (cvFont != null) {

            // if cachedFont is null, then we're in this method to look up a font for the CacheContainer's fontSizeCache
            if (cachedFont == null) {
                return cvFont;

            } else if (origin != null && origin != StyleOrigin.USER) {
                return cvFont;

            }
        }

        return SKIP;
    }

    /**
     * Called when we must getInheritedStyle a value from a parent node in the scenegraph.
     */
    private CascadingStyle lookupInheritedFont(
            Node styleable,
            String property,
            int distance) {

        Node parent = styleable != null ? styleable.getParent() : null;

        int nlooks = distance;
        while (parent != null && nlooks > 0) {

            CssStyleHelper parentStyleHelper = parent.styleHelper;
            if (parentStyleHelper != null) {

                nlooks -= 1;

                Set<PseudoClass> transitionStates = parent.pseudoClassStates;
                CascadingStyle cascadingStyle = parentStyleHelper.getStyle(parent, property, transitionStates);

                if (cascadingStyle != null) {

                    final ParsedValueImpl cssValue = cascadingStyle.getParsedValueImpl();

                    if ("inherit".equals(cssValue.getValue()) == false) {
                        return cascadingStyle;
                    }
                }

            }

            parent = parent.getParent();

        }

        return null;
    }


    /**
     * Called from CssMetaData getMatchingStyles
     * @param node
     * @param styleableProperty
     * @return
     */
    List<Style> getMatchingStyles(Styleable node, CssMetaData styleableProperty) {

        final List<CascadingStyle> styleList = new ArrayList<CascadingStyle>();

        getMatchingStyles(node, styleableProperty, styleList);

        List<CssMetaData<? extends Styleable, ?>> subProperties = styleableProperty.getSubProperties();
        if (subProperties != null) {
            for (int n=0,nMax=subProperties.size(); n<nMax; n++) {
                final CssMetaData subProperty = subProperties.get(n);
                getMatchingStyles(node, subProperty, styleList);
            }
        }

        Collections.sort(styleList);

        final List<Style> matchingStyles = new ArrayList<Style>(styleList.size());
        for (int n=0,nMax=styleList.size(); n<nMax; n++) {
            final Style style = styleList.get(n).getStyle();
            if (!matchingStyles.contains(style)) matchingStyles.add(style);
        }

        return matchingStyles;
    }

    private void getMatchingStyles(Styleable node, CssMetaData styleableProperty, List<CascadingStyle> styleList) {

        if (node != null) {

            String property = styleableProperty.getProperty();
            Node _node = node instanceof Node ? (Node)node : null;
            final Map<String, List<CascadingStyle>> smap = getCascadingStyles(_node);
            if (smap == null) return;

             List<CascadingStyle> styles = smap.get(property);

            if (styles != null) {
                styleList.addAll(styles);
                for (int n=0, nMax=styles.size(); n<nMax; n++) {
                    final CascadingStyle style = styles.get(n);
                    final ParsedValueImpl parsedValue = style.getParsedValueImpl();
                    getMatchingLookupStyles(node, parsedValue, styleList);
                }
            }

            if (styleableProperty.isInherits()) {
                Styleable parent = node.getStyleableParent();
                while (parent != null) {
                    CssStyleHelper parentHelper = parent instanceof Node
                            ? ((Node)parent).styleHelper
                            : null;
                    if (parentHelper != null) {
                        parentHelper.getMatchingStyles(parent, styleableProperty, styleList);
                    }
                    parent = parent.getStyleableParent();
                }
            }

        }

    }

    // Pretty much a duplicate of resolveLookups, but without the state
    private void getMatchingLookupStyles(Styleable node, ParsedValueImpl parsedValue, List<CascadingStyle> styleList) {

        if (parsedValue.isLookup()) {

            Object value = parsedValue.getValue();

            if (value instanceof String) {

                final String property = (String)value;
                // gather up any and all styles that contain this value as a property
                Styleable parent = node;
                do {
                    final Node _parent = parent instanceof Node ? (Node)parent : null;
                    final CssStyleHelper helper = _parent != null
                            ? _parent.styleHelper
                            : null;
                    if (helper != null) {

                        final int start = styleList.size();

                        final Map<String, List<CascadingStyle>> smap = helper.getCascadingStyles(_parent);
                        if (smap != null) {

                            List<CascadingStyle> styles = smap.get(property);

                            if (styles != null) {
                                styleList.addAll(styles);
                            }

                        }

                        final int end = styleList.size();

                        for (int index=start; index<end; index++) {
                            final CascadingStyle style = styleList.get(index);
                            getMatchingLookupStyles(parent, style.getParsedValueImpl(), styleList);
                        }
                    }

                } while ((parent = parent.getStyleableParent()) != null);

            }
        }

        // If the value doesn't contain any values that need lookup, then bail
        if (!parsedValue.isContainsLookups()) {
            return;
        }

        final Object val = parsedValue.getValue();
        if (val instanceof ParsedValueImpl[][]) {
        // If ParsedValueImpl is a layered sequence of values, resolve the lookups for each.
            final ParsedValueImpl[][] layers = (ParsedValueImpl[][])val;
            for (int l=0; l<layers.length; l++) {
                for (int ll=0; ll<layers[l].length; ll++) {
                    if (layers[l][ll] == null) continue;
                        getMatchingLookupStyles(node, layers[l][ll], styleList);
                }
            }

        } else if (val instanceof ParsedValueImpl[]) {
        // If ParsedValueImpl is a sequence of values, resolve the lookups for each.
            final ParsedValueImpl[] layer = (ParsedValueImpl[])val;
            for (int l=0; l<layer.length; l++) {
                if (layer[l] == null) continue;
                    getMatchingLookupStyles(node, layer[l], styleList);
            }
        }

    }

}