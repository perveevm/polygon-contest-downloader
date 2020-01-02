#!/bin/bash

PROBLEM_PATH=$1

CHECKER_NAME="check"

for HEADER in $(ls ${PROBLEM_PATH}/files | grep "\.h")
do
	cp ${PROBLEM_PATH}/files/${HEADER} ${PROBLEM_PATH}
done

echo "Compiling checker ${CHECKER_NAME}.cpp..."
g++ -o ${PROBLEM_PATH}/${CHECKER_NAME} ${PROBLEM_PATH}/${CHECKER_NAME}.cpp -std=c++17 -O2 -DEJUDGE
