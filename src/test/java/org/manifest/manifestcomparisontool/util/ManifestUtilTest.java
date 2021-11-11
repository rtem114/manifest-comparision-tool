package org.manifest.manifestcomparisontool.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class ManifestUtilTest {

    @Test
    public void testManifestToString() throws IOException {
        File manifestFile = Path.of("src/test/resources/manifest/manifest1.mf").toFile();
        Manifest manifest = new Manifest(new FileInputStream(manifestFile));

        String expected = "Manifest-Version: 1.0\n" +
            "Bundle-ManifestVersion: 2\n" +
            "Bundle-Name: MyService bundle 1\n" +
            "Bundle-SymbolicName: com.sample.myservice1\n" +
            "Bundle-Version: 1.0.1\n" +
            "Bundle-Activator: com.sample.myservice.Activator\n" +
            "Import-Package: org.apache.commons.logging;version=\"1.0.4\",javax.naming;resolution:=optional\n" +
            "Export-Package: com.sample.myservice.api;version=\"1.0.1\"\n" +
            "Private-Package: org.apache.commons.lang3.builder,org.antlr.runtime.debug\n" +
            "Bundle-ClassPath: .,lib/advancedPersistentLookupLib-1.2.jar,lib/commons-lang3-3.10.jar\n";

        ManifestUtil.toString(manifest);
        assertThat(manifest.getMainAttributes().size(), is(10));
        assertThat(ManifestUtil.toString(manifest), is(expected));

    }

    @Test
    public void testManifestToMap() throws IOException {
        File manifestFile = Path.of("src/test/resources/manifest/manifest1.mf").toFile();
        Manifest manifest = new Manifest(new FileInputStream(manifestFile));

        Map<Object, Set<Object>> manifestMap = ManifestUtil.toMap(manifest);

        assertThat(manifestMap.size(), is(10));
    }

    @Test
    public void tesDifferentiate() throws IOException {
        File manifestFile1 = Path.of("src/test/resources/manifest/manifest1.mf").toFile();
        Manifest manifest1 = new Manifest(new FileInputStream(manifestFile1));

        File manifestFile2 = Path.of("src/test/resources/manifest/manifest2.mf").toFile();
        Manifest manifest2 = new Manifest(new FileInputStream(manifestFile2));

        Manifest difference = ManifestUtil.differentiate(manifest1, manifest2);

        assertThat(difference.getMainAttributes().size(), is(7));
        assertThat(difference.getMainAttributes().getValue("Bundle-Name"), is("MyService bundle 1"));
        assertThat(difference.getMainAttributes().getValue("Bundle-Version"), is("1.0.1"));
        assertThat(difference.getMainAttributes().getValue("Bundle-ClassPath"), is("lib/commons-lang3-3.10.jar"));
        assertThat(difference.getMainAttributes().getValue("Bundle-SymbolicName"), is("com.sample.myservice1"));
    }


}
