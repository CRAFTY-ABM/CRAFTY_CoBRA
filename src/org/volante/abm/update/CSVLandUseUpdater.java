/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2014 School of GeoScience, University of Edinburgh, Edinburgh, UK
 * 
 * CRAFTY is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * CRAFTY is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * School of Geoscience, University of Edinburgh, Edinburgh, UK
 * 
 */
package org.volante.abm.update;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.volante.abm.agent.LandUseAgent;
import org.volante.abm.agent.assembler.DefaultSocialAgentAssembler;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.example.AgentPropertyIds;
import org.volante.abm.example.allocation.AgentFinder;
import org.volante.abm.models.utils.CellVolatilityObserver;
import org.volante.abm.models.utils.GivingInStatisticsMessenger;
import org.volante.abm.models.utils.TakeoverMessenger;
import org.volante.abm.models.utils.TakeoverObserver;
import org.volante.abm.output.GivingInStatisticsObserver;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ABMPersister;
import org.volante.abm.serialization.transform.IntTransformer;

import com.csvreader.CsvReader;
 

/**
 * Reads protected land use for ticks from a CSV file and controls land use competition 
 * accordingly. The adjustment is performed at the beginning of each tick
 * (e.g. before perceiving social networks) @TODO or after?.   
 * 
 */



public class CSVLandUseUpdater extends AbstractUpdater implements TakeoverMessenger, GivingInStatisticsMessenger {


	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(CSVLandUseUpdater.class);

	/**
	 * Name of column in CSV file that specifies the year a row belongs to
	 */

	@Attribute(required = false)
	String X_COL = "x";
	@Attribute(required = false)
	String Y_COL = "y";


	IntTransformer	xTransformer	= null;
	IntTransformer	yTransformer	= null;


	@Attribute(required = true)
	String restrictionColumn;
	@Attribute(required= false)
	String maskChar = "Y";

	@Attribute(required = false)
	String updateColumn;
	@Attribute(required = false)
	String updateFR;
	@Attribute(required= false)
	String updateChar = null;

	Integer frId;
	protected AgentFinder agentFinder; 

	@Attribute(required=false)
	boolean yearInFilename = true;

	@Attribute(required=false)
	boolean reapplyPreviousFile = false;

	@Attribute(required=false)
	String filename = null;

	String previousFilename = null;


	@ElementMap(inline=true,key="year",attribute=true,entry="csvFile",required=false)
	Map<Integer,String> yearlyFilenames = new HashMap<Integer, String>();


	protected Set<TakeoverObserver> takeoverObserver = new HashSet<>();

	protected Set<GivingInStatisticsObserver> statisticsObserver = new HashSet<>();
	protected Set<CellVolatilityObserver> cellVolatilityObserver = new HashSet<CellVolatilityObserver>();

	/**
	 * Do the actual updating
	 */
	@Override
	public void prePreTick()
	{
		try {

			if (region!=null) {
				// Reset mask
				((Collection<Cell>) this.region.getAllCells()).parallelStream().forEach(cell -> {
					cell.setObjectProperty( AgentPropertyIds.valueOf(restrictionColumn), false);
 
				});
			}

			CsvReader file = getFileForYear();
			if( file != null ) {
				applyFile( file );
			}
		} catch ( Exception e )
		{
			this.log.fatal( "Couldn't update Capitals: " + e.getMessage() );
			e.printStackTrace();
		}
	}

	/**
	 * If there's a file to be applied this year, then get it.
	 * Next, check to see if the year is in the filename, and there's a file that matches.
	 * Finally if we should re-apply the same file (e.g. if there is time-varying noise being added), return that.
	 * Otherwise, return null
	 * @return
	 * @throws IOException 
	 */
	CsvReader getFileForYear() throws IOException
	{
		ABMPersister p = info.getPersister();
		String fn = null;
		String yearly = yearlyFilenames.get( info.getSchedule().getCurrentTick() );
		if (yearly != null
				&& p.csvFileOK(getClass(), yearly, region.getPersisterContextExtra(), X_COL, Y_COL)) {
			fn = yearly;
		} else if (yearInFilename
				&& p.csvFileOK(getClass(), filename, region.getPersisterContextExtra(), X_COL, Y_COL)) {
			fn = filename;
		} else if( reapplyPreviousFile && previousFilename != null ) {
			fn = previousFilename;
		}

		this.log.debug("Read " + fn);


		if( fn != null )
		{
			previousFilename = fn;
			return p.getCSVReader(fn, region.getPersisterContextExtra());
		}


		return null;
	}

	/**
	 * Use the csv file to set the land use change prohibition flag for the cells
	 * @param file
	 * @throws IOException 
	 */
	void applyFile( CsvReader reader ) throws IOException
	{
		//Assume we've got the CSV file, and we've read the headers in
		while( reader.readRecord() ) //For each entry
		{
			if (logger.isDebugEnabled()) {
				logger.trace("Read row " + reader.getCurrentRecord());
			}

			int x = Integer.parseInt(reader.get(X_COL));
			if (xTransformer != null) {
				x = xTransformer.transform(x);
			}

			int y = Integer.parseInt(reader.get(Y_COL));
			if (yTransformer != null) {
				y = yTransformer.transform(y);
			}


			//Try to get the cell
			Cell cell = region.getCell(x, y);

			if( cell == null ) //Complain if we couldn't find it - implies there's data that doesn't line up!
			{
				log.debug("Update for unknown cell:" + reader.get(X_COL) + ", " + reader.get(Y_COL));
				continue; //Go to next line
			}



			boolean masked = reader.get(restrictionColumn).equalsIgnoreCase(maskChar);
			// logger.trace(yn);

			// Renew mask when true (set all false in prePreTick() )  
			if (masked) {
				cell.setObjectProperty(AgentPropertyIds.valueOf(restrictionColumn), masked);
			}


			boolean updated = reader.get(updateColumn).equalsIgnoreCase(updateChar);



			// 	System.out.println(updateFR + frId);
 

			if (updated) { 
 

				LandUseAgent agent = agentFinder.findAgent(cell, Integer.MIN_VALUE, frId);


				for (TakeoverObserver observer : takeoverObserver) {
					observer.setTakeover(this.region, cell.getOwner(), agent);
				}

				for (CellVolatilityObserver o : cellVolatilityObserver) {
					o.increaseVolatility(cell);
				}

				// <- LOGGING
				if (logger.isDebugEnabled()) {
					logger.debug("Ownership from :" + cell.getOwner() + " --> " + agent);
				}
				// LOGGING ->


				// ignore allowance (e.g. PA) setting 

				region.setOwnership(agent, cell);
				

				for (TakeoverObserver observer : takeoverObserver) {
					observer.setTakeover(region, cell.getOwner(), agent);
				}

				for (CellVolatilityObserver o : cellVolatilityObserver) {
					o.increaseVolatility(cell);
				}
				
				//@TODO adapt to the network service  
				//				if (region.getNetworkService() != null) {
				//					if (region.getNetwork() != null) {
				//M
				//						if (region.getGeography() != null && agent instanceof GeoAgent) {
				//							((GeoAgent) agent).addToGeography();
				//						}
				//						if (agent instanceof SocialAgent) {
				//							region.getNetworkService().addAndLinkNode(region.getNetwork(), (SocialAgent) agent);
				//						}
				//					} else {
				//						if (!networkNullErrorOccurred) {
				//							logger.warn(
				//									"Network object not present during creation of new agent (subsequent error messages are suppressed)");
				//							networkNullErrorOccurred = true;
				//						}
				//					}
				//				}


			}





		}
	}

	/**
	 * Load in config stuff
	 */
	@Override
	public void initialise( ModelData data, RunInfo info, Region extent ) throws Exception
	{
		super.initialise( data, info, extent );

		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->
		logger.info("Loading land use restriction CSVs annually (" + restrictionColumn + ")");



		if (updateColumn != null) { 

			agentFinder = new DefaultSocialAgentAssembler();
			try {
				agentFinder.initialise(data, info, extent);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (!this.region.getFunctionalRoleMapByLabel().containsKey(
					updateFR)) {
				logger.warn("Couldn't find FunctionalRole by label: "
						+ updateFR + ". Assigning default!");
				frId = Integer.MIN_VALUE;
			} else {
				frId = this.region.getFunctionalRoleMapByLabel()
						.get(updateFR).getSerialID();
			}
		}


		
		// initialise observers
		for (TakeoverObserver o : takeoverObserver) {
			o.initTakeOvers(extent);
		}
		for (GivingInStatisticsObserver o : statisticsObserver) {
			o.initGivingInStatistic(extent);
		}


	}


	@Override
	public void registerTakeoverOberserver(TakeoverObserver observer) {
		takeoverObserver.add(observer);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Register TakeoverObserver " + observer);
		}
		// LOGGING ->
	}

	@Override
	public void registerGivingInStatisticOberserver(GivingInStatisticsObserver observer) {
		this.statisticsObserver.add(observer);
	}
}
