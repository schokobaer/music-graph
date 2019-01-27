package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.GenreModel;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WikiDataQueryService {


    @Autowired
    private WikidataService wikidataService;
    @Autowired
    private MusicbrainzService musicbrainzService;
    @Autowired
    private TdbQueryService tdbQueryService;

    public Map<String, String> getGenresOfArtistByName(String artistName) {
        Map<String, String> params = getQueryParamWikiDataID(artistName);
        List<Map<String, RDFNode>> result = wikidataService.querySelect(wikidataService.GET_GENRES_OF_ARTIST, params);
        return createGenreMap(result);
    }

    public Map<String, String> getGenresOfArtistByArtistWikiDataID(String artistWikiDataID) {
        Map<String, String> params = new HashMap<>();
        params.put("$paramArtist", "wd:" + artistWikiDataID);
        List<Map<String, RDFNode>> result = wikidataService.querySelect(wikidataService.GET_GENRES_OF_ARTIST, params);
        return createGenreMap(result);
    }

    public List<GenreModel> getFavouriteGenres() {
        List<ArtistModel> artists = tdbQueryService.getTopFiveFavouriteArtists();
        Map<String, GenreModel> favouriteGenres = new HashMap<>();
        for (ArtistModel artist : artists) {
            Map<String, String> genres = getGenresOfArtistByArtistWikiDataID(artist.getArtistWikiDataID());
            for(String genreKey : genres.keySet()) {
                if(favouriteGenres.containsKey(genreKey)) {
                    GenreModel existingGenre = favouriteGenres.get(genreKey);
                    existingGenre.setNumberOfFavouriteBandsWithGenre(existingGenre.getNumberOfFavouriteBandsWithGenre() + 1);
                } else {
                    favouriteGenres.put(genreKey, new GenreModel(genres.get(genreKey), genres.get(genreKey), genreKey, 1));
                }
            }
        }
        List<GenreModel> genreList = new ArrayList<>(favouriteGenres.values());
        genreList.sort(Comparator.comparing(GenreModel::getNumberOfFavouriteBandsWithGenre).reversed());
        return genreList;
    }

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
        Map<String, String> map = new TreeMap<>();

        for (Map<String, RDFNode> row: queryResult) {
            map.put(row.get("bandLabel").asLiteral().getString(), row.get("band").toString());
        }
        return map;
    }

    private Map<String, String> createGenreMap(List<Map<String, RDFNode>> queryResult) {
        Map<String, String> map = new HashMap<>();

        for (Map<String, RDFNode> row: queryResult) {
            map.put(row.get("genreLabel").asLiteral().getString(), row.get("genre").toString());
        }
        return map;
    }

    public void setWikidataServices(WikidataService wikidataService, MusicbrainzService musicbrainzService) {
        this.wikidataService = wikidataService;
        this.musicbrainzService = musicbrainzService;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
