#!/bin/bash

REQUIRES="quinzical.jar run.sh README.md src/ res/ categories/ wiki/"

for R in $REQUIRES; do
  if [[ ! -e "$R" ]]; then
    echo "No \`$R\` found" >& 2
    exit 1
  fi
done

zip -r quinzical28.zip $REQUIRES
