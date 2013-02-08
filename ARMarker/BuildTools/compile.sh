#IF this file is changed under windows use "tr -d '\r' < edited.sh > final.sh " to remove the bad line endings.

#Get the base dir so we can change the directory to the base dir.
BASEDIR=`dirname $0`

echo 
echo Compiling Native code. Refresh Workspace after this is done!
echo 
#Change to the directory of the project, change this is if the project movies.
cd $BASEDIR

#Run the ndk build file. Change this to the correct location.
$NDKDIR./ndk-build V=1 