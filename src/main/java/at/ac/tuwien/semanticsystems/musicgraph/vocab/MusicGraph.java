package at.ac.tuwien.semanticsystems.musicgraph.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class MusicGraph {

    private static final String baseUri = "http://semantics.tuwien.ac.at/group4#";
    private static final Model baseModel = ModelFactory.createDefaultModel();

    /** Properties **/
    public static final Property numberOfListenings = baseModel.createProperty(baseUri, "numberOfListenings");
    public static final Property listenedAt = baseModel.createProperty(baseUri, "listenedAt");

    /** Classes **/


}
