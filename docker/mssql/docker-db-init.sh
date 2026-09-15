#!/bin/bash

set -euxo pipefail

echo "Running set up script..."
until /opt/mssql-tools18/bin/sqlcmd \
	-S "localhost" \
	-U sa \
	-P "${MSSQL_SA_PASSWORD}" \
	-d master \
	-i ./sql/db-init.sql \
	-b \
  -C; do
	echo "Sql Server not up yet; Retrying..." && sleep 2
done

echo "Finished set up"
