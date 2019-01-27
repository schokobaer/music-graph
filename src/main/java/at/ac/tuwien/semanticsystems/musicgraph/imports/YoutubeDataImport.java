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

    private boolean runing = false;
    private float progress = 0;
    private YoutubeVideoService youtubeVideoService;

    @Value("${dataimport.youtube.max}")
    private int maxDataImport = 100;

    @Autowired
    public void setYoutubeVideoService(YoutubeVideoService youtubeVideoService) {
        this.youtubeVideoService = youtubeVideoService;
    }


    @Override
    public Model importData(File file) {
        if (this.runing) {
            return null;
        }

        this.runing = true;
        this.progress = 0;

        Model dataModel = null;
        try {
            dataModel = youtubeVideoService.parseHistoryFile(file, maxDataImport);
        } catch (IOException e) {
            return null;
        }

        // Find out amount
        int amount = 0;
        ResIterator itr = dataModel.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            amount++;
            itr.nextResource();
        }

        int count = 0;
        itr = dataModel.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            Resource video = itr.nextResource();

            Resource song = youtubeVideoService.getMusicVideo(video, dataModel);
            if (song != null) {
                count++;
                this.progress = Float.parseFloat(count + "") / Float.parseFloat(amount + "");
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


            count++;
            this.progress = Float.parseFloat(count + "") / Float.parseFloat(amount + "");
        }

        this.runing = false;

        return dataModel;
    }

    @Override
    public boolean isProcessing() {
        return this.runing;
    }

    @Override
    public float getProgress() {
        return this.progress;
    }
}
