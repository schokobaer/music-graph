package at.ac.tuwien.semanticsystems.musicgraph.tdb;

import org.apache.jena.query.Dataset;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.tdb.setup.StoreParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
