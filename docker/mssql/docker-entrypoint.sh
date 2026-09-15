#!/bin/bash

set -euxo pipefail

/db-init.sh & /opt/mssql/bin/sqlservr
