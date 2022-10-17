#!/usr/bin/env bash
############################################################
# source https://github.com/Javernaut/ffmpeg-android-maker #
############################################################

# Defining essential directories #

export BASE_DIR="$( cd "$( dirname "$0" )" && pwd )"

export SOURCES_DIR=${BASE_DIR}/sources
export STATS_DIR=${BASE_DIR}/stats
export SCRIPTS_DIR=${BASE_DIR}/scripts
export OUTPUT_DIR=${BASE_DIR}/output


# Directory to use as a place to build/install FFmpeg and its dependencies
BUILD_DIR=${BASE_DIR}/build
# Separate directory to build FFmpeg to
export BUILD_DIR_FFMPEG=$BUILD_DIR/ffmpeg
# All external libraries are installed to a single root
# to make easier referencing them when FFmpeg is being built.
export BUILD_DIR_EXTERNAL=$BUILD_DIR/external

# Function that copies *.so files and headers of the current ANDROID_ABI to the proper place inside OUTPUT_DIR
function prepareOutput() {
    OUTPUT_LIB=${OUTPUT_DIR}/lib/${ANDROID_ABI}
    mkdir -p "${OUTPUT_LIB}"
    cp "${BUILD_DIR_FFMPEG}"/"${ANDROID_ABI}"/lib/*.so "${OUTPUT_LIB}"

    OUTPUT_HEADERS=${OUTPUT_DIR}/include/${ANDROID_ABI}
    mkdir -p "${OUTPUT_HEADERS}"
    cp -r "${BUILD_DIR_FFMPEG}"/"${ANDROID_ABI}"/include/* "${OUTPUT_HEADERS}"
}

# Saving stats about text relocation presence.
function checkTextRelocations() {
    TEXT_REL_STATS_FILE=${STATS_DIR}/text-relocations.txt
    ${FAM_READELF} --dynamic ${BUILD_DIR_FFMPEG}/${ANDROID_ABI}/lib/*.so | grep 'TEXTREL\|File' >> ${TEXT_REL_STATS_FILE}

    if grep -q TEXTREL "${TEXT_REL_STATS_FILE}"; then
    echo "There are text relocations in output files:"
    cat "${TEXT_REL_STATS_FILE}"
    exit 1
    fi
}

# Actual work of the script #

# Clearing previously created binaries
rm -rf "${BUILD_DIR}"
rm -rf "${STATS_DIR}"
rm -rf "${OUTPUT_DIR}"
mkdir -p "${STATS_DIR}"
mkdir -p "${OUTPUT_DIR}"

# Exporting more necessary variables
source "${SCRIPTS_DIR}/export-host-variables.sh"
source "${SCRIPTS_DIR}/parse-arguments.sh"

# Check the host machine for proper setup and fail fast otherwise
"${SCRIPTS_DIR}"/check-host-machine.sh || exit 1

# Treating FFmpeg as just a module to build after its dependencies
COMPONENTS_TO_BUILD=${EXTERNAL_LIBRARIES[*]}
COMPONENTS_TO_BUILD+=( "ffmpeg" )

# Get the source code of component to build
for COMPONENT in ${COMPONENTS_TO_BUILD[*]}
do
    echo "Getting source code of the component: ${COMPONENT}"
    SOURCE_DIR_FOR_COMPONENT=${SOURCES_DIR}/${COMPONENT}

    mkdir -p "${SOURCE_DIR_FOR_COMPONENT}"
    cd "${SOURCE_DIR_FOR_COMPONENT}" || exit

    # Executing the component-specific script for downloading the source code
    source "${SCRIPTS_DIR}/${COMPONENT}/download.sh"

    # The download.sh script has to export SOURCES_DIR_$COMPONENT variable
    # with actual path of the source code. This is done for possibility to switch
    # between different versions of a component.
    # If it isn't set, consider SOURCE_DIR_FOR_COMPONENT as the proper value
    COMPONENT_SOURCES_DIR_VARIABLE=SOURCES_DIR_${COMPONENT}
    if [[ -z "${!COMPONENT_SOURCES_DIR_VARIABLE}" ]]; then
     export SOURCES_DIR_${COMPONENT}=${SOURCE_DIR_FOR_COMPONENT}
    fi

    # Returning to the rood directory. Just in case.
    cd "${BASE_DIR}" || exit
done

# Main build loop
for ABI in ${FFMPEG_ABIS_TO_BUILD[*]}
do
    # Exporting variables for the current ABI
    source "${SCRIPTS_DIR}/export-build-variables.sh" "${ABI}"

    for COMPONENT in ${COMPONENTS_TO_BUILD[*]}
    do
    echo "Building the component: ${COMPONENT}"
    COMPONENT_SOURCES_DIR_VARIABLE=SOURCES_DIR_${COMPONENT}

    # Going to the actual source code directory of the current component
    cd "${!COMPONENT_SOURCES_DIR_VARIABLE}" || exit

    # and executing the component-specific build script
    source ${SCRIPTS_DIR}/${COMPONENT}/build.sh || exit 1

    # Returning to the root directory. Just in case.
    cd "${BASE_DIR}" || exit
    done

    checkTextRelocations || exit 1

    prepareOutput
done
