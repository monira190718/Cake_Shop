package com.sparktech.restaurant.profile;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfilePictureService {

    private static final Path UPLOAD_DIRECTORY =
            Paths.get(
                    "uploads",
                    "profile-pictures"
            ).toAbsolutePath().normalize();

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png"
            );

    public String saveProfilePicture(
            MultipartFile picture
    ) throws IOException {

        if (picture == null || picture.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a profile picture."
            );
        }

        if (picture.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "Profile picture must be smaller than 5 MB."
            );
        }

        String contentType =
                picture.getContentType();

        if (contentType == null
                ||
                !ALLOWED_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only JPG, JPEG and PNG pictures are allowed."
            );
        }

        /*
         * Fileটি আসলেই image কি না পরীক্ষা করা হচ্ছে।
         */
        try (InputStream inputStream =
                     picture.getInputStream()) {

            if (ImageIO.read(inputStream) == null) {

                throw new IllegalArgumentException(
                        "The selected file is not a valid picture."
                );
            }
        }

        Files.createDirectories(
                UPLOAD_DIRECTORY
        );

        String extension =
                getExtension(contentType);

        String newFileName =
                UUID.randomUUID()
                        + extension;

        Path destination =
                UPLOAD_DIRECTORY
                        .resolve(newFileName)
                        .normalize();

        /*
         * Upload directory-এর বাইরে file save হওয়া বন্ধ করবে।
         */
        if (!destination.startsWith(UPLOAD_DIRECTORY)) {

            throw new IllegalArgumentException(
                    "Invalid picture location."
            );
        }

        try (InputStream inputStream =
                     picture.getInputStream()) {

            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return newFileName;
    }

    private String getExtension(
            String contentType
    ) {

        if ("image/png".equals(contentType)) {
            return ".png";
        }

        return ".jpg";
    }
}