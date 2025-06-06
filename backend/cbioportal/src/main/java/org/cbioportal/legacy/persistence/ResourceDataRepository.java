package org.cbioportal.legacy.persistence;

import java.util.List;
import org.cbioportal.legacy.model.ResourceData;
import org.springframework.cache.annotation.Cacheable;

public interface ResourceDataRepository {

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<ResourceData> getAllResourceDataOfSampleInStudy(
      String studyId,
      String sampleId,
      String resourceId,
      String projection,
      Integer pageSize,
      Integer pageNumber,
      String sortBy,
      String direction);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<ResourceData> getAllResourceDataOfPatientInStudy(
      String studyId,
      String patientId,
      String resourceId,
      String projection,
      Integer pageSize,
      Integer pageNumber,
      String sortBy,
      String direction);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<ResourceData> getAllResourceDataForStudy(
      String studyId,
      String resourceId,
      String projection,
      Integer pageSize,
      Integer pageNumber,
      String sortBy,
      String direction);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<ResourceData> getResourceDataForAllPatientsInStudy(
      String studyId,
      String resourceId,
      String projection,
      Integer pageSize,
      Integer pageNumber,
      String sortBy,
      String direction);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<ResourceData> getResourceDataForAllSamplesInStudy(
      String studyId,
      String resourceId,
      String projection,
      Integer pageSize,
      Integer pageNumber,
      String sortBy,
      String direction);
}
