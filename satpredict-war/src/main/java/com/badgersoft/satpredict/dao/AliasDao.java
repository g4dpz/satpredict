package com.badgersoft.satpredict.dao;

import com.badgersoft.satpredict.domain.AliasEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by davidjohnson on 28/08/2016.
 */
public interface AliasDao extends PagingAndSortingRepository<AliasEntity, Long> {
}
