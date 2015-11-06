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
 * Created by Sascha Holzhauer on 19 Mar 2015
 */
package org.volante.abm.agent.bt;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.property.AgentPropertyRegistry;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.decision.DecisionTrigger;
import org.volante.abm.schedule.RunInfo;

/**
 * @author Sascha Holzhauer
 *
 */
public abstract class AbstractBT implements BehaviouralType {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(AbstractBT.class);

	protected Region region;

	@Attribute(required = true)
	protected String label = "NN";

	@Attribute
	protected int serialID = UNKNOWN_SERIAL;

	@ElementList(name = "triggers", entry = "trigger", required = false)
	protected Set<DecisionTrigger> triggerSet = new LinkedHashSet<DecisionTrigger>();

	@ElementMap(entry = "agentProperty", key = "name", attribute = true, required = false, inline = true)
	protected Map<String, Double> agentProperties2Set = new HashMap<String, Double>();

	protected boolean initialised = false;

	/**
	 * @see org.volante.abm.serialization.Initialisable#initialise(org.volante.abm.data.ModelData,
	 *      org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
	 */
	@Override
	public void initialise(ModelData data, RunInfo info, Region extent)
			throws Exception {
		this.region = extent;
		this.initialised = true;
	}

	public Agent assignNewBehaviouralComp(Agent agent) {
		if (!initialised) {
			// <- LOGGING
			logger.error(this
					+ "> has not yet been initialised (assignNewBehaviouralComp called)");
			throw new IllegalStateException(
					this
					+ "> has not yet been initialised (assignNewBehaviouralComp called)");
			// LOGGING ->
		}
		for (Entry<String, Double> entry : agentProperties2Set.entrySet()) {
			agent.setProperty(AgentPropertyRegistry.get(entry.getKey()), entry
					.getValue().doubleValue());
		}
		return agent;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.label;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#getSerialID()
	 */
	@Override
	public int getSerialID() {
		return this.serialID;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#getRegion()
	 */
	public Region getRegion() {
		return this.region;
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#addDecisionTrigger(org.volante.abm.decision.DecisionTrigger)
	 */
	public void addDecisionTrigger(DecisionTrigger trigger) {
		this.triggerSet.add(trigger);
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#removeDecisionTrigger(java.lang.String)
	 */
	public boolean removeDecisionTrigger(String id) {
		DecisionTrigger trigger2remove = null;
		for (DecisionTrigger trigger : triggerSet) {
			if (trigger.getId().equals(id)) {
				trigger2remove = trigger;
				break;
			}
		}
		return this.triggerSet.remove(trigger2remove);
	}

	/**
	 * @see org.volante.abm.agent.bt.BehaviouralType#getDecisionTriggers()
	 */
	public Iterable<DecisionTrigger> getDecisionTriggers() {
		return this.triggerSet;
	}
}