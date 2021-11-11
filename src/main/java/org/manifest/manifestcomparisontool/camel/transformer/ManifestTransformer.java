package org.manifest.manifestcomparisontool.camel.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import org.apache.camel.Converter;
import org.springframework.stereotype.Component;

@Component
public class ManifestTransformer {

    @Converter
    public static Manifest toManifest(InputStream inputStream) throws IOException {
        return new Manifest(inputStream);
    }
}
