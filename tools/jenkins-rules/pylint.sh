#!bin/bash

ignore_files=
num=0
#folder=$1
wkp=$1

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
for i in `cat $SCRIPT_DIR/filenames`;
do
    ignore_files[$num]=$i;
    num=`expr $num + 1`;
done


cd $wkp

for p in `find . -name "*.py" | cut -c3-  `
    do

        #git log -n 1 --pretty=oneline --name-status $i >test.log
        #st=$(git log -n 1 --pretty=oneline --after="2010-06-10" --name-status $i | tail -1 | cut -c1)
        #epoch_date=$(git log --after="2010-05-24" --pretty=format:"%ct" $i)
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
		    pylint --rcfile=.pylintrc --output-format=parseable ${wkp}/$p 
                fi
        fi
    done
