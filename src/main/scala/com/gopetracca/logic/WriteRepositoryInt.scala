package com.gopetracca.logic

import org.apache.spark.sql.DataFrame

trait WriteRepositoryInt {
  // TODO: Should we also return the DF? Will make testing easier.
  def write(df: DataFrame): DataFrame
}
