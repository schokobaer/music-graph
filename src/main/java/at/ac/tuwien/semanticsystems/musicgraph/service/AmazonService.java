package at.ac.tuwien.semanticsystems.musicgraph.service;

import at.ac.tuwien.semanticsystems.musicgraph.service.AmazonArtistIDToNameMapperThread;
import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.logical_source_resolver.CsvResolver;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.util.RmlMappingLoader;
import com.taxonic.carml.vocab.Rdf;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AmazonService {


    /**
     * tries to create a rdf4j.Model from a AmazonMusic history
     *
     * @param path path to the CSV containing the history
     * @return rdf model containing the amazon history data
     */
    public Model getAmazonModel(String path) throws FileNotFoundException {
        Map<String, String> artistMapping = getAmazonArtistMapping(path);


        File file = new File(path);



        File tempFile = new File("resources/data", "AmazonHistory.csv");
        try {
            Files.copy(file.toPath(),tempFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException(e.getMessage());
        }


        Set<TriplesMap> mappingAmazon =
                RmlMappingLoader
                        .build()
                        .load(RDFFormat.TURTLE, Paths.get("resources/amazonMapping.ttl"));


        RmlMapper mapperAmazon =
                RmlMapper
                        .newBuilder()
                        // Add the resolvers to suit your need
                        .setLogicalSourceResolver(Rdf.Ql.Csv, new CsvResolver())
                        .iriUnicodeNormalization(Normalizer.Form.NFKC)
                        .fileResolver(Paths.get("resources/data"))
                        .build();

        Model result =  mapperAmazon.map(mappingAmazon);

        tempFile.delete();
        return result;
    }


    /**
     * tries to resolve all artistIDs in a CSV found at the given path
     *
     *
     * @param path path to the csv file
     * @return a map with artistIDs as key and artist name as value
     */
   public Map<String, String> getAmazonArtistMapping(String path) {

       Set<String> artistIDs = new HashSet<>();

       /* get all artist IDs */
       CSVParser parser = null;
       try (Reader reader = new FileReader(path)){
           parser = CSVParser.parse(reader, CSVFormat.RFC4180.withFirstRecordAsHeader());

           for (final CSVRecord record : parser) {
               artistIDs.add(record.get("artistAsin"));
           }

       } catch (FileNotFoundException e) {
            e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           if (null != parser) {
               try {
                   parser.close();
               } catch (IOException e) {
               }

           }
       }

       /* resolve artist IDs */
       return AmazonArtistIDToNameMapperThread.getArtisIdNameMap(artistIDs);
   }
}
