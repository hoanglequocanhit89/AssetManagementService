package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable = false, length = 64)
  private String name;

  @JsonIgnore // to prevent circular reference
  @OneToMany(mappedBy = "location")
  private List<User> users;

  @JsonIgnore // to prevent circular reference
  @OneToMany(mappedBy = "location")
  private List<Asset> assets;
}
