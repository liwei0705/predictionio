package io.prediction.api

import io.prediction.core.BaseDataSource
import io.prediction.EmptyParams
import io.prediction.BaseParams
import org.apache.spark.rdd.RDD

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.reflect._

abstract class LDataSource[DSP <: BaseParams : ClassTag,
    DP, TD : Manifest, Q, A]
  extends BaseDataSource[DSP, DP, RDD[TD], Q, A] {

  def readBase(sc: SparkContext): Seq[(DP, RDD[TD], RDD[(Q, A)])] = {
    read.map { case (dp, td, qaSeq) => {
      (dp, sc.parallelize(Array(td)), sc.parallelize(qaSeq))
    }}
  }

  def read(): Seq[(DP, TD, Seq[(Q, A)])]
}

abstract class PDataSource[DSP <: BaseParams : ClassTag, DP, TD, Q, A]
  extends BaseDataSource[DSP, DP, TD, Q, A] {

  def readBase(sc: SparkContext): Seq[(DP, TD, RDD[(Q, A)])] = {
    read(sc).map { case (dp, td, qaRdd) => {
      // TODO(yipjustin). Maybe do a size check on td, to make sure the user
      // doesn't supply a huge TD to the driver program.
      (dp, td, qaRdd)
    }}
  }

  def read(sc: SparkContext): Seq[(DP, TD, RDD[(Q, A)])]
}
