#!/usr/bin/env python3
import os
import subprocess

# ---------- Executes code in SQL file ---------------
def execute(sqlfile):
    pguser = os.environ["POSTGRES_USER"]
    subprocess.run(["psql", "-v", "ON_ERROR_STOP=1", "--username", pguser, "-f", sqlfile])

# ---------- APP MAIN ----------------

# Gets releases already run
release_history = []
history_file_name = "/var/lib/postgresql/release-history.txt"
if os.path.exists(history_file_name):
    with open("release-history.txt", "r+") as history_file:
        for line in history_file:
            release_history.append(line)

# Determines releases to run.
# Should be all releases available that have not been added to the release_history file.
releases = [file for file in os.listdir("releases") if os.path.isdir("releases/" + file)]
releases_to_run = [release for release in releases if release not in release_history]
print("Releases to be run: " + str(releases_to_run))

# Runs those releases
with open(history_file_name, "w") as release_file:
    for release in releases_to_run:

        print("------ Release " + release + " ------")
        schemadir = "releases/" + release +"/schema"
        datadir = "releases/" + release + "/data"
        schemafiles = [schemadir + "/" + file for file in os.listdir(schemadir)]
        datafiles = [datadir + "/" + file for file in os.listdir(datadir)]

        print("Executing schema files")
        for schemafile in schemafiles:
            execute(schemafile)

        print("Executing data files")
        for datafile in datafiles:
            execute(datafile)

        release_file.write(release + "\n")

# Done
print("Done running releases")