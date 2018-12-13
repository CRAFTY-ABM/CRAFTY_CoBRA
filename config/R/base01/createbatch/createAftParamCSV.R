#######################################################################
# Prameter Creation Script (PSC) for AFT parameter CSV files.
#
# Input:		Parameter meta definitions
# Output:		AftParams_<AFT>.csv files
#
# Project:		CRAFTY_CoBRA IMPRESSIONS
# Setting:		base01
# Last update: 	14/10/2016
# Author: 		Sascha Holzhauer
#######################################################################


#######################################################################
# Factors for variations
#######################################################################

params <- c('givingIn',	'givingInDistributionMean',	'givingInDistributionSD', 'givingUp',
		'givingUpDistributionMean', 'givingUpDistributionSD', 'serviceLevelNoiseMin', 'serviceLevelNoiseMax')

capitalSensitivity <- "medium"

# medium applies a neutral factor of 1, low and high apply according factors as defined below:
giStages <- c("medium")
guStages <- c("medium")


########### GU alteration factors #######################################
factorMatrix <- matrix(rep(1.0, times=length(simp$mdata$aftNames)*length(params)), ncol=length(params))
colnames(factorMatrix) <- params
rownames(factorMatrix) <- simp$mdata$aftNames

# GI and GU need do be defined separated because of cross variations:
paramFactorsGi <- list()
paramFactorsGi[["medium"]] <- factorMatrix
paramFactorsGi[["low"]] <- factorMatrix
paramFactorsGi[["low"]][,"givingIn"] <- 0.8
paramFactorsGi[["low"]][,"givingInDistributionMean"] <- 0.8
paramFactorsGi[["high"]] <- factorMatrix
paramFactorsGi[["high"]][,"givingIn"] <- 1.2
paramFactorsGi[["high"]][,"givingInDistributionMean"] <- 1.2

paramFactorsGu <- list()
paramFactorsGu[["medium"]] <- factorMatrix
paramFactorsGu[["low"]] <- factorMatrix
paramFactorsGu[["low"]][,"givingUp"] <- 0.8
paramFactorsGu[["low"]][,"givingUpDistributionMean"] <- 0.8
paramFactorsGu[["high"]] <- factorMatrix
paramFactorsGu[["high"]][,"givingUp"] <- 1.2
paramFactorsGu[["high"]][,"givingUpDistributionMean"] <- 1.2


# Define deviations for factors < 1
aftFactorsLow <- c()

# Define deviations for factors > 1
aftFactorsHigh <- c()

#######################################################################
# File creation
#######################################################################

###### Load Template
tData <- read.csv(paste(simp$batchcreation$agentparam_tmpldir, "AFT.csv", sep=""))

# uncomment for testing purposes:
# aft = afts[1]
# capSense = capitalSensitivity[1]
# scenario = scenarios[1]
# multifunc = multifuncionality[1]
# giStage = giStages[1]
# guStage = guStages[1]


adaptG <- function(factor, aft) {
	if(factor < 1) {
		factor = factor * if (aft %in% names(aftFactorsLow)) aftFactorsLow[[aft]] else 1
	} else {
		if(factor > 1) {
			factor = factor * if (aft %in% names(aftFactorsHigh)) aftFactorsHigh[[aft]] else 1
		} else factor = 1
	}
	factor
}

afts <- simp$mdata$aftNames[!simp$mdata$aftNames %in% "Unmanaged"]

for (aft in afts) {
	data <- data.frame(stringsAsFactors=FALSE)
	aftParamId = -1
	for (scenario in simp$batchcreation$scenarios) {
			for (giStage in giStages) {
				for (guStage in guStages) {	
					aftParamId = aftParamId + 1
		
					d <- c()
					d["aftParamId"] <- aftParamId
					
					giFactor <- paramFactorsGi[[giStage]][aft,]
					giFactor <- unlist(lapply(giFactor, adaptG, aft))
					guFactor <- paramFactorsGu[[guStage]][aft,]
					guFactor <- unlist(lapply(guFactor, adaptG, aft))
					
					d <- c(d,tData[tData$Scenario == scenario & tData$AFT == aft,c(-1,-2,-3)] * giFactor * guFactor)
			
					d["productionCsvFile"] <- paste(simp$batchcreation$versiondirs$production, 
							"/production/", aft, ".csv", sep="")
					
					data <- rbind(data, as.data.frame(d, stringsAsFactors=FALSE))
				}
		}
	}	
	filename = paste(simp$batchcreation$inputdatadirs$aftparams,
			'/AftParams_', aft, '.csv', sep='')
	
	futile.logger::flog.info("Write AFT param file %s...",
				filename,
				name = "template.create.aftparam")
		
	shbasic::sh.ensurePath(filename, stripFilename = TRUE)
	write.csv(data, filename, row.names = FALSE)
}