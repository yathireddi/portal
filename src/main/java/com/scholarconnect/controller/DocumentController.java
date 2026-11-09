package com.scholarconnect.controller;

import com.scholarconnect.dto.ApiResponse;
import com.scholarconnect.dto.DocumentDTO;
import com.scholarconnect.entity.Document;
import com.scholarconnect.security.UserPrincipal;
import com.scholarconnect.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<DocumentDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("applicationId") Long applicationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        DocumentDTO dto = documentService.uploadDocument(file, documentType, applicationId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully", dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        String role = principal.getAuthorities().iterator().next().getAuthority();
        Resource resource = documentService.getDocumentResource(id, principal.getId(), role);
        Document document = documentService.getDocumentEntity(id);

        String contentType = document.getContentType() != null ? document.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getOriginalFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        documentService.deleteDocument(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
    }
}
