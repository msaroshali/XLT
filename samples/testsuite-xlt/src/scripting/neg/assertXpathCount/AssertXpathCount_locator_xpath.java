/*
 * Copyright (c) 2005-2021 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scripting.neg.assertXpathCount;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.InvalidSelectorException;

import scripting.util.PageOpener;

import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverScriptTestCase;
import com.xceptance.xlt.api.webdriver.XltDriver;

/**
 * 
 */
public class AssertXpathCount_locator_xpath extends AbstractWebDriverScriptTestCase
{

    /**
     * Constructor.
     */
    public AssertXpathCount_locator_xpath()
    {
        super(new XltDriver(true), null);
    }

    /**
     * located by link
     * 
     * @throws Throwable
     */
    @Test(expected = InvalidSelectorException.class)
    public void test() throws Throwable
    {
        PageOpener.examplePage(this);
        assertXpathCount("xpath=id('anc_sel1')", 1);
    }

    @After
    public void after()
    {
        getWebDriver().quit();
    }
}