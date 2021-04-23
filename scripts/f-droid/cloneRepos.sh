NUMBER=400
FILE="versoesDownload.txt"
OUTPUT_DIR="github_repos"
COUNT=0

SLEEP=10  # sleep 10 seconds...
INTVL=10  # every 10 clones

mkdir -p $OUTPUT_DIR  # create if it does not exist

for r in $(cat $FILE | head -n $NUMBER); 
do 
	IFS=';'
	read -a strarr <<< "$r"
	echo "${strarr[0]}"
	echo "${strarr[1]}"
	REPO_NAME=$(basename ${strarr[0]});
	SHOW_COUNT=$(printf "%04d" $COUNT);
	Version=${strarr[1]};
	OUTPUT_REPO="$OUTPUT_DIR/$REPO_NAME-$Version";
	echo "[$SHOW_COUNT] Cloning $r..."; 


	# TEST IF DIRECTORY EXISTS
	if [ -d $OUTPUT_REPO ]; then
		echo "The directory $OUTPUT_REPO already exists!"
	else
		printf "git clone -b %s --single-branch %s %s" ${strarr[1]}, ${strarr[0]}, $OUTPUT_REPO 
		git clone -b ${strarr[1]} --single-branch ${strarr[0]} $OUTPUT_REPO
		echo "Done."; 
	fi
	COUNT=$((COUNT+1)); 
	if [ $(($COUNT%$INTVL)) == 0 ]; then sleep $SLEEP; fi
done
