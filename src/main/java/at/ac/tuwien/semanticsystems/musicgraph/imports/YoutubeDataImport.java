package at.ac.tuwien.semanticsystems.musicgraph.imports;

import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeVideoService;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.Schema;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component("youtubeImport")
public class YoutubeDataImport implements DataImport {

    private YoutubeVideoService youtubeVideoService;

    @Value("${dataimport.youtube.max}")
    private int maxDataImport = 100;

    @Autowired
    public void setYoutubeVideoService(YoutubeVideoService youtubeVideoService) {
        this.youtubeVideoService = youtubeVideoService;
    }


    @Override
    public Model importData(File file) {
        Model dataModel = ModelFactory.createDefaultModel();

        // TODO: RDFize the file


        List<YoutubeVideoService.YoutubeVideo> videos = null;
        try {
            videos = youtubeVideoService.parseFile(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int count = 0;
        for (YoutubeVideoService.YoutubeVideo video: videos) {
            Resource res = dataModel.createResource();
            res.addProperty(RDF.type, MusicGraph.YoutubeVideo);
            res.addProperty(Schema.name, video.getVideoTitle());
            res.addProperty(MusicGraph.clickedAt, video.getViewDate());

            count++;
            if (count >= maxDataImport) {
                break;
            }
        }


        // TODO: Implement a limit to parse videos
        ResIterator itr = dataModel.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            Resource video = itr.nextResource();

            Resource song = youtubeVideoService.getMusicVideo(video, dataModel);
            if (song != null) {
                continue;
            }
            Resource artist = youtubeVideoService.getArtist(video, dataModel);
            if (artist != null) {
                song = dataModel.createResource();
                song.addProperty(RDF.type, MusicGraph.YoutubeSongVideo)
                        .addProperty(Schema.creditedTo, artist);
                if (video.hasProperty(MusicGraph.clickedAt)) {
                    song.addProperty(MusicGraph.listenedAt, video.getProperty(MusicGraph.clickedAt).getObject());
                }
                if (video.hasProperty(MusicGraph.listenedAt)) {
                    song.addProperty(MusicGraph.listenedAt, video.getProperty(MusicGraph.listenedAt).getObject());
                }
            }


        }

        return dataModel;
    }
}
