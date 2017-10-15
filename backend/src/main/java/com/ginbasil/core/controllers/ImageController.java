package com.ginbasil.core.controllers;

import com.ginbasil.core.App;
import com.ginbasil.core.DarknetLauncher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
public class ImageController {

    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadFile) {
        if (uploadFile.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }

        final String[] report = {""};

        try {

            String fileName = generateInternalFilenane(uploadFile.getOriginalFilename());
            saveUploadedFile(uploadFile, fileName);
            Map<String, Double> map = DarknetLauncher.predictImageCategories(getPathToFile(fileName));

            map.entrySet().forEach(e -> report[0] += e.getKey() + " -> " + e.getValue() + "\n");

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(report[0], new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/api/like")
    public ResponseEntity<?> getImagesByLike(@RequestParam("like") String likedImage, @RequestParam("threshold") String th) {
        Double threshold = Double.valueOf(th);
        Set<String> images = App.model.getImagesByName(likedImage, threshold);

        StringBuilder sb = new StringBuilder();
        if (!images.isEmpty()) {
            images.forEach(image -> {
                sb.append("<img src=\"images/");
                sb.append(image);
                sb.append("\" width=\"200px\" height=\"200px\"/><br/>");
            });
        } else {
            sb.append("No matches");
        }

        return new ResponseEntity<>(sb.toString(), new HttpHeaders(), HttpStatus.OK);
    }

    private void saveUploadedFile(MultipartFile file, String fileName) throws IOException {
        byte[] bytes = file.getBytes();

        Path path = Paths.get(getPathToFile(fileName));
        Files.write(path, bytes);
    }

    private String generateInternalFilenane(String origName) {
        String firstPart = UUID.randomUUID().toString();
        String secondPart = origName.substring(origName.lastIndexOf('.'), origName.length());

        return firstPart + secondPart;
    }

    private String getImageDir() {
        return System.getProperty("user.dir") + "/src/main/resources/static/images/";
    }

    private String getPathToFile(String fileName) {
        return getImageDir() + fileName;
    }

}
