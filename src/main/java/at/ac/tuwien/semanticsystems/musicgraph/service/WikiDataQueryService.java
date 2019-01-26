package at.ac.tuwien.semanticsystems.musicgraph.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WikiDataQueryService {




    public Map<String, String> getSimilarArtistsGenre(String artistName) {
        Map<String, String> similarArtists = new HashMap<>();
        //TODO query artists
        /* dummy impl */
        similarArtists.put("Alter Bridge", "wd:Q335036");
        similarArtists.put("Die Toten Hosen", "wd:Q32461");
        similarArtists.put("Billy Talent", "wd:Q154815");
        return similarArtists;
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
}
