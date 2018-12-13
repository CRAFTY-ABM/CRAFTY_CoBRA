################################################################################
# General SIMulation Properties:
#
# Project:		CRAFTY_CoBRA IMPRESSIONS
# Last update: 	14/10/2016
# Author: 		Sascha Holzhauer
################################################################################

#### COMMON PACKAGES ###########################################################
library(craftyr)
library(kfigr)
library(shbasic)

#### FUNCTIONS #################################################################
#eg. for simp$dirs$param$getparamdir

### Simulation Data ############################################################
if (!exists("simp")) simp <-  craftyr::param_getDefaultSimp()

simp$sim$worldname 			<- "EU"
simp$sim$version			<- NULL
simp$sim$allocversion		<- "NN"
simp$sim$scenario			<- "Baseline"
simp$sim$regionalisation	<- "28"
simp$sim$regions			<- c("AT", "BE", "BG", "CZ", "DE", "DK", "EE", "EL", "ES",
		"FI", "FR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", 
		"NL", "PL", "PT", "RO", "SE", "SI", "SK", "UK")
simp$sim$runids				<- c("0-0")
simp$sim$filepartorder_demands <- c("scenario", "U", "datatype", "U", "regions")
simp$sim$hasregiondir		<- TRUE

### Directories ################################################################
simp$dirs$output$data		<- paste(simp$dirs$outputdir, "Data/", sep="")
simp$dirs$output$rdata		<- paste(simp$dirs$outputdir, "RData/", sep="") 
simp$dirs$output$raster		<- paste(simp$dirs$outputdir, "Raster/", sep="") 
simp$dirs$output$figures	<- paste(simp$dirs$outputdir, "Figures/", sep="")
simp$dirs$output$reports	<- paste(simp$dirs$outputdir, "Reports/", sep="")

### CSV Column Names ###########################################################
simp$csv$cname_region 		<- "Region"
simp$csv$cname_tick 		<- "Tick"
simp$csv$cname_aft 			<- "Agent"
simp$csv$cname_x			<- "X"
simp$csv$cname_y			<- "Y"

### Model Data ################################################################
simp$mdata$capitals 		<- c("Crop.productivity", "Forest.productivity", "Grassland.productivity",
                           "Financial.capital","Human.capital", "Social.capital","Manufactured.capital","Urban.capital")
simp$mdata$services			<- c("Meat", "Crops" , "Diversity", "Timber", "Carbon", "Urban", "Recreation")

simp$mdata$aftNames			<- c("-1" = "Unmanaged", "0" = 'Ext_AF', "1" = 'IA', 
								  "2" = 'Int_AF', "3" = 'Int_Fa', "4" = 'IP', "5" = 'MF', "6" = 'Min_man', 
								  "7" = 'Mix_Fa', "8" = 'Mix_For', "9" = 'Mix_P', "10" = 'Multifun', "11" = 'P-Ur', 
								  "12" = 'UL', "13" = 'UMF', "14" = 'Ur', "15" = 'VEP', "16" = 'EP')

simp$dirs$param$getparamdir <- function(simp, datatype = NULL) {
	return <- paste(simp$dirs$data,
			if (is.null(datatype)) { 
						simp$sim$folder
					} else if (datatype %in% c("capitals")) {
						paste("worlds", simp$sim$worldname,
								if(!is.null(simp$sim$regionalisation)) paste("regionalisations", 
											simp$sim$regionalisation, sep="/"), "capitals", sep="/")
					} else if (datatype %in% c("demand")) {
						paste(simp$sim$folder, "worlds", simp$sim$worldname,
								if(!is.null(simp$sim$regionalisation)) paste("regionalisations", 
											simp$sim$regionalisation, simp$sim$scenario, sep="/"), sep="/")
					} else if (datatype %in% c("agentparams")) {
						paste(simp$sim$folder, "agents", sep="/")
					} else if (datatype %in% c("productivities")) {
						paste(simp$sim$folder, "production", sep="/")
					} else if (datatype %in% c("competition")) {
						paste(simp$sim$folder, "competition", sep="/")
					} else if (datatype %in% c("runs")) {
						simp$sim$folder
					},
			sep="/")
}
						  
### Figure Settings ###########################################################
simp$fig$resfactor			<- 2
simp$fig$outputformat 		<- "png" #"jpeg"
simp$fig$init				<- craftyr::output_visualise_initFigure
simp$fig$numfigs			<- 1
simp$fig$numcols			<- 1
simp$fig$height				<- 500
simp$fig$width				<- 500
simp$fig$splitfigs			<- FALSE
simp$fig$facetlabelsize 	<- 14

simp$colours$AFT <- c(
		"-1" = "black", 
		"0" = 'deepskyblue4',
		"1" = 'deepskyblue', 
		"2" = 'darkorchid4',
		"3" = 'darkorchid1',
		"4" = 'orange1',
		"5" = 'lightgoldenrod',
		"6" = 'indianred4', 
		"7" = 'indianred1',
		"8" = 'green4',
		"9" = 'deepskyblue4',
		"10" = 'deepskyblue', 
		"11" = 'darkorchid4',
		"12" = 'darkorchid1',
		"13" = 'orange1',
		"14" = 'lightgoldenrod',
		"15" = 'indianred4'
)
