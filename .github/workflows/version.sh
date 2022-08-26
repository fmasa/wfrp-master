#!/usr/bin/env sh

if [ "$GITHUB_REF_TYPE" == "tag" ]; then
  echo $GITHUB_REF_NAME
  exit 0
fi

echo "dev-${GITHUB_SHA:0:7}"