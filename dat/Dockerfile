FROM node:6

#RUN apk --no-cache add g++ make python && \
RUN npm install -g dat

# Secret keys are kept here, but need to be in sync with the $DATA_DIR dir, so mount both of these!
VOLUME /root/.dat

ADD docker-entrypoint.sh /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 3282 8080
