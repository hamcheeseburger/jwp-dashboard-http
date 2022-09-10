package org.apache.catalina.support;

import org.apache.catalina.exception.FileAccessException;
import org.apache.catalina.exception.NotFoundException;
import org.apache.coyote.HttpMime;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class Resource {

    private static final String BASE_PATH = "static";

    private final String target;

    public Resource(final String target) {
        this.target = target;
    }

    public String read() {
        final URL resource = getClass().getClassLoader().getResource(BASE_PATH + target);
        validateExist(resource);
        return read(new File(getUri(resource)));
    }

    private void validateExist(final URL resource) {
        if (resource == null) {
            throw new NotFoundException(target + " 자원을 찾을 수 없음");
        }
    }

    private String read(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }

    public HttpMime getContentType() {
        final URL resource = getClass().getClassLoader().getResource(BASE_PATH + target);
        validateExist(resource);
        final File file = new File(getUri(resource));
        return HttpMime.find(findOutContentType(file));
    }

    private String findOutContentType(final File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }

    private URI getUri(final URL resource) {
        try {
            return resource.toURI();
        } catch (URISyntaxException e) {
            throw new FileAccessException();
        }
    }

    public String getTarget() {
        return target;
    }
}
