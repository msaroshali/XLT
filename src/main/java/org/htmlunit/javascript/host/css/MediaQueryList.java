/*
 * Copyright (c) 2002-2023 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.javascript.host.css;

import static org.htmlunit.javascript.configuration.SupportedBrowser.CHROME;
import static org.htmlunit.javascript.configuration.SupportedBrowser.EDGE;
import static org.htmlunit.javascript.configuration.SupportedBrowser.FF;
import static org.htmlunit.javascript.configuration.SupportedBrowser.FF_ESR;

import org.htmlunit.cssparser.dom.MediaListImpl;
import org.htmlunit.cssparser.parser.CSSErrorHandler;

import org.htmlunit.WebWindow;
import org.htmlunit.css.CssStyleSheet;
import org.htmlunit.javascript.configuration.JsxClass;
import org.htmlunit.javascript.configuration.JsxConstructor;
import org.htmlunit.javascript.configuration.JsxFunction;
import org.htmlunit.javascript.configuration.JsxGetter;
import org.htmlunit.javascript.host.event.EventTarget;

/**
 * A JavaScript object for {@code MediaQueryList}.
 *
 * @author Ahmed Ashour
 * @author Ronald Brill
 */
@JsxClass
public class MediaQueryList extends EventTarget {

    private String media_;

    /**
     * Default constructor.
     */
    @JsxConstructor({CHROME, EDGE, FF, FF_ESR})
    public MediaQueryList() {
    }

    /**
     * Constructor.
     *
     * @param mediaQueryString the media query string
     */
    public MediaQueryList(final String mediaQueryString) {
        media_ = mediaQueryString;
    }

    /**
     * Returns the {@code media} property.
     * @return the {@code media} property
     */
    @JsxGetter
    public String getMedia() {
        return media_;
    }

    /**
     * Returns whether the document currently matches the media query list or not.
     * @return whether the document currently matches the media query list or not
     */
    @JsxGetter
    public boolean isMatches() {
        final WebWindow webWindow = getWindow().getWebWindow();
        final CSSErrorHandler errorHandler = webWindow.getWebClient().getCssErrorHandler();
        final MediaListImpl mediaList = CssStyleSheet.parseMedia(errorHandler, media_);
        return CssStyleSheet.isActive(mediaList, webWindow);
    }

    /**
     * Adds the {@code listener} event handler for this element.
     * @param listener the {@code listener} event handler for this element
     */
    @JsxFunction
    public void addListener(final Object listener) {
        // dummy impl for the moment
    }

    /**
     * Removes the {@code listener} event handler for this element.
     * @param listener the {@code listener} event handler to be removed
     */
    @JsxFunction
    public void removeListener(final Object listener) {
        // dummy impl for the moment
    }
}