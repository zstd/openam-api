package ru.git.security.impl;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.git.security.OpenAmApi;

/**
 * @see ru.git.security.impl.OpenAmHttpApi
 *
 * @author: zaycev_ab
 */
public class OpenAmHttpApiIT {

    private String openamUrl;
    private String openamUser;
    private String openamPass;

    @Test
    public void testGetAuthToken() throws Exception {
        OpenAmApi api = new ApiBuilder(openamUrl,openamUser,openamPass).
                                build(OpenAmHttpApi.class, true);
        String token = api.getAuthToken();
        Assert.assertNotNull(token,"Token must not be null");
    }


    @BeforeMethod
    @Parameters( {"openam-url","openam-user","openam-pass"} )
    public void initFramework(String openamUrl,String openamUser,String openamPass) {
        this.openamUrl = openamUrl;
        this.openamUser = openamUser;
        this.openamPass = openamPass;
    }
}
