package org.manifest.manifestcomparisontool.camel.router;

import java.io.InputStream;
import java.nio.file.Path;

import org.apache.camel.builder.RouteBuilder;
import org.manifest.manifestcomparisontool.camel.transformer.ManifestTransformer;
import org.manifest.manifestcomparisontool.util.ManifestHeader;
import org.manifest.manifestcomparisontool.util.ManifestUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * Fetches secondary file for comparision.
 *
 * Since source files are streamed and comparision is live, when source file1 is changed, second file will be automatically fetched for comparision.
 *
 */
@Component
public class SecondaryFileRouter extends RouteBuilder {

    @Value("${in1:}")
    private String in1;

    @Value("${in2:}")
    private String in2;

    @Autowired
    private ManifestTransformer manifestTransformer;

    public static final String SECONDARY_FOR_FILE_1_ROUTE = "direct:loadSecondFile";

    public static final String SECONDARY_FOR_FILE_2_ROUTE = "direct:loadFirstFile";

    @Override
    public void configure() {
        fetchFile(SECONDARY_FOR_FILE_2_ROUTE, in1, ManifestHeader.FILE_1);
        fetchFile(SECONDARY_FOR_FILE_1_ROUTE, in2, ManifestHeader.FILE_2);
    }

    private void fetchFile(String routeId, String fileAbsolutePath, ManifestHeader header){
        from(routeId)
            .pollEnrich().simple(getCamelFilePath(fileAbsolutePath))
            .convertBodyTo(InputStream.class)
            .transform().method(manifestTransformer)
            .setHeader(ManifestUtil.MANIFEST_HEADER, constant(header))
            .to(FileComparisionRouter.COMPARE_ROUTE);
    }

    private String getCamelFilePath(String file) {
        String fileName = Path.of(file).getFileName().toString();
        String directory = Path.of(file).getParent().toString();
        return String.format("file:%s/?fileName=%s&idempotent=false&noop=true", directory, fileName);
    }

}
