package cli_codegen

import sbt._
import Keys._
import nio.Keys._
import plugins._
import java.nio.file._

/**
 * A generic plugin that issues a CLI command when sources change and produces
 * output files that are tracked by sbt. It's a thin layer over the capabilities
 * of sbt directly. The SBT manual describes everything below but does not
 * provide a configurable plugin to do it for generically without writing your
 * own tasks. For anything complicated, you can use this plugin's source code as
 * a starting point for your own.
 */
object CLICodegenPlugin extends AutoPlugin {
  override def requires = JvmPlugin // needed since JvmPlugin resets sourceGenerator :-(
 
 object autoImport {
    val codegenRun = taskKey[Seq[File]]("Run CLI command.")
    val codegenInputSources = settingKey[Seq[Glob]]("List of input sources globs.")
    val codegenCommand = settingKey[Seq[String] => (Seq[String], Seq[String])]("Codegen command generator given a sequence of input files, return a command to run and the command's output file.")
    val codegenOutputFilesListFile = settingKey[Option[File]]("File that once the command runs, contains the list of output files one per line.")
    val cliOutdir = settingKey[File]("Output directory. Default is project's 'sourceManaged'/main/cli_codegen.")
  }
  import autoImport._

  // goes into ThisBuild
  override def buildSettings = Seq(
  )

  // per project
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    codegenOutputFilesListFile := None,
    codegenRun / fileInputs ++= codegenInputSources.value,
    cliOutdir := sourceManaged.value / "main" / "cli_codegen",
  ) ++ configSettings(Compile)    

  def configSettings(c: Configuration) = inConfig(c)(Seq(
    codegenRun := codegenTask.value,
    sourceGenerators += codegenRun.taskValue,
  ))

  //define inline in autoImport or *settings via `theTask := {` or separately like this
  private def codegenTask = Def.task {
    import scala.sys.process._
    val logger = streams.value.log
    val input_files_strs = codegenRun.inputFiles.map(_.toString)
    val outdir = cliOutdir.value
    val (command, outputs) = codegenCommand.value(input_files_strs)
    val output_files = outputs.map(outdir / _)
    val output_exists = output_files.map(of => of.exists()).filterNot(identity).length == 0

    def run() = {
      logger.debug("Generating scala code using CLI code generator.")
      outdir.mkdirs()
      logger.debug(s"""Generating generated scala source files into ${output_files.mkString(", ")}""")
      logger.info(s"""Codegen input files: ${input_files_strs.mkString(", ")}""")
      logger.info(s"Codegen pre-arranged output files: ${output_files.mkString(", ")}")
      logger.debug("CLI command:")
      logger.debug(command.mkString(" "))
      command.!
    }
    // a non-zero exit value does not seem to be generated from npx apollo
    codegenRun.inputFileChanges match {
      case fc@FileChanges(c,d,m,u) =>
        if(fc.hasChanges || !output_exists)
          run() match {
            case 1 =>
              throw new MessageOnlyException("Error running codegen command. Output may not exist or be inconsistent.")
            case _ =>
          }
      case _ =>
    }
    val more_files = codegenOutputFilesListFile.value match {
      case Some(f) =>
        scala.io.Source.fromFile(f).getLines().toSeq.map(s => Paths.get(s.trim).toFile)
      case _ => Nil // no file of filesystem
    }

    // // testing: if files were not created, create them with zero content
    // output_files.foreach{f =>
    //   if(!f.exists()) Files.createFile(f.toPath())
    // }
    output_files ++ more_files
  }
}

