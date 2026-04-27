package com.petadopt.entity;

import com.petadopt.enums.AdoptionStatus;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "adoption_requests")
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopter_id", nullable = false)
    private User adopter;

    @Column(columnDefinition = "TEXT")
    private String personalIntro;

    @Column(columnDefinition = "TEXT")
    private String petExperience;

    @Column(columnDefinition = "TEXT")
    private String housingCondition;

    @Column(nullable = false)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdoptionStatus status;

    @Column(columnDefinition = "TEXT")
    private String reviewNote;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime reviewTime;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = AdoptionStatus.PENDING;
        }
        createTime = LocalDateTime.now();
    }
}
