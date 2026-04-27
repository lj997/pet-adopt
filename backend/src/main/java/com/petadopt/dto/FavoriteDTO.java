package com.petadopt.dto;

import com.petadopt.enums.PetGender;
import com.petadopt.enums.PetStatus;
import com.petadopt.enums.PetType;
import lombok.Data;

public class FavoriteDTO {

    @Data
    public static class FavoriteResponse {
        private Long id;
        private Long petId;
        private String petName;
        private PetType petType;
        private String breed;
        private Integer age;
        private PetGender gender;
        private String city;
        private String thumbnail;
        private PetStatus petStatus;
        private Long rescuerId;
        private String rescuerNickname;
        private String createTime;
    }
}
