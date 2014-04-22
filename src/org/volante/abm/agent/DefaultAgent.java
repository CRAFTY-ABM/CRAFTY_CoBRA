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
package org.volante.abm.agent;

import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Region;
import org.volante.abm.data.Service;
import org.volante.abm.models.ProductionModel;
import org.volante.abm.models.nullmodel.NullProductionModel;

import com.moseph.modelutils.fastdata.DoubleMap;
import com.moseph.modelutils.fastdata.UnmodifiableNumberMap;
/**
 * This is a default agent
 * @author jasper
 *
 */
public class DefaultAgent extends AbstractAgent
{
	/*
	 * Characteristic fields (define an agent)
	 */
	protected ProductionModel	production	= NullProductionModel.INSTANCE;
	protected double			givingUp	= -Double.MAX_VALUE;
	protected double			givingIn	= Double.MAX_VALUE;
	protected PotentialAgent type;
	public DefaultAgent() {}
	public DefaultAgent( String id, ModelData data )
	{
		this.id = id;
		initialise( data );
	}
	
	public DefaultAgent( PotentialAgent type, ModelData data, Region r, ProductionModel prod, double givingUp, double givingIn )
	{
		this.type = type;
		this.region = r;
		this.production = prod;
		this.givingUp = givingUp;
		this.givingIn = givingIn;
		initialise( data );
	}
	
	public DefaultAgent( PotentialAgent type, String id, ModelData data, Region r, ProductionModel prod, double givingUp, double givingIn )
	{
		this( type, data, r, prod, givingUp, givingIn );
		this.id = id;
	}
	
	public void initialise( ModelData data )
	{
		productivity = new DoubleMap<Service>( data.services );
	}

	@Override
	public void updateSupply()
	{
		productivity.clear();
		for( Cell c : cells )
		{
			production.production( c, c.getModifiableSupply() );
			c.getSupply().addInto( productivity );
		}
	}
	
	@Override
	public void considerGivingUp()
	{
		if( currentCompetitiveness < givingUp ) {
			giveUp();
		}
	}
	
	@Override
	public boolean canTakeOver( Cell c, double incoming ) 
	{
		return incoming > (getCompetitiveness() + givingIn);
	}

	@Override
	public UnmodifiableNumberMap<Service> supply( Cell c ) 
	{ 
		DoubleMap<Service> prod = productivity.duplicate();
		production.production( c, prod ); 
		return prod;
	}
	public void setProductionFunction( ProductionModel f ) { this.production = f; }
	public ProductionModel getProductionFunction() { return production; }
	public void setGivingUp( double g ) { this.givingUp = g; }
	public void setGivingIn( double g ) { this.givingIn = g; }
	@Override
	public double getGivingUp() { return givingUp; }
	@Override
	public double getGivingIn() { return givingIn; }
	@Override
	public PotentialAgent getType() { return type; }
	
	@Override
	public String infoString()
	{
		return "Giving up: " + givingUp + ", Giving in: " + givingIn + ", nCells: " + cells.size();
	}

	@Override
	public int getMilieuGroup() {
		return 1;
	}

	@Override
	public String getAgentId() {
		return this.id;
	}
}
