#!/bin/bash

PROBLEM_PATH=$1

declare -a DELETE_FILE
declare -a DELETE_DIRECTORY

for CUR_FILE in $(ls ${PROBLEM_PATH})
do
	if [[ -f ${PROBLEM_PATH}/${CUR_FILE} ]];
	then
		if [ "${CUR_FILE}" == "check" ];
		then
			continue
		elif [ "${CUR_FILE}" == "main_ac" ];
		then
			continue
		else
			rm ${PROBLEM_PATH}/${CUR_FILE}
		fi
	else
		if [ "${CUR_FILE}" == "tests" ];
		then
			continue
		else
			rm -r ${PROBLEM_PATH}/${CUR_FILE}
		fi
	fi
done
