package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.dto.PetDTO;
import com.petadopt.enums.PetStatus;
import com.petadopt.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public Result<PetDTO.PetResponse> createPet(@Valid @RequestBody PetDTO.CreateRequest request) {
        PetDTO.PetResponse pet = petService.createPet(request);
        return Result.success("发布成功", pet);
    }

    @PutMapping("/{id}")
    public Result<PetDTO.PetResponse> updatePet(@PathVariable Long id, @RequestBody PetDTO.UpdateRequest request) {
        PetDTO.PetResponse pet = petService.updatePet(id, request);
        return Result.success("更新成功", pet);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updatePetStatus(@PathVariable Long id, @RequestParam PetStatus status) {
        petService.updatePetStatus(id, status);
        return Result.success("状态更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/list")
    public Result<List<PetDTO.PetListResponse>> getAllAvailablePets() {
        List<PetDTO.PetListResponse> pets = petService.getAllAvailablePets();
        return Result.success(pets);
    }

    @PostMapping("/search")
    public Result<List<PetDTO.PetListResponse>> searchPets(@RequestBody PetDTO.SearchRequest request) {
        List<PetDTO.PetListResponse> pets = petService.searchPets(request);
        return Result.success(pets);
    }

    @GetMapping("/my")
    public Result<List<PetDTO.PetListResponse>> getMyPets() {
        List<PetDTO.PetListResponse> pets = petService.getMyPets();
        return Result.success(pets);
    }

    @GetMapping("/{id}")
    public Result<PetDTO.PetResponse> getPetById(@PathVariable Long id) {
        PetDTO.PetResponse pet = petService.getPetById(id);
        return Result.success(pet);
    }
}
