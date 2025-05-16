package com.rookie.asset_management.entity;

import com.rookie.asset_management.enums.ReturningRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "returning_requests")
@Getter
@Setter
public class ReturningRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "assigment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne
    @JoinColumn(name = "accepted_by", nullable = false)
    private User acceptedBy;

    @Column(name = "requested_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate returnedDate;

    @Enumerated(EnumType.STRING)
    private ReturningRequestStatus status;
}
