package org.manifest.manifestcomparisontool.camel.processor;


import java.util.List;
import java.util.jar.Manifest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.manifest.manifestcomparisontool.util.ManifestUtil;
import org.springframework.stereotype.Component;


@Component
public class ComparisionProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        List<Exchange> bodyList = exchange.getIn().getBody(List.class);
        Manifest man1 = bodyList.get(0).getIn().getBody(Manifest.class);
        Manifest man2 = bodyList.get(1).getIn().getBody(Manifest.class);

        //MANIFEST_HEADER identifies what file we compare with. FILE
        //FILE_1 means that we compare  FILE_1 with FILE_2.
        //FILE_2 means that we compare  FILE_2 with FILE_1.
        Object manifestFileOutput = bodyList.get(0).getIn().getHeader(ManifestUtil.MANIFEST_HEADER);

        //calculate the difference
        Manifest diffManifest = ManifestUtil.differentiate(man1, man2);

        exchange.getIn().setBody(ManifestUtil.toString(diffManifest));
        //set manifest file output header, so the differences will be stored in corresponding file
        exchange.getIn().setHeader(ManifestUtil.MANIFEST_HEADER, manifestFileOutput);

    }
}
