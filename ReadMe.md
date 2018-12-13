# CRAFTY CoBRA IMPRESSIONS EU Model

## Configuration

The base configuration is:

* startTick: 2010
* endTick:	?
* world:	EU28
* scenario:	base01
* FRs:		?
* BTs:		Pseudo
* Preferences: ?
* Capitals:	Cprod, Fprod, Infra, Lprod, Nat, Econ (?)
* Services:	Meat, Cereal, Recreation, Timber

## Parameterisation

There are some R scripts that ease the creation of configuration file from templates.
These can be run all at once by executing './config/R/base01/createbatch/createWorld.R
The scripts in './config/R/base01/createbatch/' can be copied to another subfolder, e.g. './config/R/base02' and adjusted to create another set of scenarios.

NOTE: the simp configuration must be correct before the scripts may be applied (e.g., simp$mdata$aftNames)

Agenda for defining agent types:

1. Adapt '/config/R/simp-machine_cluster.R' (consider to rename it) and execute
1. Configure 'simp$mdata$aftNames' ('./config/R/simpBasic.R')
1. Configure './data/agents/FunctionalRoles.xml'
2. Define properties in './data/agents/template/AFT.csv'
3. Run './config/R/base01/createbatch/createWorld.R' (calls 'createAftParamCSV.R' and 'create1by1RunCSV.R')
4. Define production in './data/production/<AFT>.csv'


## Run

Right click the file './config/launcher/CraftyCoBRA_Impressions.launch' and choose 'Run as...' > <First entry> Note: you need to substitute the project name first (see above)!


## ReleaseToLinuxCluster

This ant script facilitates the transfer of model configuration data to e.g. a linux cluster. The '-FS' version
of the script assumes you have mapped the cluster file system to a network drive of your local file system
(see [here](https://www.wiki.ed.ac.uk/display/ecdfwiki/Transferring+Data) for Eddie) 

## Some Notes

* CRAFTY-CoBRA supports parallel processing of regions which requires MPI to be present and mpi.jar in the Java
classpath. If MPI is not present, mpi.jar _must_ be excluded from the Java classpath. CRAFTY-CoBRA will then issue a warning (No MPI in classpath!) which can be ignored.

* CRAFTY-CoBRA currently issues a number of warnings from LEventbus. They basically mean that decision making
processes are triggered, but no actual decision for that trigger configured. In most cases the warnings can be 
ignored.

## Post-Processing
The folder ./config/R contains templates to aggregate and visualise simulation output data with R.
See [crafty wiki](https://www.wiki.ed.ac.uk/display/CRAFTY/Post-Processing) for details.

***

If you have any further questions don't hesitate to contact
Sascha.Holzhauer@ed.ac.uk 