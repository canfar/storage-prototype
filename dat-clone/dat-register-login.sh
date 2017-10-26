#!/bin/sh

dat register --username ${DAT_LOGIN_USERNAME} --email ${DAT_LOGIN_EMAIL} --password ${DAT_LOGIN_PASSWORD} --server ${DAT_REGISTRY_SERVER}
dat login --email ${DAT_LOGIN_EMAIL} --password ${DAT_LOGIN_PASSWORD} --server ${DAT_REGISTRY_SERVER}

