package vh

import org.apache.log4j.LogManager
import org.apache.spark.{SparkConf, SparkContext}

object HV {

  def main(args: Array[String]) {
    val logger: org.apache.log4j.Logger = LogManager.getRootLogger

    if (args.length != 3) {
      logger.error("Usage:\nvh.MatrixMultiply <matrix a> <matrix b> <output dir>")
      System.exit(1)
    }

    def dot_product(row_list:List[(Int, Int, Int)], col_list:List[(Int, Int, Int)]) : Int = {
      val len = row_list.length
      var result = 0

      for( index <- 0 until len) {
        result += row_list(index)._3 * col_list(index)._3
      }
      result
    }

    val conf = new SparkConf().setAppName("Dense Matrix Multiplication H-V")
    val sc = new SparkContext(conf)

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_a = sc.textFile(args(0)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    // Read csv of matrix (row, col, value) and convert to integer
    val matrix_b = sc.textFile(args(1)).map(
      cell => (cell.split(",")(0).toInt, cell.split(",")(1).toInt, cell.split(",")(2).toInt))

    val horizontal_p_a = matrix_a.map(cell_a => (cell_a._1, cell_a)).groupByKey()

    val vertical_p_b = matrix_b.map(cell_b => (cell_b._2, cell_b)).groupByKey()

    val joined = horizontal_p_a.cartesian(vertical_p_b).map {
      case ((row1, iter_rows), (col2, iter_cols)) => ((row1, iter_rows.toList), (col2, iter_cols.toList))
    }

    val result = joined.map {
      case  ((row1, iter_rows), (col2, iter_cols)) => ((row1, col2), dot_product(iter_rows, iter_cols))
    }

    result.saveAsTextFile(args(2))

  }
}
