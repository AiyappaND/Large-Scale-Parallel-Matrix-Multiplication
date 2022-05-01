package vh

import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}

object VH {

  def main(args: Array[String]) {
    val logger: org.apache.log4j.Logger = LogManager.getRootLogger

    if (args.length != 3) {
      logger.error("Usage:\nvh.MatrixMultiply <matrix a> <matrix b> <output dir>")
      System.exit(1)
    }

    val conf = new SparkConf().setAppName("Dense Matrix Multiplication V-H")
    val sc = new SparkContext(conf)

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_a = sc.textFile(args(0)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_b = sc.textFile(args(1)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    // Partition first matrix vertically, using column as key => (col, (row, col, value))
    val vertical_p_a = matrix_a.map(cell_a => (cell_a._2, cell_a))

    // Partition first matrix horizontally, using row as key => (row, (row, col, value))
    val horizontal_p_b = matrix_b.map(cell_b => (cell_b._1, cell_b))

    // Join the matrices on key, and multiply values
    val joined_result = vertical_p_a.join(horizontal_p_b)
      .map {
        case (_, (cell_a, cell_b)) => ((cell_a._1, cell_b._2), (cell_a._3 * cell_b._3))
      }

    // Reduce by key (sum all individual product results)
    val multiplication_result = joined_result.reduceByKey(_+_)

    // Save result matrix to file
    multiplication_result.saveAsTextFile(args(2))
  }
}
