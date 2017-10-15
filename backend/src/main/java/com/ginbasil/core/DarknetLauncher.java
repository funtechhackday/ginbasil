package com.ginbasil.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DarknetLauncher {

    private static final Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+)%:\\s+(\\w+)");

    public static Map<String, Double> predictImageCategories(String path) {
        Map<String, Double> ret = new HashMap<>();

        String[] darknetArgs = {"./darknet", "classifier", "predict", "cfg/imagenet1k.data", "cfg/resnet152.cfg",
                "resnet152.weights", path};

        try {
            ProcessBuilder pb = new ProcessBuilder(darknetArgs);
            pb.directory(new File(App.DARKNET_DIR));

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while (null != (line = reader.readLine())) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    double percent = Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(2)) / 100;
                    String category = matcher.group(3);
                    ret.put(category, percent);
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            //ignore
        }

        return ret;
    }

}
