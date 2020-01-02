#!/bin/bash

PROBLEM_PATH=$1
SCRIPT_FILE=$2

if [ ! -f ${SCRIPT_FILE} ];
then
	exit 0
fi

for HEADER in $(ls ${PROBLEM_PATH}/files | grep "\.h")
do
	cp ${PROBLEM_PATH}/files/${HEADER} ${PROBLEM_PATH}
done

for GENERATOR in $(cat ${SCRIPT_FILE} | cut -d ' ' -f1 | sort | uniq)
do
	echo "Compiling generator ${GENERATOR}.cpp..."

	GENERATOR_NAME=$(echo "${GENERATOR}" | rev | cut -d '/' -f1 | rev)

	cp ${PROBLEM_PATH}/files/${GENERATOR_NAME}.cpp ${PROBLEM_PATH}
	g++ -o ${PROBLEM_PATH}/${GENERATOR_NAME} ${PROBLEM_PATH}/${GENERATOR_NAME}.cpp -std=c++17 -O2
done
