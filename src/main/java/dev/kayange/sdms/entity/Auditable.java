package dev.kayange.sdms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.kayange.sdms.domain.RequestContext;
import dev.kayange.sdms.exception.ApiException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

@Getter @Setter  @MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"} , allowGetters = true)
public abstract class Auditable {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_seq")
    @SequenceGenerator(name = "primary_key_seq", sequenceName = "primary_key_seq", allocationSize = 1)
    @Column(name = "id", updatable = false)
    private Long id;
    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();
    @CreatedBy
    @NotNull
    @Column(name = "created_by", updatable = false)
    private Long createdBy;
    @NotNull
    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private Long updatedBy;
    @NotNull
    @Column(name = "created_at",nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @NotNull
    @Column(name = "updated_at",nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforePersist(){
        var userId = RequestContext.getUserId();
        if(userId == null){
            throw new ApiException("Can not persist entity without a user id in Request context for this thread");
        }
        setCreatedAt(LocalDateTime.now());
        setCreatedBy(userId);
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }

    @PreUpdate
    public void beforeUpdate(){
        var userId = RequestContext.getUserId();
        if(userId == null){
            throw new ApiException("Can not update entity without a user id in Request context for this thread");
        }
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(userId);
    }
}
