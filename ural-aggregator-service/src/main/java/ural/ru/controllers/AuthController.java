package ural.ru.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ural.ru.services.proxy.ProxyService;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController extends AbstractCommonController {

    protected AuthController(Map<String, ProxyService> proxyServiceMap) {
        super(proxyServiceMap);
    }

    @PostMapping("/login")
    ResponseEntity<?> login(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        return processProxyRequest(body, method, request);
    }

    @PostMapping("/refresh")
    ResponseEntity<?> refresh(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        return processProxyRequest(body, method, request);
    }

    @PostMapping("/logout")
    ResponseEntity<?> logout(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        return processProxyRequest(body, method, request);
    }

    @PostMapping("/logout/all")
    ResponseEntity<?> logoutAll(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        return processProxyRequest(body, method, request);
    }

    private ResponseEntity<?> processProxyRequest(byte[] body, HttpMethod method, HttpServletRequest request) {
        var proxyService = getProxyService();
        return proxyService.processProxyRequest(body, method, request);
    }

    @Override
    protected String getProxyServiceName() {
        return "authProxyService";
    }
}
