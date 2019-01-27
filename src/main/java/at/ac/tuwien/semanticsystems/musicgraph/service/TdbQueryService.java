package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.tdb.TdbManager;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import com.google.common.io.Resources;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.system.Txn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TdbQueryService {

    private static final String GET_FAVOURITE_ARTISTS = "query/localTDB.favouriteArtists.rq";

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

    public List<ArtistModel> getFavouriteArtists() {
        List<ArtistModel> artists = new ArrayList<>();
        ResultSet resultSet = executeQueryFromResource(GET_FAVOURITE_ARTISTS);

        while(resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            Resource artist = solution.getResource("?artist");
            String artistTdbUri = artist.getURI();
            int firstSpace = artistTdbUri.indexOf("%");
            String artistName = "";
            if(firstSpace < 0) {
                artistName = artist.getLocalName();
            } else {
                String artistNameBeforeFirstSpace = artistTdbUri.substring(0, firstSpace);
                int lastSlash = artistTdbUri.lastIndexOf("/");
                artistNameBeforeFirstSpace = artistNameBeforeFirstSpace.substring(lastSlash + 1, artistNameBeforeFirstSpace.length());
                artistName = artistNameBeforeFirstSpace + artistTdbUri.substring(firstSpace, artistTdbUri.length());
                artistName = artistName.replaceAll("%20", " ");
            }
            String artistWikiDataUri = solution.getResource("?artistWikiDataUri").getURI();
            int numberOfSongsListened = Integer.parseInt(solution.getLiteral("?numberOfSongsListened").getString());
            ArtistModel artistModel = new ArtistModel(artistTdbUri, artistName, artistWikiDataUri, numberOfSongsListened);
            artists.add(artistModel);
        }
        return artists;
    }
}
