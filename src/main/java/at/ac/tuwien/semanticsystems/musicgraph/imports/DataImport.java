package at.ac.tuwien.semanticsystems.musicgraph.imports;

import org.apache.jena.rdf.model.Model;

import java.io.File;

public interface DataImport {

    /**
     * Gets as input a file to load the raw data. Then converts the data to RDF
     * and extends if neccesary the RDF Model and its Resources to have all needed
     * information.
     *
     * @param file Input file for importing the data.
     * @return Model with needed RDF data
     */
    Model importData(File file);

    boolean isProcessing();

    float getProgress();
}
