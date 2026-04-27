package com.petadopt.dto;

import com.petadopt.enums.AdoptionStatus;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AdoptionRequestDTO {

    @Data
    public static class CreateRequest {
        @NotNull(message = "宠物ID不能为空")
        private Long petId;

        private String personalIntro;

        private String petExperience;

        private String housingCondition;

        @NotBlank(message = "联系方式不能为空")
        private String contactInfo;
    }

    @Data
    public static class ReviewRequest {
        @NotNull(message = "申请ID不能为空")
        private Long requestId;

        @NotNull(message = "审核状态不能为空")
        private AdoptionStatus status;

        private String reviewNote;
    }

    @Data
    public static class AdoptionResponse {
        private Long id;
        private Long petId;
        private String petName;
        private String petThumbnail;
        private Long adopterId;
        private String adopterNickname;
        private String personalIntro;
        private String petExperience;
        private String housingCondition;
        private String contactInfo;
        private AdoptionStatus status;
        private String reviewNote;
        private String createTime;
        private String reviewTime;
    }

    @Data
    public static class MyAdoptionResponse {
        private Long id;
        private Long petId;
        private String petName;
        private String petThumbnail;
        private Long rescuerId;
        private String rescuerNickname;
        private AdoptionStatus status;
        private String createTime;
    }
}
