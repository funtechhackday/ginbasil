package com.ginbasil.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleDataModel {

    //filename -> darknet report
    private Map<String, Map<String, Double>> recognizedImages = new HashMap<>();

    private Map<String, List<String>> imagesByCategories = new HashMap<>();

    public SimpleDataModel() { }

    public void putReport(String name, Map<String, Double> report) {
        recognizedImages.put(name, report);

        report.forEach((key, value) -> {
            List<String> images = imagesByCategories.get(key);
            if (null == images) {
                images = new ArrayList<>();
            }
            images.add(name);
            imagesByCategories.put(key, images);
        });
    }

    public Set<String> getImagesByName(String name, Double threshold) {
        Set<String> ret = new HashSet<>();

        Map<String, Double> report = recognizedImages.get(name);
        Map<String, Double> sortedReport = sortByValues(report);
        sortedReport.forEach((category, value) -> {
            if (value > threshold) {
                Set<String> images = imagesByCategories.get(category).stream()
                        .filter(v -> !name.trim().equalsIgnoreCase(v)).collect(Collectors.toSet());

                if (ret.isEmpty()) {
                    ret.addAll(images);
                } else {
                    Set<String> intersection = ret.stream().filter(images::contains).collect(Collectors.toSet());
                    if (!intersection.isEmpty()) {
                        ret.clear();
                        ret.addAll(intersection);
                    }
                }
            }
        });

        return ret;
    }

    private Map sortByValues(Map map) {
        List<Object> list = new LinkedList<Object>(map.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));

        Map sortedHashMap = new LinkedHashMap();
        for (Object aList : list) {
            Map.Entry entry = (Map.Entry) aList;
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }


}
