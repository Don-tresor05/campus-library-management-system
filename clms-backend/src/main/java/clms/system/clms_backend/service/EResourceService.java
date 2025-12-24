package clms.system.clms_backend.service;

import clms.system.clms_backend.model.EResource;
import clms.system.clms_backend.repository.EResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EResourceService {
    private final EResourceRepository resourceRepository;

    public List<EResource> getAllResources() {
        return resourceRepository.findAll();
    }

    public EResource getResource(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }

    @Transactional
    public EResource createResource(EResource resource) {
        return resourceRepository.save(resource);
    }

    @Transactional
    public EResource updateResource(Long id, EResource details) {
        EResource resource = getResource(id);

        resource.setTitle(details.getTitle());
        resource.setDescription(details.getDescription());
        resource.setAccessUrl(details.getAccessUrl());
        resource.setFormat(details.getFormat());

        return resourceRepository.save(resource);
    }

    @Transactional
    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }

    public List<EResource> searchResources(String keyword) {
        return resourceRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
