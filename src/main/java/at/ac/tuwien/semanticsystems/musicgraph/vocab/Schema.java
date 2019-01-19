package at.ac.tuwien.semanticsystems.musicgraph.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Schema {

    private static final String baseUri = "http://schema.org/";
    private static final Model baseModel = ModelFactory.createDefaultModel();

    public static final Property creditedTo = baseModel.createProperty(baseUri + "creditedTo");

    public static final Resource MusicAlbum = baseModel.createResource(baseUri + "MusicAlbum");
    public static final Resource MusicRelease = baseModel.createResource(baseUri + "MusicRelease");
    public static final Resource MusicGroup = baseModel.createResource(baseUri + "MusicGroup");
}
