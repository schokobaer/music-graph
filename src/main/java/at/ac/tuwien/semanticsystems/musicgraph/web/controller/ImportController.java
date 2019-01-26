package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.imports.DataImport;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.system.Txn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class ImportController {

    @Autowired
    private RDFConnection con;

    @Value("${tbd.path}")
    private String tbdPath;

    private DataImport youtubeImport;

    @Autowired
    public void setYoutubeImport(@Qualifier("youtubeImport") DataImport youtubeImport) {
        this.youtubeImport = youtubeImport;
    }

    private void updateData(Model model) throws FileNotFoundException {
        Dataset dsg = con.fetchDataset();

        // Write in TDB
        Txn.executeWrite(dsg, () -> con.load(model));

        // Write in TTL to persist
        FileOutputStream fos = new FileOutputStream(new File(tbdPath));
        Txn.executeRead(dsg, () -> dsg.getDefaultModel().write(fos, "TURTLE"));
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
        throw new NotImplementedException();
    }
}
