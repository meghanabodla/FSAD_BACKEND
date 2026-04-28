package com.klu.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootPath;

    public FileStorageService(@Value("${app.file-storage-path}") String storagePath) throws IOException {
        this.rootPath = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(this.rootPath);
    }

    public String store(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path folder = rootPath.resolve(folderName).normalize();
            Files.createDirectories(folder);
            String sanitized = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = folder.resolve(sanitized).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store file", ex);
        }
    }

    public Resource loadAsResource(String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new IllegalStateException("File not found");
            }
            return resource;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load file", ex);
        }
    }
}
