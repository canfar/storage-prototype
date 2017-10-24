#!/bin/sh

###############################################################################
#
# License stuff goes here...
#
###############################################################################

if [[ ! -z "${DAT_LOGIN_EMAIL+x}" && ! -z "${DAT_LOGIN_PASSWORD+x}" ]]; then

    if [[ ! -z "${DAT_LOGIN_USERNAME}" ]]; then
        echo "Registering user ${DAT_LOGIN_USERNAME}.  DO NOT include \${DAT_LOGIN_USERNAME} to prevent registration."
        dat register --username ${DAT_LOGIN_USERNAME} --email ${DAT_LOGIN_EMAIL} --password ${DAT_LOGIN_PASSWORD} --server ${DAT_REGISTRY_SERVER}
    fi

    echo "Logging in as ${DAT_LOGIN_EMAIL}"
    dat login --email ${DAT_LOGIN_EMAIL} --password ${DAT_LOGIN_PASSWORD} --server ${DAT_REGISTRY_SERVER}
fi

exec dat ${@} --server ${DAT_REGISTRY_SERVER}
