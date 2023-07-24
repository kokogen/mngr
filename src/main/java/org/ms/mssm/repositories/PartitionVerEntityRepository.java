package org.ms.mssm.repositories;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.entities.PartitionIdEntity;

@JdbcRepository(dialect =  Dialect.POSTGRES)
public interface PartitionVerEntityRepository extends CrudRepository<PartitionVerEntity, String> {
}
