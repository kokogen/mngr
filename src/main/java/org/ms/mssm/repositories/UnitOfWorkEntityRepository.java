package org.ms.mssm.repositories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import org.ms.mssm.entities.PartitionIdEntity;
import org.ms.mssm.entities.PartitionVerEntity;
import org.ms.mssm.entities.UnitOfWorkEntity;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
@Join("partitions")
public interface UnitOfWorkEntityRepository extends CrudRepository<UnitOfWorkEntity, Long> {
    @Query(
            value = "delete from public.uow_partition_ver where uow_id = :uowId;",
            nativeQuery = true
    )
    public void deletePartitionVerSetByUowId(Long uowId);

    @Query(
            value = "insert into public.uow_partition_ver(uow_id, part_ver_id) values(:uowId, :partVerId);"
            , nativeQuery = true
    )
    public void addPartitionVerEntity(Long uowId, String partVerId);

    @Query(
            value = "delete from public.uow_partition_ver where uow_id = :uowId and part_ver_id = :partVerId;"
            , nativeQuery = true
    )
    public void deletePartitionVerEntity(Long uowId, String partVerId);
}
