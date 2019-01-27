package at.ac.tuwien.semanticsystems.musicgraph.service;

import com.google.common.io.Resources;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

@Component
public class WikidataService {

    private static final String SPARQL_ENDPOINT = "https://query.wikidata.org/sparql";


    public static final String SELECT_ARTIST_INFO = "query/wikidata.artistinfo.rq";
    public static final String CONSTRUCT_SONG_INFO = "query/wikidata.songinfo.rq";
    public static final String GET_ARTIST_FROM_GENRE = "query/wikidata.selectSimilarArtistGenre.rq";
    public static final String GET_GENRES_OF_ARTIST = "query/wikidata.getGenresOfArtist.rq";
    public static final String GET_COUNTRIES_OF_ARTIST = "query/wikidata.getCountriesOfArtist.rq";
    public static final String GET_DECADES_OF_ARTIST = "query/wikidata.getDecadesOfArtist.rq";
    public static final String GET_ARTIST_FROM_COUNTRY = "query/wikidata.selectSimilarArtistCountry.rq";
    public static final String GET_ARTIST_FROM_DECADE = "query/wikidata.selectSimilarArtistDecade.rq";


    private QueryExecution getQueryExecution(String sparqlQuery, Map<String, String> replacements) {
        String sparql = null;
        try {
            sparql = Resources.toString(Resources.getResource(sparqlQuery), Charset.forName("UTF-8"));
            for (String key : replacements.keySet()) {
                sparql = sparql.replace(key,replacements.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Query query = QueryFactory.create(sparql);
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
        return queryExecution;
    }

    public List<Map<String, RDFNode>> querySelect(String sparqlQuery, Map<String, String> replacements) {
        QueryExecution queryExecution = getQueryExecution(sparqlQuery, replacements);
        try {
            ResultSet results = queryExecution.execSelect();
            List<Map<String, RDFNode>> table = new LinkedList<>();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                Map<String, RDFNode> map = new HashMap<>();
                for (String var: results.getResultVars()) {
                    map.put(var, solution.get(var));
                }
                table.add(map);
            }
            return table;
        } finally {
            queryExecution.close();
        }
    }

    public List<RDFNode> querySelectList(String sparqlQuery, Map<String, String> replacements) {
        QueryExecution queryExecution = getQueryExecution(sparqlQuery, replacements);
        try {
            ResultSet results = queryExecution.execSelect();
            List<RDFNode> resultsList = new ArrayList<>();
            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                for (String var: results.getResultVars()) {
                    resultsList.add(solution.get(var));
                }
            }
            return resultsList;
        } finally {
            queryExecution.close();
        }
    }

    public Map<String, RDFNode> querySingleSelect(String sparqlQuery) {
        List<Map<String, RDFNode>> results = querySelect(sparqlQuery, null);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public Model queryGraph(String sparqlQuery, Map<String, String> replacements) {
        QueryExecution queryExecution = getQueryExecution(sparqlQuery, replacements);
        try {
            Model model = queryExecution.execConstruct();
            return model;
        } finally {
            queryExecution.close();
        }
    }
}
