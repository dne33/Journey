package journey;

import com.opencsv.bean.CsvToBeanBuilder;
import journey.data.Station;
import journey.repository.StationDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;

/**
 * Class to read data from a CSV into the database.
 */
public class ReadCSV {
    private static final Logger log = LogManager.getLogger();

    private ReadCSV() {}
    /**
     * Imports data into the database.
     */
    public static void readStations() {
        FileReader file = null;
        BufferedReader br = null;
        try {
            InputStream is = ReadCSV.class.getResourceAsStream("/EV_Roam_charging_stations.csv");
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
        } catch (Exception e) {
            log.error(e);
        }

        assert br != null;
        List<Station> beans = new CsvToBeanBuilder<Station>(br)
            .withType(Station.class)
            .build()
            .parse();

        StationDAO stationDAO = new StationDAO();
        for (Station s : beans) {
            String connectors = s.getConnectorsList();
            connectors = connectors.substring(1, connectors.length() - 1);
            String[] connectorsList = connectors.split("},\\{");


            String maxTimeLimit = s.getMaxTimeLimit();
            int time = 0;
            if (Utils.isInt(maxTimeLimit)) {
                time = Integer.parseInt(maxTimeLimit);
            }

            s.setMaxTime(time);
            s.setConnectors(connectorsList);

            s.setRating(0);
            s.setFavourite(false);

            stationDAO.insertStation(s);
        }
    }
}
