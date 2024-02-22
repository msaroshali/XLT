/*
 * Copyright (c) 2005-2024 Xceptance Software Technologies GmbH
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
package scripting.pos;

import org.junit.After;
import org.junit.Test;

import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverScriptTestCase;
import com.xceptance.xlt.api.webdriver.XltDriver;
import scripting.util.PageOpener;


/**
 * 
 */
public class AssertText extends AbstractWebDriverScriptTestCase
{

	/**
	 * Constructor.
	 */
	public AssertText()
	{
		super( new XltDriver( true ), null );
	}

	@Test
	public void test() throws Throwable
	{
		PageOpener.examplePage( this );

		// whitespace divs
		assertText( "id=ws2_spaces_only", "          " );
		assertText( "id=ws2_html_spaces_only", "          " );

		// draw border to make 'em visible
		click( "id=ws2_spaces_only_makeVisible" );
		click( "id=ws2_html_spaces_only_makeVisible" );

		// assert again
		assertText( "id=ws2_spaces_only", "          " );
		assertText( "id=ws2_html_spaces_only", "          " );
	}

    @After
    public void after()
    {
        getWebDriver().quit();
    }
}