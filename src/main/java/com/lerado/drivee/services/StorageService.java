package com.lerado.drivee.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

    void init();

    Path store(MultipartFile file);

    Path store(MultipartFile file, String relativeTo);

    void delete(String filename);

    void delete(String filename, String relativeTo);

    Path load(String filename);

    Path load(String filename, String relativeTo);

    Stream<Path> loadAll(String directory);

    Resource loadAsResource(String filename);

    Resource loadAsResource(String filename, String relativeTo);

    void deleteAll();
}
