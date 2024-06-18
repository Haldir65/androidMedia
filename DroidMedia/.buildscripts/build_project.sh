#!/bin/sh

. $(dirname "$0")/functions.sh

# Display all commands before executing them.
set -o errexit
set -o errtrace


function _build_main_project(){
     _green "[Build] now building main app \n"
    ./gradlew assembleDebug "-Dorg.gradle.jvmargs=-XX:+UseZGC -XX:+ZGenerational -Xmx8g"
    _blue "[Build] done building main app \n"
}

function _build_sub_app(){
    pushd carica
    _green "[Build] now building sub app \n"
    ./gradlew :VidCompose:assembleDebug "-Dorg.gradle.jvmargs=-XX:+UseZGC -XX:+ZGenerational -Xmx8g"
    _blue "[Build] done building sub app \n"
    popd
}

function main(){
    _build_main_project
    _build_sub_app
}


while getopts "t:a:s:i:f:mxh\?" o; do
    case "${o}" in
        a)
           some_arg="${OPTARG}"
            _green " [some_arg] = ${some_arg}\n"
            ;;
		s)
			some_arg="${OPTARG}"
            _green " [some_arg] = ${some_arg}\n"
			;;
        f)
            some_arg="${OPTARG}"
            _green " [some_arg] = ${some_arg}\n"
			;;    
		t)
            BUILD_TARGET="${OPTARG}"
            _blue "BUILD_TARGET = ${BUILD_TARGET}\n"
            case $BUILD_TARGET in
            all | everything)
                _green "BUILD_TARGET = ${BUILD_TARGET}\n build main app , then build sub app \n"
                _build_main_project
                _build_sub_app
                ;;
            main | media | mainapp)
                _green "BUILD_TARGET = ${BUILD_TARGET}\n"
                _build_main_project
                ;;
            sub | s | subapp)
                _green "BUILD_TARGET = ${BUILD_TARGET}\n"
                _build_sub_app
                ;;
            zz | "zse" | ju | "Vatican City")
                _green "BUILD_TARGET = ${BUILD_TARGET}\n"
                ;;
            *)
                _red "BUILD_TARGET = unknown build target \n"
                usage
                exit -1  # 当输出为"未知"时退出执行，并指定退出状态码为-1
                ;;
            esac
            ;;
		i)
			MACOS_X86_64_VERSION="${OPTARG}"
            _green "MACOS_X86_64_VERSION = ${MACOS_X86_64_VERSION}\n"
			;;
		a)
			MACOS_ARM64_VERSION="${OPTARG}"
            _green "MACOS_ARM64_VERSION = ${MACOS_ARM64_VERSION}\n"
			;;
        m)
            BUILD_TARGET="${OPTARG}"
            _blue "BUILD_TARGET = ${BUILD_TARGET}\n"
            ;;
		u)
			catalyst="1"
			CATALYST_IOS="${OPTARG}"
			;;
        x)
            bold=""
            subbold=""
            normal=""
            dim=""
            alert=""
            alertdim=""
            archbold=""
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))