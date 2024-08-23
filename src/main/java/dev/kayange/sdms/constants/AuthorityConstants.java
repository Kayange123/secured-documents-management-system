package dev.kayange.sdms.constants;

public class AuthorityConstants {
    public static final String ROLE_PREFIX = "ROLE_";
    public static final int NINETY_DAYS = 90;
    public static final String ROLE = "role";
    public static final String EMPTY_VALUE = "empty";
    public static final String AUTHORITIES = "authorities";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "document:create,document:read,document:update,document:delete,user:create,user:update,user:read";
    public static final String SUPER_ADMIN_AUTHORITIES = "document:create,document:read,document:update,document:delete,user:create,user:update,user:read,user:delete";
    public static final String MANAGER_AUTHORITIES = "document:create,document:read,document:update,document:delete";

    public static final String KAYANGE_STORE = "KAYANGE_STORE";
    public static final int PASSWORD_ENCODING_STRENGTH = 12;
}
