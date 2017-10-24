#!/bin/sh
set -e

###############################################################################
#
# License stuff goes here...
#
###############################################################################

if [[ ! -z "${DAT_TITLE+x}" && ! -z "${DAT_DESCRIPTION+x}" && ! -e "${DATA_DIR}/dat.json" ]]; then
    echo "Creating new dat with { title: \"${DAT_TITLE}\", description: \"${DAT_DESCRIPTION}\" }"
    dat create --title "${DAT_TITLE}" --description "${DAT_DESCRIPTION}" -d ${DATA_DIR} ${DATA_DIR}
else
    echo ""
    echo "NOT creating new dat.  Either \${DAT_TITLE} and \${DAT_DESCRIPTION} were not specified, or there is already
     a dat.json present."
    echo ""
fi

echo "Running dat ${@}"
exec dat "${@}"
