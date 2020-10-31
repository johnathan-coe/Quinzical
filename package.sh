#!/bin/bash

# Build the application with Maven
./canner.sh

REQUIRES="quinzical.jar run.sh README.md src/ categories/ wiki/"

for R in $REQUIRES; do
  if [[ ! -e "$R" ]]; then
    echo "No \`$R\` found" >& 2
    exit 1
  fi
done

zip -r quinzical28.zip $REQUIRES
