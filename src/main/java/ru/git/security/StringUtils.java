package ru.git.security;

/**
 * //TODO: write something
 *
 * @author: zaycev_ab
 */
public class StringUtils {

    public static final boolean isEmpty(String string) {
        if(string == null || string.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
