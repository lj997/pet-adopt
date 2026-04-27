package com.petadopt.repository;

import com.petadopt.entity.AdoptionRequest;
import com.petadopt.enums.AdoptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    List<AdoptionRequest> findByAdopterIdOrderByCreateTimeDesc(Long adopterId);

    List<AdoptionRequest> findByPetRescuerIdOrderByCreateTimeDesc(Long rescuerId);

    List<AdoptionRequest> findByPetRescuerIdAndStatusOrderByCreateTimeDesc(Long rescuerId, AdoptionStatus status);

    Optional<AdoptionRequest> findByIdAndPetRescuerId(Long id, Long rescuerId);

    boolean existsByPetIdAndAdopterId(Long petId, Long adopterId);
}
