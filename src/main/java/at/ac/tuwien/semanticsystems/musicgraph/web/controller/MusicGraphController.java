package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.service.TdbQueryService;
import at.ac.tuwien.semanticsystems.musicgraph.service.WikiDataQueryService;
import at.ac.tuwien.semanticsystems.musicgraph.tdb.TdbManager;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import com.google.common.io.Resources;
import github.jjbinks.bandsintown.dto.Artist;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.system.Txn;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;


@Controller
public class MusicGraphController {
    private String appMode;

    @Autowired
    private TdbQueryService tdbQueryService;

    @Autowired
    private WikiDataQueryService wikiDataQueryService;

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/favouriteArtists")
    public String favouriteArtists(Model model) {
        List<ArtistModel> artists = tdbQueryService.getFavouriteArtists();
        model.addAttribute("artists", artists);
        return "favouriteArtists";
    }

    @RequestMapping("/uploadData")
    public String uploadData(Model model) {
        return "uploadData";
    }

}