# Repository for the Path-Ranking Algorithm
This repository contains the code used for the following papers:
+ [Relational retrieval using a combination of path-constrained random walks](https://noon99jaki.github.io/publication/2010/2010.ML.PRA.pdf)
+ [Random Walk Inference and Learning in A Large Scale Knowledge Base](https://noon99jaki.github.io/publication/2011/2011.emnlp.paper.pdf)
+ [Learning relational features with backward random walks](https://noon99jaki.github.io/publication/2015.acl.pdf)

If you use this code or results in your research, and wish to cite as appropriate:
```
@Article{lao2010pra,
  author="Lao, Ni
  and Cohen, William W.",
  title="Relational retrieval using a combination of path-constrained random walks",
  journal="Machine Learning",
  year="2010",
  month="Oct",
  day="01",
  volume="81",
  number="1",
  pages="53--67",
  issn="1573-0565"
}
@inproceedings{lao2011kb,
  title={Random walk inference and learning in a large scale knowledge base},
  author={Lao, Ni and Mitchell, Tom and Cohen, William W},
  booktitle={Proceedings of the Conference on Empirical Methods in Natural Language Processing},
  pages={529--539},
  year={2011},
  organization={Association for Computational Linguistics}
}
```

## Instructions
Compiled jar file can be found at http://www.cs.cmu.edu/~nlao/data/pra-src-20140421.jar
Data files can be found under data/

1. ### Produce edges file from a NELL knowledge dump 
arg1=NELL dump file
arg2=probability threshold
arg3=output format)
```
java -cp pra-src-20140421.jar edu.cmu.pra.data.WKnowledge createEdgeFile NELL.08m.165.cesv.csv 0.8 edges
```

1. ### index edge file to a compact graph reprensentation 
arg1=graph folder which contains *.edges
arg2=edges/db)
```
java -cp pra-src-20140421.jar edu.cmu.pra.SmallJobs indexGraph ../graphs/NELL165/ edges
```

1. ### create Queries 
arg1=graph folder
arg2=query folder
arg3=whether its for training (boolean)
arg4=whether adding the range of relation as part of the query (boolean)
```
java -cp pra-src-20140421.jar edu.cmu.pra.SmallJobs createQueries ../graphs/NELL165/ ./queriesR_train/ true false
```

1. ### Train model (or do predictions) for a relation with parameters specified by ./conf
```
java -cp pra-src-20140421.jar edu.cmu.pra.LearnerPRA
```

1. ### Train models (or do predictions) in batch mode.
arg1=path to java class files
arg2=text file containing a list of relation names
```
java -cp pra-src-20140421.jar edu.cmu.pra.SmallJobs batchLearnerPRA pra-src-20140421.jar selected_relations
```
