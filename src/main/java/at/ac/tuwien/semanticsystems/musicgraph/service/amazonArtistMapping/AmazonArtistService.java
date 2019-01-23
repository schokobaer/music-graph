package at.ac.tuwien.semanticsystems.musicgraph.service.amazonArtistMapping;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AmazonArtistService {


   public Map<String, String> getAmazonArtistMapping(String path) {

       Set<String> artistIDs = new HashSet<>();

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

       return AmazonArtistIDToNameMapperThread.getArtisIdNameMap(artistIDs);


   }


}
