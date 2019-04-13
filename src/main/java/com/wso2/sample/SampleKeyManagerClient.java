package com.wso2.sample;
/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses. For specific
 * language governing the permissions and limitations under this license,
 * please see the license as well as any agreement you’ve entered into with
 * WSO2 governing the purchase of this software and any associated services.
 */

import com.wso2.finance.open.banking.sca.keymanager.SCABasedKeyManagerClient;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.OAuthApplicationInfo;
import org.wso2.carbon.identity.application.common.model.xsd.AuthenticationStep;
import org.wso2.carbon.identity.application.common.model.xsd.IdentityProvider;
import org.wso2.carbon.identity.application.common.model.xsd.LocalAndOutboundAuthenticationConfig;
import org.wso2.carbon.identity.application.common.model.xsd.LocalAuthenticatorConfig;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceIdentityApplicationManagementException;
import org.wso2.carbon.identity.application.mgt.stub.IdentityApplicationManagementServiceStub;

import java.rmi.RemoteException;

/**
 * Sample class to set authenticators in KeyManagerClient
 **/
public class SampleKeyManagerClient extends SCABasedKeyManagerClient {

    private static final String BASIC_AUTHENTICATOR_NAME = "FOOBasicCustomAuth";
    private static final String BASIC_AUTHENTICATOR_DISPLAY_NAME = "FOO Authenticator";
    private static final String FEDERATED_AUTHENTICATOR_NAME = "BARFedCustomAuth";

    @Override
    public void setAuthenticators(LocalAndOutboundAuthenticationConfig localAndOutboundAuthenticationConfig,
                                  OAuthApplicationInfo oAuthApplicationInfo)
            throws RemoteException, IdentityApplicationManagementServiceIdentityApplicationManagementException,
            APIManagementException {
        AuthenticationStep[] authenticationSteps = new AuthenticationStep[2];

        //Step 1 - Basic authentication
        LocalAuthenticatorConfig localAuthenticatorConfig = new LocalAuthenticatorConfig();
        LocalAuthenticatorConfig[] localAuthenticatorConfigs = new LocalAuthenticatorConfig[1];
        AuthenticationStep basicAuthenticationStep = new AuthenticationStep();

        localAuthenticatorConfig.setName(BASIC_AUTHENTICATOR_NAME);
        localAuthenticatorConfig.setDisplayName(BASIC_AUTHENTICATOR_DISPLAY_NAME);
        localAuthenticatorConfig.setEnabled(true);
        localAuthenticatorConfigs[0] = localAuthenticatorConfig;

        basicAuthenticationStep.setStepOrder(1);
        basicAuthenticationStep.setLocalAuthenticatorConfigs(localAuthenticatorConfigs);
        basicAuthenticationStep.setAttributeStep(true);
        basicAuthenticationStep.setSubjectStep(true);
        //set step 1
        authenticationSteps[0] = basicAuthenticationStep;

        //Step 2 - Federated authentication
        IdentityProvider identityProvider = null;

        IdentityApplicationManagementServiceStub stub = super.getIdentityApplicationManagementServiceStub();

        if (stub != null) {
            IdentityProvider[] federatedIdPs = stub.getAllIdentityProviders();
            if (federatedIdPs != null && federatedIdPs.length > 0) {
                for (IdentityProvider registeredIdentityProvider : federatedIdPs) {
                    if (registeredIdentityProvider.getIdentityProviderName().equals(FEDERATED_AUTHENTICATOR_NAME)) {
                        identityProvider = registeredIdentityProvider;
                        break;
                    }
                }
            }
        } else {
            throw new APIManagementException("Retrieving IdentityApplicationManagementServiceStub failed.");
        }

        IdentityProvider[] identityProviders = new IdentityProvider[1];
        identityProviders[0] = identityProvider;

        AuthenticationStep authenticationStep = new AuthenticationStep();
        authenticationStep.setStepOrder(2);
        authenticationStep.setFederatedIdentityProviders(identityProviders);

        //set step 2
        authenticationSteps[1] = authenticationStep;

        //set authentication steps
        localAndOutboundAuthenticationConfig.setAuthenticationSteps(authenticationSteps);
    }
}
