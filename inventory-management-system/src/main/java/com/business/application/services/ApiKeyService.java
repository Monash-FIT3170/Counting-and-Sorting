package com.business.application.services;

import com.business.application.domain.ApiKey;
import com.business.application.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public Page<ApiKey> list(Pageable pageable, Specification<ApiKey> specification) {
        return apiKeyRepository.findAll(specification, pageable);
    }

    public ApiKey save(ApiKey apiKey) {
        return apiKeyRepository.save(apiKey);
    }

    public void delete(Long id) {
        apiKeyRepository.deleteById(id);
    }

    public ApiKey findById(Long id) {
        return apiKeyRepository.findById(id).orElse(null);
    }
}
