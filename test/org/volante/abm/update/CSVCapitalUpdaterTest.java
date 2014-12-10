package org.volante.abm.update;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.volante.abm.data.Capital;
import org.volante.abm.data.Cell;
import org.volante.abm.data.Region;
import org.volante.abm.example.BasicTestsUtils;

import com.csvreader.CsvReader;

public class CSVCapitalUpdaterTest extends BasicTestsUtils
{

	/**
	 * Logger
	 */
	static private Logger	logger	= Logger.getLogger(CSVCapitalUpdaterTest.class);

	@Test
	public void testGeneralOperation() throws Exception
	{
		
		CSVCapitalUpdater updater = new CSVCapitalUpdater();
		updater.yearlyFilenames.put(2000,"csv/Region1-2000.csv");
		updater.yearlyFilenames.put(2001,"csv/Region1-2001.csv");
		updater = persister.roundTripSerialise( updater );
		Region r = setupWorldWithUpdater( 2000, updater, c11, c12, c21, c22 );
		
		runInfo.getSchedule().tick(); //Does 2000 tick
		checkRegionCells( r, "csv/Region1-2000.csv" );
		runInfo.getSchedule().tick();
		checkRegionCells( r, "csv/Region1-2001.csv" );
	}
	
	@Test
	public void testYearlyFilename() throws Exception
	{
		CSVCapitalUpdater updater = new CSVCapitalUpdater();
		updater.filename = "csv/Region1-%y.csv";
		updater.yearInFilename = true;
		Region r = setupWorldWithUpdater( 2000, updater, c11, c12, c21, c22 );
		
		runInfo.getSchedule().tick(); //Does 2000 tick
		checkRegionCells( r, "csv/Region1-2000.csv" );
		runInfo.getSchedule().tick();
		checkRegionCells( r, "csv/Region1-2001.csv" );
	}
	
	public Region setupWorldWithUpdater( int year, AbstractUpdater updater, Cell...cells ) throws Exception
	{
		Region r = setupBasicWorld( cells );
		updater.initialise( modelData, runInfo, r );
		runInfo.getSchedule().register( updater );
		runInfo.getSchedule().setStartTick( year );
		return r;
	}

	
	public void checkRegionCells( Region r, String csvFile ) throws IOException
	{
		CsvReader target = runInfo.getPersister().getCSVReader( csvFile );
		while( target.readRecord() )
		{
			Cell cell = r.getCell(Integer.parseInt(target.get("X")),
					Integer.parseInt(target.get("Y")));
			for( Capital c : modelData.capitals )
			{
				if( target.get( c.getName() ) != null )
				{
					double exp = Double.parseDouble( target.get(c.getName() ));
					double got = cell.getEffectiveCapitals().getDouble( c );
					assertEquals( "Capital " + c.getName(), exp, got, 0.00001 );
					logger.info("Got: " + got + ", Exp: " + exp + " for " + c.getName() + " on "
							+ cell);
				}
			}
		}
	}

}
