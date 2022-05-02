package hv

import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.mutable.ListBuffer
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

    // Partition A horizontally, group by row, sort by column number, then only keep the cell value
    val horizontal_p_a = matrix_a.map(cell_a => (cell_a._1, cell_a))
                        .groupByKey()
                        .mapValues(v => v.to[ListBuffer].sortBy(_._2).map(_._3)) // [(0,1,10), (0,2,5), (0,0,4)] becomes [4,10,5]

    // Partition B vertically, group by column, sort by row number, then only keep the cell value
    val vertical_p_b = matrix_b.map(cell_b => (cell_b._2, cell_b))
                              .groupByKey()
                              .mapValues(v => v.to[ListBuffer].sortBy(_._1).map(_._3))

  
    // Join every row of A with every column of B and then compute the dot product
    val joined = horizontal_p_a.cartesian(vertical_p_b).flatMap {
      case ((rowA, iter_rows), (colB, iter_cols)) => 

        for (i<- 0 until iter_rows.length) yield {

          ((rowA, colB), iter_rows(i)*iter_cols(i))
        }
    }

    // Sum up dot product results for each cell of the matrix
    val result = joined.reduceByKey(_+_)

    // Save result to text file
    result.saveAsTextFile(args(2))

  }
}
