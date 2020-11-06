#!/bin/sh


mvn clean install
# build classpath
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
# read classpath
CP=`cat cp.txt`
# invoke

MEM=-Xmx6g

if [ ! -d out/contracts ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.CollectContracts
fi

if [ ! -f results/evolution.tex ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.AnalyseContractEvolution
fi

if [ ! -f results/gini.tex ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.AnalyseContractUsage
fi

if [ ! -f results/dataset.tex ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.CollectDataSetStats
fi

if [ ! -f results/programversion_stats.csv ]; then
 	java $MEM -cp "$CP:target/classes" contractstudy.scripts.CollectProgramVersionStats
fi

if [ ! -f results/contraints_across_versions.csv ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.AnalyseContractUsageAcrossVersions
fi

if [ ! -f results/hierarchy.tex ]; then
	java $MEM -cp "$CP:target/classes" contractstudy.scripts.AnalyseHierarchyContracts
fi




