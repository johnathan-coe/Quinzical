#!/bin/bash

if [[ -z $JAR ]]; then
  JAR=quinzical.jar
fi

if [[ -z $JAVAFX_PATH ]]; then
  JAVAFX_PATH=~/javafx-sdk-11.0.2/lib
fi

# Check for the Jeopardy jar
if [[ ! -e "$JAR" ]]; then
  echo "No \`$JAR\` file could be found." >& 2
  exit 1
fi

# Check for JavaFX 11.0.2
if [[ ! -d "$JAVAFX_PATH" ]]; then
  echo "JavaFX 11.0.2 is not installed at the \`$JAVAFX_PATH\`." >&2
  exit 1
fi

java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.media,javafx.base,javafx.fxml,javafx.web -jar $JAR
