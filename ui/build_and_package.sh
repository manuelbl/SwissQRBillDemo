#!/bin/sh
rm -rf build
yarn build
zip qrbill-ui.zip -r build
