package com.metaverse.gamming.repository;

import com.metaverse.gamming.model.FileMeta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetaRepository extends CrudRepository<FileMeta, Integer> {
}
