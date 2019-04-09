package de.paluno.mse.palaver.generalhelper;

/**
 * Created by asus on 2017/6/18.
 */

public class CharacterHelper {
    public static boolean characterCompareInUpperLetters(char a, char b) {
        return (upperLetters(a) - upperLetters(b)) > 0;
    }

    public static char upperLetters(char c) {
        if (c >= 'a' && c <= 'z') {
            char a = (char) (c - 32);
            return a;
        } else return c;
    }

    public static boolean number(char a) {
        return a >= '0' && a <= '9';
    }
}
