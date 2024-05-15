package com.business.application.services;

import com.business.application.domain.SampleAddress;
import com.business.application.repository.SampleAddressRepository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SampleAddressService {

    private final SampleAddressRepository repository;

    public SampleAddressService(SampleAddressRepository repository) {
        this.repository = repository;
    }

    public Optional<SampleAddress> get(Long id) {
        return repository.findById(id);
    }

    public SampleAddress update(SampleAddress entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SampleAddress> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SampleAddress> list(Pageable pageable, Specification<SampleAddress> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
