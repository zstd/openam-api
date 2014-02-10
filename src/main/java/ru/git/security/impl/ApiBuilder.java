package ru.git.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.git.security.OpenAmApi;
import ru.git.security.OpenAmApiException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * //TODO: write something
 *
 * @author: zaycev_ab
 */
public class ApiBuilder extends ApiBase {

    private static Logger LOG = LoggerFactory.getLogger(ApiBase.class);

    public ApiBuilder(String openAmUrl, String openUser, String openPassword) {
        super(openAmUrl, openUser, openPassword);
    }

    public OpenAmApi build(Class<? extends OpenAmApi> targetClass,boolean checkTokenRetrieval) throws InstantiationException {
        try {
            URL url = new URL(openAmUrl);
        } catch (MalformedURLException e) {
            LOG.error("Erorr creating openAmUrl {}",e.getMessage(),e);
            throw new InstantiationException("OpenAmUrl " + openAmUrl +" is incorrect");
        }
        OpenAmApi result = null;
        if(targetClass == OpenAmHttpApi.class) {
            result = new OpenAmHttpApi(openAmUrl, openAmUser, openAmPassword);
        } else {
            LOG.error("Undefined target class {}",targetClass);
            throw new InstantiationException("Undefined target class " + targetClass);
        }

        if(checkTokenRetrieval) {
            String token = null;
            try {
                token = result.getAuthToken();
            } catch (OpenAmApiException e) {
                LOG.error("Exception at getAuthToken {}",e.getMessage(),e);
            }
            if(token == null) {
                throw new InstantiationException("Check token retrieval failed - token is null");
            }
        }
        return result;
    }
}
