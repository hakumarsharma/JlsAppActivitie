#!bin/bash

ignore_files=
num=0
wkp=$1

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
for i in `cat $SCRIPT_DIR/filenames`;
do
    ignore_files[$num]=$i;
    num=`expr $num + 1`;
done


cd $wkp

for p in `find . -name "*.java" | cut -c3-  `
    do
	#git log -n 1 --pretty=oneline --name-status $p >test.log
        #st=$(git log -n 1 --pretty=oneline --after="2018-10-05" --name-status $p | tail -1 | cut -c1)
        #epoch_date=$(git log --after="2018-10-05" --pretty=format:"%ct" $p)
	st=1
        if [ "$st" ]
        then
            ignore_it=0
            for j in "${ignore_files[@]}"
                do
                    if [[ "$p" = "$j" ]]; then
                        ignore_it=1
                        break
                    fi;
                done
                if [ $ignore_it -eq 0 ]; then
		    alias pmd="/home/jenkins/pmd-bin-6.8.0/bin/run.sh pmd"
		    /home/jenkins/pmd-bin-6.8.0/bin/run.sh pmd -d ${wkp}/$p  -f  xml  -R rulesets/java/quickstart.xml   -r test.xml 2>&1
		    files=$(echo $p |   rev | cut -d "/" -f1 | rev)
		    cat  test.xml  > test"$files".xml
                fi
        fi
    done
