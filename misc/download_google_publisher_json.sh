# use curl to download the publisher acct json file from $PUBLISHER_ACCT_JSON_FILE_URI, if set,
# to the path/filename set in $PUBLISHER_ACCT_JSON_FILE.
if [[ $PUBLISHER_ACCT_JSON_FILE && ${PUBLISHER_ACCT_JSON_FILE} && $PUBLISHER_ACCT_JSON_FILE_URI && ${PUBLISHER_ACCT_JSON_FILE_URI} ]]
then
    echo "Keystore detected - downloading..."
    curl -L -o ${PUBLISHER_ACCT_JSON_FILE} ${PUBLISHER_ACCT_JSON_FILE_URI}
else
    echo "Keystore uri not set.  .APK artifact will not be signed."
fi