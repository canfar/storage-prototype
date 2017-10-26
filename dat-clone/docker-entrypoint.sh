#!/bin/sh

###############################################################################
#
# License stuff goes here...
#
###############################################################################

if [[ ! -z "${DAT_LOGIN_EMAIL+x}" && ! -z "${DAT_LOGIN_PASSWORD+x}" ]]; then
    echo "Logging in as ${DAT_LOGIN_EMAIL}"
    dat login --email ${DAT_LOGIN_EMAIL} --password ${DAT_LOGIN_PASSWORD} --server ${DAT_REGISTRY_SERVER}
fi

if [[ ! -z "${DEBUG+x}" ]]; then
    SYNC="time dat sync -d ${DATA_DIR}/${DAT_KEY} --watch false"
else
    SYNC="dat sync -d ${DATA_DIR}/${DAT_KEY}"
fi

eval "dat clone ${DAT_REGISTRY_HOST}/dat://${DAT_KEY} --empty ${DATA_DIR}; ${SYNC}"
