package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import org.apache.jena.rdfconnection.RDFConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
public class MusicGraphController {
    private String appMode;

    @RequestMapping("/")
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/favouriteArtists")
    public String favouriteArtists(Model model) {
        List<ArtistModel> artists = new ArrayList<>();
        ArtistModel artistModel = new ArtistModel("josef", "st", "https://www.google.at");
        artists.add(artistModel);
        artists.add(artistModel);
        artists.add(artistModel);
        artists.add(artistModel);

        model.addAttribute("artists", artists);
        return "favouriteArtists";
    }

}