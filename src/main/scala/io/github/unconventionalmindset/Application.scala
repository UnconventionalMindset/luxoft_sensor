package io.github.unconventionalmindset

import io.github.unconventionalmindset.processors.{FileProcessor, Process}

@main
def main(args: String*): Unit =
  val fileProcessor = FileProcessor(args(args.length - 1))
  new Process(fileProcessor).process()
