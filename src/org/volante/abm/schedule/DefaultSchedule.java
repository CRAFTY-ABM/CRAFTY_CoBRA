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
package org.volante.abm.schedule;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.volante.abm.agent.Agent;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.RegionSet;
import org.volante.abm.output.Outputs;
import org.volante.abm.schedule.ScheduleStatusEvent.ScheduleStage;


public class DefaultSchedule implements Schedule {
	Logger							log				= Logger.getLogger(this.getClass());
	RegionSet						regions;
	int								tick			= 0;
	int								targetTick;
	int								endTick;

	List<PreTickAction>				preTickActions	= new ArrayList<PreTickAction>();
	List<PostTickAction>			postTickActions	= new ArrayList<PostTickAction>();

	Outputs							output			= new Outputs();
	private RunInfo					info;

	List<ScheduleStatusListener>	listeners		= new ArrayList<ScheduleStatusListener>();

	/*
	 * Constructors
	 */
	public DefaultSchedule() {
	}

	public DefaultSchedule(RegionSet regions) {
		this();
		this.regions = regions;
	}

	@Override
	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
		this.info = info;
		output = info.outputs;
		info.setSchedule(this);
	}

	@Override
	public void tick() {
		log.info("\n********************\nStart of tick " + tick + "\n********************");
		fireScheduleStatus(new ScheduleStatusEvent(tick, ScheduleStage.PRE_TICK, true));
		info.getPersister().setContext("y", tick + "");
		preTickUpdates();

		fireScheduleStatus(new ScheduleStatusEvent(tick, ScheduleStage.MAIN_LOOP, true));

		// Reset the effective capital levels
		for (Cell c : regions.getAllCells()) {
			c.initEffectiveCapitals();
		}
		// Allow institutions to update capitals
		for (Region r : regions.getAllRegions()) {
			if (r.hasInstitutions()) {
				r.getInstitutions().updateCapitals();
			}
		}

		// Recalculate agent competitiveness and give up
		log.info("Update agents' competitiveness and consider giving up ...");
		for (Agent a : regions.getAllAgents()) {

			a.tickStartUpdate();
			a.updateCompetitiveness();
			a.considerGivingUp();
		}

		// Remove any unneeded agents
		for (Region r : regions.getAllRegions()) {
			r.cleanupAgents();
		}

		// Allocate land
		for (Region r : regions.getAllRegions()) {
			r.getAllocationModel().allocateLand(r);
		}

		// Calculate supply
		log.info("Update agents' supply...");
		for (Agent a : regions.getAllAgents()) {
			a.updateSupply();
		}

		// Allow the demand model to update for global supply supply for each region
		for (Region r : regions.getAllRegions()) {
			r.getDemandModel().updateSupply();
		}

		for (Agent a : regions.getAllAgents()) {
			a.updateCompetitiveness();
			a.tickEndUpdate();
		}


		fireScheduleStatus(new ScheduleStatusEvent(tick, ScheduleStage.POST_TICK, true));
		postTickUpdates();

		output();
		log.info("\n********************\nEnd of tick " + tick + "\n********************");
		fireScheduleStatus(new ScheduleStatusEvent(tick, ScheduleStage.PAUSED, false));
		tick++;
	}

	@Override
	public void finish() {
		output.finished();
	}

	/*
	 * Run controls
	 */

	/**
	 * @see org.volante.abm.schedule.Schedule#runFromTo(int, int)
	 */
	@Override
	public void runFromTo(int start, int end) {
		log.info("Starting run for set number of ticks");
		log.info("Start: " + start + ", End: " + end);

		setStartTick(start);
		setEndTick(end);
		run();
		finish();
	}

	/**
	 * @see org.volante.abm.schedule.Schedule#runUntil(int)
	 */
	@Override
	public void runUntil(int target) {
		setTargetTick(target);
		while (tick <= targetTick) {
			tick();
		}
	}

	/**
	 * @see org.volante.abm.schedule.Schedule#run()
	 */
	@Override
	public void run() {
		while (tick <= endTick) {
			tick();
		}
	}

	/**
	 * @see org.volante.abm.schedule.Schedule#setTargetTick(int)
	 */
	@Override
	public void setTargetTick(int target) {
		this.targetTick = target;
	}

	/**
	 * @see org.volante.abm.schedule.Schedule#setEndTick(int)
	 */
	@Override
	public void setEndTick(int end) {
		this.endTick = end;
	}

	@Override
	public int getEndTick() {
		return endTick;
	}

	@Override
	public void setTargetToNextTick() {
		setTargetTick(tick);
	}

	/*
	 * Pre and post tick events and registering
	 */

	private void preTickUpdates() {
		log.info("Pre Tick\t\t" + hashCode());
		for (PreTickAction p : preTickActions) {
			p.preTick();
		}
	}

	private void postTickUpdates() {
		log.info("Post Tick\t\t" + hashCode());
		for (PostTickAction p : postTickActions) {
			p.postTick();
		}
	}

	@Override
	public void register(Object o) {
		if (o instanceof PreTickAction && !preTickActions.contains(o)) {
			preTickActions.add((PreTickAction) o);
		}
		if (o instanceof PostTickAction && !postTickActions.contains(o)) {
			postTickActions.add((PostTickAction) o);
		}
	}

	private void output() {
		output.doOutput(regions);
	}

	/*
	 * Getters and setters
	 */

	/**
	 * @see org.volante.abm.schedule.Schedule#setStartTick(int)
	 */
	@Override
	public void setStartTick(int tick) {
		this.tick = tick;
	}

	@Override
	public int getCurrentTick() {
		return tick;
	}

	@Override
	public void setRegions(RegionSet regions) {
		this.regions = regions;
	}

	void fireScheduleStatus(ScheduleStatusEvent e) {
		for (ScheduleStatusListener l : listeners) {
			l.scheduleStatus(e);
		}
	}

	@Override
	public void addStatusListener(ScheduleStatusListener l) {
		listeners.add(l);
	}
}
