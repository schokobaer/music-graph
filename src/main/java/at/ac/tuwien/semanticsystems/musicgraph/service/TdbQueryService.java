package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.tdb.TdbManager;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import com.google.common.io.Resources;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.system.Txn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service
public class TdbQueryService {

    private static final String GET_ALL_ARTISTS_WITH_NUMBER_LISTENED = "query/localTDB.allFavouriteArtists.rq";
    private static final String GET_TOP_FIVE_FAVOURITE_ARTISTS= "query/localTDB.topFiveFavouriteArtists.rq";

    @Autowired
    private TdbManager tdbManager;

    private ResultSet executeQuery(Query query) {
        Dataset tdb = tdbManager.getDataset();
        return Txn.calculateRead(tdb, () -> {
            try (QueryExecution qExec = QueryExecutionFactory.create(query, tdb)) {
                return ResultSetFactory.copyResults(qExec.execSelect());
            }
        });
    }

    private ResultSet executeQueryFromResource(String resourceLocation) {
        try {
            Query query = QueryFactory.create(Resources.toString(Resources.getResource(resourceLocation), Charset.forName("UTF-8")));
            return executeQuery(query);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String prettyPrintArtistName(Resource artist) {
        String artistTdbUri = artist.getURI();
        int firstSpace = artistTdbUri.indexOf("%");
        String artistName = "";
        if(firstSpace < 0) {
            artistName = artist.getLocalName();
        } else {
            String artistNameBeforeFirstSpace = artistTdbUri.substring(0, firstSpace);
            int lastSlash = artistTdbUri.lastIndexOf("/");
            artistNameBeforeFirstSpace = artistNameBeforeFirstSpace.substring(lastSlash + 1);
            artistName = artistNameBeforeFirstSpace + artistTdbUri.substring(firstSpace);
            artistName = artistName.replaceAll("%20", " ");
        }
        return artistName;
    }

    private List<ArtistModel> parseFavouriteArtistResultSet(ResultSet resultSet) {
        List<ArtistModel> artists = new ArrayList<>();
        while(resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            Resource artist = solution.getResource("?artist");
            String artistTdbUri = artist.getURI();
            String artistName = prettyPrintArtistName(artist);
            Resource artistWikiData = solution.getResource("?artistWikiDataUri");
            String artistWikiDataUri = artistWikiData.getURI();
            String artistWikiDataID = artistWikiData.getLocalName();
            int numberOfSongsListened = Integer.parseInt(solution.getLiteral("?numberOfSongsListened").getString());
            ArtistModel artistModel = new ArtistModel(artistTdbUri, artistName, artistWikiDataUri, artistWikiDataID, numberOfSongsListened);
            artists.add(artistModel);
        }
        return artists;
    }

    public List<ArtistModel> getAllFavouriteArtists() {
        return parseFavouriteArtistResultSet(executeQueryFromResource(GET_ALL_ARTISTS_WITH_NUMBER_LISTENED));
    }

    public List<ArtistModel> getTopFiveFavouriteArtists() {
        return parseFavouriteArtistResultSet(executeQueryFromResource(GET_TOP_FIVE_FAVOURITE_ARTISTS));
    }
}
