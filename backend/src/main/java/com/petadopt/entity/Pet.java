package com.petadopt.entity;

import com.petadopt.enums.PetGender;
import com.petadopt.enums.PetStatus;
import com.petadopt.enums.PetType;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType type;

    private String breed;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private PetGender gender;

    @Column(columnDefinition = "TEXT")
    private String healthCondition;

    @Column(columnDefinition = "TEXT")
    private String personality;

    private Boolean sterilized;

    private Boolean vaccinated;

    private String city;

    @ElementCollection
    @CollectionTable(name = "pet_photos", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescuer_id", nullable = false)
    private User rescuer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus status;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = PetStatus.AVAILABLE;
        }
        if (sterilized == null) {
            sterilized = false;
        }
        if (vaccinated == null) {
            vaccinated = false;
        }
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
