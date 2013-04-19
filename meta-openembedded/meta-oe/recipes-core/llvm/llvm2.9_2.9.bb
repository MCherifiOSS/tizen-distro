require llvm.inc

PR = "${INC_PR}.0"

SRC_URI += "file://0035-gcc-4.7.patch"

SRC_URI_append_libc-uclibc = " file://arm_fenv_uclibc.patch "

PARALLEL_MAKE_virtclass-native = ""

EXTRA_OECMAKE = "\
    -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm${LLVM_RELEASE}/tblgen \
    -DLLVM_TARGETS_TO_BUILD="X86;${LLVM_ARCH}" \
    -DCMAKE_LINKER:FILEPATH=${LD} \
    -DCMAKE_AR:FILEPATH=${AR} \
    -DCMAKE_OBJCOPY:FILEPATH=${OBJCOPY} \
    -DCMAKE_OBJDUMP:FILEPATH=${OBJDUMP} \
    -DCMAKE_RANLIB:FILEPATH=${RANLIB} \
    -DCMAKE_STRIP:FILEPATH=${STRIP} \
    -DNM_PATH:FILEPATH=${NM} \
    -DLLVM_ENABLE_PIC:BOOL=ON \
    -DLLVM_TARGET_ARCH:STRING=${LLVM_ARCH} \
    -DLLVM_ENABLE_ASSERTIONS:BOOL=ON \
    -DCMAKE_BUILD_TYPE:STRING=RelWithDebInfo \
    -DBUILD_SHARED_LIBS:BOOL=ON \
"

LLVM_RELEASE = "2.9"

SRC_URI[md5sum] = "793138412d2af2c7c7f54615f8943771"
SRC_URI[sha256sum] = "661236cfa17428b48cfa9cbb9909f7569c64b8ecd219fd91dbc00e3b557b3779"
