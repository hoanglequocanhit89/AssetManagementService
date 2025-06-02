package com.rookie.asset_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
  @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
  private List<User> users;

  @JsonIgnore // to prevent circular reference
  @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
  private List<Asset> assets;
}
