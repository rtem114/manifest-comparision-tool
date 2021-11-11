package org.manifest.manifestcomparisontool.camel.router;

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileEndpoint;
import org.manifest.manifestcomparisontool.camel.processor.ComparisionProcessor;
import org.manifest.manifestcomparisontool.util.ManifestHeader;
import org.manifest.manifestcomparisontool.util.ManifestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This component does the actual comparision. It waits for 2 files, compares it and sends result to corresponding output.
 * <p>
 * Comparision is live, so when you do changes in source file1, it will automatically fetch file2 and do the comparision.
 * <p>
 * For example: You start the service with manifest1 and manifest2. It will do the comparision and output results to the corresponding out1 and out2.
 * In runtime, if you change manifest1, out1 wil be automatically updated. The same thing for manifest2.
 */
@Component
public class FileComparisionRouter extends RouteBuilder {

    @Value("${out1:}")
    private String out1;

    @Value("${out2:}")
    private String out2;

    public static final String COMPARE_ROUTE = "direct:compare";

    @Autowired
    private ComparisionProcessor comparisionProcessor;

    @Override
    public void configure() {
        FileEndpoint fileEndpoint1 = getFileEndpoint(out1);
        FileEndpoint fileEndpoint2 = getFileEndpoint(out2);

        from(COMPARE_ROUTE)
            .aggregate(constant(true), AggregationStrategies.groupedExchange())
            //waits for 2 files for comparision
            .completionSize(2)
            .process(comparisionProcessor)
            // if header is FILE_1, then results will be sent to manifestout1, if FILE_2, then to manifestout2.
            .choice().when(header(ManifestUtil.MANIFEST_HEADER).isEqualTo(ManifestHeader.FILE_1)).to(fileEndpoint1)
            .otherwise().when(header(ManifestUtil.MANIFEST_HEADER).isEqualTo(ManifestHeader.FILE_2)).to(fileEndpoint2)
            .log("Comparision is done")
            .end();

    }

    private FileEndpoint getFileEndpoint(String absolutePathToFile) {
        String streamPath = String.format("file://?fileName=%s", absolutePathToFile);
        FileEndpoint fileEndpoint = this.endpoint(streamPath, FileEndpoint.class);
        return fileEndpoint;
    }
}
