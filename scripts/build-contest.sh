#!/bin/bash

CONTEST_PATH=$1
CONTEST_ID=$2

if [[ -d ${CONTEST_PATH}/problems ]];
then
	echo "Can't create problems directory because it already exists! Finishing script..."
	exit 0
fi

mkdir ${CONTEST_PATH}/problems

java -jar polygon-contest-downloader.jar <put your key here> <put your secret here> ${CONTEST_ID} ${CONTEST_PATH}

for PROBLEM in $(ls ${CONTEST_PATH} | grep .zip | sed "s/.zip//")
do
	mkdir ${CONTEST_PATH}/problems/${PROBLEM}
	unzip ${CONTEST_PATH}/${PROBLEM}.zip -d ${CONTEST_PATH}/problems/${PROBLEM}

	./compile-scripts/compile-checker.sh ${CONTEST_PATH}/problems/${PROBLEM}
	./compile-scripts/compile-generators.sh ${CONTEST_PATH}/problems/${PROBLEM} ${CONTEST_PATH}/${PROBLEM}.script
	./compile-scripts/compile-solution.sh ${CONTEST_PATH}/problems/${PROBLEM} $(cat ${CONTEST_PATH}/${PROBLEM}.solution)

	if [[ -f ${CONTEST_PATH}/${PROBLEM}.script ]];
	then
		echo "Generating tests..."

		chmod +x ${CONTEST_PATH}/${PROBLEM}.script
		${CONTEST_PATH}/${PROBLEM}.script

		for TEST in $(ls ${PWD} | grep -E "^[[:digit:]]+$")
		do
			echo "Moving test ${TEST} to ${CONTEST_PATH}/problems/${PROBLEM}/tests..."
			mv ${PWD}/${TEST} ${CONTEST_PATH}/problems/${PROBLEM}/tests
		done
	fi

	./normalize-tests-names.sh ${CONTEST_PATH}/problems/${PROBLEM} 3
	./generate-answers.sh ${CONTEST_PATH}/problems/${PROBLEM}
	./clean-up.sh ${CONTEST_PATH}/problems/${PROBLEM}
done
