package be.valuya.winbooks.api.extra;

import be.valuya.jbooks.model.WbBookYearFull;
import be.valuya.jbooks.model.WbDocument;
import be.valuya.jbooks.model.WbPeriod;
import be.valuya.winbooks.api.extra.config.DocumentMatchingMode;
import be.valuya.winbooks.api.extra.config.WinbooksFileConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class WinbooksDocumentsServiceTest {

    private WinbooksDocumentsService documentsService;
    private Path henriDossierPath;
    private WinbooksFileConfiguration fileConfiguration;

    @Before
    public void init() throws URISyntaxException {
        URL henriUrl = getClass().getClassLoader().getResource("HENRI");
        URI henriUri = henriUrl.toURI();
        henriDossierPath = Paths.get(henriUri);
        Assert.assertTrue(Files.exists(henriDossierPath));
        Assert.assertTrue(Files.isDirectory(henriDossierPath));

        fileConfiguration = new WinbooksFileConfiguration();
        fileConfiguration.setBasePathName("HENRI");
        fileConfiguration.setRootPath(henriDossierPath);
        fileConfiguration.setDocumentMatchingMode(DocumentMatchingMode.EAGERLY_CACHE_ALL_DOCUMENTS);
    }

    @Test
    public void testDocuments() {
        documentsService = new WinbooksDocumentsService();

        WbBookYearFull wbBookYearFull = new WbBookYearFull();
        wbBookYearFull.setIndex(2);
        wbBookYearFull.setShortName("Ex. 2020");
        wbBookYearFull.setYearBeginInt(2020);
        wbBookYearFull.setYearEndInt(2020);

        WbPeriod cloturePeriod99 = new WbPeriod();
        cloturePeriod99.setIndex(99);
        cloturePeriod99.setShortName("Cloture");
        cloturePeriod99.setWbBookYearFull(wbBookYearFull);
        wbBookYearFull.setPeriodList(List.of(cloturePeriod99));

        List<WbDocument> documents = documentsService.streamBookYearDocuments(fileConfiguration, wbBookYearFull)
                .collect(Collectors.toList());

        Assert.assertEquals(2, documents.size());


        WbDocument docNumber1 = documents.stream()
                .filter(d -> d.getDocumentNumber().equals("20200001"))
                .findFirst().orElseThrow();
        Assert.assertEquals("OPDCLO", docNumber1.getDbkCode());
        Assert.assertEquals(cloturePeriod99, docNumber1.getWbPeriod());
        String doc1Part0FileName = MessageFormat.format(docNumber1.getFileNameTemplate(), 0);
        Assert.assertEquals("OPDCLO_99_20200001_00000_00000___00000_A.pdf", doc1Part0FileName);

        WbDocument docNumber2 = documents.stream()
                .filter(d -> d.getDocumentNumber().equals("20200002"))
                .findFirst().orElseThrow();
        Assert.assertEquals("OPDCLO", docNumber2.getDbkCode());
        Assert.assertEquals(cloturePeriod99, docNumber2.getWbPeriod());
        String doc2Part0FileName = MessageFormat.format(docNumber2.getFileNameTemplate(), 0);
        Assert.assertEquals("OPDCLO_99_20200002___00000_A.pdf", doc2Part0FileName);


    }


}
