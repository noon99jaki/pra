=============
Quick Guide:
=============

This is Ni's PRA package. The following are some basic commands.

#index edge file to a compact graph reprensentation 
#arg1=graph folder which contains *.edges
#arg2=edges/db)
java -cp ~/code_java/ni/class edu.cmu.pra.SmallJobs indexGraph ../graphs/NELL446/ edges

#create Queries 
#arg1=graph folder
#arg2=query folder
#arg3=whether its for training (boolean)
#arg4=whether adding the range of relation as part of the query (boolean)
java -cp ~/code_java/ni/class edu.cmu.pra.SmallJobs createQueries ../graphs/NELL446/ ./queriesR_train/ true true

#Train model (or do predictions) for a relation with parameters specified by ./conf
java -cp ~/code_java/ni/class edu.cmu.pra.LearnerPRA

#Train models (or do predictions) in batch mode. paramters are specified in ./grid
java -cp ~/code_java/ni/class edu.cmu.lti.util.run.TunnerSweep 

# do predictions for relations in reporter_keys
java -cp ~/code_java/ni/class edu.cmu.pra.SmallJobs predict

#sort predictions by their scores 
#arg1=prediction folder
#arg2=score threshold
#arg3=output folder) 
java -cp ~/code_java/ni/class edu.cmu.pra.SmallJobs sortPredictions ../predictions/ 1.0

#Test APIs for PRA model 
#arg1=graph folder
#arg2=model file
#arg3=the query node
java -cp ~/code_java/ni/class edu.cmu.pra.Tests testPRA ../graphs/NELL446/ ./models/athleteplaysforteam c$a_rod

