require gdb.inc

DEPENDS = "ncurses readline"
PR = "r0"

PACKAGES =+ 'gdbserver '
FILES_gdbserver = '${bindir}/gdbserver'

RRECOMMENDS_gdb = "glibc-thread-db"

inherit gettext

SRC_URI += file://kill_arm_map_symbols.patch;patch=1 \
#FIXME	   file://uclibc.patch;patch=1 \
	   file://gdbserver-cflags-last.diff;patch=1;pnum=0"

LDFLAGS_append = " -s"
export CFLAGS_append=" -L${STAGING_LIBDIR}"

EXTRA_OEMAKE = "'SUBDIRS=intl mmalloc libiberty opcodes bfd sim gdb etc utils'"

EXTRA_OECONF = "--disable-gdbtk --disable-tui --disable-x \
                --with-curses --disable-multilib --with-readline --disable-sim \
                --program-prefix=''"

do_configure () {
# override this function to avoid the autoconf/automake/aclocal/autoheader
# calls for now
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
        CPPFLAGS="" oe_runconf
}

do_install () {
	make -C bfd/doc chew LDFLAGS= CFLAGS=-O2
	oe_runmake DESTDIR='${D}' install
	install -d ${D}${bindir}
	install -m 0755 gdb/gdbserver/gdbserver ${D}${bindir}
}
