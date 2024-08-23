package dev.kayange.sdms.service.implementation;

import dev.kayange.sdms.cache.CacheStore;
import dev.kayange.sdms.dao.ConfirmationRepository;
import dev.kayange.sdms.dao.CredentialRepository;
import dev.kayange.sdms.dao.RoleRepository;
import dev.kayange.sdms.dao.UserRepository;
import dev.kayange.sdms.domain.RequestContext;
import dev.kayange.sdms.dto.User;
import dev.kayange.sdms.entity.Confirmation;
import dev.kayange.sdms.entity.CredentialEntity;
import dev.kayange.sdms.entity.RoleEntity;
import dev.kayange.sdms.entity.UserEntity;
import dev.kayange.sdms.enumeration.Authority;
import dev.kayange.sdms.enumeration.EventType;
import dev.kayange.sdms.enumeration.LoginType;
import dev.kayange.sdms.event.UserEvent;
import dev.kayange.sdms.exception.ApiException;
import dev.kayange.sdms.mapper.ObjectMapper;
import dev.kayange.sdms.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationRepository confirmationRepository;
    private final CredentialRepository credentialRepository;
    private final ApplicationEventPublisher publisher;
    private final CacheStore<String, Integer> userCacheStore;

    @Value("${application.mailing.front-end.url}")
    private String confirmationUrl;

    @Override
    public void createNewUser(String firstName, String lastName, String email, String password) {
       var user = createUser(firstName, lastName, email);
        UserEntity userEntity = userRepository.save(user);
        var credentialEntity = new CredentialEntity( password, userEntity);
        var confirmationEntity = new Confirmation(userEntity);
        credentialRepository.save(credentialEntity);
        Confirmation confirmation = confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("subject", "Confirm your Email", "confirmationUrl", confirmationUrl, "activationCode", confirmation.getKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseThrow(()->new ApiException("Role Not Found"));
    }

    @Override
    public void verifyAccount(String token) {
        Confirmation validToken = confirmationRepository.findByKey(token).orElseThrow(() -> new ApiException("Token is Invalid"));
        var user = getAUserByEmail(validToken.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
        confirmationRepository.delete(validToken);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var user = getAUserByEmail(email);
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if(userCacheStore.get(user.getEmail())== null){
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userCacheStore.put(user.getEmail(), user.getLoginAttempts());
                if(userCacheStore.get(user.getEmail()) > 5){
                    user.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setAccountNonLocked(true);
                user.setLoginAttempts(0);
                user.setLastLogin(LocalDateTime.now());
                userCacheStore.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }

    @Override
    public User getUserByUserId(String userId) {
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(()-> new ApiException("User not found"));

        return User.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .accountNonExpired(userEntity.isAccountNonExpired())
                .accountNonLocked(userEntity.isAccountNonLocked())
                .authorities(userEntity.getRole().getAuthorities().getValue())
                .enabled(userEntity.isEnabled())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastLogin(userEntity.getLastLogin().toString())
                .qrCodeImageUri(userEntity.getQrCodeImageUri())
                .createdBy(userEntity.getCreatedBy())
                .updatedBy(userEntity.getUpdatedBy())
                .bio(userEntity.getBio())
                .loginAttempts(userEntity.getLoginAttempts())
                .imageUrl(userEntity.getImageUrl())
                .mfa(userEntity.isMfa())
                .createdAt(userEntity.getCreatedAt().toString())
                .phone(userEntity.getPhone())
                .role(userEntity.getRole().getName())
                .credentialsNonExpired(ObjectMapper.isCredentialsNonExpired(getCredentialsByUserId(userEntity.getId())))
                .build();
    }

    @Override
    public User getUserByEmail(String email) {
        var user = userRepository.findByEmailIgnoreCase(email).orElseThrow(()-> new ApiException("Could not find user with thus Email"));
        return ObjectMapper.convertToUser(user, user.getRole(),  getCredentialsByUserId(user.getId()));
    }

    @Override
    public CredentialEntity getCredentialsByUserId(Long id) {
        return credentialRepository.getCredentialByUserId(id).orElseThrow(()-> new ApiException("Unable to find user credentials"));
    }

    private UserEntity createUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }

    private UserEntity getAUserByEmail (String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException("No User Found with This Token"));
    }

    private UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity role) {
        return UserEntity.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .userId(UUID.randomUUID().toString())
                .enabled(false)
                .mfa(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .lastLogin(LocalDateTime.now())
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .phone(EMPTY)
                .bio(EMPTY)
                .role(role)
                .build();
    }

}
