package io.github.unconventionalmindset
package processors

class FileProcessor(directory: String):
  def getListOfFiles: List[java.io.File] =
    val d = java.io.File(directory)
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).toList
    else
      List.empty[java.io.File]
