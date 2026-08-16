package cpt.api.account_management_service.constants;

public class RegexExpressions {
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$";
    public static final String USERNAME_REGEX = "^.{5,64}$";
    public static final String EMAIL_REGEX = "^[^@]+@[^@]+\\.[^@]+$";
    public static final String URL_SAFE_CHAR_REGEX = "^[a-zA-Z0-9_\\-.~]*$";
}