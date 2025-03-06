SUMMARY = "SciPy: Scientific Library for Python"
HOMEPAGE = "https://www.scipy.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5f477c3073ea2d02a70a764319f5f873"

inherit pkgconfig pypi python_mesonpy

SRC_URI[sha256sum] = "cd58a314d92838f7e6f755c8a2167ead4f27e1fd5c1251fd54289569ef3495ec"

DEPENDS += " \
	${PYTHON_PN}-numpy-native \
	${PYTHON_PN}-pybind11 \
	${PYTHON_PN}-pybind11-native \
	${PYTHON_PN}-pythran-native \
	${PYTHON_PN}-gast-native \
	${PYTHON_PN}-beniget-native \
	${PYTHON_PN}-ply-native \
	lapack \
        xsimd \
"

DEPENDS:append:class-target = " \
	${PYTHON_PN}-numpy \
"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-numpy \
"

PACKAGECONFIG ?= "lapack"

PACKAGECONFIG[openblas] = "-Dblas=openblas -Dlapack=openblas,,openblas,openblas"
PACKAGECONFIG[lapack] = "-Dblas=lapack -Dlapack=lapack,,lapack,lapack"
PACKAGECONFIG[f77] = "-Duse-g77-abi=true,,,"

CLEANBROKEN = "1"

export LAPACK = "${STAGING_LIBDIR}"
export BLAS = "${STAGING_LIBDIR}"

F90:class-native = "${FC}"
F90:class-target = "${TARGET_PREFIX}gfortran"
export F90

export F77 = "${TARGET_PREFIX}gfortran"

# Amend the file created by the meson class
# See https://scipy.github.io/devdocs/building/cross_compilation.html
do_write_config:append() {
    sed -i "/\[properties\]/anumpy-include-dir = '${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/numpy/_core/include'" ${WORKDIR}/meson.cross
}

do_install:append() {
    sed -i -e "s|--sysroot |--sysroot ${STAGING_DIR_TARGET}|g" ${D}${libdir}/${PYTHON_DIR}/site-packages/scipy/__config__.py
    sed -i -e "s|--sysroot=[^ ']*|--sysroot=${STAGING_DIR_TARGET}|g" ${D}${libdir}/${PYTHON_DIR}/site-packages/scipy/__config__.py
    sed -i -e 's|-ffile-prefix-map[^ ]*||g; s|-fdebug-prefix-map[^ ]*||g; s|-fmacro-prefix-map[^ ]*||g; s|${STAGING_DIR_TARGET}||g' ${D}${libdir}/${PYTHON_DIR}/site-packages/scipy/__config__.py


    PYTHON_BASEVERSION="${PYTHON_BASEVERSION}"
    rm ${D}${libdir}/${PYTHON_DIR}/site-packages/scipy/__pycache__/__config__.cpython-${PYTHON_BASEVERSION//.}.pyc
}

BBCLASSEXTEND = "native nativesdk"
