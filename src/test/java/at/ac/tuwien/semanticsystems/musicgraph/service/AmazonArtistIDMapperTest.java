package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.service.amazonArtistMapping.AmazonArtistIDToNameMapperThread;
import at.ac.tuwien.semanticsystems.musicgraph.service.amazonArtistMapping.AmazonArtistService;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmazonArtistIDMapperTest {

    private final String ALTERBRIDGEID = "B001T2QV7O";


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
}
