package clms.system.clms_backend.controller;

import clms.system.clms_backend.model.EResource;
import clms.system.clms_backend.service.EResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eresources")
@RequiredArgsConstructor
public class EResourceController {
    private final EResourceService resourceService;

    @GetMapping
    public ResponseEntity<List<EResource>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EResource> getResource(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResource(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<EResource>> searchResources(@RequestParam String keyword) {
        return ResponseEntity.ok(resourceService.searchResources(keyword));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EResource> createResource(@RequestBody EResource resource) {
        return ResponseEntity.ok(resourceService.createResource(resource));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<EResource> updateResource(@PathVariable Long id, @RequestBody EResource resource) {
        return ResponseEntity.ok(resourceService.updateResource(id, resource));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok().build();
    }
}
