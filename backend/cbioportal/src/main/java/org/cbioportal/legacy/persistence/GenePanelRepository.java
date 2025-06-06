package org.cbioportal.legacy.persistence;

import java.util.List;
import java.util.Set;
import org.cbioportal.legacy.model.GenePanel;
import org.cbioportal.legacy.model.GenePanelData;
import org.cbioportal.legacy.model.GenePanelToGene;
import org.cbioportal.legacy.model.MolecularProfileCaseIdentifier;
import org.cbioportal.legacy.model.meta.BaseMeta;
import org.springframework.cache.annotation.Cacheable;

public interface GenePanelRepository {

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanel> getAllGenePanels(
      String projection, Integer pageSize, Integer pageNumber, String sortBy, String direction);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  BaseMeta getMetaGenePanels();

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  GenePanel getGenePanel(String genePanelId);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanel> fetchGenePanels(List<String> genePanelIds, String projection);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanelData> getGenePanelDataBySampleListId(
      String molecularProfileId, String sampleListId);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanelData> fetchGenePanelData(String molecularProfileId, List<String> sampleIds);

  List<GenePanelData> fetchGenePanelDataByMolecularProfileIds(Set<String> molecularProfileIds);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanelData> fetchGenePanelDataByMolecularProfileId(String molecularProfileId);

  List<GenePanelData> fetchGenePanelDataInMultipleMolecularProfiles(
      List<MolecularProfileCaseIdentifier> molecularProfileSampleIdentifiers);

  List<GenePanelData> fetchGenePanelDataInMultipleMolecularProfilesByPatientIds(
      List<MolecularProfileCaseIdentifier> molecularProfileSampleIdentifiers);

  @Cacheable(
      cacheResolver = "generalRepositoryCacheResolver",
      condition = "@cacheEnabledConfig.getEnabled()")
  List<GenePanelToGene> getGenesOfPanels(List<String> genePanelIds);
}
