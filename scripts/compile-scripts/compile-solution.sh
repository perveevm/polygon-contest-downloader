#!/bin/bash

PROBLEM_PATH=$1
SOLUTION_NAME=$2

for HEADER in $(ls ${PROBLEM_PATH}/files | grep "\.h")
do
	cp ${PROBLEM_PATH}/files/${HEADER} ${PROBLEM_PATH}
done

cp ${PROBLEM_PATH}/solutions/${SOLUTION_NAME} ${PROBLEM_PATH}

SOLUTION_EXECUTABLE_NAME="main_ac"

echo "Compiling solution ${SOLUTION_NAME}..."
g++ -o ${PROBLEM_PATH}/${SOLUTION_EXECUTABLE_NAME} ${PROBLEM_PATH}/${SOLUTION_NAME} -std=c++17 -O2
