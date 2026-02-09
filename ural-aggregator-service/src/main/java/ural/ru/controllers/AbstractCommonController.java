package ural.ru.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ural.ru.services.proxy.ProxyService;

import java.util.Map;

@RestController
public abstract class AbstractCommonController {

    protected final ProxyService proxyService;

    protected AbstractCommonController(Map<String, ProxyService> proxyServiceMap) {
        String name = getProxyServiceName();
        proxyService = proxyServiceMap.get(name);
    }

    protected ResponseEntity<?> sendAndReceive(
            byte[] body,
            HttpMethod httpMethod,
            HttpServletRequest httpServletRequest
    ) {
        return proxyService.processProxyRequest(body, httpMethod, httpServletRequest);
    }

    protected abstract String getProxyServiceName();

}
