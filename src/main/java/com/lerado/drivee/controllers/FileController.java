package com.lerado.drivee.controllers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lerado.drivee.dto.responses.FileResponseDto;
import com.lerado.drivee.exceptions.StorageException;
import com.lerado.drivee.services.StorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "files")
@RequiredArgsConstructor
public class FileController {

    final private StorageService storageService;

    /**
     * List files uploaded by a given user
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "{userId}")
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public List<FileResponseDto> listUploadedFiles(@PathVariable long userId) {
        List<FileResponseDto> list = this.storageService.loadAll(String.valueOf(userId))
                .map(path -> {
                    try {
                        return new FileResponseDto(
                                // File name
                                path.toFile().getName(),
                                // Download url
                                MvcUriComponentsBuilder.fromMethodName(
                                        FileController.class,
                                        "serveFile",
                                        userId,
                                        path.getFileName().toString(),
                                        "attachment").build().toUriString(),
                                // Preview url
                                MvcUriComponentsBuilder.fromMethodName(
                                        FileController.class,
                                        "serveFile",
                                        userId,
                                        path.getFileName().toString(),
                                        "inline").build().toUriString(),
                                // Extension
                                Files.probeContentType(path));
                    } catch (IOException e) {
                        throw new StorageException("Could not determine file extension");
                    }
                })
                .sorted((a, b) -> a.getCreatedAt() < b.getCreatedAt() ? 1 : -1)
                .collect(Collectors.toList());

        return list;
    }

    /**
     * Serve a file that was stored by a user
     *
     * @param userId
     * @param filename
     * @return
     */
    @GetMapping(path = "{userId}/{filename:.+}")
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public ResponseEntity<Resource> serveFile(@PathVariable long userId, @PathVariable String filename,
            @RequestParam String content) {
        Resource file = this.storageService.loadAsResource(filename, String.valueOf(userId));
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Optional.of(content).orElse("attachment");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentType + "; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    /**
     * Remove uploaded file
     *
     * @param userId
     * @param filename
     * @return
     */
    @DeleteMapping(path = "{userId}/{filename:.+}")
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public ResponseEntity<String> deleteFile(
            @PathVariable long userId,
            @PathVariable String filename) {
        this.storageService.delete(filename, String.valueOf(userId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove multiple files
     *
     * @param userId
     * @param filename
     * @return
     */
    @DeleteMapping(path = "{userId}")
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public ResponseEntity<String> deleteFile(
            @PathVariable long userId,
            @RequestParam String[] filenames) {
        for (String filename : filenames) {
            this.storageService.delete(filename, String.valueOf(userId));
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Store uploaded file
     *
     * @param file
     * @param userId
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = "{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam MultipartFile file,
            @PathVariable long userId,
            RedirectAttributes redirectAttributes) {
        this.storageService.store(file, String.valueOf(userId));
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Store multuple files
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = "{userId}/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    public ResponseEntity<String> handleMultipleFileUpload(
            @RequestParam(value = "files") MultipartFile[] files,
            @PathVariable long userId,
            RedirectAttributes redirectAttributes) {
        for (MultipartFile multipartFile : files) {
            this.storageService.store(multipartFile, String.valueOf(userId));
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location).build();
    }
}
