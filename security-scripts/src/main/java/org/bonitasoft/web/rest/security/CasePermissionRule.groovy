/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.bonitasoft.web.rest.security

import org.bonitasoft.engine.api.*
import org.bonitasoft.engine.api.permission.APICallContext
import org.bonitasoft.engine.api.permission.PermissionRule
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.identity.UserSearchDescriptor
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.engine.search.SearchResult
import org.bonitasoft.engine.session.APISession
import org.json.JSONObject

/**
 * @author Baptiste Mesta
 */
class CasePermissionRule implements PermissionRule {


    @Override
    boolean check(APISession apiSession, APICallContext apiCallContext, APIAccessor apiAccessor, Logger logger) {
        long currentUserId = apiSession.getUserId();
        if (apiCallContext.getMethod().equals("GET")) {
            return checkGetMethod(apiCallContext, apiAccessor, currentUserId, logger)
        } else if (apiCallContext.getMethod().equals("POST")) {
            return checkPostMethod(apiCallContext, apiAccessor, currentUserId, logger)
        }
    }

    private boolean checkPostMethod(APICallContext apiCallContext, APIAccessor apiAccessor, long currentUserId, Logger logger) {
        def body = apiCallContext.getBody()
        def processDefinitionId = body.optLong("processDefinitionId")
        if (processDefinitionId <= 0) {
            return false;
        }
        def processAPI = apiAccessor.getProcessAPI()
        def identityAPI = apiAccessor.getIdentityAPI()
        User user = identityAPI.getUser(currentUserId);
        SearchOptionsBuilder searchOptionBuilder = new SearchOptionsBuilder(0, 10);
        searchOptionBuilder.filter(UserSearchDescriptor.USER_NAME, user.getUserName());
        SearchResult<User> listUsers = processAPI.searchUsersWhoCanStartProcessDefinition(processDefinitionId, searchOptionBuilder.done());
        logger.debug("RuleCase : nb Result [" + listUsers.getCount() + "] ?");
        def canStart = listUsers.getCount() == 1
        logger.debug("RuleCase : User allowed to start? " + canStart)
        return canStart
    }

    private boolean checkGetMethod(APICallContext apiCallContext, APIAccessor apiAccessor, long currentUserId, Logger logger) {
        def processAPI = apiAccessor.getProcessAPI()
        def filters = apiCallContext.getFilters()
        if (apiCallContext.getResourceId() != null) {
            def processInstanceId = Long.valueOf(apiCallContext.getResourceId())
            def isInvolved = processAPI.isInvolvedInProcessInstance(currentUserId, processInstanceId)
            logger.debug("RuleCase : allowed because get on process that user is involved in")
            return isInvolved
        } else {
            def stringUserId = String.valueOf(currentUserId)
            if (stringUserId.equals(filters.get("started_by")) || stringUserId.equals(filters.get("user_id")) || stringUserId.equals(filters.get("supervisor_id"))) {
                logger.debug("RuleCase : allowed because searching filters contains user id")
                return true
            }
        }
        return false;
    }
}
