##########################################################################################
# Create capital CSV files for regions, integrating proportions of Conv/OF farmers FRs and 
# according BTs
#
# Project:		CoBRA-Illustration
# Last update: 	13/09/2016
# Author: 		Sascha Holzhauer
# Instructions:	Run machine-specific SIMP file first (see craftyr documentation)
##########################################################################################

source("C:/Data/LURG/workspace/CRAFTY_CoBRA_Demo/config/R/simp-machine_T440p2LURG.R")

simp$sim$folder 	<- "setA"
setwd(paste(simp$dirs$simp, simp$sim$folder, "createbatch", sep="/"))
source("../simp.R")

afts2ignore = c("Forester", "UNMANAGED")

simp$sim$filepartorder 		<- c("regions", "U", "datatype")
capitalfiledata <- input_tools_getModelInputFilenames(simp, extension="csv", 
		datatype="capitals")[[1]]
capitalfiledata$Filename <- gsub("_capitals", "_CapitalsOrg", capitalfiledata$Filename)

capitalfiledataNew <- input_tools_getModelInputFilenames(simp, extension="csv", 
		datatype="capitals")[[1]]

agentProbs <- list("EE" = data.frame("Conv" = 901, "OF"=116),
		"LV" = data.frame("Conv" = 3692, "OF"=114))

bcClusterNums = list("EE" = data.frame("Conv" = 5, "OF"=6),
		"LV" = data.frame("Conv" = 5, "OF"=6))

for(region in simp$sim$regions) {
	# region <- simp$sim$regions[1]

	of <- prob::probspace(colnames(agentProbs[[region]]),t(agentProbs[[region]][1,]))
	
	prob::sim(of, ntrials=1)
	
	capdata <- read.csv(capitalfiledata[capitalfiledata$Region == region, "Filename"],stringsAsFactors =FALSE)
	
	# capdata <- capdata[1:1000,]
	capdata[capdata$Agent == "VLI_Livestock","Agent"] <- "NC_Livestock"
	
	options(stringsAsFactors = FALSE)
	
	colnames(capdata)[colnames(capdata)=="Agent"] <- "FR"
	modes <- prob::sim(of, ntrials=sum(!capdata$FR %in% afts2ignore))
	frorg <- do.call(rbind, strsplit(capdata[!capdata$FR %in% afts2ignore, "FR"],"_"))
	
	capdata[!capdata$FR %in% afts2ignore, "FR"] <- paste(frorg[,1], modes$x, "_", frorg[,2], sep="")
	
	capdata[,"BT"] <- 0
	capdata[grepl("Conv", capdata$FR),"BT"] <- sample(as.integer(bcClusterNums[[region]]["Conv"]), 
			size = sum(grepl("Conv",capdata$FR)), replace = TRUE)
	
	capdata[grepl("OF",capdata$FR),"BT"] <- sample(as.integer(bcClusterNums[[region]]["OF"]), 
			size = sum(grepl("OF",capdata$FR)), replace = TRUE) + 5
	
	write.csv(capdata[,-1], file = capitalfiledataNew[capitalfiledata$Region == region, "Filename"], row.names = FALSE)
}


