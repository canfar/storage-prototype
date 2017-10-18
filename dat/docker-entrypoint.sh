#!/bin/sh

###############################################################################
#
# License stuff goes here...
#
###############################################################################

if [[ ! -e "${DATA_DIR}/dat.json" ]]; then
    exec dat create --title "${DAT_TITLE}" --description "${DAT_DESCRIPTION}" -d ${DATA_DIR} ${DATA_DIR} | tail -1 > /home/node/feeds
fi

exec dat "$@"
