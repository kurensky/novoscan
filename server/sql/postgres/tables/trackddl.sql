#!/bin/sh
--



psql -f trackddl.tab
psql -f trackddl.ind
psql -f trackddl.con
psql -f trackddl.sqs
