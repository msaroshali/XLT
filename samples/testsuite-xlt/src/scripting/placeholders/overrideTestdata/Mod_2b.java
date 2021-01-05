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
package scripting.placeholders.overrideTestdata;

import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverModule;
import scripting.placeholders.overrideTestdata.Mod_2c;

/**
 * Use test data and define them.
 */
public class Mod_2b extends AbstractWebDriverModule
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doCommands(final String...parameters) throws Exception
    {
        final Mod_2c _mod_2c = new Mod_2c();
        _mod_2c.execute();

        assertText("id=specialchar_1", resolve("${gtd1}"));
        type("id=in_txt_1", resolve("${t1} - 2"));

    }
}