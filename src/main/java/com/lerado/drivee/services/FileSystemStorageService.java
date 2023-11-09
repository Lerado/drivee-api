package com.lerado.drivee.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.lerado.drivee.exceptions.StorageException;
import com.lerado.drivee.exceptions.StorageFileNotFoundException;

@Service
public class FileSystemStorageService implements StorageService {

    @Value(value = "${app.storage.uploads.directory-path}")
    private String directoryPath;

    public String getDirectoryPath() {
        return directoryPath;
    }

    public Path getRootLocation() {
        if (this.getDirectoryPath().trim().length() == 0) {
            throw new StorageException("Storage directory path can not be empty");
        }
        return Paths.get(this.getDirectoryPath());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(this.getRootLocation());
        } catch (IOException e) {
            throw new StorageException("Could not initialize directory", e);
        }
    }

    @Override
    public Path store(MultipartFile file) {
        return this.store(file, null);
    }

    @Override
    public Path store(MultipartFile file, String relativeTo) {
        try {
            if (file.isEmpty())
                throw new StorageException("Failed to store empty file");

            // Build relative path
            Path relativePath = this.getRootLocation();

            if (relativeTo != null) {
                relativePath = Files.createDirectories(
                        Paths.get(
                                relativePath.toString(),
                                relativeTo))
                        .normalize()
                        .toAbsolutePath();
            }

            String filename = String.format("%s_%s", String.valueOf(System.currentTimeMillis()), file.getOriginalFilename());

            // Build destination file
            Path destinationFile = relativePath
                    .resolve(Paths.get(filename))
                    .normalize()
                    .toAbsolutePath();

            // Make sure destination in inside the root location
            if (!destinationFile.getParent().equals(relativePath)) {
                throw new StorageException("Can not create file outside of root directory");
            }

            // Save file
            InputStream fileStream = file.getInputStream();
            Files.copy(fileStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return destinationFile;
        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Path load(String filename) {
        return this.load(filename, "");
    }

    @Override
    public Path load(String filename, String relativeTo) {
        return Paths.get(
                this.getRootLocation().toString(),
                relativeTo)
                .resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        return this.loadAsResource(filename, "");
    }

    @Override
    public Resource loadAsResource(String filename, String relativeTo) {
        try {
            Path file = this.load(filename, relativeTo);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.getRootLocation().toFile());
    }

    @Override
    public Stream<Path> loadAll(String directory) {
        try {

            Path directoryPath = Paths.get(
                    this.getRootLocation().toString(),
                    directory)
                    .toAbsolutePath();

            if (!directoryPath.toFile().exists()) {
                throw new StorageFileNotFoundException("Could not find directory: " + directory);
            }

            return Files.walk(directoryPath, 1)
                    .filter(path -> !path.equals(directoryPath))
                    .map(directoryPath::relativize);

        } catch (IOException e) {
            throw new StorageException("Failed to read files stored in directory: " + directory, e);
        }
    }

    @Override
    public void delete(String filename) {
        this.delete(filename, "");
    }

    @Override
    public void delete(String filename, String relativeTo) {
        try {
            Path filePath = this.load(filename,relativeTo);
        FileSystemUtils.deleteRecursively(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + filename, e);
        }
    }
}