package com.badgersoft.satpredict.dao;

import com.badgersoft.satpredict.domain.TleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by davidjohnson on 25/08/2016.
 */
public interface TleDao extends PagingAndSortingRepository<TleEntity, Long> {

    @Query
    List<TleEntity> findByCatnum(Long catnum);
}
