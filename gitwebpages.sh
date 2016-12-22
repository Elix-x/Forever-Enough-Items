#!/bin/bash

USER="Elix-x"
NAME="Forever-Enough-Items"
GROUP="code/elix_x/mods/fei"

git config --global user.email "$USER@users.noreply.github.com"
git config --global user.name "Travis-Maven"

git clone https://$USER:$GITACCESSTOKEN@github.com/$USER/$USER.github.io.git

mkdir -p "$USER.github.io/maven2/$GROUP/$NAME/$TRAVIS_BRANCH/"
cp -r "./build/libs/." "./$USER.github.io/maven2/$GROUP/$NAME/$TRAVIS_BRANCH/"

cd "$USER.github.io"

git add *
git commit -m "Uploading maven artifacts for $NAME for $TRAVIS_BRANCH"
git push

cd ./..
rm -rf "$USER.github.io"