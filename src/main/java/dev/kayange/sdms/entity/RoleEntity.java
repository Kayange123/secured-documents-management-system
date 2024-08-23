package dev.kayange.sdms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kayange.sdms.enumeration.Authority;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "roles")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString @JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RoleEntity extends Auditable {
    private String name;
    private Authority authorities;
}
