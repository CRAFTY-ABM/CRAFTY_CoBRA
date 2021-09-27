/**
 * 
 */
package org.volante.abm.institutions;


import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.volante.abm.agent.Agent;
import org.volante.abm.agent.fr.FunctionalRole;
import org.volante.abm.data.Cell;
 
import org.volante.abm.example.AgentPropertyIds;
 


/**
 * @author Bumsuk Seo
 * 		   Sascha Holzhauer
 *
 */

public class FrSpatialRestrictingInstitution extends FrRestrictingInstitution {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(FrRestrictingInstitution.class);

	@Element(required = true)
	protected String spatialLayer;


	/**
	 * Checks configured restriction CSV file.
	 * 
	 * @param fr
	 * @param cell
	 * @return true if the given {@link FunctionalRole} is allowed to occupy the given cell.
	 */
	@Override
	public boolean isAllowed(FunctionalRole fr, Cell cell) {

		boolean masked = (boolean) cell.getObjectProperty(AgentPropertyIds.valueOf(spatialLayer));

		if (!masked) {
			return true; // allowed if not masked 
		}
 
		String label2request =
				(cell.getOwner().getFC().getFR().getLabel().equals(Agent.NOT_MANAGED_FR_ID) ? this.labelUnmanaged
						: cell.getOwner().getFC().getFR().getLabel());
		if (!restrictedRoles.contains(label2request, fr.getLabel())) {
			// <- LOGGING
			logger.warn("Allowed Types Map does not contain an entry for " + label2request
					+ " > " + fr.getLabel() + "! Assuming 0.");
			// LOGGING ->
			return true;
		} else {
			
			boolean allowed = restrictedRoles.get(label2request, fr.getLabel()) <= 0;
			
			if (!allowed) { 
			// <- LOGGING
			logger.trace( fr.getLabel() + ">" + label2request +" not allowed due to " + spatialLayer);
			// LOGGING ->
			}
			
			return allowed;
		}

	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ("FR spatial restricting institution (" + spatialLayer + ")") ;
	}

}
