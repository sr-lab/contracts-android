NUMBER=2
FILE="github_unique_repos.txt"
OUTPUT_DIR="github_repos"
COUNT=0

mkdir -p $OUTPUT_DIR  # create if it does not exist

for r in $(cat $FILE | head -n $NUMBER); 
do 
	REPO_NAME=$(basename $r);
	SHOW_COUNT=$(printf "%04d" $COUNT);
	OUTPUT_REPO="$OUTPUT_DIR/$SHOW_COUNT-$REPO_NAME";
	echo "[$SHOW_COUNT] Cloning $r..."; 
	# TEST IF DIRECTORY EXISTS
	if [ -d $OUTPUT_REPO ]; then
		echo "The directory $OUTPUT_REPO already exists!"
	else
		git clone $r $OUTPUT_REPO
		echo "Done."; 
	fi
	COUNT=$((COUNT+1)); 
done
