#!/bin/sh
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <version>"
    exit 1
fi

podman pull ghcr.io/manuelbl/qrbill/qrbill-service:$1
podman tag ghcr.io/manuelbl/qrbill/qrbill-service:$1 ghcr.io/manuelbl/qrbill/qrbill-service:latest
systemctl restart qrbill
