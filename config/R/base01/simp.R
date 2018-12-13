################################################################################
# Version specific SIMulation Properties:
#
# Project:		CRAFTY_CoBRA IMPRESSIONS
# Last update: 	14/10/2016
# Author: 		Sascha Holzhauer
################################################################################

# General SIMulation Properties ################################################

if (!exists("simp")) simp <- craftyr::param_getDefaultSimp()

simp$sim$version				<- "calib"
simp$sim$parentf				<- NULL
simp$sim$scenario				<- "B1"
simp$sim$regionalisation		<- "2"
simp$sim$runids					<- c("0-0")
simp$sim$id 					<- "B1-0"


### Directories ################################################################
simp = shbasic::shbasic_adjust_outputfolders(simp, pattern = "%VFOLDER%", value = 
				if (is.null(simp$sim$folder)) "" else (simp$sim$folder))


### Figure Settings ############################################################
simp$fig$resfactor		<- 3
simp$fig$outputformat 	<- "png"
simp$fig$init			<- craftyr::output_visualise_initFigure
simp$fig$numfigs		<- 1
simp$fig$numcols		<- 1
simp$fig$height			<- 1000
simp$fig$width			<- 1500
simp$fig$splitfigs		<- FALSE
simp$fig$facetlabelsize <- 14

### Batch Run Creation Settings #################################################
simp$batchcreation$scenarios				<- c("B1")
simp$batchcreation$startrun 				<- 0
simp$batchcreation$regionalisations			<- c("2")
simp$batchcreation$percentage_takeovers 	<- c(30) 
simp$batchcreation$competition 				<- "Competition_linear.xml"
simp$batchcreation$institutions				<- "institutions/Institutions_CapitalDynamics.xml"
simp$batchcreation$allocation				<- "GiveUpGiveInAllocationModel.xml"
											
simp$batchcreation$socialnetwork 			<- "SocialNetwork_HDFF.xml"
simp$batchcreation$searchabilities			<- c(30)
simp$batchcreation$inputdatadir 			<- sprintf("%s/data", simp$dirs$project)
simp$batchcreation$agentparam_tmpldir		<- paste(simp$batchcreation$inputdatadir, "/agents/templates/", sep="")
simp$batchcreation$gu_stages				<- c("medium")
simp$batchcreation$gi_stages				<- c("medium")
simp$batchcreation$placeholders				<- c(0)

simp$batchcreation$inputdatadirs$aftparams	<- paste(simp$batchcreation$inputdatadir, "/agents", sep="")
simp$batchcreation$inputdatadirs$production	<- paste(simp$batchcreation$inputdatadir, "/production", sep="")
simp$batchcreation$inputdatadirs$competition	<- paste(simp$batchcreation$inputdatadir, "/competition", sep="")
simp$batchcreation$inputdatadirs$allocation	<- paste(simp$batchcreation$inputdatadir, "/allocation", sep="")
simp$batchcreation$inputdatadirs$worldfile	<- paste(simp$batchcreation$inputdatadir, "/world", sep="")
simp$batchcreation$inputdatadirs$agentdef 	<- paste(simp$batchcreation$inputdatadir, "/agents", sep="")


simp$batchcreation$laramodel				<- "../lara/RegionalLaraModel.xml"

simp$batchcreation$versiondirs$production	<- "./"
simp$batchcreation$versiondirs$competition	<- "./"
simp$batchcreation$versiondirs$allocation	<- "./"
simp$batchcreation$versiondirs$worldfile	<- "./"
simp$batchcreation$versiondirs$agentdef 	<- "./"
simp$batchcreation$versiondirs$btdef		<- "./"
simp$batchcreation$versiondirs$demandfolder	<- "./"


simp$dirs$param$getparamdir <- function(simp, datatype = NULL) {
	return <- paste(simp$dirs$data,
			if (is.null(datatype)) { 
						""
					} else if (datatype %in% c("capitals")) {
						paste("worlds", simp$sim$worldname,
								if(!is.null(simp$sim$regionalisation)) paste("regionalisations", 
											simp$sim$regionalisation, sep="/"), "capitals", sep="/")
					} else if (datatype %in% c("demand")) {
						paste("worlds", simp$sim$worldname,
								if(!is.null(simp$sim$regionalisation)) paste("regionalisations", 
											simp$sim$regionalisation, simp$sim$scenario, sep="/"), sep="/")
					} else if (datatype %in% c("agentparams")) {
						paste("agents", sep="/")
					} else if (datatype %in% c("productivities")) {
						paste("production", sep="/")
					} else if (datatype %in% c("competition")) {
						paste("competition", sep="/")
					} else if (datatype %in% c("runs")) {
						""
					},
			sep="/")
}