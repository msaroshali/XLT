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
package com.xceptance.xlt.engine.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketImplFactory;

import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.common.XltConstants;

/**
 * The XLT socket monitoring facility.
 * <p>
 * Note that this class replaces the global {@link SocketImplFactory}!
 * <p>
 * Limitations:
 * <ul>
 * <li>socket statistics are accessible only to that thread that actually used the socket</li>
 * <li>works best with request/response scenarios, otherwise certain values are meaningless</li>
 * </ul>
 */
public final class XltSockets
{
    private static final String PROP_COLLECT_NETWORK_DATA = XltConstants.XLT_PACKAGE_PATH + ".socket.collectNetworkData";

    static
    {
        if (XltProperties.getInstance().getProperty(PROP_COLLECT_NETWORK_DATA, true))
        {
            try
            {
                // set the global socket impl factory
                Socket.setSocketImplFactory(new InstrumentedSocketImplFactory());
            }
            catch (final IOException ex)
            {
                throw new RuntimeException("Failed to initialize XLT sockets", ex);
            }
        }
    }

    /**
     * Initializes the XltSockets sub system.
     */
    public static void initialize()
    {
        // nothing to do here yet, just to make sure the static initializer block is executed
    }

    /**
     * Private constructor to avoid object instantiation.
     */
    private XltSockets()
    {
    }
}
