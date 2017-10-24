FROM at88mph/dat:alpine

# Override base entrypoint
ADD docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /data

# This MUST be mounted to wherever the share is located.
VOLUME /data

CMD ["publish", "-d", "/data"]
