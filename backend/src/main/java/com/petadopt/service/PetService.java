package com.petadopt.service;

import com.petadopt.dto.PetDTO;
import com.petadopt.entity.Pet;
import com.petadopt.entity.User;
import com.petadopt.enums.PetStatus;
import com.petadopt.enums.PetType;
import com.petadopt.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public PetDTO.PetResponse createPet(PetDTO.CreateRequest request) {
        User rescuer = userService.getCurrentUser();

        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        pet.setHealthCondition(request.getHealthCondition());
        pet.setPersonality(request.getPersonality());
        pet.setSterilized(request.getSterilized() != null ? request.getSterilized() : false);
        pet.setVaccinated(request.getVaccinated() != null ? request.getVaccinated() : false);
        pet.setCity(request.getCity());
        pet.setPhotos(request.getPhotos() != null ? request.getPhotos() : new ArrayList<>());
        pet.setRescuer(rescuer);
        pet.setStatus(PetStatus.AVAILABLE);

        pet = petRepository.save(pet);
        return convertToResponse(pet);
    }

    @Transactional
    public PetDTO.PetResponse updatePet(Long id, PetDTO.UpdateRequest request) {
        User rescuer = userService.getCurrentUser();
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (!pet.getRescuer().getId().equals(rescuer.getId())) {
            throw new RuntimeException("您没有权限修改此宠物信息");
        }

        if (request.getName() != null) {
            pet.setName(request.getName());
        }
        if (request.getBreed() != null) {
            pet.setBreed(request.getBreed());
        }
        if (request.getAge() != null) {
            pet.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            pet.setGender(request.getGender());
        }
        if (request.getHealthCondition() != null) {
            pet.setHealthCondition(request.getHealthCondition());
        }
        if (request.getPersonality() != null) {
            pet.setPersonality(request.getPersonality());
        }
        if (request.getSterilized() != null) {
            pet.setSterilized(request.getSterilized());
        }
        if (request.getVaccinated() != null) {
            pet.setVaccinated(request.getVaccinated());
        }
        if (request.getCity() != null) {
            pet.setCity(request.getCity());
        }
        if (request.getPhotos() != null) {
            pet.setPhotos(request.getPhotos());
        }

        pet = petRepository.save(pet);
        return convertToResponse(pet);
    }

    @Transactional
    public void updatePetStatus(Long id, PetStatus status) {
        User rescuer = userService.getCurrentUser();
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (!pet.getRescuer().getId().equals(rescuer.getId())) {
            throw new RuntimeException("您没有权限修改此宠物状态");
        }

        pet.setStatus(status);
        petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        User rescuer = userService.getCurrentUser();
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (!pet.getRescuer().getId().equals(rescuer.getId())) {
            throw new RuntimeException("您没有权限删除此宠物");
        }

        petRepository.delete(pet);
    }

    public PetDTO.PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));
        return convertToResponse(pet);
    }

    public List<PetDTO.PetListResponse> getAllAvailablePets() {
        List<Pet> pets = petRepository.findByStatusOrderByCreateTimeDesc(PetStatus.AVAILABLE);
        return pets.stream().map(this::convertToListResponse).collect(Collectors.toList());
    }

    public List<PetDTO.PetListResponse> searchPets(PetDTO.SearchRequest request) {
        Specification<Pet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), PetStatus.AVAILABLE));

            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.equal(root.get("city"), request.getCity()));
            }
            if (request.getMinAge() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("age"), request.getMinAge()));
            }
            if (request.getMaxAge() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("age"), request.getMaxAge()));
            }
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), keyword),
                        cb.like(root.get("breed"), keyword)
                ));
            }

            query.orderBy(cb.desc(root.get("createTime")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Pet> pets = petRepository.findAll(spec);
        return pets.stream().map(this::convertToListResponse).collect(Collectors.toList());
    }

    public List<PetDTO.PetListResponse> getMyPets() {
        User rescuer = userService.getCurrentUser();
        List<Pet> pets = petRepository.findByRescuerIdOrderByCreateTimeDesc(rescuer.getId());
        return pets.stream().map(this::convertToListResponse).collect(Collectors.toList());
    }

    private PetDTO.PetResponse convertToResponse(Pet pet) {
        PetDTO.PetResponse response = new PetDTO.PetResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        response.setType(pet.getType());
        response.setBreed(pet.getBreed());
        response.setAge(pet.getAge());
        response.setGender(pet.getGender());
        response.setHealthCondition(pet.getHealthCondition());
        response.setPersonality(pet.getPersonality());
        response.setSterilized(pet.getSterilized());
        response.setVaccinated(pet.getVaccinated());
        response.setCity(pet.getCity());
        response.setPhotos(pet.getPhotos());
        response.setStatus(pet.getStatus());
        response.setRescuerId(pet.getRescuer().getId());
        response.setRescuerNickname(pet.getRescuer().getNickname());
        response.setRescuerPhone(pet.getRescuer().getPhone());
        response.setCreateTime(pet.getCreateTime() != null ? pet.getCreateTime().format(FORMATTER) : null);
        return response;
    }

    private PetDTO.PetListResponse convertToListResponse(Pet pet) {
        PetDTO.PetListResponse response = new PetDTO.PetListResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        response.setType(pet.getType());
        response.setBreed(pet.getBreed());
        response.setAge(pet.getAge());
        response.setGender(pet.getGender());
        response.setCity(pet.getCity());
        response.setStatus(pet.getStatus());
        if (pet.getPhotos() != null && !pet.getPhotos().isEmpty()) {
            response.setThumbnail(pet.getPhotos().get(0));
        }
        response.setCreateTime(pet.getCreateTime() != null ? pet.getCreateTime().format(FORMATTER) : null);
        return response;
    }
}
