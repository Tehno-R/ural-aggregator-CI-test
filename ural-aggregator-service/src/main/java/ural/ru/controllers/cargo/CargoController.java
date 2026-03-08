package ural.ru.controllers.cargo;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ural.ru.controllers.AbstractCommonController;
import ural.ru.services.proxy.ProxyService;

import java.util.Map;

@RestController
@RequestMapping("/cargo")
@Tag(name = "Контроллер для управления грузами")
public class CargoController extends AbstractCommonController {

    protected CargoController(Map<String, ProxyService> proxyServiceMap) {
        super(proxyServiceMap);
    }

    @PostMapping
    public ResponseEntity<?> create(byte[] body, HttpMethod method, HttpServletRequest request) {
        return sendAndReceive(body, method, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(byte[] body, HttpMethod method, HttpServletRequest request) {
        return sendAndReceive(body, method, request);
    }

    @GetMapping
    public ResponseEntity<?> getPaginatedList(byte[] body, HttpMethod method, HttpServletRequest request) {
        return sendAndReceive(body, method, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(byte[] body, HttpMethod method, HttpServletRequest request) {
        return sendAndReceive(body, method, request);
    }

    @Override
    protected String getProxyServiceName() {
        return "cargoProxyService";
    }
}
