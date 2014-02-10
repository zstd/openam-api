package ru.git.security.impl;

import ru.git.security.StringUtils;

/**
 * //TODO: write something
 *
 * @author: zaycev_ab
 */
public class ApiBase {

    protected String openAmUrl;
    protected String openAmUser;
    protected String openAmPassword;

    public ApiBase(String openAmUrl, String openAmUser, String openAmPassword) {
        if(StringUtils.isEmpty(openAmUrl)) {
            throw new NullPointerException("openAmUrl is null or empty");
        }
        if(StringUtils.isEmpty(openAmUser)) {
            throw new NullPointerException("openAmUser is null or empty");
        }
        if(StringUtils.isEmpty(openAmPassword)) {
            throw new NullPointerException("openAmPassword is null or empty");
        }
        this.openAmUrl = openAmUrl;
        this.openAmUser = openAmUser;
        this.openAmPassword = openAmPassword;
    }
}
