package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class WikidataServiceTest {

    @Test
    public void querySongsOfLinkinPark() {
        WikidataService wikidataService = new WikidataService();
        List<Map<String, RDFNode>> results = wikidataService.querySelect(WikidataService.SELECT_ARTIST_INFO);

        for (Map<String, RDFNode> row: results) {
            System.out.println(row.get("lbl").asLiteral().getString());
        }

    }

    @Test
    public void queryGraphFromLinkinParkSongs() {
        WikidataService wikidataService = new WikidataService();
        Model result = wikidataService.queryGraph(WikidataService.CONSTRUCT_SONG_INFO);
        result.write(System.out, "TURTLE");

    }
}
