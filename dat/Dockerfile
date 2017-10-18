FROM node:6-alpine

RUN apk --no-cache add g++ make python && \
  npm install -g dat

ENV DAT_TITLE="My Data Title"
ENV DAT_DESCRIPTION="My Data Description"
ENV DATA_DIR="/data"

ADD docker-entrypoint.sh /docker-entrypoint.sh

VOLUME ${DATA_DIR}

# Secret keys are kept here, but need to be in sync with the $DATA_DIR dir, so mount both of these!
VOLUME /root/.dat

WORKDIR ${DATA_DIR}

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 3282 8080

CMD ["share"]
