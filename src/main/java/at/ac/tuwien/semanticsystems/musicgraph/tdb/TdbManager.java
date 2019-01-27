package at.ac.tuwien.semanticsystems.musicgraph.tdb;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.setup.StoreParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.lang.System.out;

@Component
public class TdbManager {

    @Value("${tbd.path}")
    private String tdbPath;

    @PostConstruct
    public void setup() {
        Location location = Location.create(tdbPath);
        StoreParams storeParams = StoreParams.getSmallStoreParams();
        TDBFactory.setup(location, storeParams);
    }

    public Dataset getDataset() {
        return TDBFactory.createDataset(Location.create(tdbPath));
    }

}
