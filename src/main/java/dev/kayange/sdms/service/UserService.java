package dev.kayange.sdms.service;

import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.entity.CredentialEntity;
import dev.kayange.sdms.entity.RoleEntity;
import dev.kayange.sdms.enumeration.LoginType;

public interface UserService {
    void createNewUser(String firstName, String lastName, String email, String password);
    RoleEntity getRoleName(String name);
    void verifyAccount(String token);
    void updateLoginAttempt(String email, LoginType loginType);
    User getUserByUserId(String userId);
    User getUserByEmail(String email);
    CredentialEntity getCredentialsByUserId(Long id);
}
