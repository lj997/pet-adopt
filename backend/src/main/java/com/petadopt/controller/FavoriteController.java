package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.dto.FavoriteDTO;
import com.petadopt.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle/{petId}")
    public Result<Void> toggleFavorite(@PathVariable Long petId) {
        favoriteService.toggleFavorite(petId);
        return Result.success("操作成功", null);
    }

    @PostMapping("/add/{petId}")
    public Result<Void> addFavorite(@PathVariable Long petId) {
        favoriteService.addFavorite(petId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/remove/{petId}")
    public Result<Void> removeFavorite(@PathVariable Long petId) {
        favoriteService.removeFavorite(petId);
        return Result.success("取消收藏成功", null);
    }

    @GetMapping("/check/{petId}")
    public Result<Map<String, Boolean>> checkFavorite(@PathVariable Long petId) {
        boolean isFavorite = favoriteService.isFavorite(petId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<List<FavoriteDTO.FavoriteResponse>> getMyFavorites() {
        List<FavoriteDTO.FavoriteResponse> favorites = favoriteService.getMyFavorites();
        return Result.success(favorites);
    }
}
