package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataLinkingService {

    @Autowired
    private WikidataService wikidataService;


    public void addSimilarArtists(String wikidataArtistID) {

        Map<String, String> params = new HashMap<>();
        params.put("$paramArtist", wikidataArtistID);

        Model similarArtists = wikidataService.queryGraph(wikidataService.GET_ARTIST_FROM_GENRE, params);
        similarArtists.add(wikidataService.queryGraph(wikidataService.GET_ARTIST_FROM_COUNTRY, params));
        //TODO get similar artists decade

        //TODO persist

        similarArtists.write(System.out, "TURTLE");

    }

    public void setWikidataService(WikidataService wikidataService) {
        this.wikidataService = wikidataService;
    }
}
