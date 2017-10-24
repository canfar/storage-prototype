FROM at88mph/dat:alpine

ENV DATA_DIR="/data"

# Override base entrypoint
ADD docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /data

# Ensure this is an external mount to make available to another process, like the 'publish' command.
VOLUME /data

CMD ["share"]
