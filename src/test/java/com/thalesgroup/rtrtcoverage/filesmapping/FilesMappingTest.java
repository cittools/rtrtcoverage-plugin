package com.thalesgroup.rtrtcoverage.filesmapping;

import hudson.FilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.thalesgroup.rtrtcoverage.tioreader2.TestSuiteTrace;
import com.thalesgroup.rtrtcoverage.tioreader2.TioReader2;

public class FilesMappingTest {

    @Test
    public void test_C() throws Exception {

        // Extract tio data
        List<TestSuiteTrace> traces = new ArrayList<TestSuiteTrace>();
        for (FilePath tioFile : new FilePath(new File(this.getClass().getResource("./tios").getPath())).list("*.TIO")) {
            TioReader2 tioReader = new TioReader2(tioFile.read());
            TestSuiteTrace tst = tioReader.readTio();
            tst.setName(tioFile.getName());
            traces.add(tst);
        }

        FilesMapping mapping = new FilesMapping();
        mapping.build(
                traces,
                new FilePath(new File(this.getClass().getResource("").getPath())),
                "*/**/*_aug.c",
                new FilePath(new File(this.getClass().getResource("./tios").getPath())));
        Assert.assertEquals("2b406919", mapping.get("AIRPORTMANAGER__CHECKDATABASESWAP.C").getKey());
        Assert.assertEquals("435f06c1", mapping.get("AIRPORTMANAGER__CHECKDATABASESWAP.C").getCrc());
        Assert.assertEquals(1,
                mapping.get("AIRPORTMANAGER_STATIC_GETINSTANCE.C").getAssociedTios().size());
        Assert.assertEquals("CLASS_AIRPORTMANAGER_4.TIO",
                mapping.get("AIRPORTMANAGER_STATIC_GETINSTANCE.C").getAssociedTios().get(0).getName());

    }

    @Test
    public void test_Ada() throws Exception {
        // Extract tio data
        List<TestSuiteTrace> traces = new ArrayList<TestSuiteTrace>();
        for (FilePath tioFile : new FilePath(new File(this.getClass().getResource("./ada/tios").getPath())).list("*.tio")) {
            TioReader2 tioReader = new TioReader2(tioFile.read());
            TestSuiteTrace tst = tioReader.readTio();
            tst.setName(tioFile.getName());
            traces.add(tst);
        }

        FilesMapping mapping = new FilesMapping();
        mapping.build(
                traces,
                new FilePath(new File(this.getClass().getResource("").getPath())),
                "ada/**/*_adb.adb",
                new FilePath(new File(this.getClass().getResource("./ada/tios").getPath())));
        Assert.assertNotNull(mapping.get("CODECOVERAGE.ADB"));
        Assert.assertEquals("4907780d", mapping.get("CODECOVERAGE.ADB").getKey());
        Assert.assertEquals("3c2b766a", mapping.get("CODECOVERAGE.ADB").getCrc());
        Assert.assertEquals(1,
                mapping.get("CODECOVERAGE.ADB").getAssociedTios().size());
        Assert.assertEquals("CODECOVERAGE.TIO",
                mapping.get("CODECOVERAGE.ADB").getAssociedTios().get(0).getName().toUpperCase());

    }

    @Test
    public void exportTest() throws Exception {
        // Extract tio data
        List<TestSuiteTrace> traces = new ArrayList<TestSuiteTrace>();
        for (FilePath tioFile : new FilePath(new File(this.getClass().getResource("./tios").getPath())).list("*.TIO")) {
            TioReader2 tioReader = new TioReader2(tioFile.read());
            TestSuiteTrace tst = tioReader.readTio();
            tst.setName(tioFile.getName());
            traces.add(tst);
        }

        File outputFile = new File(this.getClass().getResource("").getPath()+"/file_identities_output.xml");
        FilesMapping refMapping = new FilesMapping();
        refMapping.build(
                traces,
                new FilePath(new File(this.getClass().getResource("").getPath())),
                "*/**/*_aug.c",
                new FilePath(new File(this.getClass().getResource("./tios").getPath())));
        (new FileIdentitiesExport()).export(refMapping, outputFile);
    }

    @Test
    public void importTest() throws Exception {
        File inputFile = new File(this.getClass().getResource("").getPath()+"/file_identities_output.xml");
        FilesMapping refMapping = new FilesMapping();

        // Extract tio data
        List<TestSuiteTrace> traces = new ArrayList<TestSuiteTrace>();
        for (FilePath tioFile : new FilePath(new File(this.getClass().getResource("./tios").getPath())).list("*.TIO")) {
            TioReader2 tioReader = new TioReader2(tioFile.read());
            TestSuiteTrace tst = tioReader.readTio();
            tst.setName(tioFile.getName());
            traces.add(tst);
        }

        refMapping.build(
                traces,
                new FilePath(new File(this.getClass().getResource("").getPath())),
                "*/**/*_aug.c",
                new FilePath(new File(this.getClass().getResource("./tios").getPath())));
        (new FileIdentitiesExport()).export(refMapping, inputFile);

        FilesMapping importedMapping = new FilesMapping();
        importedMapping = (new FileIdentitiesImport()).importXml(inputFile);

        Assert.assertTrue(refMapping.equals(importedMapping));
    }
}
