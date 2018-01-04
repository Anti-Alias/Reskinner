#!/usr/bin/env bash

# Gets data and schema files
schemafiles=`find docker-entrypoint-initdb.d/schema -type f`
datafiles=`find docker-entrypoint-initdb.d/data -type f`

# Executes schema files in lexographical of file name,
printf '\n\n\n\n\n'
echo "----------- Executing schema files ------------"
for file in $schemafiles; do
    echo "Executing $file ..."
    psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f $file
    echo "Done executing $file"
done

# Executes data files in lexographical order of file name.
printf '\n\n'
echo "----------- Executing data files ------------"
for file in $datafiles; do
    echo "Executing $file ..."
    psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f $file
    echo "Done executing $file"
done

# Done
printf '\n\n\n\n\n'
