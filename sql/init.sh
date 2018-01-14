#!/usr/bin/env bash

# Visual separator
printf "\n\n\n\n\n\n\\n\n\n"

# Gets all releases
releases=`ls docker-entrypoint-initdb.d/releases`

# Picks only latest release if deployment type specifies it.
if [ "$DB_DEPLOYMENT_TYPE" == "last" ]
then
    releasearray=($releases)
    releasenum=${releasearray[-1]}
    releases=${releasearray[-1]}
    echo "Only running db code for release '$releasenum'"
elif [ "$DB_DEPLOYMENT_TYPE" == "all" ]
then
    echo "Running db code for all releases."
else
    echo "ERROR: DB_DEPLOYMENT_TYPE was '$DB_DEPLOYMENT_TYPE' but expected [all, last]"
    exit 1
fi


# For all releases...
for release in $releases; do

    echo "---------------- Executing sql code for release '$release' ----------------"

    # Gets data and schema files
    schemafiles=`find docker-entrypoint-initdb.d/releases/$release/schema -type f`
    datafiles=`find docker-entrypoint-initdb.d/releases/$release/data -type f`

    # Executes schema files in lexographical order of file name,
    echo "Executing schema files"
    for file in $schemafiles; do
        echo "Executing $file ..."
        psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f $file
        echo "Done executing $file"
    done

    # Executes data files in lexographical order of file name.
    echo "Executing data files"
    for file in $datafiles; do
        echo "Executing $file ..."
        psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER -f $file
        echo "Done executing $file"
    done
    echo "Done with sql code for release 'release'"

done

# Done
printf "\n\n\n\n\n\n\\n\n\n"