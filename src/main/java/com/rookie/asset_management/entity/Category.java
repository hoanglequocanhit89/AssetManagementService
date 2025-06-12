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
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 64, unique = true)
  private String name;

  @Column(length = 2, unique = true, columnDefinition = "CHAR(2)")
  private String prefix;

  @JsonIgnore // to prevent circular reference in JSON serialization
  @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
  @Filter(name = "activeAssets", condition = "disabled = :isDisabled")
  List<Asset> assets;
}
