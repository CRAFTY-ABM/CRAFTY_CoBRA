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
 * Created by Sascha Holzhauer on 05.02.2014
 */
package org.volante.abm.institutions;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.InnovationAgent;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.institutions.innovation.Innovation;
import org.volante.abm.institutions.recruit.InstitutionTargetRecruitment;
import org.volante.abm.schedule.RunInfo;


/**
 * @author Sascha Holzhauer
 *
 */
public class InnovativeInstitution extends AbstractInstitution {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(InnovativeInstitution.class);

	/**
	 * Innovation to release
	 */
	@Element(required = true)
	protected Innovation				innovation = null;

	/**
	 * Component responsible for recruitment of informed agents.
	 */
	@Element(name = "targetRecruitmentComp", required = true)
	protected InstitutionTargetRecruitment targetRecruitmentComp = null;

	/**
	 * Tick of release
	 */
	@Element(required = false)
	protected int innovationReleaseTick = 0;

	/**
	 * @see org.volante.abm.institutions.AbstractInstitution#initialise(org.volante.abm.data.ModelData, org.volante.abm.schedule.RunInfo, org.volante.abm.data.Region)
	 */
	@Override
	public void initialise(ModelData data, RunInfo info, Region extent) throws Exception {
		// <- LOGGING
		logger.info("Initialise " + this);
		// LOGGING ->

		super.initialise(data, info, extent);

		if (this.innovationReleaseTick < info.getSchedule().getCurrentTick()) {
			logger.warn("This innovation's innovation release tick was before it's initialisation. "
					+ "Check parameter innovationReleaseTick!");
		}
		this.targetRecruitmentComp.initialise(data, info, extent);
	}

	/**
	 * @see org.volante.abm.institutions.AbstractInstitution#update()
	 */
	@Override
	public void update() {
		// <- LOGGING
		logger.info("Update " + this);
		// LOGGING ->

		if (rInfo.getSchedule().getCurrentTick() == this.innovationReleaseTick) {
			spreadInnovation();
		}
	}

	/**
	 * @param targetRecruitmentComp
	 */
	public void setInstitutionTargetRecruitment(InstitutionTargetRecruitment targetRecruitmentComp) {
		this.targetRecruitmentComp = targetRecruitmentComp;
	}

	/**
	 * @return applied target recruitment component
	 */
	public InstitutionTargetRecruitment getInstitutionTargetRecruitment() {
		return this.targetRecruitmentComp;
	}

	/**
	 * 
	 */
	protected void spreadInnovation() {
		if (!this.region.getInnovationRegistry().hasInnovationRegistered(
				innovation.getIdentifier())) {
			try {
				innovation.initialise(this.modelData, this.rInfo, this.region);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			logger.warn("Innovation with ID " + innovation.getIdentifier()
					+ " has already been registered. Check IDs!");
		}

		// <- LOGGING
		logger.info("Make agents aware...");
		// LOGGING ->

		Collection<InnovationAgent> innovationAgents = new ArrayList<InnovationAgent>();

		if (innovation.getAffectedAFTs().contains("all")) {
			for (Agent agent : this.region.getAllAgents()) {
				if (agent instanceof InnovationAgent) {
					innovationAgents.add((InnovationAgent) agent);
				}
			}
		} else {
			for (Agent agent : this.region.getAllAgents()) {
				if (agent instanceof InnovationAgent
						&& innovation.getAffectedAFTs().contains(
								agent.getType().getID())) {
					innovationAgents.add((InnovationAgent) agent);
				}
			}

			if (innovationAgents.size() == 0) {
				logger.warn("List of innovative agents is empty - no agents can be affected (affectedAFTs = "
						+ innovation.getAffectedAFTs() + ")!");
			}
		}

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Potential number of agents to inform: "
					+ innovationAgents.size());
		}
		// LOGGING ->

		innovationAgents = this.targetRecruitmentComp.getRecruitedAgents(innovationAgents);

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("Number of agents to inform: "
					+ innovationAgents.size());
		}
		// LOGGING ->

		for (InnovationAgent agent : innovationAgents) {
			agent.makeAware(innovation);
			agent.makeTrial(innovation);
		}
	}

	/**
	 * @return current innovation
	 */
	public Innovation getCurrentInnovation() {
		return this.innovation;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Innovative Institution";
	}
}
