package org.cbioportal.legacy.persistence.mybatis;

import java.util.ArrayList;
import java.util.List;
import org.cbioportal.legacy.model.GenericAssayAdditionalProperty;
import org.cbioportal.legacy.model.meta.GenericAssayMeta;
import org.cbioportal.legacy.persistence.GenericAssayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class GenericAssayMyBatisRepository implements GenericAssayRepository {

  @Autowired private GenericAssayMapper genericAssayMapper;

  private static final Logger LOG = LoggerFactory.getLogger(GenericAssayMyBatisRepository.class);

  @Override
  @Cacheable(
      cacheResolver = "staticRepositoryCacheOneResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  public List<GenericAssayMeta> getGenericAssayMeta(List<String> stableIds) {

    return genericAssayMapper.getGenericAssayMeta(stableIds);
  }

  @Override
  @Cacheable(
      cacheResolver = "staticRepositoryCacheOneResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  public List<GenericAssayAdditionalProperty> getGenericAssayAdditionalproperties(
      List<String> stableIds) {
    return genericAssayMapper.getGenericAssayAdditionalproperties(stableIds);
  }

  @Override
  @Cacheable(
      cacheResolver = "staticRepositoryCacheOneResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  public List<String> getGenericAssayStableIdsByMolecularIds(List<String> molecularProfileIds) {

    List<Integer> molecularProfileInternalIds =
        genericAssayMapper.getMolecularProfileInternalIdsByMolecularProfileIds(molecularProfileIds);
    if (molecularProfileInternalIds.size() > 0) {
      List<Integer> geneticEntityIds =
          genericAssayMapper.getGeneticEntityIdsByMolecularProfileInternalIds(
              molecularProfileInternalIds);
      if (geneticEntityIds.size() > 0) {
        // return result
        return genericAssayMapper.getGenericAssayStableIdsByGeneticEntityIds(geneticEntityIds);
      } else {
        LOG.error(
            "Returned an Empty list. Cannot find accociate entity ids for molecular profiles: "
                + molecularProfileIds.toString());
      }
    } else {
      LOG.error(
          "Returned an Empty list. Cannot find internal ids for molecular profiles: "
              + molecularProfileIds.toString());
    }
    // log error and return empty list if something went wrong
    return new ArrayList<String>();
  }
}
