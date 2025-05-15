package com.rookie.asset_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
