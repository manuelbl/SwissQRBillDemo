#!/bin/sh
rm -rf dist
yarn dist
zip qrbill-ui.zip -r dist
