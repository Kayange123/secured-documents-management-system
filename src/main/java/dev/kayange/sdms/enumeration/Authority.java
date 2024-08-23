package dev.kayange.sdms.enumeration;

import dev.kayange.sdms.constants.AuthorityConstants;

public enum Authority {
    USER(AuthorityConstants.USER_AUTHORITIES),
    ADMIN(AuthorityConstants.ADMIN_AUTHORITIES),
    SUPER_ADMIN(AuthorityConstants.SUPER_ADMIN_AUTHORITIES),
    MANAGER(AuthorityConstants.MANAGER_AUTHORITIES),
    ;

    private final String value;


    Authority(String value) {
        this.value = value;
    }

    public String getValue(){ return this.value; }
}
