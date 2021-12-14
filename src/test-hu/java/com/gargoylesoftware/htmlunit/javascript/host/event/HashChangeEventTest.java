/*
 * Copyright (c) 2002-2021 Gargoyle Software Inc.
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
package com.gargoylesoftware.htmlunit.javascript.host.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlPageTest;
import com.gargoylesoftware.htmlunit.junit.BrowserRunner;
import com.gargoylesoftware.htmlunit.junit.BrowserRunner.Alerts;

/**
 * Tests for {@link HashChangeEvent}.
 *
 * @author Frank Danek
 * @author Ronald Brill
 */
@RunWith(BrowserRunner.class)
public class HashChangeEventTest extends WebDriverTestCase {

    private static final String DUMP_EVENT_FUNCTION = "  function dump(event) {\n"
        + "    if (event) {\n"
        + "      log(event);\n"
        + "      log(event.type);\n"
        + "      log(event.bubbles);\n"
        + "      log(event.cancelable);\n"
        + "      log(event.composed);\n"

        + "      log(event.oldURL);\n"
        + "      log(event.newURL);\n"
        + "    } else {\n"
        + "      log('no event');\n"
        + "    }\n"
        + "  }\n";

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HashChangeEvent]", "hashchange", "false", "false", "false", "", ""},
            IE = "exception")
    public void create_ctor() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = new HashChangeEvent('hashchange');\n"
            + "      dump(event);\n"
            + "    } catch (e) { log('exception') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HashChangeEvent]", "hashchange", "true", "false", "false", "null", "§§URL§§#1"},
            IE = "exception")
    public void create_ctorWithDetails() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = new HashChangeEvent('hashchange', {\n"
            + "        'bubbles': true,\n"
            + "        'oldURL': null,\n"
            + "        'newURL': '" + URL_FIRST + "#1'\n"
            + "      });\n"
            + "      dump(event);\n"
            + "    } catch (e) { log('exception') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HashChangeEvent]", "", "false", "false", "false", "", ""},
            IE = "exception")
    public void create_createEvent() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = document.createEvent('HashChangeEvent');\n"
            + "      dump(event);\n"
            + "    } catch (e) { log('exception') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HashChangeEvent]", "[object HashChangeEvent]",
                       "hashchange", "true", "false", "false", "§§URL§§", "§§URL§§#1"},
            CHROME = {"[object HashChangeEvent]", "missing initHashChangeEvent"},
            EDGE = {"[object HashChangeEvent]", "missing initHashChangeEvent"},
            IE = "exception createEvent")
    public void initHashChangeEvent() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = document.createEvent('HashChangeEvent');\n"
            + "      log(event);\n"
            + "    } catch (e) { log('exception createEvent'); return; }\n"

            + "    if (!event.initHashChangeEvent) {log('missing initHashChangeEvent'); return;}\n"

            + "    try {\n"
            + "      event.initHashChangeEvent('hashchange', true, false, '" + URL_FIRST + "', '"
            + URL_FIRST + "#1');\n"
            + "      dump(event);\n"
            + "    } catch (e) { log('exception initHashChangeEvent') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = "exception",
            FF = {"[object HashChangeEvent]", "hashchange", "true", "false", "false", "§§URL§§", "§§URL§§#1"},
            FF_ESR = {"[object HashChangeEvent]", "hashchange", "true", "false", "false", "§§URL§§", "§§URL§§#1"})
    public void dispatchEvent() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = document.createEvent('HashChangeEvent');\n"
            + "      event.initHashChangeEvent('hashchange', true, false, '" + URL_FIRST + "', '"
            + URL_FIRST + "#1');\n"
            + "      dispatchEvent(event);\n"
            + "    } catch (e) { log('exception') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "  window.onhashchange = dump;\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        expandExpectedAlertsVariables(URL_FIRST);
        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object Event]", "hashchange", "true", "false", "false", "undefined", "undefined"},
            IE = {"[object Event]", "hashchange", "true", "false", "undefined", "undefined", "undefined"})
    public void dispatchEvent_event() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    try {\n"
            + "      var event = document.createEvent('Event');\n"
            + "      event.initEvent('hashchange', true, false);\n"
            + "      dispatchEvent(event);\n"
            + "    } catch (e) { log('exception') }\n"
            + "  }\n"
            + DUMP_EVENT_FUNCTION
            + "  window.onhashchange = dump;\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("supported")
    public void onHashChange_supported() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + "  function test() {\n"
            + "    if ('onhashchange' in window) { log('supported') }\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageVerifyTitle2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(DEFAULT = {"[object HashChangeEvent]", "hashchange", "false", "false", "false", "§§URL§§", "§§URL§§#1"},
            IE = {"[object Event]", "hashchange", "false", "false", "undefined", "undefined", "undefined"})
    public void onHashChange() throws Exception {
        final String html = HtmlPageTest.STANDARDS_MODE_PREFIX_
            + "<html><head><script>\n"
            + LOG_TITLE_FUNCTION
            + DUMP_EVENT_FUNCTION
            + "  window.onhashchange = dump;\n"
            + "</script></head><body>\n"
            + "  <a id='click' href='#1'>change hash</a>\n"
            + "</body></html>";

        expandExpectedAlertsVariables(URL_FIRST);
        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("click")).click();

        verifyTitle2(driver, getExpectedAlerts());
    }
}
