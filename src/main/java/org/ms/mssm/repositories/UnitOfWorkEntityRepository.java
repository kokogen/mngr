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
import java.util.Set;

@JdbcRepository(dialect = Dialect.POSTGRES)
//@Join("partitions")
//@Join("requiredPartitionIds")
public interface UnitOfWorkEntityRepository extends CrudRepository<UnitOfWorkEntity, Long> {
    @Query(
            value = "delete from public.uow_partition_ver where uow_id = :uowId;",
            nativeQuery = true
    )
    public void deletePartitionVerSetByUowId(Long uowId);

    @Query(
            value = "delete from public.uow_required_partition where uow_id = :uowId;",
            nativeQuery = true
    )
    public void deletePartitionIdSetByUowId(Long uowId);

    @Query(
            value = "insert into public.uow_partition_ver(uow_id, part_ver_id) values(:uowId, :partVerId);"
            , nativeQuery = true
    )
    public void addPartitionVerEntity(Long uowId, String partVerId);

    @Query(
            value = "insert into public.uow_required_partition(uow_id, part_id) values(:uowId, :partId);"
            , nativeQuery = true
    )
    public void addPartitionIdEntity(Long uowId, String partId);

    @Query(
            value = "select p.* from public.partition_ver as p join public.uow_partition_ver as pw on pw.part_ver_id = p.part_ver_id and pw.uow_id = :uowId;"
            , nativeQuery = true
    )
    public Set<PartitionVerEntity> getPartitionVerListByUowId(Long uowId);

    @Query(
            value = "select p.* from public.partition as p join public.uow_required_partition as pw on pw.part_id = p.part_id and pw.uow_id = :uowId;"
            , nativeQuery = true
    )
    public Set<PartitionIdEntity> getPartitionIdListByUowId(Long uowId);
}
