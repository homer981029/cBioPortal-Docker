package org.cbioportal.legacy.persistence.mybatis;

import java.util.List;
import org.cbioportal.legacy.model.Geneset;
import org.cbioportal.legacy.model.GenesetHierarchyInfo;
import org.cbioportal.legacy.persistence.GenesetHierarchyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GenesetHierarchyMyBatisRepository implements GenesetHierarchyRepository {

  @Autowired private GenesetHierarchyMapper genesetHierarchyMapper;

  @Override
  public List<GenesetHierarchyInfo> getGenesetHierarchyParents(List<String> genesetIds) {

    return genesetHierarchyMapper.getGenesetHierarchyParents(genesetIds);
  }

  @Override
  public List<Geneset> getGenesetHierarchyGenesets(Integer nodeId) {

    return genesetHierarchyMapper.getGenesetHierarchyGenesets(nodeId);
  }

  @Override
  public List<GenesetHierarchyInfo> getGenesetHierarchySuperNodes(List<String> genesetIds) {

    return genesetHierarchyMapper.getGenesetHierarchySuperNodes(genesetIds);
  }
}
