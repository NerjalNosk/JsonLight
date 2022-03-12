#!/bin/bash

# Jar file build script
# Process isn't to be edited
# You can only edit the following constants to adapt to your project
# Warning! This script generates an independent jar file, without any
# consideration for Maven, Gradle, Android or any other project type.
# Only use it for direct compilation of a standalone project.
# Script by: Nerjal Nosk

CC=javac;               # compilation command
CB=jar;                 # jar file building command
                        #  (based on the default "jar" command useage)
SRCDIR=src/main/java;   # source folder path (relative)
OUT=out;                # final output folder path (relative)
BIN=bin;                # binaries (.class) folder path (relative)
RELEASE=15;             # Java dependency (for default "java" compiler)
JARNAME=JsonLight;      # name of the final jar file
PACKAGEBASE=com;        # root name of the project package
PACKAGE=nerjal/json;    # project package path name, root excluded
                        #   (separated with '/')
MAIN_CLASS=0;           # project main class path path (0 if none)
                        #   (full package path separated with '.', no file extension)
RESOURCES_FOLDER=0;     # project resources folder (0 if none)

IGNORE_COMPILE=false;   # whether compilation should be done
IGNORE_CLEAN=false;     # whether cleaning should be done
IGNORE_BUILD=false;     # whether jar file building should be done


function clean() {

  if [ ! -d $BIN ] ; then
    mkdir $BIN;
  fi

  rm -rf $OUT/*;
  rm -rf $BIN/*;

  echo "clean finished"
}

function compile() {
  if [ ! -d $SRCDIR/$PACKAGEBASE/$PACKAGE ] ; then
    echo "Coudln't find package files";
    exit 1;
  fi;
  if [ ! -d $BIN ] ; then
    mkdir $BIN;
  fi;

  $CC -d $BIN --release $RELEASE -sourcepath $SRCDIR $SRCDIR/$PACKAGEBASE/$PACKAGE/**.java;

  if [ ! "$RESOURCES_FOLDER" = "0" ] ; then
    cp $RESOURCES_FOLDER $BIN/;
  else
    echo "compilation:No resource folder defined";
  fi;

  echo "compilation finished"
}

function build() {
  if [ ! -d $BIN ] ; then
    mkdir $BIN;
  fi
  if [ ! -d $OUT ] ; then
    mkdir $OUT;
  fi

  rArg=;
  if [ ! "$RESOURCES_FOLDER" = "0" ] ; then
    rArg=$RESOURCES_FOLDER;
  else
    echo "build:No resource folder defined";
  fi;

  mArg=;
  if [ ! "$MAIN_CLASS" = "0" ]; then
    mArg="--main-class $MAIN_CLASS.class";
  else
    echo "build:No main class defined";
  fi

  cd $BIN;
  $CB cf ../$OUT/$JARNAME.jar ./$PACKAGEBASE $rArg;

  echo "jar build finished"
}

function script_help() {
  echo " --- $JARNAME jar build script --- ";
  echo " [options] ";
  echo "  - noClean - prevents cleaning the $BIN and $OUT folders before build";
  echo "  - noCompile - prevents compiling the $SRCDIR/$PACKAGEBASE/$PACKAGE java files before building jar";
  echo "  - noBuild - prevents building the $OUT/$JARNAME.jar file at the end of the compilation";
  exit 0;
}


for arg in "$@" ; do
  [ $arg == "h" ] && script_help;
  [ $arg == "noClean" ] && IGNORE_CLEAN=true && echo "clean canceled.";
  [ $arg == "noCompile" ] && IGNORE_COMPILE=true && echo "compile canceled.";
  [ $arg == "noBuild" ] && IGNORE_BUILD=true && echo "build canceled.";
done;

[ ! $IGNORE_CLEAN = true ] && clean;
[ ! $IGNORE_COMPILE = true ] && compile;
[ ! $IGNORE_BUILD = true ] && build;