package org.manifest.manifestcomparisontool.camel.router;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.stream.StreamEndpoint;
import org.manifest.manifestcomparisontool.camel.strategy.LinesToStreamGroupStrategy;
import org.manifest.manifestcomparisontool.camel.transformer.ManifestTransformer;
import org.manifest.manifestcomparisontool.util.ManifestHeader;
import org.manifest.manifestcomparisontool.util.ManifestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.manifest.manifestcomparisontool.camel.router.SecondaryFileRouter.SECONDARY_FOR_FILE_1_ROUTE;
import static org.manifest.manifestcomparisontool.camel.router.SecondaryFileRouter.SECONDARY_FOR_FILE_2_ROUTE;


/**
 * This component starts streaming of file1 and file2. If you do changes in file1 or file2, file will be streamed for comparision.
 * It makes the comparision live. Any change to files will trigger comparision.
 * <p>
 * If some changes were done to file1, this component will fetch file2 and send it for comparision.
 */
@Component
public class FileStreamingRouter extends RouteBuilder {

    @Value("${in1:}")
    private String in1;

    @Value("${in2:}")
    private String in2;

    @Value("${stream.scan.delay}")
    private Integer scanStreamDelay;

    @Value("${stream.scan}")
    private boolean scanStream;

    @Value("${stream.file.watcher}")
    private boolean fileWatcher;

    @Value("${stream.retry}")
    private boolean retry;

    @Value("${stream.group.lines}")
    private Integer groupLines;

    @Autowired
    private ManifestTransformer manifestTransformer;

    @Autowired
    private LinesToStreamGroupStrategy linesToStreamGroupStrategy;

    public static final String FILE_1_ROUTE = "streamingFromFile1";

    public static final String FILE_2_ROUTE = "streamingFromFile2";


    @Override
    public void configure() {
        StreamEndpoint sourceEndpoint1 = getStreamSource(in1);
        StreamEndpoint sourceEndpoint2 = getStreamSource(in2);

        streamFromFile(sourceEndpoint1, FILE_1_ROUTE, ManifestHeader.FILE_1, SECONDARY_FOR_FILE_1_ROUTE, 1);
        streamFromFile(sourceEndpoint2, FILE_2_ROUTE, ManifestHeader.FILE_2, SECONDARY_FOR_FILE_2_ROUTE, 2);
    }


    private void streamFromFile(StreamEndpoint streamEndpoint, String routeId, ManifestHeader header, String secondaryFileRoute, Integer startupOrder) {
        from(streamEndpoint).routeId(routeId)
            .transform().method(manifestTransformer)
            .setHeader(ManifestUtil.MANIFEST_HEADER, constant(header))
            .to(FileComparisionRouter.COMPARE_ROUTE).to(secondaryFileRoute)
            .setStartupOrder(startupOrder);
    }

    private StreamEndpoint getStreamSource(String absolutePathToFile) {
        String streamPath = String.format("stream:file?fileName=%s", absolutePathToFile);
        StreamEndpoint streamEndpoint = this.endpoint(streamPath, StreamEndpoint.class);
        streamEndpoint.setFileWatcher(fileWatcher);
        streamEndpoint.setRetry(retry);
        streamEndpoint.setScanStream(scanStream);
        streamEndpoint.setScanStreamDelay(scanStreamDelay);
        streamEndpoint.setGroupLines(groupLines);
        streamEndpoint.setGroupStrategy(linesToStreamGroupStrategy);
        return streamEndpoint;
    }
}
