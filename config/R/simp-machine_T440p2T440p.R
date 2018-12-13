###############################################################################
# Machine=specific SIMP definition
#
# NOTE: Changes in super-level parameters that are used to derive further
# parameters need to trigger a re-evaluation of the derived parameters!
#
# Project:		CRAFTY_CoBRA_Impressions
# Last update: 	14/10/2016
# Author: 		Sascha Holzhauer
################################################################################

### Clean/Remove existing definitions ##########################################
rm(list=ls(name=globalenv(), pattern="[^{preserve}]"), envir=globalenv())

### Project Root ###############################################################
project			<- "C:/Data/LURG/workspace/CRAFTY_CoBRA_Impressions/"

if (!exists("preserve")) {
	preserve <- list()
	preserve$run = 0
}
#### Load default SIMP #########################################################
source(paste(project, "/config/R/simpBasic.R", sep=""))
simp$dirs$project <- project

#### Set path to itself ########################################################
simp$simpDefinition <- paste(simp$dirs$project, "config/R/simp-machine_T440p2T440p.R", sep="")

### Directories ###############################################################
simp$dirs$data 				<- paste(simp$dirs$project, "data/", sep="")
simp$dirs$simp				<- paste(simp$dirs$project, "./config/R/", sep="")

simp$dirs$outputdirWS		<- paste(simp$dirs$project, "/output/%VFOLDER%/", sep="")
simp$dirs$outputdir			<- simp$dirs$outputdirWS

simp$dirs$outputdirlocal	<- "C:/Data/LURG/Projects/IMPRESSIONS/Modelling/CRAFTY-CoBRA_Impressions/output/%VFOLDER%/"

simp$dirs$output$simulation	<- paste(simp$dirs$outputdirlocal, sep="")
simp$dirs$output$data		<- paste(simp$dirs$outputdirlocal, "Data/", sep="")
simp$dirs$output$rdata		<- paste(simp$dirs$outputdirlocal, "RData/", sep="") 

simp$dirs$output$simulation	<- paste(simp$dirs$outputdirWS, sep="")
simp$dirs$output$data		<- paste(simp$dirs$project, "Data/", sep="")
simp$dirs$output$rdata		<- paste(simp$dirs$outputdir, "RData/", sep="") 
simp$dirs$output$raster		<- paste(simp$dirs$outputdir, "Raster/", sep="") 
simp$dirs$output$figures	<- paste(simp$dirs$outputdir, "Figures/", sep="")
simp$dirs$output$reports	<- paste(simp$dirs$outputdir, "Reports/", sep="")
simp$dirs$output$tables		<- paste(simp$dirs$outputdir, "/Tables/", sep="")
simp$dirs$output$csv		<- paste(simp$dirs$outputdir, "/CSV/", sep="")
simp$dirs$output$runinfo	<- "C:/Data/LURG/Projects/IMPRESSIONS/Modelling/CRAFTY-CoBRA_Impressions/CRAFTY_CoBRA-Illustration_Runs.ods"

futile.logger::flog.info("Current working directory: %s",
			getwd(),
			name = "netsens.simp")
