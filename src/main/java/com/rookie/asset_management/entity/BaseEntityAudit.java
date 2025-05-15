package com.rookie.asset_management.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * BaseEntityAudit is an abstract class that extends BaseEntity and implements auditing features.
 * It contains fields for tracking the user who created and last modified the entity.
 * This class is used as a base class for all entities which need to have auditing information.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityAudit extends BaseEntity {

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
