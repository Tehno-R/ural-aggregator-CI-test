package ural.ru.services.proxy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ural.ru.properties.proxy.ProxyProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class ProxyService {

    protected final RestTemplate restTemplate;
    protected final ProxyProperty proxyProperty;

    public abstract ResponseEntity<?> processProxyRequest(byte[] body, HttpMethod method, HttpServletRequest request);

    protected ResponseEntity<?> sendRequest(URI uri, byte[] body, HttpMethod method, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }

        headers.remove(HttpHeaders.ACCEPT_ENCODING);
        headers.remove(HttpHeaders.HOST);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<?> serverResponse = restTemplate.exchange(uri, method, httpEntity, byte[].class);

            HttpHeaders responseHeaders = new HttpHeaders();
            Optional<HttpHeaders> responseServerHeaders = Optional.ofNullable(serverResponse.getHeaders());

            responseServerHeaders.map(headersResponse -> headersResponse.get(HttpHeaders.CONTENT_TYPE))
                    .ifPresent(content -> responseHeaders.put(HttpHeaders.CONTENT_TYPE, content));
            responseServerHeaders.map(headersResponse -> headersResponse.get(HttpHeaders.CONTENT_DISPOSITION))
                    .ifPresent(content -> responseHeaders.put(HttpHeaders.CONTENT_DISPOSITION, content));

            return new ResponseEntity<>(serverResponse.getBody(), responseHeaders, serverResponse.getStatusCode());

        } catch (HttpStatusCodeException e) {
            if (!e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.error("{} error: {}", this.getClass().getName(), e.getMessage(), e);
            }

            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    protected URI buildURI(HttpServletRequest request) {
        try {
            URI base = new URI(proxyProperty.getUrl());
            return UriComponentsBuilder.fromUri(base)
                    .path(request.getRequestURI().substring(request.getContextPath().length()))
                    .query(request.getQueryString())
                    .build(true)
                    .toUri();
        } catch (URISyntaxException e) {
            log.error("Error with create uri for proxy on cargo-service");
            throw new RuntimeException(e);
        }
    }

}
