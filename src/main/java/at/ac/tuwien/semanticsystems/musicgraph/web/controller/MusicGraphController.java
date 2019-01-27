package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import at.ac.tuwien.semanticsystems.musicgraph.service.TdbQueryService;
import at.ac.tuwien.semanticsystems.musicgraph.service.WikiDataQueryService;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.ArtistModel;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.CountryModel;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.DecadeModel;
import at.ac.tuwien.semanticsystems.musicgraph.web.Model.GenreModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        List<ArtistModel> artists = tdbQueryService.getTopFiveFavouriteArtists();
        model.addAttribute("artists", artists);
        return "favouriteArtists";
    }

    @RequestMapping("/favouriteGenres")
    public String favouriteGenres(Model model) {
        List<GenreModel> gernes = wikiDataQueryService.getFavouriteGenres();
        model.addAttribute("genres", gernes);
        return "favouriteGenres";
    }

    @RequestMapping("/favouriteCountries")
    public String favouriteCountries(Model model) {
        List<CountryModel> countries = wikiDataQueryService.getFavouriteCountries();
        model.addAttribute("countries", countries);
        return "favouriteCountries";
    }

    @RequestMapping("/favouriteDecades")
    public String favouriteDecades(Model model) {
        List<DecadeModel> decades = wikiDataQueryService.getFavouriteDecades();
        model.addAttribute("decades", decades);
        return "favouriteDecades";
    }

    @RequestMapping("/uploadData")
    public String uploadData(Model model) {
        return "uploadData";
    }

    @RequestMapping("/similarArtistsGenre")
    public String similarArtistsGenre(@RequestParam("artistName") String artistName, Model model) {
        List<ArtistModel> artists = new ArrayList<>();
        Map<String, String> map = wikiDataQueryService.getSimilarArtistsGenre(artistName);
        for (String artist : map.keySet()) {
            artists.add(new ArtistModel(artist, map.get(artist)));
        }
        model.addAttribute("artists", artists);
        return "similarArtistsGenre";
    }

    @RequestMapping("/similarArtistsCountry")
    public String similarArtistsCountry(@RequestParam("artistName") String artistName, Model model) {
        List<ArtistModel> artists = new ArrayList<>();
        Map<String, String> map = wikiDataQueryService.getSimilarArtistsCountry(artistName);
        for (String artist : map.keySet()) {
            artists.add(new ArtistModel(artist, map.get(artist)));
        }
        model.addAttribute("artists", artists);
        return "similarArtistsCountry";
    }

    @RequestMapping("/similarArtistsDecades")
    public String similarArtistsDecades(@RequestParam("artistName") String artistName, Model model) {
        List<ArtistModel> artists = new ArrayList<>();
        Map<String, String> map = wikiDataQueryService.getSimilarArtistsDecade(artistName);
        for (String artist : map.keySet()) {
            artists.add(new ArtistModel(artist, map.get(artist)));
        }
        model.addAttribute("artists", artists);
        return "similarArtistsDecades";
    }
}