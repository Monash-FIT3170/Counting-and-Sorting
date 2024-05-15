package com.business.application.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.business.application.domain.SampleAddress;

public interface SampleAddressRepository
        extends
            JpaRepository<SampleAddress, Long>,
            JpaSpecificationExecutor<SampleAddress> {

}
