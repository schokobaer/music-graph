package at.ac.tuwien.semanticsystems.musicgraph.imports;

import at.ac.tuwien.semanticsystems.musicgraph.service.AmazonService;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;

@Component("amazonImport")
public class AmazonDataImport implements DataImport {

    @Autowired
    AmazonService service;

    @Override
    public Model importData(File file) {
        org.eclipse.rdf4j.model.Model model;
        try {
            //TODO proper conversion from rdf4j to jena Model
           return  (Model) service.getAmazonModel(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            //TODO exceptionhandling
            e.printStackTrace();
            return null;
        }
    }
}
