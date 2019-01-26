package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WikiDataQueryService {


    @Autowired
    private WikidataService wikidataService;
    @Autowired
    private MusicbrainzService musicbrainzService;


    public Map<String, String> getSimilarArtistsGenre(String artistName) {

        /* resolve wikidataURI */
        String wikidataId = musicbrainzService.getWikidataResourceByArtistName("Alter Bridge");
        wikidataId = "wd:" + wikidataId.substring(wikidataId.lastIndexOf('Q'));

        Map<String, String> params = new HashMap<>();
        params.put("$paramArtist", wikidataId);

        return params;

    }

    public Map<String, String> getSimilarArtistsCountry(String artistName) {
        Map<String, String> similarArtists = new HashMap<>();
        //TODO query artists
        /* dummy impl */
        similarArtists.put("Alter Bridge", "wd:Q335036");
        similarArtists.put("Die Toten Hosen", "wd:Q32461");
        similarArtists.put("Billy Talent", "wd:Q154815");
        return similarArtists;
    }

    public Map<String, String> getSimilarArtistsDecade(String artistName) {
        Map<String, String> similarArtists = new HashMap<>();
        //TODO query artists
        /* dummy impl */
        similarArtists.put("Alter Bridge", "wd:Q335036");
        similarArtists.put("Die Toten Hosen", "wd:Q32461");
        similarArtists.put("Billy Talent", "wd:Q154815");
        return similarArtists;
    }

    public Map<String, String> getSimilarSongsGenre(String songName) {
        Map<String, String> similarArtists = new HashMap<>();
        //TODO query artists
        /* dummy impl */
        similarArtists.put("Hells Bells", "wd:Q1512224");
        similarArtists.put("In the End", "wd:Q20003");
        similarArtists.put("Rusted from the Rain","Q7382465");
        return similarArtists;
    }

    public void setWikidataService(WikidataService wikidataService) {
        this.wikidataService = wikidataService;
    }
}
