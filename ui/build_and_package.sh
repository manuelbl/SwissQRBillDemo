#!/bin/sh
rm -rf dist
npm run build
cd dist
zip qrbill-ui.zip -r ui
