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
 * Created by Sascha Holzhauer on 06.03.2014
 */
package org.volante.abm.decision.innovations;

/**
 * @author Sascha Holzhauer
 *
 */
public class SimpleInnovationStatus implements InnovationStatus {

	protected InnovationState	state					= InnovationStates.UNAWARE;

	double	adoptedNeighbourShare	= Double.NaN;

	/**
	 * @return the adoptedNeighbourShare
	 */
	@Override
	public double getAdoptedNeighbourShare() {
		return adoptedNeighbourShare;
	}

	/**
	 * @param adoptedNeighbourShare
	 *        the adoptedNeighbourShare to set
	 */
	@Override
	public void setAdoptedNeighbourShare(double adoptedNeighbourShare) {
		this.adoptedNeighbourShare = adoptedNeighbourShare;
	}

	/**
	 * @see org.volante.abm.decision.innovations.InnovationStatus#getState()
	 */
	@Override
	public InnovationState getState() {
		return this.state;
	}

	/**
	 * @see org.volante.abm.decision.innovations.InnovationStatus#aware()
	 */
	@Override
	public void aware() {
		this.state = InnovationStates.AWARE;
	}

	/**
	 * @see org.volante.abm.decision.innovations.InnovationStatus#trial()
	 */
	@Override
	public void trial() {
		this.state = InnovationStates.TRIAL;
	}

	/**
	 * @see org.volante.abm.decision.innovations.InnovationStatus#adopt()
	 */
	@Override
	public void adopt() {
		this.state = InnovationStates.ADOPTED;
	}

	/**
	 * @see org.volante.abm.decision.innovations.InnovationStatus#reject()
	 */
	@Override
	public void reject() {
		this.state = InnovationStates.REJECTED;
	}
}