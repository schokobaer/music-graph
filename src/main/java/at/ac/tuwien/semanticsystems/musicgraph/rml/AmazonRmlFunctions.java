package at.ac.tuwien.semanticsystems.musicgraph.rml;

import com.taxonic.carml.engine.function.FnoFunction;
import com.taxonic.carml.engine.function.FnoParam;

import java.util.Map;

public class AmazonRmlFunctions {



    private Map<String, String> artistMapping;

    public AmazonRmlFunctions(Map<String, String> artistMapping) {
        this.artistMapping = artistMapping;
    }


    @FnoFunction("http://semantics.tuwien.ac.at/group4/amazonArtistFunction")
    public String amazonArtistFunction(@FnoParam("http://semantics.tuwien.ac.at/group4/stringParameterArtistID") String artistID) {
        return artistMapping.get(artistID);
    }

}
