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
 */
package org.volante.abm.output;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.volante.abm.agent.Agent;
import org.volante.abm.agent.PotentialAgent;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Regions;
import org.volante.abm.schedule.RunInfo;


/**
 * @author Sascha Holzhauer
 *
 */
public class AggregateAFTCompositionCSVOutputter extends AggregateCSVOutputter {

	Map<Region, Map<PotentialAgent, Double>>	aftData	= new HashMap<Region, Map<PotentialAgent, Double>>();

	protected boolean							initialised	= false;
	/**
	 * @see org.volante.abm.output.AbstractOutputter#getDefaultOutputName()
	 */
	@Override
	public String getDefaultOutputName() {
		return "AggregateAFTComposition";
	}

	public void initAftColumns(Regions regions) {
		Set<PotentialAgent> pAgentSet = new HashSet<PotentialAgent>();
		for (Region r : regions.getAllRegions()) {
			pAgentSet.addAll(r.getPotentialAgents());
			HashMap<PotentialAgent, Double> pMap = new HashMap<PotentialAgent, Double>();
			aftData.put(r, pMap);
		}

		for (PotentialAgent pa : pAgentSet) {
			addColumn(new PotentialAgentColumn(pa));
		}
	}

	/**
	 * Calculates the compositions of AFTs for every region.
	 * 
	 * @see org.volante.abm.output.TableOutputter#doOutput(org.volante.abm.data.Regions)
	 */
	@Override
	public void doOutput(Regions regions) {
		if (!this.initialised) {
			initAftColumns(regions);
			this.initialised = true;
		}

		for (Region r : regions.getAllRegions()) {

			int[] pagentNumbers = new int[r.getPotentialAgents().size()];
			for (Agent a : r.getAgents()) {
				pagentNumbers[a.getType().getSerialID()]++;
			}

			int sum = 0;
			for (int i = 0; i < pagentNumbers.length; i++) {
				sum += pagentNumbers[i];
			}
			for (PotentialAgent p : r.getPotentialAgents()) {
				aftData.get(r).put(p, new Double((double) pagentNumbers[p.getSerialID()] / sum));
			}
		}
		super.doOutput(regions);
	}

	public class PotentialAgentColumn implements TableColumn<Region> {
		PotentialAgent	pagent;

		/**
		 * @param pagent
		 */
		public PotentialAgentColumn(PotentialAgent pagent) {
			this.pagent = pagent;
		}

		/**
		 * @see org.volante.abm.output.TableColumn#getHeader()
		 */
		@Override
		public String getHeader() {
			return "AFT:" + pagent.getSerialID();
		}

		/**
		 * @see org.volante.abm.output.TableColumn#getValue(java.lang.Object,
		 *      org.volante.abm.data.ModelData, org.volante.abm.schedule.RunInfo,
		 *      org.volante.abm.data.Regions)
		 */
		@Override
		public String getValue(Region r, ModelData data, RunInfo info, Regions rs) {
			if (!aftData.get(r).containsKey(pagent)) {
				return doubleFmt.format(Double.NaN);
			} else {
				return doubleFmt.format(aftData.get(r).get(pagent).doubleValue());
			}
		}
	}
}