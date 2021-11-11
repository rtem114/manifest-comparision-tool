package org.manifest.manifestcomparisontool.camel.strategy;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.camel.component.stream.GroupStrategy;
import org.springframework.stereotype.Component;

@Component
public class LinesToStreamGroupStrategy implements GroupStrategy {

    @Override
    public Object groupLines(List<String> lines) {
        StringBuilder buffer = new StringBuilder();
        for(String current : lines) {
            buffer.append(current).append("\n");
        }

        return new ByteArrayInputStream(buffer.toString().getBytes());
    }
}