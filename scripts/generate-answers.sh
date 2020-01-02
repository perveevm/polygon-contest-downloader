#!/bin/bash

PROBLEM_PATH=$1

for TEST in $(ls ${PROBLEM_PATH}/tests)
do
	${PROBLEM_PATH}/main_ac<${PROBLEM_PATH}/tests/${TEST}>${PROBLEM_PATH}/tests/${TEST}.a
done
