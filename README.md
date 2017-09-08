---
---

CANFAR (C3TP) Storage Prototype with Alluxio and Flink
======================================================

## Alluxio Deployment

All deployment is handled by Docker.  Orchestration is handled by [Docker Compose](https://docs.docker.com/compose/overview/) via the provided
docker-compose.yml files on each site.

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
Both the Worker 2 and Proxy 2 need to be on the same host to properly
serve from this site.
- Alluxio Worker 2 - East Cloud (alluxio-proxy VM - 206.167.180.97)
  - Secondary worker strategically located to serve from a secondary site.
- Alluxio Proxy 2 - East Cloud (alluxio-proxy VM - 206.167.180.97)
  - Secondary proxy strategically located to serve from a secondary site.  This Proxy will request from the Master, then perform I/O operations from the local worker.

### Scripts
- **.alluxio.env**
  - Provides environment variables to use for the Docker Compose commands for each site running Alluxio.  Run with `$>. .alluxio.env`.
- **.flink.env**
  - Provides environment variables to use for the Docker Compose commands for each site running Flink.  Run with `$>. .flink.env`.
- **.openstack.east.env \| .openstack.west.env**
  - Provides environment variables to use with Docker Machine to run VMs on East Cloud or West Cloud OpenStack.  Run with `$>. .openstack.east.env` \| `$>. .openstack.west.env`.
- **mount-local.sh \| mount-s3-minio.sh \| mount-s3-uvic.sh**
  - Shell scripts to mount a local directory or an S3 bucket.  This uses the REST API of the Alluxio Master Proxy (Alluxio Proxy 1 above).

### Deploy
VMs are started using [Docker Machine](https://docs.docker.com/machine/overview/).

#### Start a VM on West Cloud
Modify for East Cloud as appropriate.

`. .openstack.west.env && docker-machine create --driver openstack <$VM_NAME>` where $VM_NAME is the name of the VM.  This was used to create the `alluxio` and `flink-west` VMs.
After the VM is started, execute
`eval $(docker-machine env <$VM_NAME>)`
To "activate" the VM, and then subsequent `docker` commands will be executed on that VM.

<aside class="warning">
The East Cloud OpenStack instance has issues booting VMs and more wait time is usually required.
</aside>

