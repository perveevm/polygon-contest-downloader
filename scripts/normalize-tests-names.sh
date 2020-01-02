#!/bin/bash

PROBLEM_PATH=$1
NAME_LEN=$2

for TEST in $(ls ${PROBLEM_PATH}/tests)
do
	while [ ${NAME_LEN} -gt ${#TEST} ]
	do
		mv ${PROBLEM_PATH}/tests/${TEST} ${PROBLEM_PATH}/tests/0${TEST}
		TEST="0${TEST}"
	done
done
