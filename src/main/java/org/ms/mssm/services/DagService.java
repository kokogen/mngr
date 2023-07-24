package org.ms.mssm.services;

import jakarta.inject.Singleton;
import org.ms.mssm.entities.Dag;
import org.ms.mssm.repositories.DagRepository;
import org.ms.mssm.repositories.PartitionTemplateRepository;

import java.util.List;

@Singleton
public class DagService {
    private final DagRepository dagRepository;
    private final PartitionTemplateRepository partitionTemplateRepository;

    public DagService(DagRepository dagRepository, PartitionTemplateRepository partitionTemplateRepository) {
        this.dagRepository = dagRepository;
        this.partitionTemplateRepository = partitionTemplateRepository;
    }

    public List<Dag> readAll(){
        return dagRepository.findAll();
    }
}
