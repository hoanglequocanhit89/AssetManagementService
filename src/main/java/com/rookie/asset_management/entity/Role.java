package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    private Integer id;
    @JsonIgnore // to prevent circular reference
    @OneToMany(mappedBy = "role")
    private List<User> users;
}
