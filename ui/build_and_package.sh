#!/bin/sh
rm -rf dist
yarn build
zip qrbill-ui.zip -r dist
