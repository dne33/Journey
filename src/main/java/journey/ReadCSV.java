package journey;

import com.opencsv.bean.CsvToBeanBuilder;
import journey.data.Station;
import journey.repository.StationDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Objects;

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
        InputStreamReader file;
        try {
            file = new InputStreamReader(Objects.requireNonNull(ReadCSV.class.getResourceAsStream("/EV_Roam_charging_stations.csv")));
            assert file != null;
            List<Station> beans = new CsvToBeanBuilder<Station>(file)
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
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
