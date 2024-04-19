#!/bin/bash
ARCH=`uname -m`

_green() {
    printf '\033[1;31;32m'
    printf -- "%b" "$1"
    printf '\033[0m'
}

_red() {
    printf '\033[1;31;31m'
    printf -- "%b" "$1"
    printf '\033[0m'
}

_yellow() {
    printf '\033[1;31;33m'
    printf -- "%b" "$1"
    printf '\033[0m'
}

_purple() {
    printf "\033[0;35m$1"
}

_orange() {
    printf "\033[0;33m$1"
}

_Cyan() {
    printf "\033[0;36m$1"
}

_blue() {
    printf "\033[0;34m$1"
}


function _download_if_not_exists(){
    local url=$1
    local FILE=$2
    if [ ! -f "$FILE" ]; then
        _blue "$FILE doesn't exists\n"
        wget $url -O $FILE
    else
        _green "$FILE already exists\n"
    fi
}