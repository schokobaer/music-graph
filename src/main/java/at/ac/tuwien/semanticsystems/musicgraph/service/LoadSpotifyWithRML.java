package at.ac.tuwien.semanticsystems.musicgraph.service;

import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.logical_source_resolver.JsonPathResolver;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.util.RmlMappingLoader;
import com.taxonic.carml.vocab.Rdf;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Set;

public class LoadSpotifyWithRML {

    public void loadSpotifyWithRML(){
        Set<TriplesMap> mapping =
                RmlMappingLoader
                        .build()
                        .load(RDFFormat.TURTLE, Paths.get("resources/spotifyMapping.ttl"));

        String test = Paths.get("resources").toAbsolutePath().toString();
        String abc = "";
        RmlMapper mapper =
                RmlMapper
                        .newBuilder()
                        // Add the resolvers to suit your need
                        .setLogicalSourceResolver(Rdf.Ql.JsonPath, new JsonPathResolver())
                        // optional:
                        // specify IRI unicode normalization form (default = NFC)
                        // see http://www.unicode.org/unicode/reports/tr15/tr15-23.html
                        .iriUnicodeNormalization(Normalizer.Form.NFKC)
                        // set file directory for sources in mapping
                        .fileResolver(Paths.get("resources/data"))
                        .build();

        org.eclipse.rdf4j.model.Model result = mapper.map(mapping);
        Rio.write(result,System.out,RDFFormat.TURTLE);
    }
}
