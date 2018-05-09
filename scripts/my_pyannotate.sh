#!/bin/bash
REPO_PATH="$(git rev-parse --show-toplevel)"

cd $REPO_PATH

# Get type annotations by running unit tests
env/Scripts/pip.exe install pyannotate
env/Scripts/pip.exe install pytest-annotate
env/Scripts/python.exe -m pytest --annotate-output=./type_annotations.json

# Process the annotations
env/Scripts/python scripts/process_annotations.py type_annotations.json clean_type_annotations.json
rm type_annotations.json

echo
echo "To preview the changes, run:"
echo "pyannotate --type-info ./clean_type_annotations.json tsuro"

echo
echo "To apply the changes, run:"
echo "pyannotate --type-info ./clean_type_annotations.json tsuro -w && isort tsuro/*.py"
