FROM node

#RUN apk --no-cache add g++ make python && \
RUN npm install -g dat

# Make /bin/sh symlink to bash instead of dash because dash causes problems:
RUN echo "dash dash/sh boolean false" | debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive dpkg-reconfigure dash

# Secret keys are kept here, but need to be in sync with the $DATA_DIR dir, so mount both of these!
VOLUME /root/.dat

ADD docker-entrypoint.sh /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 3282 8080
