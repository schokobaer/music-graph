package at.ac.tuwien.semanticsystems.musicgraph.imports;

import at.ac.tuwien.semanticsystems.musicgraph.service.YoutubeVideoService;
import at.ac.tuwien.semanticsystems.musicgraph.vocab.MusicGraph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class YoutubeDataImport implements DataImport {

    private YoutubeVideoService youtubeVideoService;

    @Autowired
    public void setYoutubeVideoService(YoutubeVideoService youtubeVideoService) {
        this.youtubeVideoService = youtubeVideoService;
    }


    @Override
    public Model importData(File file) {
        Model dataModel = ModelFactory.createDefaultModel();

        // TODO: RDFize the file


        // TODO: Implement a limit to parse videos
        ResIterator itr = dataModel.listResourcesWithProperty(RDF.type, MusicGraph.YoutubeVideo);
        while (itr.hasNext()) {
            Resource video = itr.nextResource();

            dataModel = youtubeVideoService.getMusicVideo(video, dataModel);

        }

        return dataModel;
    }
}
