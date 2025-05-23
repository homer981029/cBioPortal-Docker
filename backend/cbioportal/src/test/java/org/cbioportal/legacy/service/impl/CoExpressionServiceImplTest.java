package org.cbioportal.legacy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.cbioportal.legacy.model.CoExpression;
import org.cbioportal.legacy.model.EntityType;
import org.cbioportal.legacy.model.Gene;
import org.cbioportal.legacy.model.GeneMolecularData;
import org.cbioportal.legacy.model.Geneset;
import org.cbioportal.legacy.model.GenesetMolecularData;
import org.cbioportal.legacy.model.MolecularProfile;
import org.cbioportal.legacy.persistence.SampleListRepository;
import org.cbioportal.legacy.service.GeneService;
import org.cbioportal.legacy.service.GenesetDataService;
import org.cbioportal.legacy.service.GenesetService;
import org.cbioportal.legacy.service.MolecularDataService;
import org.cbioportal.legacy.service.MolecularProfileService;
import org.cbioportal.legacy.service.util.CoExpressionAsyncMethods;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CoExpressionServiceImplTest extends BaseServiceImplTest {

  private static final double THRESHOLD = 0.3;

  @InjectMocks private CoExpressionServiceImpl coExpressionService;

  @Mock private CoExpressionAsyncMethods asyncMethods;
  @Mock private MolecularDataService molecularDataService;
  @Mock private GenesetDataService genesetDataService;
  @Mock private GeneService geneService;
  @Mock private GenesetService genesetService;
  @Mock private MolecularProfileService molecularProfileService;
  @Mock private SampleListRepository sampleListRepository;

  @Test
  public void getGeneCorrelationForQueriedGene() throws Exception {

    List<GeneMolecularData> molecularDataList = createGeneMolecularData();
    Mockito.when(
            molecularDataService.getMolecularData(
                MOLECULAR_PROFILE_ID_A, SAMPLE_LIST_ID, null, "SUMMARY"))
        .thenReturn(molecularDataList);
    Mockito.when(
            molecularDataService.getMolecularData(
                MOLECULAR_PROFILE_ID_B, SAMPLE_LIST_ID, null, "SUMMARY"))
        .thenReturn(molecularDataList);

    List<Gene> genes = createGenes();

    Mockito.when(geneService.getGene("2")).thenReturn(genes.get(0));

    Mockito.when(geneService.getGene("3")).thenReturn(genes.get(1));

    Mockito.when(geneService.getGene("4")).thenReturn(genes.get(2));

    MolecularProfile geneMolecularProfile = createGeneMolecularProfile();

    Mockito.when(molecularProfileService.getMolecularProfile(MOLECULAR_PROFILE_ID_A))
        .thenReturn(geneMolecularProfile);
    Mockito.when(molecularProfileService.getMolecularProfile(MOLECULAR_PROFILE_ID_B))
        .thenReturn(geneMolecularProfile);

    List<List<String>> allValuesA = createAllValuesA();
    List<String> valuesB = createValuesB();
    List<CompletableFuture<CoExpression>> coExpressions = createCoExpressions();

    Mockito.when(asyncMethods.computeCoExpression("2", allValuesA.get(0), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(0));
    Mockito.when(asyncMethods.computeCoExpression("3", allValuesA.get(1), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(1));
    Mockito.when(asyncMethods.computeCoExpression("4", allValuesA.get(2), valuesB, THRESHOLD))
        .thenReturn(CompletableFuture.supplyAsync(() -> null));

    List<CoExpression> result =
        coExpressionService.getCoExpressions(
            "1",
            EntityType.GENE,
            SAMPLE_LIST_ID,
            MOLECULAR_PROFILE_ID_A,
            MOLECULAR_PROFILE_ID_B,
            THRESHOLD);

    Assert.assertEquals(2, result.size());
    CoExpression coExpression1 = result.get(0);
    Assert.assertEquals("2", coExpression1.getGeneticEntityId());
    Assert.assertEquals(new BigDecimal("0.5"), coExpression1.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.6666666666666667"), coExpression1.getpValue());
    CoExpression coExpression2 = result.get(1);
    Assert.assertEquals("3", coExpression2.getGeneticEntityId());
    Assert.assertEquals(
        new BigDecimal("0.8660254037844386"), coExpression2.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.3333333333333333"), coExpression2.getpValue());
  }

  @Test
  public void fetchGeneCoExpressions() throws Exception {

    List<GeneMolecularData> molecularDataList = createGeneMolecularData();
    Mockito.when(
            molecularDataService.fetchMolecularData(
                MOLECULAR_PROFILE_ID_A, Arrays.asList(SAMPLE_ID1, SAMPLE_ID2), null, "SUMMARY"))
        .thenReturn(molecularDataList);
    Mockito.when(
            molecularDataService.fetchMolecularData(
                MOLECULAR_PROFILE_ID_B, Arrays.asList(SAMPLE_ID1, SAMPLE_ID2), null, "SUMMARY"))
        .thenReturn(molecularDataList);

    List<Gene> genes = createGenes();

    Mockito.when(geneService.getGene("2")).thenReturn(genes.get(0));

    Mockito.when(geneService.getGene("3")).thenReturn(genes.get(1));

    Mockito.when(geneService.getGene("4")).thenReturn(genes.get(2));

    MolecularProfile geneMolecularProfile = createGeneMolecularProfile();

    Mockito.when(molecularProfileService.getMolecularProfile(MOLECULAR_PROFILE_ID_A))
        .thenReturn(geneMolecularProfile);
    Mockito.when(molecularProfileService.getMolecularProfile(MOLECULAR_PROFILE_ID_B))
        .thenReturn(geneMolecularProfile);

    List<List<String>> allValuesA = createAllValuesA();
    List<String> valuesB = createValuesB();
    List<CompletableFuture<CoExpression>> coExpressions = createCoExpressions();

    Mockito.when(asyncMethods.computeCoExpression("2", allValuesA.get(0), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(0));
    Mockito.when(asyncMethods.computeCoExpression("3", allValuesA.get(1), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(1));
    Mockito.when(asyncMethods.computeCoExpression("4", allValuesA.get(2), valuesB, THRESHOLD))
        .thenReturn(CompletableFuture.supplyAsync(() -> null));

    List<CoExpression> result =
        coExpressionService.fetchCoExpressions(
            "1",
            EntityType.GENE,
            Arrays.asList(SAMPLE_ID1, SAMPLE_ID2),
            MOLECULAR_PROFILE_ID_A,
            MOLECULAR_PROFILE_ID_B,
            THRESHOLD);

    Assert.assertEquals(2, result.size());
    CoExpression coExpression1 = result.get(0);
    Assert.assertEquals("2", coExpression1.getGeneticEntityId());
    Assert.assertEquals(new BigDecimal("0.5"), coExpression1.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.6666666666666667"), coExpression1.getpValue());
    CoExpression coExpression2 = result.get(1);
    Assert.assertEquals("3", coExpression2.getGeneticEntityId());
    Assert.assertEquals(
        new BigDecimal("0.8660254037844386"), coExpression2.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.3333333333333333"), coExpression2.getpValue());
  }

  @Test
  public void getGenesetCoExpressions() throws Exception {

    List<GenesetMolecularData> molecularDataList = createGenesetMolecularData();
    Mockito.when(
            genesetDataService.fetchGenesetData("profile_id_gsva_scores_a", SAMPLE_LIST_ID, null))
        .thenReturn(molecularDataList);
    Mockito.when(
            genesetDataService.fetchGenesetData("profile_id_gsva_scores_b", SAMPLE_LIST_ID, null))
        .thenReturn(molecularDataList);

    List<Geneset> genesets = createGenesets();

    Mockito.when(genesetService.getGeneset("BIOCARTA_ASBCELL_PATHWAY")).thenReturn(genesets.get(0));

    Mockito.when(genesetService.getGeneset("KEGG_DNA_REPLICATION")).thenReturn(genesets.get(1));

    Mockito.when(genesetService.getGeneset("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE"))
        .thenReturn(genesets.get(2));

    MolecularProfile genesetMolecularProfile = createGenesetMolecularProfile();

    Mockito.when(molecularProfileService.getMolecularProfile("profile_id_gsva_scores_a"))
        .thenReturn(genesetMolecularProfile);
    Mockito.when(molecularProfileService.getMolecularProfile("profile_id_gsva_scores_b"))
        .thenReturn(genesetMolecularProfile);

    List<List<String>> allValuesA = createAllValuesA();
    List<String> valuesB = createValuesB();
    List<CompletableFuture<CoExpression>> coExpressions = createCoExpressions();

    Mockito.when(
            asyncMethods.computeCoExpression(
                "BIOCARTA_ASBCELL_PATHWAY", allValuesA.get(0), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(2));
    Mockito.when(
            asyncMethods.computeCoExpression(
                "KEGG_DNA_REPLICATION", allValuesA.get(1), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(3));
    Mockito.when(
            asyncMethods.computeCoExpression(
                "REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE",
                allValuesA.get(2),
                valuesB,
                THRESHOLD))
        .thenReturn(CompletableFuture.supplyAsync(() -> null));

    List<CoExpression> result =
        coExpressionService.getCoExpressions(
            "GENESET_ID_TEST",
            EntityType.GENESET,
            SAMPLE_LIST_ID,
            "profile_id_gsva_scores_a",
            "profile_id_gsva_scores_b",
            THRESHOLD);

    Assert.assertEquals(2, result.size());
    CoExpression coExpression1 = result.get(0);
    Assert.assertEquals("KEGG_DNA_REPLICATION", coExpression1.getGeneticEntityId());
    Assert.assertEquals(
        new BigDecimal("0.8660254037844386"), coExpression1.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.3333333333333333"), coExpression1.getpValue());
    CoExpression coExpression2 = result.get(1);
    Assert.assertEquals("BIOCARTA_ASBCELL_PATHWAY", coExpression2.getGeneticEntityId());
    Assert.assertEquals(new BigDecimal("0.5"), coExpression2.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.6666666666666667"), coExpression2.getpValue());
  }

  @Test
  public void fetchGenesetCoExpressions() throws Exception {

    List<GenesetMolecularData> molecularDataList = createGenesetMolecularData();
    Mockito.when(
            genesetDataService.fetchGenesetData(
                "profile_id_gsva_scores_a", Arrays.asList(SAMPLE_ID1, SAMPLE_ID2), null))
        .thenReturn(molecularDataList);
    Mockito.when(
            genesetDataService.fetchGenesetData(
                "profile_id_gsva_scores_b", Arrays.asList(SAMPLE_ID1, SAMPLE_ID2), null))
        .thenReturn(molecularDataList);

    List<Geneset> genesets = createGenesets();

    Mockito.when(genesetService.getGeneset("BIOCARTA_ASBCELL_PATHWAY")).thenReturn(genesets.get(0));

    Mockito.when(genesetService.getGeneset("KEGG_DNA_REPLICATION")).thenReturn(genesets.get(1));

    Mockito.when(genesetService.getGeneset("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE"))
        .thenReturn(genesets.get(2));

    Mockito.when(genesetService.getGeneset("BIOCARTA_ASBCELL_PATHWAY")).thenReturn(genesets.get(0));

    MolecularProfile genesetMolecularProfile = createGenesetMolecularProfile();

    Mockito.when(molecularProfileService.getMolecularProfile("profile_id_gsva_scores_b"))
        .thenReturn(genesetMolecularProfile);
    Mockito.when(
            genesetDataService.fetchGenesetData(
                "profile_id_gsva_scores_b", Arrays.asList(SAMPLE_ID1, SAMPLE_ID2), null))
        .thenReturn(molecularDataList);

    List<List<String>> allValuesA = createAllValuesA();
    List<String> valuesB = createValuesB();
    List<CompletableFuture<CoExpression>> coExpressions = createCoExpressions();

    Mockito.when(
            asyncMethods.computeCoExpression(
                "BIOCARTA_ASBCELL_PATHWAY", allValuesA.get(0), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(2));
    Mockito.when(
            asyncMethods.computeCoExpression(
                "KEGG_DNA_REPLICATION", allValuesA.get(1), valuesB, THRESHOLD))
        .thenReturn(coExpressions.get(3));
    Mockito.when(
            asyncMethods.computeCoExpression(
                "REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE",
                allValuesA.get(2),
                valuesB,
                THRESHOLD))
        .thenReturn(CompletableFuture.supplyAsync(() -> null));

    List<CoExpression> result =
        coExpressionService.fetchCoExpressions(
            "GENESET_ID_TEST",
            EntityType.GENESET,
            Arrays.asList(SAMPLE_ID1, SAMPLE_ID2),
            "profile_id_gsva_scores_a",
            "profile_id_gsva_scores_b",
            THRESHOLD);

    Assert.assertEquals(2, result.size());
    CoExpression coExpression1 = result.get(0);
    Assert.assertEquals("KEGG_DNA_REPLICATION", coExpression1.getGeneticEntityId());
    Assert.assertEquals(
        new BigDecimal("0.8660254037844386"), coExpression1.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.3333333333333333"), coExpression1.getpValue());
    CoExpression coExpression2 = result.get(1);
    Assert.assertEquals("BIOCARTA_ASBCELL_PATHWAY", coExpression2.getGeneticEntityId());
    Assert.assertEquals(new BigDecimal("0.5"), coExpression2.getSpearmansCorrelation());
    Assert.assertEquals(new BigDecimal("0.6666666666666667"), coExpression2.getpValue());
  }

  private List<GeneMolecularData> createGeneMolecularData() {
    List<GeneMolecularData> molecularDataList = new ArrayList<>();
    GeneMolecularData geneMolecularData1 = new GeneMolecularData();
    geneMolecularData1.setEntrezGeneId(ENTREZ_GENE_ID_1);
    geneMolecularData1.setValue("2.1");
    geneMolecularData1.setSampleId("sample_id1");
    molecularDataList.add(geneMolecularData1);
    GeneMolecularData geneMolecularData2 = new GeneMolecularData();
    geneMolecularData2.setEntrezGeneId(ENTREZ_GENE_ID_1);
    geneMolecularData2.setValue("3");
    geneMolecularData2.setSampleId("sample_id2");
    molecularDataList.add(geneMolecularData2);
    GeneMolecularData geneMolecularData3 = new GeneMolecularData();
    geneMolecularData3.setEntrezGeneId(ENTREZ_GENE_ID_1);
    geneMolecularData3.setValue("3");
    geneMolecularData3.setSampleId("sample_id3");
    molecularDataList.add(geneMolecularData3);
    GeneMolecularData geneMolecularData4 = new GeneMolecularData();
    geneMolecularData4.setEntrezGeneId(ENTREZ_GENE_ID_2);
    geneMolecularData4.setValue("2");
    geneMolecularData4.setSampleId("sample_id1");
    molecularDataList.add(geneMolecularData4);
    GeneMolecularData geneMolecularData5 = new GeneMolecularData();
    geneMolecularData5.setEntrezGeneId(ENTREZ_GENE_ID_2);
    geneMolecularData5.setValue("3");
    geneMolecularData5.setSampleId("sample_id2");
    molecularDataList.add(geneMolecularData5);
    GeneMolecularData geneMolecularData6 = new GeneMolecularData();
    geneMolecularData6.setEntrezGeneId(ENTREZ_GENE_ID_2);
    geneMolecularData6.setValue("2");
    geneMolecularData6.setSampleId("sample_id3");
    molecularDataList.add(geneMolecularData6);
    GeneMolecularData geneMolecularData7 = new GeneMolecularData();
    geneMolecularData7.setEntrezGeneId(ENTREZ_GENE_ID_3);
    geneMolecularData7.setValue("1.1");
    geneMolecularData7.setSampleId("sample_id1");
    molecularDataList.add(geneMolecularData7);
    GeneMolecularData geneMolecularData8 = new GeneMolecularData();
    geneMolecularData8.setEntrezGeneId(ENTREZ_GENE_ID_3);
    geneMolecularData8.setValue("5");
    geneMolecularData8.setSampleId("sample_id2");
    molecularDataList.add(geneMolecularData8);
    GeneMolecularData geneMolecularData9 = new GeneMolecularData();
    geneMolecularData9.setEntrezGeneId(ENTREZ_GENE_ID_3);
    geneMolecularData9.setValue("3");
    geneMolecularData9.setSampleId("sample_id3");
    molecularDataList.add(geneMolecularData9);
    GeneMolecularData geneMolecularData10 = new GeneMolecularData();
    geneMolecularData10.setEntrezGeneId(ENTREZ_GENE_ID_4);
    geneMolecularData10.setValue("1");
    geneMolecularData10.setSampleId("sample_id1");
    molecularDataList.add(geneMolecularData10);
    GeneMolecularData geneMolecularData11 = new GeneMolecularData();
    geneMolecularData11.setEntrezGeneId(ENTREZ_GENE_ID_4);
    geneMolecularData11.setValue("4");
    geneMolecularData11.setSampleId("sample_id2");
    molecularDataList.add(geneMolecularData11);
    GeneMolecularData geneMolecularData12 = new GeneMolecularData();
    geneMolecularData12.setEntrezGeneId(ENTREZ_GENE_ID_4);
    geneMolecularData12.setValue("0");
    geneMolecularData12.setSampleId("sample_id3");
    molecularDataList.add(geneMolecularData12);
    return molecularDataList;
  }

  private List<Gene> createGenes() {
    List<Gene> genes = new ArrayList<>();
    Gene gene1 = new Gene();
    gene1.setEntrezGeneId(2);
    gene1.setHugoGeneSymbol("HUGO2");
    gene1.setGeneticEntityId(GENETIC_ENTITY_ID_2);
    genes.add(gene1);
    Gene gene2 = new Gene();
    gene2.setEntrezGeneId(3);
    gene2.setHugoGeneSymbol("HUGO3");
    gene2.setGeneticEntityId(GENETIC_ENTITY_ID_3);
    genes.add(gene2);
    Gene gene3 = new Gene();
    gene3.setEntrezGeneId(4);
    gene3.setHugoGeneSymbol("HUGO4");
    gene3.setGeneticEntityId(GENETIC_ENTITY_ID_4);
    genes.add(gene3);
    return genes;
  }

  private List<GenesetMolecularData> createGenesetMolecularData() {
    List<GenesetMolecularData> molecularDataList = new ArrayList<>();
    GenesetMolecularData genesetMolecularData1 = new GenesetMolecularData();
    genesetMolecularData1.setGenesetId("GENESET_ID_TEST");
    genesetMolecularData1.setValue("2.1");
    genesetMolecularData1.setSampleId("sample_id1");
    molecularDataList.add(genesetMolecularData1);
    GenesetMolecularData genesetMolecularData2 = new GenesetMolecularData();
    genesetMolecularData2.setGenesetId("GENESET_ID_TEST");
    genesetMolecularData2.setValue("3");
    genesetMolecularData2.setSampleId("sample_id2");
    molecularDataList.add(genesetMolecularData2);
    GenesetMolecularData genesetMolecularData3 = new GenesetMolecularData();
    genesetMolecularData3.setGenesetId("GENESET_ID_TEST");
    genesetMolecularData3.setValue("3");
    genesetMolecularData3.setSampleId("sample_id3");
    molecularDataList.add(genesetMolecularData3);
    GenesetMolecularData genesetMolecularData4 = new GenesetMolecularData();
    genesetMolecularData4.setGenesetId("BIOCARTA_ASBCELL_PATHWAY");
    genesetMolecularData4.setValue("2");
    genesetMolecularData4.setSampleId("sample_id1");
    molecularDataList.add(genesetMolecularData4);
    GenesetMolecularData genesetMolecularData5 = new GenesetMolecularData();
    genesetMolecularData5.setGenesetId("BIOCARTA_ASBCELL_PATHWAY");
    genesetMolecularData5.setValue("3");
    genesetMolecularData5.setSampleId("sample_id2");
    molecularDataList.add(genesetMolecularData5);
    GenesetMolecularData genesetMolecularData6 = new GenesetMolecularData();
    genesetMolecularData6.setGenesetId("BIOCARTA_ASBCELL_PATHWAY");
    genesetMolecularData6.setValue("2");
    genesetMolecularData6.setSampleId("sample_id3");
    molecularDataList.add(genesetMolecularData6);
    GenesetMolecularData genesetMolecularData7 = new GenesetMolecularData();
    genesetMolecularData7.setGenesetId("KEGG_DNA_REPLICATION");
    genesetMolecularData7.setValue("1.1");
    genesetMolecularData7.setSampleId("sample_id1");
    molecularDataList.add(genesetMolecularData7);
    GenesetMolecularData genesetMolecularData8 = new GenesetMolecularData();
    genesetMolecularData8.setGenesetId("KEGG_DNA_REPLICATION");
    genesetMolecularData8.setValue("5");
    genesetMolecularData8.setSampleId("sample_id2");
    molecularDataList.add(genesetMolecularData8);
    GenesetMolecularData genesetMolecularData9 = new GenesetMolecularData();
    genesetMolecularData9.setGenesetId("KEGG_DNA_REPLICATION");
    genesetMolecularData9.setValue("3");
    genesetMolecularData9.setSampleId("sample_id3");
    molecularDataList.add(genesetMolecularData9);
    GenesetMolecularData genesetMolecularData10 = new GenesetMolecularData();
    genesetMolecularData10.setGenesetId("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE");
    genesetMolecularData10.setValue("1");
    genesetMolecularData10.setSampleId("sample_id1");
    molecularDataList.add(genesetMolecularData10);
    GenesetMolecularData genesetMolecularData11 = new GenesetMolecularData();
    genesetMolecularData11.setGenesetId("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE");
    genesetMolecularData11.setValue("4");
    genesetMolecularData11.setSampleId("sample_id2");
    molecularDataList.add(genesetMolecularData11);
    GenesetMolecularData genesetMolecularData12 = new GenesetMolecularData();
    genesetMolecularData12.setGenesetId("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE");
    genesetMolecularData12.setValue("0");
    genesetMolecularData12.setSampleId("sample_id3");
    molecularDataList.add(genesetMolecularData12);
    return molecularDataList;
  }

  private List<Geneset> createGenesets() {
    List<Geneset> genesets = new ArrayList<>();
    Geneset geneset1 = new Geneset();
    geneset1.setGenesetId("BIOCARTA_ASBCELL_PATHWAY");
    geneset1.setName("BIOCARTA_ASBCELL_PATHWAY");
    genesets.add(geneset1);
    Geneset geneset2 = new Geneset();
    geneset2.setGenesetId("KEGG_DNA_REPLICATION");
    geneset2.setName("KEGG_DNA_REPLICATION");
    genesets.add(geneset2);
    Geneset geneset3 = new Geneset();
    geneset3.setGenesetId("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE");
    geneset3.setName("REACTOME_DIGESTION_OF_DIETARY_CARBOHYDRATE");
    genesets.add(geneset3);
    return genesets;
  }

  private List<List<String>> createAllValuesA() {
    List<List<String>> allValuesA = new ArrayList<>();
    List<String> valuesA1 = new ArrayList<>();
    valuesA1.add("2");
    valuesA1.add("3");
    valuesA1.add("2");
    allValuesA.add(valuesA1);
    List<String> valuesA2 = new ArrayList<>();
    valuesA2.add("1.1");
    valuesA2.add("5");
    valuesA2.add("3");
    allValuesA.add(valuesA2);
    List<String> valuesA3 = new ArrayList<>();
    valuesA3.add("1");
    valuesA3.add("4");
    valuesA3.add("0");
    allValuesA.add(valuesA3);
    return allValuesA;
  }

  private List<String> createValuesB() {
    return new ArrayList<>(Arrays.asList("2.1", "3", "3"));
  }

  private List<CompletableFuture<CoExpression>> createCoExpressions() {
    List<CompletableFuture<CoExpression>> coExpressions = new ArrayList<>();
    CoExpression coExpression1 = new CoExpression();
    coExpression1.setGeneticEntityId("2");
    coExpression1.setSpearmansCorrelation(new BigDecimal("0.5"));
    coExpression1.setpValue(new BigDecimal("0.6666666666666667"));
    coExpressions.add(CompletableFuture.supplyAsync(() -> coExpression1));
    CoExpression coExpression2 = new CoExpression();
    coExpression2.setGeneticEntityId("3");
    coExpression2.setSpearmansCorrelation(new BigDecimal("0.8660254037844386"));
    coExpression2.setpValue(new BigDecimal("0.3333333333333333"));
    coExpressions.add(CompletableFuture.supplyAsync(() -> coExpression2));
    CoExpression coExpression3 = new CoExpression();
    coExpression3.setGeneticEntityId("BIOCARTA_ASBCELL_PATHWAY");
    coExpression3.setSpearmansCorrelation(new BigDecimal("0.5"));
    coExpression3.setpValue(new BigDecimal("0.6666666666666667"));
    coExpressions.add(CompletableFuture.supplyAsync(() -> coExpression3));
    CoExpression coExpression4 = new CoExpression();
    coExpression4.setGeneticEntityId("KEGG_DNA_REPLICATION");
    coExpression4.setSpearmansCorrelation(new BigDecimal("0.8660254037844386"));
    coExpression4.setpValue(new BigDecimal("0.3333333333333333"));
    coExpressions.add(CompletableFuture.supplyAsync(() -> coExpression4));
    return coExpressions;
  }

  private MolecularProfile createGeneMolecularProfile() {
    MolecularProfile geneMolecularProfile = new MolecularProfile();
    geneMolecularProfile.setMolecularAlterationType(
        MolecularProfile.MolecularAlterationType.MRNA_EXPRESSION);
    return geneMolecularProfile;
  }

  private MolecularProfile createGenesetMolecularProfile() {
    MolecularProfile genesetMolecularProfile = new MolecularProfile();
    genesetMolecularProfile.setMolecularAlterationType(
        MolecularProfile.MolecularAlterationType.GENESET_SCORE);
    return genesetMolecularProfile;
  }
}
