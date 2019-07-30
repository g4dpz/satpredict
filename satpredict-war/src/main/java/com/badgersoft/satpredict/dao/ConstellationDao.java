package com.badgersoft.satpredict.dao;

import com.badgersoft.satpredict.domain.ConstellationEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by davidjohnson on 28/08/2016.
 */
public interface ConstellationDao extends PagingAndSortingRepository<ConstellationEntity, Long> {
}
