#!/bin/bash

CC=javac;
CB=jar;
SRCDIR=src;
OUT=out;
BIN=bin;
RELEASE=15;
JARNAME=JsonLight;
PACKAGEBASE=com;
PACKAGE=nerjal/json;
IGNORE_COMPILE=false;
IGNORE_CLEAN=false;
IGNORE_BUILD=false;


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

  echo "compilation finished"
}

function build() {
  if [ ! -d $BIN ] ; then
    mkdir $BIN;
  fi
  if [ ! -d $OUT ] ; then
    mkdir $OUT;
  fi

  cd $BIN;
  $CB cf ../$OUT/$JARNAME.jar ./$PACKAGEBASE;

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