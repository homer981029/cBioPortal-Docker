package org.cbioportal.legacy.persistence.cachemaputil;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cbioportal.legacy.model.CancerStudy;
import org.cbioportal.legacy.model.MolecularProfile;
import org.cbioportal.legacy.model.SampleList;
import org.cbioportal.legacy.persistence.GenericAssayRepository;
import org.cbioportal.legacy.persistence.MolecularProfileRepository;
import org.cbioportal.legacy.persistence.SampleListRepository;
import org.cbioportal.legacy.persistence.StudyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheMapBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(CacheMapBuilder.class);

  @Autowired private StudyRepository studyRepository;

  @Autowired private MolecularProfileRepository molecularProfileRepository;

  @Autowired private SampleListRepository sampleListRepository;

  @Autowired private GenericAssayRepository genericAssayRepository;

  private static final int REPOSITORY_RESULT_LIMIT =
      Integer.MAX_VALUE; // retrieve all entries (no limit to return size)
  private static final int REPOSITORY_RESULT_OFFSET = 0; // retrieve all entries (do not skip any)

  public Map<String, MolecularProfile> buildMolecularProfileMap() {
    Map<String, MolecularProfile> molecularProfileMap =
        molecularProfileRepository
            .getAllMolecularProfiles(
                "SUMMARY", REPOSITORY_RESULT_LIMIT, REPOSITORY_RESULT_OFFSET, null, "ASC")
            .stream()
            .collect(Collectors.toMap(MolecularProfile::getStableId, Function.identity()));
    LOG.debug("  molecular profile map size: " + molecularProfileMap.size());
    return molecularProfileMap;
  }

  public Map<String, SampleList> buildSampleListMap() {
    Map<String, SampleList> sampleListMap =
        sampleListRepository
            .getAllSampleLists(
                "SUMMARY", REPOSITORY_RESULT_LIMIT, REPOSITORY_RESULT_OFFSET, null, "ASC")
            .stream()
            .collect(Collectors.toMap(SampleList::getStableId, Function.identity()));
    LOG.debug("  sample list map size: " + sampleListMap.size());
    return sampleListMap;
  }

  public Map<String, CancerStudy> buildCancerStudyMap() {
    Map<String, CancerStudy> cancerStudyMap =
        studyRepository
            .getAllStudies(
                null, "SUMMARY", REPOSITORY_RESULT_LIMIT, REPOSITORY_RESULT_OFFSET, null, "ASC")
            .stream()
            .collect(Collectors.toMap(CancerStudy::getCancerStudyIdentifier, Function.identity()));
    LOG.debug("  cancer study map size: " + cancerStudyMap.size());
    return cancerStudyMap;
  }
}
