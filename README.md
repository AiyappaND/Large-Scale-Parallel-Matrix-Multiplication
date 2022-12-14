# Dense Matrix Multiplication with Spark

Spring 2022

About
-----------

This project implements scalable matrix multiplication for two different types of partitioning.

One approach used vertical partitioning for the left matrix and horizontal for the right,
and the second approach uses the opposite (horizontal - vertical)

The script to generate input data is present in the `scripts/` folder, modify the matrix dimensions as required.

Code authors
-----------
- Alina Sarwar
- Mbongeni (N'Dabe) Mahluza
- Aiyappa Devaiah
- Zoe Corning

Installation
------------
These components are installed:
- JDK 1.8
- Scala 2.12.2
- Hadoop 3.2.2
- Spark 3.2.1 (without bundled Hadoop)
- Maven
- AWS CLI (for EMR execution)

Environment
-----------
1) Example ~/.bash_aliases:
   export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
   export HADOOP_HOME=/opt/hadoop
   export SCALA_HOME=/usr/bin/scala
   export SPARK_HOME=/opt/spark
   export YARN_CONF_DIR=$HADOOP_HOME/etc/hadoop
   export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$SCALA_HOME/bin:$SPARK_HOME/bin
   export SPARK_DIST_CLASSPATH=$(hadoop classpath)

2) Explicitly set JAVA_HOME in $HADOOP_HOME/etc/hadoop/hadoop-env.sh:
   export JAVA_HOME=/usr/lib/jvm/java-8-oracle

Execution
---------
All the build & execution commands are organized in the Makefile.
1) Unzip project file.
2) Open command prompt.
3) Navigate to directory where project files unzipped.
4) Edit the Makefile to customize the environment at the top.
   - Sufficient for standalone: hadoop.root, jar.name, local.input
   Other defaults acceptable for running standalone.
5) Standalone Hadoop:
   - make switch-standalone		-- set standalone Hadoop environment (execute once)
   - make local
6) Pseudo-Distributed Hadoop: (https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/SingleCluster.html#Pseudo-Distributed_Operation)
   - make switch-pseudo			-- set pseudo-clustered Hadoop environment (execute once)
   - make pseudo					-- first execution
   - make pseudoq				-- later executions since namenode and datanode already running
7) AWS EMR Hadoop: (you must configure the emr.* config parameters at top of Makefile)
   - make upload-input-aws		-- only before first execution
   - make aws					-- check for successful execution with web interface (aws.amazon.com)
   - download-output-aws			-- after successful execution & termination
   - download-logs-aws			-- after successful execution & termination