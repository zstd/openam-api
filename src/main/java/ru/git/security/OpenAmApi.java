package ru.git.security;

import java.util.Map;
import java.util.Set;

/**
 * Общий интерфейс API к OpenAm
 *
 * @author: zaycev_ab
 */
public interface OpenAmApi {

    public Set<String> getAllUsers();
    public Set<String> getAllGroups();
    public boolean checkUserExists(String userName);
    public boolean checkGroupExists(String groupName);
    public Map<String,Set<String>> getAllGroupsWithUsers(boolean includeEmptyGroups);
    public Map<String,Set<String>> getAllUsersWithGroups(boolean includeUsersWithoutGroups);

    public boolean isSupported();

    public String getAuthToken() throws OpenAmApiException;

    public Set<String> getAllUsers(String adminToken);
    public Set<String> getAllGroups(String adminToken);
    public boolean checkUserExists(String adminToken,String userName);
    public boolean checkGroupExists(String adminToken,String groupName);
    public Map<String,Set<String>> getAllGroupsWithUsers(String adminToken,boolean includeEmptyGroups);
    public Map<String,Set<String>> getAllUsersWithGroups(String adminToken,boolean includeUsersWithoutGroups);

}
