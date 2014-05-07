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
package org.volante.abm.visualisation;


import javax.swing.JComponent;

import org.simpleframework.xml.Root;
import org.volante.abm.data.Cell;
import org.volante.abm.data.ModelData;
import org.volante.abm.data.Regions;
import org.volante.abm.schedule.PostTickAction;
import org.volante.abm.schedule.RunInfo;


@Root
public interface Display extends PostTickAction {
	public void update();

	public String getTitle();

	public JComponent getDisplay();

	public void initialise(ModelData data, RunInfo info, Regions extent) throws Exception;

	public void setModelDisplays(ModelDisplays d);

	public void addCellListener(Display d);

	public void cellChanged(Cell c);

	public void fireCellChanged(Cell c);
}
