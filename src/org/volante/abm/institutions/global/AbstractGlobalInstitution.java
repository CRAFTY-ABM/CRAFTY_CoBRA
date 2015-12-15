/**
 * This file is part of
 * 
 * CRAFTY - Competition for Resources between Agent Functional TYpes
 *
 * Copyright (C) 2015 School of GeoScience, University of Edinburgh, Edinburgh, UK
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
 * Created by Sascha Holzhauer on 29 Jul 2015
 */
package org.volante.abm.institutions.global;


import java.util.HashSet;
import java.util.Set;

import org.volante.abm.agent.fr.FunctionalComponent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.ScenarioLoader;

import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;

/**
 * @author Sascha Holzhauer
 *
 */
public abstract class AbstractGlobalInstitution implements GlobalInstitution {

	/**
	 * @see org.volante.abm.institutions.Institution#adjustCapitals(org.volante.abm.data.Cell)
	 */
	@Override
	public void adjustCapitals(Cell c) {
		// do nothing
	}

	/**
	 * @see org.volante.abm.institutions.Institution#adjustCompetitiveness(org.volante.abm.agent.fr.FunctionalRole, org.volante.abm.data.Cell, com.moseph.modelutils.fastdata.UnmodifiableNumberMap, double)
	 */
	@Override
	public double adjustCompetitiveness(FunctionalRole agent, Cell location, UnmodifiableNumberMap<Service> provision,
			double competitiveness) {
		// return unchanged
		return competitiveness;
	}

	/**
	 * @see org.volante.abm.institutions.Institution#isAllowed(org.volante.abm.agent.fr.FunctionalComponent, org.volante.abm.data.Cell)
	 */
	@Override
	public boolean isAllowed(FunctionalComponent agent, Cell location) {
		// why not?
		return true;
	}

	/**
	 * @see org.volante.abm.institutions.Institution#isAllowed(org.volante.abm.agent.fr.FunctionalRole,
	 *      org.volante.abm.data.Cell)
	 */
	@Override
	public boolean isAllowed(FunctionalRole fr, Cell location) {
		return true;
	}

	public Set<FunctionalRole> getFrsExludedFromGivingIn() {
		return new HashSet<>();
	}

	/**
	 * @see org.volante.abm.institutions.Institution#update()
	 */
	@Override
	public void update() {
		// nothing to do
	}

	/**
	 * @see org.volante.abm.serialization.Initialisable#initialise(org.volante.abm.data.ModelData, org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
	 */
	@Override
	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
		// nothing to do
	}


	/**
	 * In case of overriding, make sure to call
	 * {@link AbstractGlobalInstitution#initialise(RunInfo, ModelData, ScenarioLoader)} or register at
	 * {@link GlobalInstitutionsRegistry} and regions.
	 * 
	 * @see org.volante.abm.institutions.global.GlobalInstitution#initialise(RunInfo, ModelData, ScenarioLoader)
	 */
	@Override
	public void initialise(RunInfo rinfo, ModelData mdata, ScenarioLoader sloader) {
		// register
		GlobalInstitutionsRegistry.getInstance().registerGlobalInstitution(this);
		for (Region region : sloader.getRegions().getAllRegions()) {
			region.getInstitutions().addInstitution(this);
		}
	}
}
