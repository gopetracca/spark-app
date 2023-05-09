package com.gopetracca.logic

import org.apache.spark.sql.DataFrame

trait ReadRepositoryInt {
  def load(): DataFrame
}
