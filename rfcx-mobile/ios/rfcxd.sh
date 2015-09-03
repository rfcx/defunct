#!/bin/bash
RECORDING_LENGTH=$1
if [ -z "$1" ]
  then
    RECORDING_LENGTH=60
fi
LOCATION_BASE=$2
if [ -z "$2" ]
  then
    LOCATION_BASE="/var/mobile/rfcx-mobile"
fi
let PAUSE=`echo $RECORDING_LENGTH`-5
cd "$LOCATION_BASE/tmp/pcm/scratch"
rm -f "$LOCATION_BASE/tmp/pcm/scratch/*.aiff"
recAudio > /dev/null &
sleep $PAUSE
FILE_CURR="`ls | grep .aiff`"
sleep 4
killall -INT recAudio
sleep 1
DATE=`date +%s`
mv "$LOCATION_BASE/tmp/pcm/scratch/$FILE_CURR" "$LOCATION_BASE/tmp/pcm/$DATE.aiff"
/usr/local/bin/node "$LOCATION_BASE/node/app.js" --format aiff --file $DATE > /dev/null &
/bin/bash "$LOCATION_BASE/ios/rfcxd.sh" $RECORDING_LENGTH $LOCATION_BASE