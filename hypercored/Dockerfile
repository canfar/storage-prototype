FROM node:6-alpine

RUN apk --no-cache add g++ make python && \
  npm install -g sodium-native dat hypercored

ENV DAT_TITLE=DAO
ENV DAT_DESCRIPTION="DAO Archive"

# Create the .dat and the DAT itself.
RUN dat create '--title' "${DAT_TITLE}" '--description' "${DAT_DESCRIPTION}" -d /data /data | tail -1 > /home/node/feeds

# Volumes are probably the sub-directories?
#VOLUME /data
#VOLUME /root/.dat

EXPOSE 7000 3282 8887
CMD ["hypercored", "--port", "7000", "--cwd", "/home/node"]
