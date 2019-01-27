package at.ac.tuwien.semanticsystems.musicgraph.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class WikiData {

    public static final String wdt = "http://www.wikidata.org/prop/direct/";
    public static final String wd = "http://www.wikidata.org/entity/";
    private static final Model baseModel = ModelFactory.createDefaultModel();

    /** Properties **/
    public static final Property instanceOf = baseModel.createProperty(wdt + "P31");
    public static final Property genre = baseModel.createProperty(wdt + "P136");
    public static final Property performer = baseModel.createProperty(wdt + "P175");
    public static final Property inception = baseModel.createProperty(wdt + "P571");
    public static final Property countryOfOrigin = baseModel.createProperty(wdt + "P495");
    public static final Property musicbrainzId = baseModel.createProperty(wdt + "P434");

    /** Classes **/
    public static final Resource Release = baseModel.createResource(wd + "Q2031291");
    public static final Resource Single = baseModel.createResource(wd + "Q134556");
    public static final Resource Genre = baseModel.createResource(wd + "Q188451");
    public static final Resource Country = baseModel.createResource(wd + "Q6256");
}
