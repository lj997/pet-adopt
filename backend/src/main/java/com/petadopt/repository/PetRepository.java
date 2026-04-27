package com.petadopt.repository;

import com.petadopt.entity.Pet;
import com.petadopt.enums.PetStatus;
import com.petadopt.enums.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    List<Pet> findByRescuerIdOrderByCreateTimeDesc(Long rescuerId);

    List<Pet> findByStatusOrderByCreateTimeDesc(PetStatus status);

    List<Pet> findByTypeAndStatusOrderByCreateTimeDesc(PetType type, PetStatus status);

    List<Pet> findByCityAndStatusOrderByCreateTimeDesc(String city, PetStatus status);

    List<Pet> findByTypeAndCityAndStatusOrderByCreateTimeDesc(PetType type, String city, PetStatus status);
}
