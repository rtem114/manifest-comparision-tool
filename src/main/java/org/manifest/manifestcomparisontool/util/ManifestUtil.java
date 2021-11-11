package org.manifest.manifestcomparisontool.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

public class ManifestUtil {

    public static final String MANIFEST_HEADER = "ManifestFile";

    private static final char ATTRIBUTE_VALUE_SEPARATOR = ',';

    private static final Collection<String> ATTRIBUTE_WHITE_LIST = Arrays.asList(
        "Manifest-Version",
        "Bundle-ManifestVersion",
        "Bundle-Name",
        "Bundle-SymbolicName",
        "Bundle-Version",
        "Bundle-Activator",
        "Import-Package",
        "Export-Package",
        "Private-Package",
        "Bundle-ClassPath");


    public static Manifest differentiate(Manifest manifest1, Manifest manifest2) {
        Map<Object, Set<Object>> differenceMap = getDifferenceMap(manifest1, manifest2);
        Manifest differenceManifest = toManifest(differenceMap);

        return differenceManifest;
    }

    public static Map<Object, Set<Object>> toMap(Manifest manifest) {
        return manifest.getMainAttributes().entrySet().stream()
            .filter(entry -> ATTRIBUTE_WHITE_LIST.contains(entry.getKey().toString()))
            .map(entry ->  Map.entry(entry.getKey(), convertAttributeValueToSet((String) entry.getValue())))
            .collect(getLinkedHashMapCollector());
    }

    public static String toString(Manifest manifest) {
        StringBuilder buffer = new StringBuilder();
        manifest.getMainAttributes().entrySet().forEach(e -> buffer.append(String.format("%s: %s\n",e.getKey(),e.getValue())));
        return buffer.toString();
    }

    private static Manifest toManifest(Map<Object, Set<Object>> manifestMap){
        Manifest manifest = new Manifest();

        for (Object key : manifestMap.keySet()) {
            manifest.getMainAttributes().putValue(key.toString(), convertSetToAttributeValue(manifestMap.get(key)));
        }
        return manifest;
    }

    private static String convertSetToAttributeValue(Set<Object> attributeSet) {
        return StringUtils.join(attributeSet, ATTRIBUTE_VALUE_SEPARATOR);
    }

    private static Set<Object> convertAttributeValueToSet(String attributeValue) {
        return Arrays.stream(
            StringUtils.split(attributeValue, ATTRIBUTE_VALUE_SEPARATOR))
            .map(String::trim)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Collector<Object, ?, LinkedHashMap> getLinkedHashMapCollector() {
        return Collectors.toMap(
            entry -> ((Map.Entry) entry).getKey(),
            entry -> ((Map.Entry) entry).getValue(),
            (k1, k2) -> k1,
            LinkedHashMap::new);
    }

    private static LinkedHashMap getDifferenceMap(Manifest manifest1, Manifest manifest2) {
        Map<Object, Set<Object>>  manifest1Map = ManifestUtil.toMap(manifest1);
        Map<Object, Set<Object>>  manifest2Map = ManifestUtil.toMap(manifest2);

        return manifest1Map.entrySet()
            .stream().filter(entry -> !entry.getValue().equals(manifest2Map.get(entry.getKey())))
            .map(entry ->  Map.entry(entry.getKey(), manifest2Map.containsKey(entry.getKey()) ? Sets.difference(entry.getValue(),manifest2Map.get(entry.getKey())) : entry.getValue()))
            .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
            .collect(getLinkedHashMapCollector());
    }


}
