package com.landlens.property.controller;

import com.landlens.common.exception.ResourceNotFoundException;
import com.landlens.property.dto.*;
import com.landlens.property.mapper.PropertyMapper;
import com.landlens.property.model.*;
import com.landlens.property.service.PropertyService;
import com.landlens.user.model.User;
import com.landlens.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@Transactional
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserRepository userRepository;

    private UUID resolveUserId(Principal principal) {
        try {
            return UUID.fromString(principal.getName());
        } catch (IllegalArgumentException e) {
            return userRepository.findByEmail(principal.getName())
                    .map(User::getId)
                    .orElseThrow(() -> new ResourceNotFoundException("User record not found for principal: " + principal.getName()));
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'SELLER')")
    public ResponseEntity<PropertyResponseDto> createProperty(
            @Valid @RequestBody PropertyRequestDto propertyDto, Principal principal) {
        UUID providerId = resolveUserId(principal);
        Property property = PropertyMapper.toEntity(propertyDto);
        Property created = propertyService.createProperty(property, providerId);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(created));
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponseDto>> searchProperties(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String village,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        List<Property> list = propertyService.searchProperties(district, village, state, status, category, minPrice, maxPrice);
        List<PropertyResponseDto> dtoList = list.stream()
                .map(PropertyMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable UUID id) {
        Property property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(property));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'SELLER')")
    public ResponseEntity<PropertyResponseDto> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyRequestDto detailsDto,
            Principal principal) {
        UUID providerId = resolveUserId(principal);
        Property details = PropertyMapper.toEntity(detailsDto);
        Property updated = propertyService.updateProperty(id, details, providerId);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'GOVERNMENT_OFFICER', 'GOVT', 'GOVERNMENT', 'SELLER')")
    public ResponseEntity<Object> deleteProperty(@PathVariable UUID id, Principal principal) {
        UUID userId = resolveUserId(principal);
        boolean isAdminOrGovt = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || 
                                  auth.getAuthority().equals("ROLE_GOVERNMENT_OFFICER") ||
                                  auth.getAuthority().equals("ROLE_GOVT") || 
                                  auth.getAuthority().equals("ROLE_GOVERNMENT"));
        propertyService.deleteProperty(id, userId, isAdminOrGovt);
        return ResponseEntity.ok("Property listing removed successfully");
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'SELLER')")
    public ResponseEntity<PropertyImageDto> addImage(
            @PathVariable UUID id,
            @RequestBody PropertyImageDto imageDto) {
        PropertyImage image = new PropertyImage();
        image.setImageUrl(imageDto.getImageUrl());
        image.setThumbnailUrl(imageDto.getThumbnailUrl());
        image.setDisplayOrder(imageDto.getDisplayOrder());

        PropertyImage created = propertyService.addImage(id, image);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(created));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<PropertyImageDto>> getImages(@PathVariable UUID id) {
        List<PropertyImage> images = propertyService.getImages(id);
        List<PropertyImageDto> dtoList = images.stream()
                .map(PropertyMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{id}/videos")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'SELLER')")
    public ResponseEntity<PropertyVideoDto> addVideo(
            @PathVariable UUID id,
            @RequestBody PropertyVideoDto videoDto) {
        PropertyVideo video = new PropertyVideo();
        video.setVideoUrl(videoDto.getVideoUrl());
        video.setDuration(videoDto.getDuration());
        video.setThumbnailUrl(videoDto.getThumbnailUrl());

        PropertyVideo created = propertyService.addVideo(id, video);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(created));
    }

    @GetMapping("/{id}/videos")
    public ResponseEntity<List<PropertyVideoDto>> getVideos(@PathVariable UUID id) {
        List<PropertyVideo> videos = propertyService.getVideos(id);
        List<PropertyVideoDto> dtoList = videos.stream()
                .map(PropertyMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{id}/save")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<SavedPropertyResponseDto> saveProperty(@PathVariable UUID id, Principal principal) {
        UUID buyerId = resolveUserId(principal);
        SavedProperty saved = propertyService.saveProperty(id, buyerId);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(saved));
    }

    @DeleteMapping("/{id}/save")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<Object> unsaveProperty(@PathVariable UUID id, Principal principal) {
        UUID buyerId = resolveUserId(principal);
        propertyService.unsaveProperty(id, buyerId);
        return ResponseEntity.ok("Property removed from saved items");
    }

    @GetMapping("/saved")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<List<SavedPropertyResponseDto>> getSavedProperties(Principal principal) {
        UUID buyerId = resolveUserId(principal);
        List<SavedProperty> saved = propertyService.getSavedProperties(buyerId);
        List<SavedPropertyResponseDto> dtoList = saved.stream()
                .map(PropertyMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{id}/visit")
    @PreAuthorize("hasAnyRole('BUYER', 'ADMIN')")
    public ResponseEntity<PropertyVisitResponseDto> scheduleVisit(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyVisitRequestDto visitDto,
            Principal principal) {
        UUID buyerId = resolveUserId(principal);
        PropertyVisit visit = new PropertyVisit();
        visit.setVisitDate(visitDto.getVisitDate());
        visit.setVisitTime(visitDto.getVisitTime());

        PropertyVisit scheduled = propertyService.scheduleVisit(id, buyerId, visit);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(scheduled));
    }

    @GetMapping("/visits")
    public ResponseEntity<List<PropertyVisitResponseDto>> getVisits(Principal principal, HttpServletRequest request) {
        UUID userId = resolveUserId(principal);
        
        boolean isProvider = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER") || auth.getAuthority().equals("ROLE_SELLER"));

        List<PropertyVisit> list;
        if (isProvider) {
            list = propertyService.getProviderVisits(userId);
        } else {
            list = propertyService.getBuyerVisits(userId);
        }

        List<PropertyVisitResponseDto> dtoList = list.stream()
                .map(PropertyMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/visits/{visitId}")
    public ResponseEntity<PropertyVisitResponseDto> updateVisitStatus(
            @PathVariable UUID visitId,
            @RequestParam String status,
            Principal principal) {
        UUID userId = resolveUserId(principal);
        PropertyVisit updated = propertyService.updateVisitStatus(visitId, status, userId);
        return ResponseEntity.ok(PropertyMapper.toResponseDto(updated));
    }

    @PostMapping("/deconflict-coordinates")
    @PreAuthorize("hasAnyRole('ADMIN', 'GOVERNMENT_OFFICER', 'GOVT', 'GOVERNMENT')")
    public ResponseEntity<String> deconflictPropertyCoordinates() {
        int updatedCount = propertyService.deconflictPlottedProperties();
        return ResponseEntity.ok("Deconfliction process complete. Adjusted coordinates for " + updatedCount + " overlapping properties.");
    }
}
