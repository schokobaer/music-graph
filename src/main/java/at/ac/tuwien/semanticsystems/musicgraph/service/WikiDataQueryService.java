package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WikiDataQueryService {


    @Autowired
    private WikidataService wikidataService;
    @Autowired
    private MusicbrainzService musicbrainzService;


    public Map<String, String> getSimilarArtistsGenre(String artistName) {
        Map<String, String> params = getQueryParamWikiDataID(artistName);
        List<Map<String, RDFNode>> result = wikidataService.querySelect(wikidataService.GET_ARTIST_FROM_GENRE, params);

        return createArtistMap(result);
    }

    public Map<String, String> getSimilarArtistsCountry(String artistName) {
        Map<String, String> params = getQueryParamWikiDataID(artistName);
        List<Map<String, RDFNode>> result = wikidataService.querySelect(wikidataService.GET_ARTIST_FROM_COUNTRY, params);

        return createArtistMap(result);
    }

    public Map<String, String> getSimilarArtistsDecade(String artistName) {
        Map<String, String> params = getQueryParamWikiDataID(artistName);
        List<Map<String, RDFNode>> result = wikidataService.querySelect(wikidataService.GET_ARTIST_FROM_DECADE, params);

        return createArtistMap(result);
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


    private Map<String, String> getQueryParamWikiDataID(String artistName) {
        /* resolve wikidataURI */
        String wikidataId = musicbrainzService.getWikidataResourceByArtistName(artistName);
        wikidataId = "wd:" + wikidataId.substring(wikidataId.lastIndexOf('Q'));

        Map<String, String> params = new HashMap<>();
        params.put("$paramArtist", wikidataId);
        return params;
    }

    private Map<String, String> createArtistMap(List<Map<String, RDFNode>> queryResult) {
        Map<String, String> map = new HashMap<>();

        for (Map<String, RDFNode> row: queryResult) {
            map.put(row.get("bandLabel").asLiteral().getString(), row.get("band").toString());
        }
        return map;
    }

    public void setWikidataServices(WikidataService wikidataService, MusicbrainzService musicbrainzService) {
        this.wikidataService = wikidataService;
        this.musicbrainzService = musicbrainzService;
    }
}
