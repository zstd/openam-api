package ru.git.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.git.security.OpenAmApi;
import ru.git.security.OpenAmApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Старое API к OpenAm на основе http запросов.
 * Считается устаревшим (deprecated).
 * На данный момент поддерживается в Openam 9.*.*,10.*.*,11.0.0
 *
 * @author: zaycev_ab
 */
public class OpenAmHttpApi extends ApiBase implements OpenAmApi {

    private static Logger LOG = LoggerFactory.getLogger(ApiBase.class);

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String AUTH_TOKEN_PATTERN_STRING = "token.id=(.*)";
    private static final Pattern AUTH_TOKEN_PATTERN = Pattern.compile(AUTH_TOKEN_PATTERN_STRING);
    // cn=role.url.all,ou=groups,dc=opensso,dc=java,dc=net --> role.url.all
    private static final Pattern GROUP_PATTERN = Pattern.compile("^cn=(.*?)[,]{1}.*");
    private static final Pattern USER_PATTERN = Pattern.compile("^uid=(.*?)[,]{1}.*");

    public OpenAmHttpApi(String openAmUrl, String openAmUser, String openAmPassword) {
        super(openAmUrl, openAmUser, openAmPassword);
    }

    @Override
    public Set<String> getAllUsers() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<String> getAllGroups() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean checkUserExists(String userName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean checkGroupExists(String groupName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Set<String>> getAllGroupsWithUsers(boolean includeEmptyGroups) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Set<String>> getAllUsersWithGroups(boolean includeUsersWithoutGroups) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isSupported() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getAuthToken() throws OpenAmApiException {
        String authUrl = null;
        try {
            authUrl = buildAuthUrl(openAmUser,openAmPassword,"ldapService");
        } catch (UnsupportedEncodingException e) {
            throw new OpenAmApiException("Error at getAuthToken -> buildAuthUrl ",e);
        }
        String openAmAuthUrl = openAmUrl + "/identity/authenticate?" + authUrl;
        HttpResult httpResult = sendGet(openAmAuthUrl);
        if(httpResult.success) {
            return extractAuthToken(httpResult.data);
        } else {
            throw new OpenAmApiException(httpResult.message);
        }
    }



    @Override
    public Set<String> getAllUsers(String adminToken) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<String> getAllGroups(String adminToken) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean checkUserExists(String adminToken, String userName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean checkGroupExists(String adminToken, String groupName) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Set<String>> getAllGroupsWithUsers(String adminToken, boolean includeEmptyGroups) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, Set<String>> getAllUsersWithGroups(String adminToken, boolean includeUsersWithoutGroups) {
        throw new RuntimeException("Not implemented");
    }

    private static String extractAuthToken(String tokenData[]) throws OpenAmApiException {
        LOG.trace("extractAuthToken {}",Arrays.toString(tokenData));
        if(tokenData == null || tokenData.length == 0) {
            throw new OpenAmApiException("Error at extractAuthToken: income data array is empty");
        }
        if(tokenData.length > 1) {
            LOG.warn("Tokens array supposed to have length == 1...");
        }
        String tokenString = tokenData[0];
        Matcher matcher = AUTH_TOKEN_PATTERN.matcher(tokenString);
        if(matcher.matches()) {
            return matcher.group(1);
        } else {
            LOG.error("Error at extractAuthToken: token string {} doesn't match the token pattern {}",
                    tokenString,AUTH_TOKEN_PATTERN_STRING);
            throw new OpenAmApiException("Token string  doesn't match the pattern");
        }
    }

    private static String buildAuthUrl(String userName, String password, String service)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder("username="+userName + "&password=" + password);
        Set<String> additionalAuthParams = new HashSet<String>(4);
        if(service != null && !service.isEmpty()) {
            additionalAuthParams.add(URLEncoder.encode("service=" + service, "UTF-8"));
        }
        if(!additionalAuthParams.isEmpty()) {
            String additionalParams = "";
            Iterator<String> iterator = additionalAuthParams.iterator();
            while (iterator.hasNext()) {
                additionalParams += iterator.next();
                if(iterator.hasNext()) {
                    additionalParams += URLEncoder.encode("&","UTF-8");
                }
            }
            result.append("&uri=").append(additionalParams);
        }
        return result.toString();
    }

    private HttpResult sendGet(String url) {
        LOG.debug("sendGet " + url);
        long start = System.currentTimeMillis();
        HttpResult result = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            String[] dataArray = getResponseStrings(con);
            result = createHttpResponse(responseCode,dataArray);
        } catch (IOException ioe) {
           LOG.error("IOException at sendGet {}",ioe.getMessage(),ioe);
           result = new HttpResult(ioe.getMessage());
        }
        LOG.info("sendGet taken " + (System.currentTimeMillis() - start) + " ms to proceed");
        return result;
    }

    private HttpResult createHttpResponse(int responseCode, String dataArray[]) {
        LOG.trace("createHttpResponse, code {}");
        if(responseCode == 200) {
            return new HttpResult(dataArray);
        } else {
            LOG.error("Url returned error code {} " + responseCode);
            StringBuilder builder = new StringBuilder("Reques returned error code").append(responseCode).append("\n");
            for (int i = 0; i < dataArray.length; i++) {
                builder.append(dataArray[i]).append("\n");
            }
            return new HttpResult(builder.toString());
        }
    }

    private String[] getResponseStrings(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> result = new LinkedList<String>();
        while ((inputLine = in.readLine()) != null) {
            result.add(inputLine);
        }
        in.close();
        return result.toArray(new String[result.size()]);
    }

    private class HttpResult {
        boolean success;
        String message = "no message";
        String[] data = new String[0];

        public HttpResult(String message) {
            this.success = false;
            this.message = message;
        }

        public HttpResult(String data[]) {
            this.success = true;
            this.data = data;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(HttpResult.class.getSimpleName()).append(" ");
            stringBuilder.append(success ? " succeed " : " failed ");
            stringBuilder.append(success ? "\n with data " : " with error message ");
            stringBuilder.append(success ? Arrays.toString(data) : message);
            return stringBuilder.toString();
        }
    }
}
