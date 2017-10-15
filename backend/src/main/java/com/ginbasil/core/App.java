package com.ginbasil.core;

import com.ginbasil.core.model.SimpleDataModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class App {

    public static String DARKNET_DIR = "";

    public static final SimpleDataModel model = new SimpleDataModel();

    public static void main(String... args) {
        SpringApplication.run(App.class, args);
        DARKNET_DIR = args[0];

        recognizeImagesFromFileStorage(System.getProperty("user.dir") + "/src/main/resources/static/images");

        Set<String> images = model.getImagesByName("12905053_1068234403222725_954912288_n.jpg", 30.0);

        System.out.println("Found " + images.size() + " similar images");
        images.forEach(System.out::println);
    }

    private static void recognizeImagesFromFileStorage(String dirPath) {
        File directory = new File(dirPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jpg"));

        for (File file: files) {
            Map<String, Double> report = DarknetLauncher.predictImageCategories(file.getAbsolutePath());

            //System.out.println("File: " + file.getName());
            //report.forEach((key, value) -> System.out.println(key + " -> " + value));
            //System.out.println();

            model.putReport(file.getName(), report);
        }

        System.out.println(files.length + " images has processed");
    }
}
