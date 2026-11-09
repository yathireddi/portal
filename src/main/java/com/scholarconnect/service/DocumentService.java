package com.scholarconnect.service;

import com.scholarconnect.dto.DocumentDTO;
import com.scholarconnect.entity.Application;
import com.scholarconnect.entity.Document;
import com.scholarconnect.entity.Role;
import com.scholarconnect.entity.User;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.exception.UnauthorizedException;
import com.scholarconnect.repository.ApplicationRepository;
import com.scholarconnect.repository.DocumentRepository;
import com.scholarconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    public DocumentService(DocumentRepository documentRepository,
                           ApplicationRepository applicationRepository,
                           UserRepository userRepository,
                           @Value("${app.upload.dir:uploads/documents}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the upload directory at " + uploadDir, ex);
        }
    }

    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, String documentType, Long applicationId, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type: " + contentType + ". Only PDF, JPG, and PNG are allowed.");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException("File size exceeds 10 MB limit");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        // Ownership verification
        if (!application.getStudent().getId().equals(userId)) {
            User user = userRepository.findById(userId).orElseThrow();
            if (user.getRole() != Role.ROLE_ADMIN) {
                throw new UnauthorizedException("You can only upload documents to your own application");
            }
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        // Sanitize
        originalFilename = Paths.get(originalFilename).getFileName().toString();
        String extension = "";
        int dotIdx = originalFilename.lastIndexOf('.');
        if (dotIdx > 0) {
            extension = originalFilename.substring(dotIdx);
        }

        String storedFilename = UUID.randomUUID().toString() + extension;
        Path targetLocation = this.fileStorageLocation.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + storedFilename + ". Please try again!", ex);
        }

        Document doc = new Document();
        doc.setApplication(application);
        doc.setDocumentType(documentType != null && !documentType.isBlank() ? documentType.toUpperCase() : "GENERAL");
        doc.setOriginalFilename(originalFilename);
        doc.setStoredFilename(storedFilename);
        doc.setFilePath("uploads/documents/" + storedFilename);
        doc.setContentType(contentType);
        doc.setFileSize(file.getSize());
        doc.setVerificationStatus("PENDING");

        Document saved = documentRepository.save(doc);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public Resource getDocumentResource(Long documentId, Long currentUserId, String role) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        Application application = document.getApplication();

        // RBAC access authorization
        boolean isOwner = application.getStudent().getId().equals(currentUserId);
        boolean isAdmin = Role.ROLE_ADMIN.name().equalsIgnoreCase(role);
        boolean isAssignedReviewer = Role.ROLE_REVIEWER.name().equalsIgnoreCase(role) &&
                application.getReviewer() != null &&
                application.getReviewer().getId().equals(currentUserId);

        if (!isOwner && !isAdmin && !isAssignedReviewer) {
            throw new UnauthorizedException("You do not have authorization to view this document");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(document.getStoredFilename()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on disk: " + document.getOriginalFilename());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found on disk");
        }
    }

    @Transactional(readOnly = true)
    public Document getDocumentEntity(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));
    }

    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsForApplication(Long applicationId) {
        return documentRepository.findByApplicationIdOrderByUploadedAtAsc(applicationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long documentId, Long currentUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + documentId));

        if (!document.getApplication().getStudent().getId().equals(currentUserId)) {
            User user = userRepository.findById(currentUserId).orElseThrow();
            if (user.getRole() != Role.ROLE_ADMIN) {
                throw new UnauthorizedException("You cannot delete this document");
            }
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(document.getStoredFilename()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }

        documentRepository.delete(document);
    }

    public DocumentDTO mapToDTO(Document d) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(d.getId());
        dto.setApplicationId(d.getApplication().getId());
        dto.setDocumentType(d.getDocumentType());
        dto.setOriginalFilename(d.getOriginalFilename());
        dto.setStoredFilename(d.getStoredFilename());
        dto.setFileUrl("/api/documents/" + d.getId());
        dto.setContentType(d.getContentType());
        dto.setFileSize(d.getFileSize());
        dto.setUploadedAt(d.getUploadedAt());
        dto.setVerificationStatus(d.getVerificationStatus());
        return dto;
    }
}

