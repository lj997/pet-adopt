package com.petadopt.controller;

import com.petadopt.common.Result;
import com.petadopt.dto.AdoptionRequestDTO;
import com.petadopt.enums.AdoptionStatus;
import com.petadopt.service.AdoptionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionRequestController {

    @Autowired
    private AdoptionRequestService requestService;

    @PostMapping
    public Result<AdoptionRequestDTO.AdoptionResponse> createRequest(@Valid @RequestBody AdoptionRequestDTO.CreateRequest request) {
        AdoptionRequestDTO.AdoptionResponse response = requestService.createRequest(request);
        return Result.success("申请提交成功", response);
    }

    @GetMapping("/my")
    public Result<List<AdoptionRequestDTO.MyAdoptionResponse>> getMyRequests() {
        List<AdoptionRequestDTO.MyAdoptionResponse> requests = requestService.getMyRequests();
        return Result.success(requests);
    }

    @GetMapping("/received")
    public Result<List<AdoptionRequestDTO.AdoptionResponse>> getReceivedRequests() {
        List<AdoptionRequestDTO.AdoptionResponse> requests = requestService.getMyReceivedRequests();
        return Result.success(requests);
    }

    @GetMapping("/received/status/{status}")
    public Result<List<AdoptionRequestDTO.AdoptionResponse>> getReceivedRequestsByStatus(@PathVariable AdoptionStatus status) {
        List<AdoptionRequestDTO.AdoptionResponse> requests = requestService.getReceivedRequestsByStatus(status);
        return Result.success(requests);
    }

    @PostMapping("/review")
    public Result<AdoptionRequestDTO.AdoptionResponse> reviewRequest(@Valid @RequestBody AdoptionRequestDTO.ReviewRequest request) {
        AdoptionRequestDTO.AdoptionResponse response = requestService.reviewRequest(request);
        return Result.success("审核完成", response);
    }

    @GetMapping("/{id}")
    public Result<AdoptionRequestDTO.AdoptionResponse> getRequestById(@PathVariable Long id) {
        AdoptionRequestDTO.AdoptionResponse response = requestService.getRequestById(id);
        return Result.success(response);
    }
}
