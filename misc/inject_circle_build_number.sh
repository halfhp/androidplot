# uses the vcs build number as the android version code and adds it to the display version string.
#MAJOR_MINOR=`cat version.txt`
VERSION_CODE=$VCS_BUILD_NUMBER
VERSION_NAME="$MAJOR_MINOR.$VERSION_CODE"

echo "Building with Version Name: $VERSION_NAME"

# update build.gradle with new version code:
perl -pi -w -e "s/theVersionCode = \d/theVersionCode = $VERSION_CODE/g;" build.gradle

# update build.gradle with new version name:
#perl -pi -w -e 's/_dev_build_/'$VERSION_NAME'/g;' build.gradle

