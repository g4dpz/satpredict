package com.badgersoft.satpredict.dao;

import com.badgersoft.satpredict.domain.TleSourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by davidjohnson on 25/08/2016.
 */
public interface TleSourceDao extends PagingAndSortingRepository<TleSourceEntity, Long> {

    @Query
    List<TleSourceEntity> findByEnabled(boolean enabled);

}
