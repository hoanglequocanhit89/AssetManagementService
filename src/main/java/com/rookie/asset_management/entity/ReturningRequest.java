package com.rookie.asset_management.entity;

import com.rookie.asset_management.enums.ReturningRequestStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "returning_requests")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ReturningRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  @CreatedBy
  @ManyToOne
  @JoinColumn(name = "requested_by", nullable = false)
  private User requestedBy;

  @ManyToOne
  @JoinColumn(name = "accepted_by")
  private User acceptedBy;

  @Column(name = "returned_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private LocalDate returnedDate;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "RETURNING_STATUS")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private ReturningRequestStatus status;

  @Column(nullable = false)
  private boolean deleted = false;

  @OneToMany(mappedBy = "returningRequest", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Notification> notifications;
}
