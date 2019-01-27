package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.imports.DataImport;
import at.ac.tuwien.semanticsystems.musicgraph.tdb.TdbManager;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.WikiData;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ImportState;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ImportController {

    @Autowired
    private TdbManager tdbManager;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);
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

    private SseEmitter getStateEmitter(DataImport dataImport) throws IOException {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        ImportState state = new ImportState();
        state.setProcessing(dataImport.isProcessing());
        state.setProgress(dataImport.getProgress());

        emitter.send(state, MediaType.APPLICATION_JSON);
        emitter.complete();

        return emitter;
    }

    @GetMapping("/import/youtube")
    public SseEmitter youtubeState() throws IOException {
        return getStateEmitter(youtubeImport);
    }

    @PostMapping("/import/youtube")
    public ModelAndView youtube(@RequestParam("file") MultipartFile file) throws IOException {

        // Convert inputdata
        File destFile = File.createTempFile("youtube", "001");
        file.transferTo(destFile);

        executorService.submit(() -> {
            Model model = youtubeImport.importData(destFile);
            updateData(model);
            destFile.delete();
        });

        return new ModelAndView("uploadFinished", "message", "Upload successful");
    }

    @PostMapping("/import/spotify")
    public ModelAndView spotify(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            throw new NotImplementedException();
        } catch (Exception e) {
            return new ModelAndView("uploadFinished", "message", "Upload failed");
        }
        //TODO: add when implemented
        //return new ModelAndView("uploadFinished", "message", "Upload successful");
    }

    @GetMapping("/import/spotify")
    public SseEmitter spotifyState() throws IOException {
        //return getStateEmitter(null);
        return null;
    }

    @PostMapping("/import/amazonmusic")
    public ModelAndView amazonMusic(@RequestParam("file") MultipartFile file) throws IOException {
        File destFile = File.createTempFile("amazon", "001");
        file.transferTo(destFile);

        executorService.submit(() -> {
            Model model = amazonImport.importData(destFile);
            updateData(model);
            destFile.delete();
        });

        return new ModelAndView("uploadFinished", "message", "Upload successful");
    }

    @GetMapping("/amazonmusic/youtube")
    public SseEmitter amazonMusicState() throws IOException {
        return getStateEmitter(amazonImport);
    }

    @GetMapping("/graph")
    public String graph() {
        Dataset dsg = tdbManager.getDataset();
        ByteOutputStream os = new ByteOutputStream();
        Txn.executeRead(dsg, () -> {
            Model model = ModelFactory.createUnion(dsg.getDefaultModel(), ModelFactory.createDefaultModel());
            model.setNsPrefix("schema", Schema.baseUri);
            model.setNsPrefix("schema", Schema.baseUri);
            model.setNsPrefix("wd", WikiData.wd);
            model.setNsPrefix("wdt", WikiData.wdt);
            model.setNsPrefix("mg", MusicGraph.entityBaseUri);
            model.write(os, "TURTLE");
        });
        return os.toString();
    }
}
