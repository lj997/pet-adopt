package com.petadopt.service;

import com.petadopt.dto.AdoptionRequestDTO;
import com.petadopt.entity.AdoptionRequest;
import com.petadopt.entity.Pet;
import com.petadopt.entity.User;
import com.petadopt.enums.AdoptionStatus;
import com.petadopt.enums.PetStatus;
import com.petadopt.repository.AdoptionRequestRepository;
import com.petadopt.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionRequestService {

    @Autowired
    private AdoptionRequestRepository requestRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public AdoptionRequestDTO.AdoptionResponse createRequest(AdoptionRequestDTO.CreateRequest request) {
        User adopter = userService.getCurrentUser();

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new RuntimeException("宠物不存在"));

        if (!pet.getStatus().equals(PetStatus.AVAILABLE)) {
            throw new RuntimeException("该宠物暂不可领养");
        }

        if (requestRepository.existsByPetIdAndAdopterId(request.getPetId(), adopter.getId())) {
            throw new RuntimeException("您已提交过该宠物的领养申请");
        }

        AdoptionRequest adoptionRequest = new AdoptionRequest();
        adoptionRequest.setPet(pet);
        adoptionRequest.setAdopter(adopter);
        adoptionRequest.setPersonalIntro(request.getPersonalIntro());
        adoptionRequest.setPetExperience(request.getPetExperience());
        adoptionRequest.setHousingCondition(request.getHousingCondition());
        adoptionRequest.setContactInfo(request.getContactInfo());
        adoptionRequest.setStatus(AdoptionStatus.PENDING);

        adoptionRequest = requestRepository.save(adoptionRequest);
        return convertToResponse(adoptionRequest);
    }

    public List<AdoptionRequestDTO.MyAdoptionResponse> getMyRequests() {
        User adopter = userService.getCurrentUser();
        List<AdoptionRequest> requests = requestRepository.findByAdopterIdOrderByCreateTimeDesc(adopter.getId());
        return requests.stream().map(this::convertToMyResponse).collect(Collectors.toList());
    }

    public List<AdoptionRequestDTO.AdoptionResponse> getMyReceivedRequests() {
        User rescuer = userService.getCurrentUser();
        List<AdoptionRequest> requests = requestRepository.findByPetRescuerIdOrderByCreateTimeDesc(rescuer.getId());
        return requests.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<AdoptionRequestDTO.AdoptionResponse> getReceivedRequestsByStatus(AdoptionStatus status) {
        User rescuer = userService.getCurrentUser();
        List<AdoptionRequest> requests = requestRepository.findByPetRescuerIdAndStatusOrderByCreateTimeDesc(rescuer.getId(), status);
        return requests.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AdoptionRequestDTO.AdoptionResponse reviewRequest(AdoptionRequestDTO.ReviewRequest request) {
        User rescuer = userService.getCurrentUser();

        AdoptionRequest adoptionRequest = requestRepository.findByIdAndPetRescuerId(request.getRequestId(), rescuer.getId())
                .orElseThrow(() -> new RuntimeException("申请不存在或您没有权限审核"));

        if (!adoptionRequest.getStatus().equals(AdoptionStatus.PENDING)) {
            throw new RuntimeException("该申请已被审核");
        }

        if (!request.getStatus().equals(AdoptionStatus.APPROVED) && !request.getStatus().equals(AdoptionStatus.REJECTED)) {
            throw new RuntimeException("无效的审核状态");
        }

        adoptionRequest.setStatus(request.getStatus());
        adoptionRequest.setReviewNote(request.getReviewNote());
        adoptionRequest.setReviewTime(LocalDateTime.now());

        if (request.getStatus().equals(AdoptionStatus.APPROVED)) {
            Pet pet = adoptionRequest.getPet();
            pet.setStatus(PetStatus.ADOPTED);
            petRepository.save(pet);
        }

        adoptionRequest = requestRepository.save(adoptionRequest);
        return convertToResponse(adoptionRequest);
    }

    public AdoptionRequestDTO.AdoptionResponse getRequestById(Long id) {
        User user = userService.getCurrentUser();
        AdoptionRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        boolean isOwner = request.getAdopter().getId().equals(user.getId()) ||
                request.getPet().getRescuer().getId().equals(user.getId());

        if (!isOwner) {
            throw new RuntimeException("您没有权限查看此申请");
        }

        return convertToResponse(request);
    }

    private AdoptionRequestDTO.AdoptionResponse convertToResponse(AdoptionRequest request) {
        AdoptionRequestDTO.AdoptionResponse response = new AdoptionRequestDTO.AdoptionResponse();
        response.setId(request.getId());
        response.setPetId(request.getPet().getId());
        response.setPetName(request.getPet().getName());
        if (request.getPet().getPhotos() != null && !request.getPet().getPhotos().isEmpty()) {
            response.setPetThumbnail(request.getPet().getPhotos().get(0));
        }
        response.setAdopterId(request.getAdopter().getId());
        response.setAdopterNickname(request.getAdopter().getNickname());
        response.setPersonalIntro(request.getPersonalIntro());
        response.setPetExperience(request.getPetExperience());
        response.setHousingCondition(request.getHousingCondition());
        response.setContactInfo(request.getContactInfo());
        response.setStatus(request.getStatus());
        response.setReviewNote(request.getReviewNote());
        response.setCreateTime(request.getCreateTime() != null ? request.getCreateTime().format(FORMATTER) : null);
        response.setReviewTime(request.getReviewTime() != null ? request.getReviewTime().format(FORMATTER) : null);
        return response;
    }

    private AdoptionRequestDTO.MyAdoptionResponse convertToMyResponse(AdoptionRequest request) {
        AdoptionRequestDTO.MyAdoptionResponse response = new AdoptionRequestDTO.MyAdoptionResponse();
        response.setId(request.getId());
        response.setPetId(request.getPet().getId());
        response.setPetName(request.getPet().getName());
        if (request.getPet().getPhotos() != null && !request.getPet().getPhotos().isEmpty()) {
            response.setPetThumbnail(request.getPet().getPhotos().get(0));
        }
        response.setRescuerId(request.getPet().getRescuer().getId());
        response.setRescuerNickname(request.getPet().getRescuer().getNickname());
        response.setStatus(request.getStatus());
        response.setCreateTime(request.getCreateTime() != null ? request.getCreateTime().format(FORMATTER) : null);
        return response;
    }
}
