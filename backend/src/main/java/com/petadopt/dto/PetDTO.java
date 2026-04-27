package com.petadopt.dto;

import com.petadopt.enums.PetGender;
import com.petadopt.enums.PetStatus;
import com.petadopt.enums.PetType;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class PetDTO {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "宠物名称不能为空")
        private String name;

        @NotNull(message = "宠物类型不能为空")
        private PetType type;

        private String breed;

        private Integer age;

        private PetGender gender;

        private String healthCondition;

        private String personality;

        private Boolean sterilized;

        private Boolean vaccinated;

        @NotBlank(message = "所在城市不能为空")
        private String city;

        private List<String> photos;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String breed;
        private Integer age;
        private PetGender gender;
        private String healthCondition;
        private String personality;
        private Boolean sterilized;
        private Boolean vaccinated;
        private String city;
        private List<String> photos;
    }

    @Data
    public static class PetResponse {
        private Long id;
        private String name;
        private PetType type;
        private String breed;
        private Integer age;
        private PetGender gender;
        private String healthCondition;
        private String personality;
        private Boolean sterilized;
        private Boolean vaccinated;
        private String city;
        private List<String> photos;
        private PetStatus status;
        private Long rescuerId;
        private String rescuerNickname;
        private String rescuerPhone;
        private String createTime;
    }

    @Data
    public static class PetListResponse {
        private Long id;
        private String name;
        private PetType type;
        private String breed;
        private Integer age;
        private PetGender gender;
        private String city;
        private String thumbnail;
        private PetStatus status;
        private String createTime;
    }

    @Data
    public static class SearchRequest {
        private PetType type;
        private String city;
        private Integer minAge;
        private Integer maxAge;
        private PetStatus status;
        private String keyword;
    }
}
