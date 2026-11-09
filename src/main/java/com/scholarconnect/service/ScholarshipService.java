package com.scholarconnect.service;

import com.scholarconnect.dto.ScholarshipDTO;
import com.scholarconnect.entity.Scholarship;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.repository.ApplicationRepository;
import com.scholarconnect.repository.ScholarshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final ApplicationRepository applicationRepository;

    public ScholarshipService(ScholarshipRepository scholarshipRepository,
                              ApplicationRepository applicationRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public List<ScholarshipDTO> getAllActive(String category, String state, String level) {
        List<Scholarship> scholarships;
        if ((category != null && !category.isBlank()) ||
            (state != null && !state.isBlank()) ||
            (level != null && !level.isBlank())) {
            scholarships = scholarshipRepository.filterScholarships(category, state, level);
        } else {
            scholarships = scholarshipRepository.findByStatusOrderByApplicationDeadlineAsc("ACTIVE");
        }

        return scholarships.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScholarshipDTO getById(Long id) {
        Scholarship scholarship = scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with ID: " + id));
        return mapToDTO(scholarship);
    }

    @Transactional(readOnly = true)
    public List<ScholarshipDTO> search(String query) {
        if (query == null || query.trim().isBlank()) {
            return getAllActive("", "", "");
        }
        return scholarshipRepository.searchActiveScholarships(query.trim())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScholarshipDTO> getAllForAdmin() {
        return scholarshipRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScholarshipDTO createScholarship(ScholarshipDTO dto) {
        Scholarship s = new Scholarship();
        updateEntityFromDTO(s, dto);
        Scholarship saved = scholarshipRepository.save(s);
        return mapToDTO(saved);
    }

    @Transactional
    public ScholarshipDTO updateScholarship(Long id, ScholarshipDTO dto) {
        Scholarship existing = scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with ID: " + id));

        updateEntityFromDTO(existing, dto);
        Scholarship updated = scholarshipRepository.save(existing);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteScholarship(Long id) {
        Scholarship existing = scholarshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with ID: " + id));

        long count = applicationRepository.countByScholarshipId(id);
        if (count > 0) {
            // Soft delete to preserve foreign key constraints
            existing.setStatus("INACTIVE");
            scholarshipRepository.save(existing);
        } else {
            scholarshipRepository.delete(existing);
        }
    }

    public ScholarshipDTO mapToDTO(Scholarship s) {
        ScholarshipDTO dto = new ScholarshipDTO();
        dto.setId(s.getId());
        dto.setScholarshipName(s.getScholarshipName());
        dto.setProvider(s.getProvider());
        dto.setDescription(s.getDescription());
        dto.setAmount(s.getAmount());
        dto.setMinimumPercentage(s.getMinimumPercentage());
        dto.setMaximumFamilyIncome(s.getMaximumFamilyIncome());
        dto.setEligibleCategories(s.getEligibleCategories());
        dto.setEligibleStates(s.getEligibleStates());
        dto.setEligibleCourses(s.getEligibleCourses());
        dto.setEducationLevel(s.getEducationLevel());
        dto.setApplicationStartDate(s.getApplicationStartDate());
        dto.setApplicationDeadline(s.getApplicationDeadline());
        dto.setStatus(s.getStatus());
        dto.setCreatedAt(s.getCreatedAt());

        long applicants = applicationRepository.countByScholarshipId(s.getId());
        dto.setApplicantCount(applicants);

        LocalDate today = LocalDate.now();
        if (s.getApplicationDeadline() != null) {
            long days = ChronoUnit.DAYS.between(today, s.getApplicationDeadline());
            dto.setDaysLeft(Math.max(0, days));
            dto.setExpired(s.getApplicationDeadline().isBefore(today));
        }

        return dto;
    }

    private void updateEntityFromDTO(Scholarship entity, ScholarshipDTO dto) {
        entity.setScholarshipName(dto.getScholarshipName().trim());
        entity.setProvider(dto.getProvider().trim());
        entity.setDescription(dto.getDescription().trim());
        entity.setAmount(dto.getAmount());
        entity.setMinimumPercentage(dto.getMinimumPercentage() != null ? dto.getMinimumPercentage() : java.math.BigDecimal.ZERO);
        entity.setMaximumFamilyIncome(dto.getMaximumFamilyIncome() != null ? dto.getMaximumFamilyIncome() : new java.math.BigDecimal("9999999.00"));
        entity.setEligibleCategories(dto.getEligibleCategories() != null && !dto.getEligibleCategories().isBlank() ? dto.getEligibleCategories() : "ALL");
        entity.setEligibleStates(dto.getEligibleStates() != null && !dto.getEligibleStates().isBlank() ? dto.getEligibleStates() : "ALL");
        entity.setEligibleCourses(dto.getEligibleCourses() != null && !dto.getEligibleCourses().isBlank() ? dto.getEligibleCourses() : "ALL");
        entity.setEducationLevel(dto.getEducationLevel() != null && !dto.getEducationLevel().isBlank() ? dto.getEducationLevel() : "ALL");
        entity.setApplicationStartDate(dto.getApplicationStartDate());
        entity.setApplicationDeadline(dto.getApplicationDeadline());
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            entity.setStatus(dto.getStatus());
        }
    }
}

