package org.ms.mssm.repositories;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.ms.mssm.entities.TaskEntity;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TaskEntityRepository extends CrudRepository<TaskEntity, Long> {

}
