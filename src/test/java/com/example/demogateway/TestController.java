package com.example.demogateway;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> postFormData(@RequestBody Mono<MultiValueMap<String, Part>> parts) {
        return parts.flux()
                    .flatMap(map -> Flux.fromIterable(map.values()))
                    .flatMap(Flux::fromIterable)
                    .filter(part -> part instanceof FilePart)
                    .reduce(new HashMap<String, Object>(), (files, part) -> {
                        MediaType contentType = part.headers().getContentType();
                        long contentLength = part.headers().getContentLength();
                        files.put(part.name(), "data:" + contentType + ";base64," + contentLength); //TODO: get part data
                        return files;
                    })
                    .map(files -> Collections.singletonMap("files", files));
    }

}
