#######################################################################
# ApplicationScript for Storing CRAFTY ouptut as R data (both
# raw and aggregated) when peforming as cluster job via craftybatch.py.
#
# Project:		TEMPLATE
# Last update: 	26/08/2016
# Author: 		Sascha Holzhauer
#######################################################################
filepartorder
# Only contained when the particular script is only executed on a specific maschine!
# Otherwise. the maschine=specific file needs to be executed before.

source("C:/Data/LURG/workspace/CRAFTY_CoBRA_Demo/config/R/simp-machine_T440p2T440p.R")
require(methods)


# Usually also in simp.R, but required here to find simp.R
simp$sim$folder 	<- "setA"	
runs = c(1)
rseeds = c(0)

########################## End of Parameters

# simp$dirs$simp is set by maschine-specific file:
setwd(paste(simp$dirs$simp, simp$sim$folder, "process", sep="/"))
# usually, the setting/scenario specific simp.R is two levels above:
source("../simp.R")

simp$sim$task		<- paste(runs[1], rseeds[1], sep="-") # Name of surounding folder, usually a description of task 

preserve <- list()
preserve$task 		<- simp$sim$task



library(plyr)

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
		
		###########################################################################
		### Read and Aggregate CSV data
		###########################################################################
		
		aggregationFunction <- function(simp, data) {
			#print(str(data))
			plyr::ddply(data, .(Runid,Region,Tick,LandUseIndex,Competitiveness), .fun=function(df) {
						df$Counter <- 1
						with(df, data.frame(
										Runid				= unique(Runid),
										Region				= unique(Region),
										Tick				= mean(Tick),
										LandUseIndex		= mean(LandUseIndex),
										Competitiveness		= mean(Competitiveness),
										AFT					= sum(Counter),
										Service.Meat		= sum(Service.Meat), 
										Service.Cereal		= sum(Service.Cereal),
										Service.Recreation 	= sum(Service.Recreation),
										Service.Timber		= sum(Service.Timber)) )
					})
		}
		
		data <- input_csv_data(simp, dataname = NULL, datatype = "Cell", columns = c("Service.Meat", "Service.Cereal",
						"Service.Recreation", "Service.Timber",  "LandUseIndex","Competitiveness", "AFT"), pertick = TRUE,
				tickinterval = simp$csv$tickinterval_cell,
				attachfileinfo = TRUE, bindrows = TRUE,
				aggregationFunction = aggregationFunction,
				skipXY = TRUE)
		
		rownames(data) <- NULL
		dataAgg <- data
		input_tools_save(simp, "dataAgg")
		
		###########################################################################
		### Store PreAlloc Data for Maps etc. 
		###########################################################################
#		simp$sim$filepartorder	<- c("regions", "D", "datatype")
#		csv_preAllocTable <- input_csv_prealloccomp(simp)
#		input_tools_save(simp, "csv_preAllocTable")
		
		
		###########################################################################
		### Take Overs
		###########################################################################
		
		dataTakeOvers <- input_csv_data(simp, dataname = NULL, datatype = "TakeOvers", pertick = FALSE,
				bindrows = TRUE,
				skipXY = TRUE)
		
		input_tools_save(simp, "dataTakeOvers")
		
		
		###########################################################################
		### AFT composition data
		###########################################################################
		
		dataAggregateAFTComposition <- input_csv_data(simp, dataname = NULL, datatype = "AggregateAFTComposition", pertick = FALSE,
				bindrows = TRUE,
				skipXY = TRUE)
		
		input_tools_save(simp, "dataAggregateAFTComposition")
		
		###########################################################################
		### AFT competitiveness data
		###########################################################################
		
		dataAggregateAFTCompetitiveness <- input_csv_data(simp, dataname = NULL, datatype = "AggregateAFTCompetitiveness", pertick = FALSE,
				bindrows = TRUE,
				skipXY = TRUE)
		
		input_tools_save(simp, "dataAggregateAFTCompetitiveness")
		
		###########################################################################
		### Aggregated Demand and Supply
		###########################################################################
		
		dataAggregateSupplyDemand <- input_csv_data(simp, dataname = NULL, datatype = "AggregateServiceDemand",
				pertick = FALSE, bindrows = TRUE)
		input_tools_save(simp, "dataAggregateSupplyDemand")
		
		
		###########################################################################
		### Giving In Statistics
		###########################################################################
		
		csv_aggregateGiStatistics <- craftyr::input_csv_data(simp, dataname = NULL, datatype = "GivingInStatistics",
				pertick = FALSE,
				bindrows = TRUE,
				skipXY = TRUE)
		craftyr::input_tools_save(simp, "csv_aggregateGiStatistics")
		
		
		###########################################################################
		### Aggregated Connectivity
		###########################################################################
		
#		dataAggregateConnectivity <- input_csv_data(simp, dataname = NULL, datatype = "LandUseConnectivity",
#				pertick = FALSE, bindrows = TRUE)
#		input_tools_save(simp, "dataAggregateConnectivity")
		
		
		###########################################################################
		### Store Actions
		###########################################################################
		
		dataActions <- input_csv_data(simp, dataname = NULL, datatype = "Actions",
				pertick = FALSE, bindrows = TRUE)
		input_tools_save(simp, "dataActions")
		
		
		###########################################################################
		### Store Cell Data for Maps etc. 
		###########################################################################
		
		data <- input_csv_data(simp, dataname = NULL, datatype = "Cell", columns = "LandUseIndex",
				pertick = TRUE, attachfileinfo = TRUE, tickinterval = 1)
		data <- do.call(rbind.data.frame, data)
		
		csv_LandUseIndex_rbinded <- data
		input_tools_save(simp, "csv_LandUseIndex_rbinded")
		
		
		###########################################################################
		### Store PerceivedSupplyDemandGaps
		###########################################################################

#		csv_PerceivedSupplyDemandGapTimber <- input_csv_data(simp, dataname = NULL, 
#				datatype = "GenericTableOutputter-PerceivedSupplyDemandGapTimber", pertick = FALSE,
#				bindrows = TRUE,
#				skipXY = TRUE)
#		if (nrow(csv_PerceivedSupplyDemandGapTimber) > 0) { 
#			csv_PerceivedSupplyDemandGapTimber$Service <- "Timber"
#		}
#		input_tools_save(simp, "csv_PerceivedSupplyDemandGapTimber")
#		
#		csv_PerceivedSupplyDemandGapCereal <- input_csv_data(simp, dataname = NULL, 
#				datatype = "GenericTableOutputter-PerceivedSupplyDemandGapCereal", pertick = FALSE,
#				bindrows = TRUE,
#				skipXY = TRUE)
#		if (nrow(csv_PerceivedSupplyDemandGapCereal) > 0) {
#			csv_PerceivedSupplyDemandGapCereal$Service <- "Cereal"
#		}
#		input_tools_save(simp, "csv_PerceivedSupplyDemandGapCereal")
		
		###########################################################################
		### Draw Maps
		###########################################################################
		simp$fig$height			<- 400
		simp$fig$width			<- 300
		
		hl_aftmap(simp, ncol = 2, ggplotaddon = ggplot2::theme(legend.position = c(0.85, 0),
						legend.justification = c(1.0, 0), legend.key.size=grid::unit(0.8, "lines")), 
						firsttick = 2000, secondtick = 2010)
		
#		simp$fig$maptitle <- "WorldX-AFTsB"
#		hl_aftmap(simp, ncol = 2, ggplotaddon = ggplot2::theme(legend.position = c(0.85, 0),
#						legend.justification = c(1.0, 0), legend.key.size=grid::unit(0.8, "lines")),
#						firsttick = 2000, secondtick = 2010)
		
		###########################################################################
		### Store Changes in Land Use
		###########################################################################
		
#		simp$sim$regions	<- "Unknown"
#		simp$sim$filepartorder <- c("runid", "D", "tick", "D", "regions", "D", "datatype", "D", "dataname")
#		data_landuse_changes = data.frame(
#					Tick = (simp$sim$starttick + 1):simp$sim$endtick,
#					Changes = metric_rasters_changes(simp, dataname = "raster_landUseIndex"),
#					Runid = simp$sim$runids)
#		input_tools_save(simp, "data_landuse_changes")
		
		###########################################################################
		### Calculate Metrics
		###########################################################################
		simp$sim$regions	<- "Unknown"

		metrics <- rbind(
#				# require raster output:
#				metric_rasters_changedcells(simp, aft = NULL, dataname = "raster_landUseIndex"),
#				metric_rasters_changes(simp, dataname = "raster_landUseIndex"),
#				metric_rasters_global_patches(simp, dataname = "raster_landUseIndex", 
#						directions = 8, relevantafts = c("NC_Cereal", "NC_Livestock")),
#				metric_rasters_global_patches(simp, dataname = "raster_landUseIndex", 
#						directions = 8, relevantafts = c("C_Cereal", "C_Livestock")),
#				metrics_rasters_connectivity(simp, afts = c("NC_Cereal", "NC_Livestock"),
#						dataname = "raster_landUseIndex"),
				
				metric_aggaft_diversity_shannon(simp, dataname = "dataAggregateAFTComposition"))
				
		simp$sim$regions	<- c("EE", "LV")
		convert_aggregate_supply(simp, celldataname = "dataAgg")
		convert_aggregate_demand(simp, demanddataname = "csv_aggregated_demand", sourcedataname = "dataAggregateSupplyDemand")
		
		metrics <- rbind(metrics,		
				metric_aggaft_proportions(simp, afts = c("NC_Cereal", "NC_Livestock"), aftsname = "NC", 
						dataname = "dataAggregateAFTComposition"),
				metric_aggaft_proportions(simp, afts = c("C_Cereal", "C_Livestock"), aftsname = "C", 
						dataname = "dataAggregateAFTComposition"),
				metric_agg_supplydemand_maximum(simp, services=c("Cereal", "Meat", "Timber"), 
						datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = TRUE,
						consideroversupply = FALSE),
				metric_agg_supplydemand_maximum(simp, services=c("Cereal", "Meat", "Timber"), 
						datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = FALSE,
						consideroversupply = TRUE),
				# undersupply
				metric_agg_supplydemand_percentage(simp, service = "Total", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = TRUE,
						consideroversupply = FALSE),
				metric_agg_supplydemand_percentage(simp, service = "Cereal", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = TRUE,
						consideroversupply = FALSE),
				metric_agg_supplydemand_percentage(simp, service = "Meat", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = TRUE,
						consideroversupply = FALSE),
				metric_agg_supplydemand_percentage(simp, service = "Timber", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = TRUE,
						consideroversupply = FALSE),
				
				# oversupply
				metric_agg_supplydemand_percentage(simp, service = "Total", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = FALSE,
						consideroversupply = TRUE),
				metric_agg_supplydemand_percentage(simp, service = "Cereal", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = FALSE,
						consideroversupply = TRUE),
				metric_agg_supplydemand_percentage(simp, service = "Meat", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = FALSE,
						consideroversupply = TRUE),
				metric_agg_supplydemand_percentage(simp, service = "Timber", datanamedemand = "csv_aggregated_demand",
						datanamesupply = "csv_aggregated_supply",
						considerundersupply = FALSE,
						consideroversupply = TRUE),
				metric_agg_supplyperreg_simpson(simp, region = NULL, 
						datanamesupply = "csv_aggregated_supply"),
				metric_agg_supplyaccrossreg_simpson(simp, service = NULL, 
						datanamesupply = "csv_aggregated_supply"),
				metric_aggaft_diversity_simpson(simp, region = NULL,
						dataname = "dataAggregateAFTComposition"),
				metric_agg_regionalsupply_efficiency(simp, service = NULL, 
					datanamesupply = "csv_aggregated_supply",
					datanameaft = "dataAggregateAFTComposition")
		)
		
		data <- do.call(rbind, lapply(simp$sim$regions, function(r) 
							metric_agg_supplydemand_percentage(simp, service = "Cereal", region = r, datanamedemand = "csv_aggregated_demand",
									datanamesupply = "csv_aggregated_supply",
									considerundersupply = TRUE, consideroversupply = FALSE)))
		metrics <- rbind(metrics, data.frame(aggregate(data.frame(Value=data$Value), by=list(Tick= data$Tick), FUN=mean), 
				Metric="RegionalUnderSupplyPercent_Cereal"))
		
		data <- do.call(rbind, lapply(simp$sim$regions, function(r) 
							metric_agg_supplydemand_percentage(simp, service = "Meat", region = r, datanamedemand = "csv_aggregated_demand",
									datanamesupply = "csv_aggregated_supply",
									considerundersupply = TRUE, consideroversupply = FALSE)))
		metrics <- rbind(metrics, data.frame(aggregate(data.frame(Value=data$Value), by=list(Tick= data$Tick), FUN=mean), 
						Metric="RegionalUnderSupplyPercent_Meat"))

		data <- do.call(rbind, lapply(simp$sim$regions, function(r) 
							metric_agg_supplydemand_percentage(simp, service = "Timber", region = r, datanamedemand = "csv_aggregated_demand",
									datanamesupply = "csv_aggregated_supply",
									considerundersupply = TRUE, consideroversupply = FALSE)))
		metrics <- rbind(metrics, data.frame(aggregate(data.frame(Value=data$Value), by=list(Tick= data$Tick), FUN=mean), 
						Metric="RegionalUnderSupplyPercent_Timber"))
		
		data_metrics <-  metrics
		input_tools_save(simp, "data_metrics")
		
		
		###########################################################################
		### Create Report
		###########################################################################
		
		if (is.null(opt$noreport) || !opt$noreport) {
			source("./createReport.R")
		}
	}
}

###########################################################################
### Draw Changes in Land Use (not useful when only one runid is executed)
###########################################################################
#datas <- data.frame()
#for (run in runs) {
#	simp$sim$runids <- c(paste(run, rseed, sep="-"))
#	simp$sim$id 	<- c(paste(run, rseed, sep="-"))
#	input_tools_load(simp, "data_landuse_changes")
#	datas <- rbind(datas, data)
#}
#visualise_lines(simp, datas, "Changes", title = "Changes in Land Use",
#		colour_column = "Runid") 