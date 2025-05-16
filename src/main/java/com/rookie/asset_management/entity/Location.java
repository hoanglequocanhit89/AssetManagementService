package com.rookie.asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location {
    @Id
    private Integer id;
    @Column(unique = true, nullable = false, length = 64)
    private String name;
}
