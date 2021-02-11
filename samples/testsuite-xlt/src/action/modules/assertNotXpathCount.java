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
package action.modules;

import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.engine.scripting.AbstractHtmlUnitActionsModule;

import action.modules.Open_ExamplePage;
import action.modules.assertNotXpathCount_actions.existing_wrongCount;
import action.modules.assertNotXpathCount_actions.non_existing_element;
import action.modules.assertNotXpathCount_actions.iframe3;

/**
 * TODO: Add class description
 */
public class assertNotXpathCount extends AbstractHtmlUnitActionsModule
{


    /**
     * Constructor.
     */
    public assertNotXpathCount()
    {
    }


    /**
     * @{inheritDoc}
     */
    protected AbstractHtmlPageAction execute(final AbstractHtmlPageAction prevAction) throws Throwable
    {
        AbstractHtmlPageAction lastAction = prevAction;
        final Open_ExamplePage open_ExamplePage = new Open_ExamplePage();
        lastAction = open_ExamplePage.run(lastAction);

        lastAction = new existing_wrongCount(lastAction);
        lastAction.run();

        lastAction = new non_existing_element(lastAction);
        lastAction.run();

        lastAction = new iframe3(lastAction);
        lastAction.run();


        return lastAction;
    }
}