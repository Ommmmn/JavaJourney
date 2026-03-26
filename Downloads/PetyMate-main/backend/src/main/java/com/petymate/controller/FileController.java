package com.petymate.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${petymate.upload.dir}")
    private String uploadDir;

    @GetMapping("/{subDir}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String subDir, @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(subDir).resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg";
                if (filename.endsWith(".png")) contentType = "image/png";
                if (filename.endsWith(".webp")) contentType = "image/webp";
                if (filename.endsWith(".gif")) contentType = "image/gif";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
