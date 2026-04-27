package com.petadopt.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "pet_id"})
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
