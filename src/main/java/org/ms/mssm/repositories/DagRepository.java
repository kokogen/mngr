package org.ms.mssm.repositories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.ms.mssm.entities.Dag;

import java.util.List;

@JdbcRepository(dialect =  Dialect.POSTGRES)
public interface DagRepository extends CrudRepository<Dag, Long> {
    @Join("partitionTemplates")
    List<Dag> findAll();
}
