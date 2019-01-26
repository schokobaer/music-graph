package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.eclipse.rdf4j.model.Model;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.AssertTrue;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmazonServiceTest {

    private final String ALTERBRIDGEID = "B001T2QV7O";

    @Autowired
    private AmazonService service;


    @Test
    public void testResolveArtistIdAlterBridge() {

        Set<String> artistIds = new HashSet<>();
        artistIds.add(ALTERBRIDGEID);

        Map<String, String> mapping  = AmazonArtistIDToNameMapperThread.getArtisIdNameMap(artistIds);

        Assert.assertTrue(mapping.get(ALTERBRIDGEID).equals("Alter Bridge"));
    }

    @Test
    public void testResolveArtistIDNotExistantShouldReturnEmptyMap() {
        Set<String> artistIds = new HashSet<>();
        artistIds.add("notFound");

        Map<String, String> mapping  = AmazonArtistIDToNameMapperThread.getArtisIdNameMap(artistIds);

        Assert.assertTrue(mapping.isEmpty());
    }

    @Test
    public void testMappingWithTestSetShouldCreate7Subjects() {
        AmazonService service = new AmazonService();
        try {
            Model model = service.getAmazonModel("resources/data/DSAR_Dominik_Scheffknecht_Mein_Song_Verlauf_TestSet.csv");
            Assert.assertTrue(model.subjects().size() == 7);
        } catch (FileNotFoundException e) {
            Assert.assertTrue(false);
        }
    }
}
