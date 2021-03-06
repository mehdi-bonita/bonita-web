/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.console.common.server.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.BooleanUtils;
import org.bonitasoft.console.common.server.auth.impl.standard.StandardAuthenticationManagerImpl;

/**
 * Utility class for Session Manager access (read in a properties file)
 *
 * @author Ruiheng Fan
 *
 */
public class AuthenticationManagerProperties {

    /**
     * Logout Hidden constant
     */
    public static final String LOGOUT_DISABLED = "logout.link.hidden";

    /**
     * Logout Visible constant
     */
    public static final String LOGOUT_ENABLED = "logout.link.visible";

    /**
     * Configuration of authentication manager implementation
     */
    protected static final String AUTHENTICATION_MANAGER = "auth.AuthenticationManager";

    /**
     * Configuration of OAuth service provider name
     */
    protected static final String OAUTH_SERVICE_PROVIDER = "OAuth.serviceProvider";

    /**
     * Configuration of OAuth consumer key
     */
    protected static final String OAUTH_CONSUMER_KEY = "OAuth.consumerKey";

    /**
     * Configuration of OAuth consumer secret
     */
    protected static final String OAUTH_CONSUMER_SECRET = "OAuth.consumerSecret";

    /**
     * Configuration of OAuth callback URL
     */
    protected static final String OAUTH_CALLBACK_URL = "OAuth.callbackURL";

    /**
     * Configuration of CAS Server URL
     */
    protected static final String CAS_SERVER_URL = "Cas.serverUrlPrefix";

    /**
     * Configuration of CAS Bonita Service URL
     */
    protected static final String CAS_BONITA_SERVICE_URL = "Cas.bonitaServiceURL";

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationManagerProperties.class.getName());


    /**
     * properties
     */
    private final Properties defaultProperties = new Properties();

    /**
     * Private constructor to prevent instantiation
     */
    protected AuthenticationManagerProperties(final File propertiesFile) {
        // Read properties file.
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propertiesFile);
            defaultProperties.load(inputStream);
        } catch (final IOException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                if (propertiesFile != null) {
                    LOGGER.log(Level.WARNING,
                            "default login config file " + propertiesFile.getName() + " is missing from the conf directory (" + propertiesFile.getPath() + ")");
                }
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING, "default login config file " + propertiesFile.getName() + " stream could not be closed.", e);
                    }
                }
            }
        }
    }

    /**
     * @return get login manager implementation
     */
    public String getAuthenticationManagerImpl() {
        final String authenticationManagerImpl = defaultProperties.getProperty(AUTHENTICATION_MANAGER);
        if (authenticationManagerImpl == null || authenticationManagerImpl.isEmpty()) {
            final String defaultImpl = StandardAuthenticationManagerImpl.class.getName();
            LOGGER.log(Level.FINEST, "The login manager implementation is undefined. Using default implementation : " + defaultImpl);
            return defaultImpl;
        }
        return authenticationManagerImpl;
    }

    /**
     * @return get OAuth service provider name
     */
    public String getOAuthServiceProviderName() {
        return defaultProperties.getProperty(OAUTH_SERVICE_PROVIDER);
    }

    /**
     * @return get OAuth consumer key
     */
    public String getOAuthConsumerKey() {
        return defaultProperties.getProperty(OAUTH_CONSUMER_KEY);
    }

    /**
     * @return get OAuth consumer secret
     */
    public String getOAuthConsumerSecret() {
        return defaultProperties.getProperty(OAUTH_CONSUMER_SECRET);
    }

    /**
     * @return get OAuth callback URL
     */
    public String getOAuthCallbackURL() {
        return defaultProperties.getProperty(OAUTH_CALLBACK_URL);
    }

    /**
     * @return get OAuth callback URL
     */
    public String getCasServerURL() {
        return defaultProperties.getProperty(CAS_SERVER_URL);
    }

    /**
     * @return get OAuth callback URL
     */
    public String getCasBonitaServiceUrl() {
        return defaultProperties.getProperty(CAS_BONITA_SERVICE_URL);
    }

    /**
     * @return if properties are set up to display the logout button
     */
    public boolean isLogoutDisabled() {
        return BooleanUtils.toBoolean(defaultProperties.getProperty(LOGOUT_DISABLED));
    }
}
