package org.manifest.manifestcomparisontool.camel;

import java.util.jar.Manifest;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.apache.camel.test.spring.junit5.UseAdviceWith;

import org.junit.jupiter.api.Test;
import org.manifest.manifestcomparisontool.util.ManifestHeader;
import org.manifest.manifestcomparisontool.util.ManifestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;


/**
 *
 * Verifies if file stream reaches secondary file fetch route
 */
@CamelSpringBootTest
@SpringBootTest
@MockEndpointsAndSkip("direct:loadFirstFile|direct:loadSecondFile|direct:compare")
@UseAdviceWith
public class FileStreamingRouterTest {

    @Autowired CamelContext camelContext;

    @EndpointInject("mock:direct:compare")
    MockEndpoint compareEndpoint;

    @EndpointInject("mock:direct:loadFirstFile")
    MockEndpoint loadFirstFileEndpoint;

    @EndpointInject("mock:direct:loadSecondFile")
    MockEndpoint loadSecondFileEndpoint;

    @Test
    public void testFileRoutingStream() throws Exception {
        camelContext.start();
        ManifestAssertionProcessor manifestAssertionProcessor = new ManifestAssertionProcessor();

        loadFirstFileEndpoint.expectedMessageCount(1);
        loadSecondFileEndpoint.expectedMessageCount(1);

        //second file is loaded when first file is triggered, so expected header is FILE_1
        loadSecondFileEndpoint.expectedHeaderReceived(ManifestUtil.MANIFEST_HEADER, ManifestHeader.FILE_1);
        //first file is loaded when second file is triggered, so expected header is FILE_2
        loadFirstFileEndpoint.expectedHeaderReceived(ManifestUtil.MANIFEST_HEADER, ManifestHeader.FILE_2);

        loadFirstFileEndpoint.whenAnyExchangeReceived(manifestAssertionProcessor);
        loadFirstFileEndpoint.whenAnyExchangeReceived(manifestAssertionProcessor);

        compareEndpoint.expectedMessageCount(2);
        compareEndpoint.whenAnyExchangeReceived(manifestAssertionProcessor);

        loadFirstFileEndpoint.assertIsSatisfied();
        loadSecondFileEndpoint.assertIsSatisfied();
        compareEndpoint.assertIsSatisfied();

    }

    class ManifestAssertionProcessor implements Processor {
        @Override
        public void process(Exchange exchange) {
            Manifest manifest = exchange.getIn().getBody(Manifest.class);
            assertThat(manifest.getMainAttributes().size(), is(10));
        }
    }
}
