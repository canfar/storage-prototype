#!/bin/sh

USAGE="Usage: ${0} <mount point> <address>"

MOUNT_POINT=${1}
ADDRESS=${2}

if [ "${MOUNT_POINT}" = "" ]; then
  echo ""
  echo ${USAGE}
  echo ""
  exit -1
elif [ "${ADDRESS}" = "" ]; then
  echo ""
  echo ${USAGE}
  echo ""
  exit -1
fi

generateJSON()
{
  cat <<EOD
{
"shared":true
}
EOD
}

echo "$(generateJSON)"
curl -v \
-H "Content-Type: application/json" \
-d "$(generateJSON)" \
"http://stortest3.cadc.dao.nrc.ca:39999/api/v1/paths/${MOUNT_POINT}/mount/?src=${ADDRESS}"

