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
 * Created by Sascha Holzhauer on 29 Jul 2014
 */
package org.volante.abm.output;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Regions;
import org.volante.abm.models.AllocationModel;
import org.volante.abm.models.utils.TakeoverMessenger;
import org.volante.abm.models.utils.TakeoverObserver;
import org.volante.abm.output.TakeoverCellOutputter.RegionFunctionalRole;
import org.volante.abm.schedule.RunInfo;
import org.volante.abm.serialization.GloballyInitialisable;


/**
 * Uses Observer pattern: Registers at those {@link AllocationModel}s that
 * implement {@link TakeoverMessenger}. This is useful since the component that
 * knows about take-overs is an exchangeable component and this way not every
 * {@link AllocationModel} is required to implement the service. Furthermore,
 * this way take-overs only need to be reported in case there is a
 * {@link TakeoverObserver} registered.
 * 
 * NOTE: Assumes that AFT IDs do not leave out any number (i.e. max(AFT-ID) ==
 * length(AFTs))
 * 
 * @author Sascha Holzhauer
 * 
 */
public class TakeoverCellOutputter extends TableOutputter<RegionFunctionalRole> implements
		GloballyInitialisable,
		TakeoverObserver {

	@Attribute(required = false)
	boolean					addTick			= true;

	@Attribute(required = false)
	boolean					addRegion		= true;

	int						maxAftID		= -1;

	Map<Region, int[][]>	numTakeOvers	= new HashMap<Region, int[][]>();

	/**
	 * @see org.volante.abm.serialization.GloballyInitialisable#initialise(org.volante.abm.data.ModelData,
	 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Regions)
	 */
	@Override
	public void initialise(ModelData data, RunInfo info, Regions regions) throws Exception {
		for (Region r : regions.getAllRegions()) {
			if (r.getAllocationModel() instanceof TakeoverMessenger) {
				((TakeoverMessenger) r.getAllocationModel()).registerTakeoverOberserver(this);
			}
		}
	}

	/**
	 * @see org.volante.abm.output.AbstractOutputter#setOutputManager(org.volante.abm.output.Outputs)
	 */
	@Override
	public void setOutputManager(Outputs outputs) {
		super.setOutputManager(outputs);

		if (addTick) {
			addColumn(new TickColumn<RegionFunctionalRole>());
		}

		if (addRegion) {
			addColumn(new RegionsColumn<RegionFunctionalRole>());
		}

		addColumn(new TakeOverAfTColumn());
	}

	public void initTakeOvers(Region region) {
		numTakeOvers.put(region, new int[region.getFunctionalRoles().size()][region
				.getFunctionalRoles().size()]);
		if (maxAftID + 1 < region.getFunctionalRoles().size()) {
			for (int i = maxAftID + 1; i < region.getFunctionalRoles().size(); i++) {
				for (FunctionalRole fr : region.getFunctionalRoleMapByLabel().values()) {
					if (fr.getSerialID() == i) {
						addColumn(new TakeOverColumn(fr.getLabel(), i));
					}
				}
			}
		}
	}

	/**
	 * @see org.volante.abm.models.utils.TakeoverObserver#setTakeover(org.volante.abm.data.Region,
	 *      org.volante.abm.agent.Agent, org.volante.abm.agent.Agent)
	 */
	public void setTakeover(Region region, Agent previousAgent, Agent newAgent) {
		numTakeOvers.get(region)[previousAgent.getFC().getFR().getSerialID()][newAgent
				.getFC().getFR()
				.getSerialID()]++;
	}

	/**
	 * @see org.volante.abm.output.TableOutputter#getData(org.volante.abm.data.Regions)
	 */
	@Override
	public Iterable<RegionFunctionalRole> getData(Regions r) {
		Collection<RegionFunctionalRole> pagents = new HashSet<RegionFunctionalRole>();
		for(Region region : r.getAllRegions()) {
			for (FunctionalRole fr : region.getFunctionalRoleMapByLabel().values()) {
				pagents.add(new RegionFunctionalRole(region, fr));
			}
		}
		return pagents;
	}

	/**
	 * @see org.volante.abm.output.AbstractOutputter#getDefaultOutputName()
	 */
	@Override
	public String getDefaultOutputName() {
		return "TakeOvers";
	}

	/**
	 * @see org.volante.abm.output.TableOutputter#writeData(java.lang.Iterable,
	 *      org.volante.abm.data.Regions)
	 */
	public void writeData(Iterable<RegionFunctionalRole> data, Regions r) throws IOException {
		super.writeData(data, r);
		// reset:
		for (int[][] nums : numTakeOvers.values()) {
			for (int i = 0; i < nums.length; i++) {
				for (int j = 0; j < nums[i].length; j++) {
					nums[i][j] = 0;
				}
			}
		}
	}

	public class RegionFunctionalRole {
		FunctionalRole fRole;
		Region			region;

		public RegionFunctionalRole(Region region, FunctionalRole fRole) {
			this.region = region;
			this.fRole = fRole;
		}

		public Region getRegion() {
			return this.region;
		}

		public FunctionalRole getFunctionalRole() {
			return this.fRole;
		}
	}

	public class TakeOverAfTColumn implements TableColumn<RegionFunctionalRole> {

		public TakeOverAfTColumn() {
		}

		@Override
		public String getHeader() {
			return "AFT";
		}

		@Override
		public String getValue(RegionFunctionalRole regionFRole, ModelData data, RunInfo info,
				Regions rs) {
			return regionFRole.getFunctionalRole().getLabel();
		}
	}

	public class TakeOverColumn implements TableColumn<RegionFunctionalRole> {
		String	aftName	= "";
		int		id;

		public TakeOverColumn(String aftName, int id) {
			this.aftName = aftName;
			this.id = id;
		}

		@Override
		public String getHeader() {
			return this.aftName;
		}

		@Override
		public String getValue(RegionFunctionalRole pragent, ModelData data, RunInfo info,
				Regions rs) {
			if (numTakeOvers.containsKey(pragent.getRegion())
					&& numTakeOvers.get(pragent.getRegion()).length > id) {
				return "" + numTakeOvers.get(pragent.getRegion())[pragent.getFunctionalRole()
						.getSerialID()][id];
			} else {
				return "0";
			}
		}
	}
}
