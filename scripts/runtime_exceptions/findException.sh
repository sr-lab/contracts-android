EXCEPTIONS2="\
MalformedInputException	\
MalformedJsonException \
MalformedParameterizedTypeException \
MalformedParametersException \
MalformedURLException \
MissingResourceException \
AuthenticationRequiredException \
InstantiationException \
NoSuchAlgorithmException \
RecoverableSecurityException \
ActivityNotFoundException \
OperationApplicationException \
SendIntentException \
CursorIndexOutOfBoundsException \
OutOfResourcesException \
CameraAccessException \
StringPrepParseException \
ICUUncheckedIOException \
IllformedLocaleException \
"

EXCEPTIONS=$(cat list_runtime_exceptions.txt)

for exception in $EXCEPTIONS
do
	OCCURRENCES=$(grep -r "$exception" github_repos/* | grep -i "throw new" | wc -l)
	echo "$exception, $OCCURRENCES"
done
