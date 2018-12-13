#######################################################################
# ApplicationScript for Creating PDF Reports of CRAFTY CoBRA ouptut
#
# Project:		TEMPLATE
# Last update: 	26/08/2016
# Author: 		Sascha Holzhauer
#######################################################################

# Only contained when the particular script is only executed on a specific maschine!
# Otherwise. the maschine=specific file needs to be executed before.

source("C:/Data/LURG/workspace/CRAFTY_CoBRA_Demo/config/R/simp-machine_T440p2T440p.R")
require(methods)
library(plyr)


preserve <- list()

runs = 1
rseeds = 0
preserve$task <- "calib"
		
# Usually also in simp.R, but required here to find simp.R
simp$sim$folder 	<- "setA"
preserve$folder 	<- simp$sim$folder 
# simp$dirs$simp is set by maschine-specific file:
setwd(paste(simp$dirs$simp, simp$sim$folder, "report/report", sep="/"))
# usually, the setting/scenario specific simp.R is two levels above:
source("../../simp.R")

for (run in runs) {
	for (rseed in rseeds) {
		# run = 1; rseed = 0
		
		preserve$run = run
		preserve$seed = rseed
		
		
		simp$sim$runids 	<- c(paste(run, rseed, sep="-"))			# run to deal with
		simp$sim$id			<- c(paste(run, rseed, sep="-"))
		
		#######################################################################
		futile.logger::flog.threshold(futile.logger::INFO, name='crafty')
		
		simp$sim$rundesclabel	<- "Runs"
		
		source("./createReport.R")
	}
}