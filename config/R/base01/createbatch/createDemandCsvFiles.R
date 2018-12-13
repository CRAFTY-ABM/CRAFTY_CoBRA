##########################################################################################
# Create capital CSV files for regions, integrating proportions of Conv/OF farmers FRs and 
# according BTs
#
# Project:		CoBRA-Illustration
# Last update: 	13/09/2016
# Author: 		Sascha Holzhauer
# Instructions:	Run machine-specific SIMP file first (see craftyr documentation)
##########################################################################################

## import configuration
source("C:/Data/LURG/workspace/CRAFTY_CoBRA_Demo/config/R/simp-machine_T440p2LURG.R")
simp$sim$folder 	<- "setA"
setwd(paste(simp$dirs$simp, simp$sim$folder, "createbatch", sep="/"))
source("../simp.R")

## define params
filenamenewdemands <- "C:/Data/LURG/Projects/IMPRESSIONS/Modelling/CRAFTY-CoBRA_Illustration/data/CoBRAillu_OrganicDemand.ods"
ofservices	<- c("Cereal", "Meat")
years = 2000:2013

## initialisation
simp$sim$filepartorder 		<- c("scenario", "U", "datatype", "U", "regions")
demandfiledata <- input_tools_getModelInputFilenames(simp, extension="csv", 
		datatype="demand")[[1]]
demandfiledata$Filename <- gsub("_demand", "_demandOrg", demandfiledata$Filename)

getSpecificFrName <- function(service, specificum = "OF") {
	parts <- splitstring(service, "_")
	return(paste(parts[[1]][1], specificum, "_", parts[[1]][2], sep=""))
}

getOfServiceName <- function(service) {
	return(paste("OF", "_", service, sep=""))
}


for (region in simp$sim$regions) {
	# region = simp$sim$regions[1]
	demands <- data.frame(Year = years)
	
	## read original Volante demand CSV file
	orgdemands <- read.csv(file=demandfiledata[demandfiledata$Region == region, "Filename"])

	## shrink and re-arrange data to simulation time span
	orgdemands$Year <- orgdemands$Year - 10
	orgdemands <- orgdemands[orgdemands$Year <= 2013,]

	## read conv/of demands for cereal and meal from ODS
	require(readODS)
	newdemands <- read.ods(filenamenewdemands, sheet = 1)
	colnames(newdemands) <- newdemands[1,]
	newdemands <- newdemands[6:nrow(newdemands),]
	
	for (service in ofservices) {
		# service <- ofservices[1]
		
		## add conventional FAO data
		demands[, service] <- as.numeric(t(newdemands[newdemands$Country == region & newdemands$Service == service & newdemands$Var == "Demand Conventional", 
			as.integer(colnames(newdemands)) %in% 2000:2013]))
		
		## add OF demand
		demands[, getOfServiceName(service)] <- as.numeric(t(newdemands[newdemands$Country == region & 
						newdemands$Service == service & newdemands$Var == "Demand Organic", 
						as.integer(colnames(newdemands)) %in% years]))

		
	}
	demands <- cbind(demands, orgdemands[, c("Timber", "Recreation")])

	## store CSV file
	outfiledata <- input_tools_getModelInputFilenames(simp, extension="csv", 
			datatype="demand")[[1]]
	write.csv(demands, file=outfiledata[outfiledata$Region == region, "Filename"], row.names = FALSE)	
}
