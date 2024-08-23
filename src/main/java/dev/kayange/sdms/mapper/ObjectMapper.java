package dev.kayange.sdms.mapper;

import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.entity.CredentialEntity;
import dev.kayange.sdms.entity.RoleEntity;
import dev.kayange.sdms.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

import static dev.kayange.sdms.constants.AuthorityConstants.NINETY_DAYS;

public class ObjectMapper {
    public static User convertToUser(UserEntity userEntity, RoleEntity role, CredentialEntity credential){
        User user = User.builder().build();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(credential));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(role.getName());
        user.setAuthorities(role.getAuthorities().getValue());

        return user;
    }

    public static boolean isCredentialsNonExpired(CredentialEntity credential) {
        return credential.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(LocalDateTime.now());
    }
}
