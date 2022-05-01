package hv

import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}

object HV {

  def main(args: Array[String]) {
    val logger: org.apache.log4j.Logger = LogManager.getRootLogger

    if (args.length != 3) {
      logger.error("Usage:\nvh.MatrixMultiply <matrix a> <matrix b> <output dir>")
      System.exit(1)
    }

    

    val conf = new SparkConf().setAppName("Dense Matrix Multiplication H-V")
    val sc = new SparkContext(conf)

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_a = sc.textFile(args(0)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_b = sc.textFile(args(1)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    //partition A horizontally, group by row
    val horizontal_p_a = matrix_a.map(cell_a => (cell_a._1, cell_a)).groupByKey()

    //partition B vertically, group by column
    val vertical_p_b = matrix_b.map(cell_b => (cell_b._2, cell_b)).groupByKey()

    

    
    //join every row of A with every column of B and then compute the dot product
    
    // VERSION 1: more fine granularity than version 1? higher degree of parallelism possible
    val joined = horizontal_p_a.cartesian(vertical_p_b).flatMap {
      case ((rowA, iter_rows), (colB, iter_cols)) => 

        for (i<- 0 until iter_rows.size) yield {

          ((rowA, colB), iter_rows.toList(i)._3*iter_cols.toList(i)._3)
        }
    }

    val result = joined.reduceByKey(_+_)

    result.saveAsTextFile(args(2))

  }
}
