package com.sparktech.restaurant.cake;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class CakeImageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE =
            5L * 1024L * 1024L;

    private final Path uploadDirectory;

    public CakeImageService() throws IOException {
        uploadDirectory = Paths.get("uploads", "cakes")
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadDirectory);
    }

    public String saveImage(
            MultipartFile imageFile
    ) throws IOException {

        validateImage(imageFile);

        String extension = getExtension(
                imageFile.getOriginalFilename(),
                imageFile.getContentType()
        );

        String filename =
                UUID.randomUUID() + extension;

        Path targetPath = uploadDirectory
                .resolve(filename)
                .normalize();

        if (!targetPath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException(
                    "Invalid picture path."
            );
        }

        Files.copy(
                imageFile.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/cake-images/" + filename;
    }

    private void validateImage(
            MultipartFile imageFile
    ) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a cake picture."
            );
        }

        if (imageFile.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Picture size cannot exceed 5 MB."
            );
        }

        String contentType = imageFile.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP pictures are allowed."
            );
        }
    }

    private String getExtension(
            String originalFilename,
            String contentType
    ) {
        if (originalFilename != null) {
            String lowerName =
                    originalFilename.toLowerCase();

            if (lowerName.endsWith(".jpg") ||
                    lowerName.endsWith(".jpeg")) {
                return ".jpg";
            }

            if (lowerName.endsWith(".png")) {
                return ".png";
            }

            if (lowerName.endsWith(".webp")) {
                return ".webp";
            }
        }

        if ("image/png".equals(contentType)) {
            return ".png";
        }

        if ("image/webp".equals(contentType)) {
            return ".webp";
        }

        return ".jpg";
    }
}