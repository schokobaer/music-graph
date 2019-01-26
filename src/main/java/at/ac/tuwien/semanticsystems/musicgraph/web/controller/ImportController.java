package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.imports.DataImport;
import at.ac.tuwien.semanticsystems.musicgraph.tdb.TdbManager;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb.TDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.nio.file.Files;

@RestController
public class ImportController {

    @Autowired
    private TdbManager tdbManager;

    private DataImport youtubeImport;
    private DataImport amazonImport;

    @Autowired
    public void setYoutubeImport(@Qualifier("youtubeImport") DataImport youtubeImport, @Qualifier("amazonImport") DataImport amazonImport) {
        this.youtubeImport = youtubeImport;
        this.amazonImport = amazonImport;
    }

    private void updateData(Model model) {
        Dataset tdb = tdbManager.getDataset();
        RDFConnection con = RDFConnectionFactory.connect(tdb);
        Txn.executeWrite(tdb, () -> con.load(model));
        con.close();
    }

    @PostMapping("/import/youtube")
    public void youtube(@RequestParam("file") MultipartFile file) throws IOException {

        // Convert inputdata
        File destFile = File.createTempFile("youtube", "001");
        file.transferTo(destFile);
        Model model = youtubeImport.importData(destFile);

        updateData(model);

        destFile.delete();
    }

    @PostMapping("/import/spotify")
    public void spotify(@RequestParam("file") MultipartFile file) throws IOException {
        throw new NotImplementedException();
    }

    @PostMapping("/import/amazonmusic")
    public void amazonMusic(@RequestParam("file") MultipartFile file) throws IOException {
        File destFile = File.createTempFile("amazon", "001");
        file.transferTo(destFile);
        Model model = amazonImport.importData(destFile);

        updateData(model);

        destFile.delete();
    }
}
