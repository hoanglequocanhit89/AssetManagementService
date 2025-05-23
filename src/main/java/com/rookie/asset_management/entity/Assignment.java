package com.rookie.asset_management.entity;

import com.rookie.asset_management.enums.AssignmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "assignments")
@Getter
@Setter
public class Assignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "asset_id", nullable = false)
  private Asset asset;

  @ManyToOne
  @JoinColumn(name = "assigned_by", nullable = false)
  private User assignedBy;

  @ManyToOne
  @JoinColumn(name = "assigned_to", nullable = false)
  private User assignedTo;

  private String note;

  @Column(name = "assigned_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private LocalDate assignedDate;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "ASSIGNMENT_STATUS")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private AssignmentStatus status;

  @OneToOne(mappedBy = "assignment")
  private ReturningRequest returningRequest;
}
