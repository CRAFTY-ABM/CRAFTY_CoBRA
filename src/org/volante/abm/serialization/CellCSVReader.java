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
package org.volante.abm.serialization;


import java.util.Arrays;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.volante.abm.data.Capital;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.serialization.RegionLoader.CellInitialiser;

import com.csvreader.CsvReader;


/**
 * Reads information from a csv file into the given region
 * 
 * @author dmrust
 * 
 */
public class CellCSVReader implements CellInitialiser {
	@Attribute
	String	csvFile		= "";
	@Attribute(required = false)
	String	agentColumn	= "Agent";
	@Attribute(required = false)
	String	xColumn		= "x";
	@Attribute(required = false)
	String	yColumn		= "y";

	Logger	log			= Logger.getLogger(getClass());

	@Override
	public void initialise(RegionLoader rl) throws Exception {
		ModelData data = rl.modelData;
		boolean hasAgentColumn = true;

		if (!rl.persister.csvFileOK("RegionLoader", csvFile, xColumn, yColumn)) {
			return;
		}
		log.info("Loading cell CSV from " + csvFile);
		CsvReader reader = rl.persister.getCSVReader(csvFile);
		if (!Arrays.asList(reader.getHeaders()).contains(agentColumn)) {
			hasAgentColumn = false;
			log.info("No Agent Column found in CSV file: " + rl.persister.getFullPath(csvFile));
		}
		while (reader.readRecord()) {
			// <- LOGGING
			if (log.isDebugEnabled()) {
				log.debug("Read row " + reader.getCurrentRecord());
			}
			// LOGGING ->

			int x = Integer.parseInt(reader.get("x"));
			int y = Integer.parseInt(reader.get("y"));
			Cell c = rl.getCell(x, y);
			for (Capital cap : data.capitals) {
				String s = reader.get(cap.getName());
				if (s != null) {
					try {
						c.getModifiableBaseCapitals().putDouble(cap, Double.parseDouble(s));
					} catch (Exception exception) {
						log.error("Exception in row " + reader.getCurrentRecord() + "("
								+ exception.getMessage() + ")");
					}
				}
			}
			if (hasAgentColumn) {
				String ag = reader.get(agentColumn);
				if (ag != null) {
					rl.setAgent(c, ag);
				}
			}
		}

		// <- LOGGING
		if (log.isDebugEnabled()) {
			log.debug("Finished reading CSV file " + rl.persister.getFullPath(csvFile));
		}
		// LOGGING ->
	}

	@Override
	public String toString() {
		return "CellCSVReader for " + csvFile;
	}
}
