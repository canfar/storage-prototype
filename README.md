---
---

CANFAR (C3TP) Storage Prototype with Alluxio and Flink
======================================================

## Alluxio Deployment

### Layout

#### Master Site
- Alluxio Master - West Cloud (alluxio VM - 206.12.48.90)
  - Central Alluxio host to control all file access.
- Alluxio Worker 1 - West Cloud (alluxio VM - 206.12.48.90)
  - Worker for the Master to thread I/O operations to.
- Alluxio Proxy 1 - West Cloud (alluxio VM - 206.12.48.90)
  - REST API interface to the Master running on port 39999.
- Flink Job Manager - West Cloud (flink-west VM - 206.12.59.34)
  - Central Flink location to manage jobs to Task Managers (slaves).
  - Remotely submit jobs on port 6123.
  - Dashboard available on port 8081.
- Flink Task Manager - West Cloud (flink-west VM - 206.12.59.34)
  - Task Manager for the Job Manager to thread out jobs to.

#### Secondary Site
- Alluxio Worker 2 - East Cloud (alluxio-proxy VM - 206.167.180.97)
  - Secondary worker strategically located to serve from a secondary site.
- Alluxio Proxy 2 - East Cloud (alluxio-proxy VM - 206.167.180.97)
  - Secondary proxy strategically located to serve from a secondary site.  This Proxy will request from the Master, then perform I/O operations from the local worker.
