package at.ac.tuwien.semanticsystems.musicgraph.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class MusicGraphController {
    private String appMode;

    @RequestMapping("/")
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/favouriteArtists")
    public String listNames(Model model) {
        List<String> nameList = new ArrayList<>();

        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");
        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");
        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");
        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");
        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");
        nameList.add("hallo");
        nameList.add("josef");
        nameList.add("es");
        nameList.add("geht");

        model.addAttribute("names", nameList);
        return "favouriteArtists";
    }
}