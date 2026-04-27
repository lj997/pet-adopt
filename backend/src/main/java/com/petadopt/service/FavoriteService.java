package com.petadopt.service;

import com.petadopt.dto.FavoriteDTO;
import com.petadopt.entity.Favorite;
import com.petadopt.entity.Pet;
import com.petadopt.entity.User;
import com.petadopt.repository.FavoriteRepository;
import com.petadopt.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void toggleFavorite(Long petId) {
        User user = userService.getCurrentUser();

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (favoriteRepository.existsByUserIdAndPetId(user.getId(), petId)) {
            favoriteRepository.deleteByUserIdAndPetId(user.getId(), petId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setPet(pet);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void addFavorite(Long petId) {
        User user = userService.getCurrentUser();

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (favoriteRepository.existsByUserIdAndPetId(user.getId(), petId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setPet(pet);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long petId) {
        User user = userService.getCurrentUser();
        favoriteRepository.deleteByUserIdAndPetId(user.getId(), petId);
    }

    public boolean isFavorite(Long petId) {
        User user = userService.getCurrentUser();
        return favoriteRepository.existsByUserIdAndPetId(user.getId(), petId);
    }

    public List<FavoriteDTO.FavoriteResponse> getMyFavorites() {
        User user = userService.getCurrentUser();
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreateTimeDesc(user.getId());
        return favorites.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private FavoriteDTO.FavoriteResponse convertToResponse(Favorite favorite) {
        FavoriteDTO.FavoriteResponse response = new FavoriteDTO.FavoriteResponse();
        response.setId(favorite.getId());

        Pet pet = favorite.getPet();
        response.setPetId(pet.getId());
        response.setPetName(pet.getName());
        response.setPetType(pet.getType());
        response.setBreed(pet.getBreed());
        response.setAge(pet.getAge());
        response.setGender(pet.getGender());
        response.setCity(pet.getCity());
        if (pet.getPhotos() != null && !pet.getPhotos().isEmpty()) {
            response.setThumbnail(pet.getPhotos().get(0));
        }
        response.setPetStatus(pet.getStatus());
        response.setRescuerId(pet.getRescuer().getId());
        response.setRescuerNickname(pet.getRescuer().getNickname());
        response.setCreateTime(favorite.getCreateTime() != null ? favorite.getCreateTime().format(FORMATTER) : null);

        return response;
    }
}
